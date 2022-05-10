package core;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class SchedulerTest {


    @Test
    void test() throws InterruptedException {
        Scheduler scheduler = new Scheduler(3, 10);
        scheduler.addTask(new MyTask("No. 1"));
        scheduler.addTask(new MyTask("No. 2"), 10000);
        scheduler.addRepeatableTask(new MyTask("Repeatable No. 3"), 200, 2, TimeUnit.SECONDS);

        scheduler.start();
    }
}