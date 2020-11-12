package io.github.underscore11code.utilbot.configs;

import net.dv8tion.jda.api.entities.Guild;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;

@ConfigSerializable
public class GuildConfig {
    private static final ObjectMapper<GuildConfig> MAPPER;
    private static final File rootDir = new File("./data/guilds");

    static {
        try {
            MAPPER = ObjectMapper.factory().get(GuildConfig.class);
        } catch (final SerializationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static HoconConfigurationLoader getLoader(String id) {
        return HoconConfigurationLoader.builder()
                .file(new File(rootDir, id + ".conf"))
                .build();
    }

    public static GuildConfig of(Guild guild) throws ConfigurateException {
        HoconConfigurationLoader loader = getLoader(guild.getId());
        CommentedConfigurationNode node = loader.load();
        GuildConfig config = MAPPER.load(node);
        config.guildId = guild.getId();
        config.save();
        return config;
    }

    public void save() throws ConfigurateException {
        HoconConfigurationLoader loader = getLoader(this.guildId);
        CommentedConfigurationNode node = loader.load();
        MAPPER.save(this, node);
        loader.save(node);
    }

    @Setting
    private String guildId;
}
