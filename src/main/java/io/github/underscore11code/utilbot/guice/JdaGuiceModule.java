package io.github.underscore11code.utilbot.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class JdaGuiceModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(JdaGuiceModule.class);

    protected void configure() {
        //add configuration logic here
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
    JDA getJda(@Token String token) throws LoginException, InterruptedException {
        return JDABuilder.createDefault(token).build().awaitReady();
    }
}
