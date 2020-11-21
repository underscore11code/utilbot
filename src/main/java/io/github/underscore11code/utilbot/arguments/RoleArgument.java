//
// A lightly modified version of https://github.com/Incendo/cloud/blob/cfac2639ad0e61b04d6fe492306dcc49b104a409/cloud-discord/cloud-jda/src/main/java/cloud/commandframework/jda/parsers/UserArgument.java
//
package io.github.underscore11code.utilbot.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@SuppressWarnings("unused")
public final class RoleArgument<C> extends CommandArgument<C, Role> {

    private final Set<ParserMode> modes;

    private RoleArgument(
            final boolean required, final @NonNull String name,
            final @NonNull Set<ParserMode> modes
    ) {
        super(required, name, new RoleParser<>(modes), Role.class);
        this.modes = modes;
    }

    /**
     * Create a new builder
     *
     * @param name Name of the component
     * @param <C>  Command sender type
     * @return Created builder
     */
    public static <C> @NonNull Builder<C> newBuilder(final @NonNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a new required command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NonNull CommandArgument<C, Role> of(final @NonNull String name) {
        return RoleArgument.<C>newBuilder(name).withParserMode(ParserMode.MENTION).asRequired().build();
    }

    /**
     * Create a new optional command component
     *
     * @param name Component name
     * @param <C>  Command sender type
     * @return Created component
     */
    public static <C> @NonNull CommandArgument<C, Role> optional(final @NonNull String name) {
        return RoleArgument.<C>newBuilder(name).withParserMode(ParserMode.MENTION).asOptional().build();
    }

    /**
     * Get the modes enabled on the parser
     *
     * @return List of Modes
     */
    public @NotNull Set<ParserMode> getModes() {
        return modes;
    }


    public enum ParserMode {
        MENTION,
        ID,
        NAME
    }


    public static final class Builder<C> extends CommandArgument.Builder<C, Role> {

        private Set<ParserMode> modes = new HashSet<>();

        private Builder(final @NonNull String name) {
            super(Role.class, name);
        }

        /**
         * Set the modes for the parsers to use
         *
         * @param modes List of Modes
         * @return Builder instance
         */
        public @NonNull Builder<C> withParsers(final @NonNull Set<ParserMode> modes) {
            this.modes = modes;
            return this;
        }

        /**
         * Add a parser mode to use
         *
         * @param mode Parser mode to add
         * @return Builder instance
         */
        public @NonNull Builder<C> withParserMode(final @NonNull ParserMode mode) {
            this.modes.add(mode);
            return this;
        }

        /**
         * Builder a new example component
         *
         * @return Constructed component
         */
        @Override
        public @NonNull RoleArgument<C> build() {
            return new RoleArgument<>(this.isRequired(), this.getName(), modes);
        }

    }


    public static final class RoleParser<C> implements ArgumentParser<C, Role> {

        private final Set<ParserMode> modes;

        private RoleParser(final @NonNull Set<ParserMode> modes) {
            this.modes = modes;
        }

        @Override
        public @NonNull ArgumentParseResult<Role> parse(
                final @NonNull CommandContext<C> commandContext,
                final @NonNull Queue<@NonNull String> inputQueue
        ) {
            final String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(
                        RoleParser.class,
                        commandContext
                ));
            }

            final JDA jda = commandContext.get("JDA");
            Exception exception = null;

            if (modes.contains(ParserMode.MENTION)) {
                if (input.endsWith(">") || modes.size() == 1) {
                    final String id;
                    if (input.startsWith("<@&")) {
                        id = input.substring(3, input.length() - 1);
                    } else {
                        id = input.substring(2, input.length() - 1);
                    }

                    try {
                        final ArgumentParseResult<Role> result = this.roleFromId(jda, input, id);
                        inputQueue.remove();
                        return result;
                    } catch (final RoleNotFoundParseException | NumberFormatException e) {
                        exception = e;
                    }
                }
            }

            if (modes.contains(ParserMode.ID)) {
                try {
                    final ArgumentParseResult<Role> result = this.roleFromId(jda, input, input);
                    inputQueue.remove();
                    return result;
                } catch (final RoleNotFoundParseException | NumberFormatException e) {
                    exception = e;
                }
            }

            if (modes.contains(ParserMode.NAME)) {
                final List<Role> roles = jda.getRolesByName(input, true);

                if (roles.size() == 0) {
                    exception = new RoleNotFoundParseException(input);
                } else if (roles.size() > 1) {
                    exception = new TooManyRolesFoundParseException(input);
                } else {
                    inputQueue.remove();
                    return ArgumentParseResult.success(roles.get(0));
                }
            }

            assert exception != null;
            return ArgumentParseResult.failure(exception);
        }

        @Override
        public boolean isContextFree() {
            return true;
        }

        private @NonNull ArgumentParseResult<Role> roleFromId(
                final @NonNull JDA jda, final @NonNull String input,
                final @NonNull String id
        )
                throws RoleNotFoundParseException, NumberFormatException {
            final Role role = jda.getRoleById(id);

            if (role == null) {
                throw new RoleNotFoundParseException(input);
            } else {
                return ArgumentParseResult.success(role);
            }
        }

    }


    public static class RoleParseException extends IllegalArgumentException {

        private static final long serialVersionUID = -6728909884195850077L;
        private final String input;

        /**
         * Construct a new Role parse exception
         *
         * @param input String input
         */
        public RoleParseException(final @NonNull String input) {
            this.input = input;
        }

        /**
         * Get the Roles input
         *
         * @return Roles input
         */
        public final @NonNull String getInput() {
            return input;
        }

    }


    public static final class TooManyRolesFoundParseException extends RoleParseException {

        private static final long serialVersionUID = 7222089412615886672L;

        /**
         * Construct a new Role parse exception
         *
         * @param input String input
         */
        public TooManyRolesFoundParseException(final @NonNull String input) {
            super(input);
        }

        @Override
        public @NonNull String getMessage() {
            return String.format("Too many Roles found for '%s'.", getInput());
        }

    }


    public static final class RoleNotFoundParseException extends RoleParseException {

        private static final long serialVersionUID = 3689949065073643826L;

        /**
         * Construct a new Role parse exception
         *
         * @param input String input
         */
        public RoleNotFoundParseException(final @NonNull String input) {
            super(input);
        }

        @Override
        public @NonNull String getMessage() {
            return String.format("Role not found for '%s'.", getInput());
        }

    }

}
