package io.github.underscore11code.utilbot.modules;

import cloud.commandframework.Description;
import cloud.commandframework.jda.JDA4CommandManager;
import io.github.underscore11code.utilbot.module.ICommandModule;
import io.github.underscore11code.utilbot.sender.IBotSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ModulePing implements ICommandModule {

    @Override
    public void registerCommands(JDA4CommandManager<IBotSender> manager) {
        manager.command(manager
                .commandBuilder("ping", Description.of("Checks the bot's connection speed to Discord"))
                .handler(commandContext -> {
                    IBotSender sender = commandContext.getSender();
                    sender.getChannel().sendMessage(getEmbed(sender.getEvent().getMessage()))
                            .queue(message -> {
                                message.editMessage(getEmbed(sender.getEvent().getMessage(), message)).queue();
                            });
                })
        );
    }

    private MessageEmbed getEmbed(Message originalMessage) {
        return new EmbedBuilder().setTitle("Pong!").build();
    }

    private MessageEmbed getEmbed(Message originalMessage, Message sentMessage) {
        return new EmbedBuilder(getEmbed(originalMessage)).setDescription("Latency: " +
                (sentMessage.getTimeCreated().toInstant().toEpochMilli() -
                        originalMessage.getTimeCreated().toInstant().toEpochMilli()) + "ms").build();
    }
}
