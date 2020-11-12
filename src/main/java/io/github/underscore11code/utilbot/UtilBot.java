package io.github.underscore11code.utilbot;

import cloud.commandframework.jda.JDA4CommandManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.underscore11code.utilbot.module.ICommandModule;
import io.github.underscore11code.utilbot.guice.IService;
import io.github.underscore11code.utilbot.module.IListenerModule;
import io.github.underscore11code.utilbot.sender.IBotSender;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class UtilBot implements IService {
    @Getter private Status status = Status.STOPPED;

    @Inject private JDA jda;
    @Inject JDA4CommandManager<IBotSender> commandManager;
    @Inject private Reflections reflections;

    private static final Logger logger = LoggerFactory.getLogger(UtilBot.class);

    @Override
    public void start() {
        status = Status.STARTING;
        logger.info("Starting!");

        reflections.getSubTypesOf(ICommandModule.class).forEach(aClass -> {
            ICommandModule module = Bootstrap.getInjector().getInstance(aClass);
            logger.info("Registering command module " + module.getClass().getSimpleName());
            module.registerCommands(commandManager);
        });

        reflections.getSubTypesOf(IListenerModule.class).forEach(aClass -> {
            IListenerModule module = Bootstrap.getInjector().getInstance(aClass);
            logger.info("Registering listener module " + module.getClass().getSimpleName());
            module.registerListeners(jda);
        });

        logger.info("Commands loaded!");
        logger.info("Started!");
        status = Status.STARTED;
    }

    @Override
    public void stop() {
        status = Status.STOPPING;
        logger.info("Stopping!");
        jda.shutdown();

        logger.info("Stopped!");
        status = Status.STOPPED;
    }

    public static String getOwnerId() {
        return "404882575543238656";
    }
}
