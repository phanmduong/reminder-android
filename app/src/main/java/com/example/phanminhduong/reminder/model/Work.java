package com.example.phanminhduong.reminder.model;

public class Work {
    private String note;
    private String deadline;
    private String name;
    public int status;
    public int group_id;
    public int id;

    public Work( String name,String note, String deadline, int status, int group_id, int id) {
        this.note = note;
        this.deadline = deadline;
        this.name = name;
        this.status = status;
        this.group_id = group_id;
        this.id = id;
    }

    public Work(String name, String note, String deadline,  int status) {
        this.note = note;
        this.deadline = deadline;
        this.name = name;
        this.status = status;
    }

    public Work( String name,String note, String deadline, int status, int id) {
        this.note = note;
        this.deadline = deadline;
        this.name = name;
        this.status = status;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Work{" +
                "note='" + note + '\'' +
                ", deadline='" + deadline + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", group_id=" + group_id +
                ", id=" + id +
                '}';
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
