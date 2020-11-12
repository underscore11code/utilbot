package io.github.underscore11code.utilbot.modules;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.Description;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.jda.JDA4CommandManager;
import io.github.underscore11code.utilbot.module.ICommandModule;
import io.github.underscore11code.utilbot.sender.IBotSender;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.Color;
import java.util.List;

public class ModuleAbout implements ICommandModule {
    @Override
    public void registerCommands(JDA4CommandManager<IBotSender> manager) {
        manager.command(manager.commandBuilder("help", Description.of("Get Help!"))
                .argument(StringArgument.greedy("search"))
                .handler(commandContext -> {
                    String search = commandContext.getOrDefault("search", "");
                    if (search == null) search = ""; // shut up IJ
                    CommandHelpHandler.HelpTopic<IBotSender> topic = manager.getCommandHelpHandler()
                            .queryHelp(commandContext.getSender(), search);
                    EmbedBuilder embed = new EmbedBuilder()
                            .setTitle("This should not happen")
                            .setColor(Color.RED);
                    if (topic instanceof CommandHelpHandler.IndexHelpTopic)
                        embed = helpIndex((CommandHelpHandler.IndexHelpTopic<IBotSender>) topic);
                    else if (topic instanceof CommandHelpHandler.VerboseHelpTopic)
                        embed = helpVerbose((CommandHelpHandler.VerboseHelpTopic<IBotSender>) topic);
                    else if (topic instanceof CommandHelpHandler.MultiHelpTopic)
                        embed = helpMulti((CommandHelpHandler.MultiHelpTopic<IBotSender>) topic);
                    commandContext.getSender().getChannel().sendMessage(embed.setTitle("Help: " + search).build()).queue();
                }));

        manager.command(manager.commandBuilder("help", Description.of("TEST"))
                .handler(commandContext -> {
                    commandContext.getSender().getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Help")
                            .setDescription(getIndexDescription(manager.getCommandHelpHandler().getAllCommands()))
                            .setColor(Color.RED)
                            .build()).queue();
                }));
    }

    private EmbedBuilder helpIndex(CommandHelpHandler.IndexHelpTopic<IBotSender> topic) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setDescription(getIndexDescription(topic.getEntries()));
        embed.setColor(Color.RED);
        return embed;
    }

    private String getIndexDescription(List<CommandHelpHandler.VerboseHelpEntry<IBotSender>> entryList) {
        StringBuilder description = new StringBuilder();
        for (CommandHelpHandler.VerboseHelpEntry<IBotSender> entry : entryList) {
            if (entry.getCommand().isHidden()) continue;
            description
                    .append("`")
                    .append(entry.getSyntaxString())
                    .append("`: ")
                    .append(entry.getCommand().getArgumentDescription(entry.getCommand().getArguments().get(0)))
                    .append("\n");
        }
        if (description.length() == 0) description.append("<No Results>");
        return description.toString();
    }

    private EmbedBuilder helpVerbose(CommandHelpHandler.VerboseHelpTopic<IBotSender> topic) {
        EmbedBuilder embed = new EmbedBuilder();
        List<CommandArgument<IBotSender, ?>> arguments = topic.getCommand().getArguments();
        CommandArgument<IBotSender, ?> root = arguments.remove(0);
        embed.setDescription(topic.getCommand().getArgumentDescription(root));
        for (CommandArgument<IBotSender, ?> argument : arguments) {
            embed.addField(argument.getName() + " " + argument.getValueType().getType().getTypeName(),
                    topic.getCommand().getArgumentDescription(argument),
                    false);
        }
        embed.setColor(Color.GREEN);
        return embed;
    }

    private EmbedBuilder helpMulti(CommandHelpHandler.MultiHelpTopic<IBotSender> topic) {
        EmbedBuilder embed = new EmbedBuilder();
        StringBuilder description = new StringBuilder();
        for (String s : topic.getChildSuggestions()) {
            description.append(s).append("\n");
        }
        embed.setDescription(description);
        embed.setColor(Color.YELLOW);
        return embed;
    }
}
