package io.github.underscore11code.utilbot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.underscore11code.utilbot.guice.MainModule;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    @Getter private static Injector injector;
    public static void main(String[] args) {
        logger.info("Initializing Main Module");
        MainModule mainModule = new MainModule();
        logger.info("Creating Injector. This causes a illegal reflective access error. " +
                "It has been fixed in Guice dev builds, awaiting an official release. It can be ignored.");
        injector = Guice.createInjector(mainModule);
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
