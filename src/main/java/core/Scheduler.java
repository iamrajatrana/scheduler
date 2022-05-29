package core;

import java.util.concurrent.*;

public class Scheduler implements IService {

    private volatile boolean running = true;
    private State state;
    private final BlockingQueue<TimedTask> queue;
    private Object lock = new Object();
    Executor executor;


    Scheduler(int poolSize, int queueSize) {
        this.state = State.NEW;
        this.executor = Executors.newFixedThreadPool(poolSize);
        this.queue = new PriorityBlockingQueue<>(queueSize,
                (TimedTask s, TimedTask t) -> {
                    return s.getScheduledTime().compareTo(t.getScheduledTime());
                });
    }


    @Override
    public void start() throws InterruptedException {
        this.state = State.STARTING;
        while (running) {
            this.state = State.RUNNING;
            TimedTask timedTask = queue.take();

            if (timedTask != null) {
                executor.execute(timedTask.getTask());
                if (timedTask.getType().equals(TimedTask.TaskType.REPEATABLE)) {
                    queue.offer(timedTask.fromTask(timedTask));
                }
            }
            waitForNextTask();
        }
        this.state = State.TERMINATED;

    }

    private void waitForNextTask() throws InterruptedException {
        synchronized (lock) {
            TimedTask nextTask = queue.peek();
            while (nextTask == null || !nextTask.shouldRunNow()) {
                if (nextTask == null) {
                    lock.wait();
                } else {
                    lock.wait(nextTask.runFromNow());
                }
                nextTask = queue.peek();
            }
        }
    }

    @Override
    public void stop() {
        this.state = State.STOPPING;
        this.running = false;
    }

    public void addTask(Task task) {
        synchronized (lock) {
            queue.offer(TimedTask.fromTask(task, 0));
            lock.notify();
        }
    }

    public void addTask(Task task, long initialDelayInMs) {
        synchronized (lock) {
            queue.offer(TimedTask.fromTask(task, initialDelayInMs));
            lock.notify();
        }
    }


    public void addRepeatableTask(Task task, long initialDelayInMs, final long period, final TimeUnit unit) {
        synchronized (lock) {
            queue.offer(TimedTask.fromTask(task, initialDelayInMs, period, unit));
            lock.notify();
        }
    }


    @Override
    public State state() {
        return this.state;
    }


}
