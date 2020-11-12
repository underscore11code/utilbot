package io.github.underscore11code.utilbot.module;

import cloud.commandframework.jda.JDA4CommandManager;
import io.github.underscore11code.utilbot.sender.IBotSender;

public interface ICommandModule {
    void registerCommands(JDA4CommandManager<IBotSender> manager);
}
