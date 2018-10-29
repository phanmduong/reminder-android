package com.example.phanminhduong.reminder;

import java.sql.Date;
import java.sql.Time;

public class Work {
    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Work(String note, String date, boolean status) {

        this.note = note;
        this.date = date;
        this.status = status;
    }

    private String date;
    private boolean status;


}
