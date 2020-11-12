package io.github.underscore11code.utilbot.guice;

public interface IService {
    void start() throws Exception;

    void stop();

    Status getStatus();

    enum  Status {
        STOPPED,
        STARTING,
        STARTED,
        STOPPING
    }
}
