package io.github.underscore11code.utilbot.modules;

import cloud.commandframework.jda.JDA4CommandManager;
import com.google.inject.Inject;
import io.github.underscore11code.utilbot.module.ICommandModule;
import io.github.underscore11code.utilbot.sender.GuildBotSender;
import io.github.underscore11code.utilbot.sender.IBotSender;
import io.github.underscore11code.utilbot.util.Emojis;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class ModuleList implements ICommandModule {
    @Inject private JDA jda;

    @Override
    public void registerCommands(JDA4CommandManager<IBotSender> manager) {
        manager.command(manager
                .commandBuilder("list", "ls")
                .senderType(GuildBotSender.class)
                .literal("roles", "r", "role")
                .handler(commandContext -> {
                    GuildBotSender sender = (GuildBotSender) commandContext.getSender();
                    sender.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("Guild Roles")
                        .setDescription(getRoles(sender.getGuild()))
                        .build())
                            .reference(commandContext.getSender().getEvent().getMessage())
                            .queue();
                })
        );

        manager.command(manager
                .commandBuilder("list", "ls")
                .senderType(GuildBotSender.class)
                .literal("guilds", "servers", "g", "s", "guild", "server")
                .permission("bot.owner")
                .handler(commandContext -> {
                    GuildBotSender sender = (GuildBotSender) commandContext.getSender();
                    sender.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Guilds")
                            .setDescription(getGuilds(jda))
                            .build())
                            .reference(commandContext.getSender().getEvent().getMessage())
                            .queue();
                }));
    }

    private String getRoles(Guild guild) {
        StringBuilder builder = new StringBuilder();
        guild.getRoles().forEach(role -> builder.append(Emojis.MENTION)
                .append(role.getAsMention())
                .append(" (")
                .append(role.getId())
                .append(")")
                .append("\n"));

        return builder.toString();
    }

    private String getGuilds(JDA jda) {
        StringBuilder builder = new StringBuilder();
        jda.getGuilds().forEach(guild -> builder.append(guild.getName())
                .append(" (")
                .append(guild.getId())
                .append(")")
                .append("\n"));

        return builder.toString();
    }
}
