package core;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TimedTask {

    private Task task;
    private AtomicInteger executionCount;
    private TaskType type = TaskType.SINGLE;
    private Calendar scheduledTime;
    private long repeatablePeriod;
    private TimeUnit repeatablePeriodUnit;

    public TimedTask(Task task, Calendar scheduledTime) {
        this.task = task;
        this.scheduledTime = scheduledTime;
        this.executionCount = new AtomicInteger(0);
    }

    public TimedTask(Task task, Calendar scheduledTime, final long period, final TimeUnit unit) {
        this(task, scheduledTime);
        this.type = TaskType.REPEATABLE;
        this.repeatablePeriod = period;
        this.repeatablePeriodUnit = unit;
    }

    public static TimedTask fromTask(Task task, long initialDelayInMs) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(now.getTimeInMillis() + initialDelayInMs);
        return new TimedTask(task, now);
    }

    public  static TimedTask fromTask(Task task, long initialDelayInMs, final long period, final TimeUnit unit) {

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(now.getTimeInMillis() + initialDelayInMs);

        return new TimedTask(task, now, period, unit);
    }

    public static TimedTask fromTask(TimedTask timedTask) {
        Calendar now = Calendar.getInstance();

        now.setTimeInMillis(now.getTimeInMillis() + timedTask.getRepeatablePeriodUnit().toMillis(timedTask.getRepeatablePeriod()));

        return new TimedTask(timedTask.getTask(), now, timedTask.getRepeatablePeriod(), timedTask.getRepeatablePeriodUnit());
    }

    public TaskType getType() {
        return type;
    }

    public Task getTask() {
        return task;
    }

    public Calendar getScheduledTime() {
        return scheduledTime;
    }

    public long runFromNow() {
        return scheduledTime.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
    }

    public boolean shouldRunNow() {
        return runFromNow() <= 0;
    }

    public AtomicInteger getExecutionCount() {
        return executionCount;
    }

    public long getRepeatablePeriod() {
        return repeatablePeriod;
    }

    public TimeUnit getRepeatablePeriodUnit() {
        return repeatablePeriodUnit;
    }

    enum TaskType {
        SINGLE,
        REPEATABLE
    }
}
