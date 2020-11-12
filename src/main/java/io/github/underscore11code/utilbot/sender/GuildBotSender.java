package io.github.underscore11code.utilbot.sender;

import io.github.underscore11code.utilbot.UtilBot;
import lombok.Data;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

@Data
public class GuildBotSender implements IBotSender {
    private final User user;
    private final Member member;
    private final TextChannel channel;
    private final MessageReceivedEvent event;

    public Guild getGuild() {
        return channel.getGuild();
    }

    @Override
    public boolean hasPermission(String permission) {
        if (member.getId().equals(UtilBot.getOwnerId())) return true;
        EnumSet<Permission> discordPermissions;
        Set<String> permissions = new HashSet<>();
        if (channel != null) discordPermissions = member.getPermissions(channel);
        else discordPermissions = member.getPermissions();
        discordPermissions.forEach(permission1 -> permissions.add("guild.permission." + permission1.getName()));

        if (member.isOwner()) permissions.add("guild.owner");
        if (member.getTimeBoosted() != null) permissions.add("guild.booster");

        return permissions.contains(permission.toLowerCase());
    }
}
