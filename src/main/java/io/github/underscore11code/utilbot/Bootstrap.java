package io.github.underscore11code.utilbot;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.underscore11code.utilbot.guice.CloudGuiceModule;
import io.github.underscore11code.utilbot.guice.JdaGuiceModule;
import lombok.Getter;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    @Getter private static Injector injector;
    public static void main(String[] args) {
        injector = Guice.createInjector(new JdaGuiceModule(), new CloudGuiceModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(Reflections.class).toInstance(new Reflections("io.github.underscore11code.utilbot"));
            }
        });
        logger.info("Starting UtilBot");
        UtilBot bot = injector.getInstance(UtilBot.class);
        Runtime.getRuntime().addShutdownHook(new Thread(bot::stop, "Shutdown"));
        try {
            bot.start();
        } catch (Exception e) {
            logger.error("Uncaught exception on init!", e);
        }
    }
}
