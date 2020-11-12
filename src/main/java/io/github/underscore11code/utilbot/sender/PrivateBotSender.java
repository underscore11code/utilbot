package io.github.underscore11code.utilbot.sender;

import io.github.underscore11code.utilbot.UtilBot;
import lombok.Data;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Data
public class PrivateBotSender implements IBotSender {
    private final User user;
    private final PrivateChannel channel;
    private final MessageReceivedEvent event;

    PrivateChannel getPrivateChannel() {
        return channel;
    }

    @Override
    public boolean hasPermission(String permission) {
        if (user.getId().equals(UtilBot.getOwnerId())) return true;
        return false;
    }
}
