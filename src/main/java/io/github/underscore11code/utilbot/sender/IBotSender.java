package io.github.underscore11code.utilbot.sender;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface IBotSender {
    User getUser();
    MessageChannel getChannel();
    MessageReceivedEvent getEvent();
    boolean hasPermission(String permission);
}
