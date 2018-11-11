package com.example.phanminhduong.reminder.model;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("link")
    private String link;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
