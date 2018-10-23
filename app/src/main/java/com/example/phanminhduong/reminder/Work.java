package com.example.phanminhduong.reminder;

import java.sql.Time;

public class Work {
    private String title;
    private Time time;
    private boolean status;

    public Work(String title, Time time, boolean status) {
        this.title = title;
        this.time = time;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
