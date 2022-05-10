package core;

import java.util.concurrent.Executor;

public interface IService {

    void start() throws InterruptedException;

    void stop();

    State state();

    enum State {
        NEW,
        STARTING,
        RUNNING,
        STOPPING,
        TERMINATED
    }

}
