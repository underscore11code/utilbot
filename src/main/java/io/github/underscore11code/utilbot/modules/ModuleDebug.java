package io.github.underscore11code.utilbot.modules;

import cloud.commandframework.Description;
import cloud.commandframework.jda.JDA4CommandManager;
import io.github.underscore11code.utilbot.module.ICommandModule;
import io.github.underscore11code.utilbot.sender.GuildBotSender;
import io.github.underscore11code.utilbot.sender.IBotSender;

public class ModuleDebug implements ICommandModule {
    @Override
    public void registerCommands(JDA4CommandManager<IBotSender> manager) {
        manager.command(manager.commandBuilder("debug", Description.of("Internal Debug Command"))
                .hidden()
                .senderType(GuildBotSender.class)
                .handler(commandContext -> {
                    commandContext.getSender().getChannel().sendMessage("doing shit").queue();

                    manager.getCommands().forEach(iBotSenderCommand -> {
                        commandContext.getSender().getChannel().sendMessage(iBotSenderCommand.getCommandMeta().getAll().toString()).queue();
                        commandContext.getSender().getChannel().sendMessage(iBotSenderCommand.toString()).queue();
                    });

                    commandContext.getSender().getChannel().sendMessage("did shit").queue();
                }));
    }
}
