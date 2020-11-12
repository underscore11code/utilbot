package io.github.underscore11code.utilbot.guice;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.jda.JDA4CommandManager;
import cloud.commandframework.jda.JDAGuildSender;
import cloud.commandframework.jda.JDAPrivateSender;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.underscore11code.utilbot.sender.GuildBotSender;
import io.github.underscore11code.utilbot.sender.IBotSender;
import io.github.underscore11code.utilbot.sender.PrivateBotSender;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(MainModule.class);

    protected void configure() {
        //add configuration logic here
        bind(Reflections.class).toInstance(new Reflections("io.github.underscore11code.utilbot"));
        bind(ExecutorService.class).toInstance(Executors.newCachedThreadPool());
    }

    @Provides @Token
    String getToken() {
        if (System.getProperty("TOKEN") != null)
            return System.getProperty("TOKEN");
        if (System.getenv("utilbot_token") != null)
            return System.getenv("utilbot_token");
        logger.error("FATAL: No token found!");
        System.exit(1);
        return null;
    }

    @Provides @Singleton
    JDA4CommandManager<IBotSender> getCommandManager(JDA jda) throws InterruptedException {
        return new JDA4CommandManager<>(jda,
                prefix -> "$",
                IBotSender::hasPermission,
                CommandExecutionCoordinator.simpleCoordinator(),
                sender -> {
                    if (sender instanceof JDAPrivateSender) {
                        JDAPrivateSender privateSender = (JDAPrivateSender) sender;
                        return new PrivateBotSender(privateSender.getUser(),
                                privateSender.getPrivateChannel(),
                                sender.getEvent().get());
                    } else if (sender instanceof JDAGuildSender) {
                        JDAGuildSender guildSender = (JDAGuildSender) sender;
                        return new GuildBotSender(guildSender.getUser(),
                                guildSender.getMember(),
                                guildSender.getTextChannel(),
                                sender.getEvent().get());
                    }
                    throw new IllegalStateException();
                },
                botUser -> {
                    if (botUser instanceof PrivateBotSender) {
                        PrivateBotSender privateBotUser = (PrivateBotSender) botUser;
                        return new JDAPrivateSender(null, privateBotUser.getUser(), privateBotUser.getChannel());
                    } else if (botUser instanceof GuildBotSender) {
                        GuildBotSender guildBotSender = (GuildBotSender) botUser;
                        return new JDAGuildSender(null, guildBotSender.getMember(), guildBotSender.getChannel());
                    }
                    throw new IllegalStateException();
                });
    }

    @Provides @Singleton
    JDA getJda(@Token String token) throws LoginException, InterruptedException {
        return JDABuilder.createDefault(token).build().awaitReady();
    }
}
