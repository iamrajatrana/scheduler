package core;

import java.util.Calendar;

public class MyTask implements Task {

    private String name;

    public MyTask(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println(name + ": " + Calendar.getInstance().getTime() + " " + Thread.currentThread().getName() );
    }
}
