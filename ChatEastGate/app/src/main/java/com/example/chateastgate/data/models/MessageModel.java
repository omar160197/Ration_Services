package com.example.chateastgate.data.models;

public class MessageModel {
    private int id;
    private String text;
    private boolean is_reading;
    private String from;
    private  String to;

    public MessageModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isIs_reading() {
        return is_reading;
    }

    public void setIs_reading(boolean is_reading) {
        this.is_reading = is_reading;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public MessageModel(int id, String text, boolean is_reading, String from, String to) {
        this.id = id;
        this.text = text;
        this.is_reading = is_reading;
        this.from = from;
        this.to = to;
    }
}
