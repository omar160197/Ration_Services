package com.appz.qrcode.client_tasks.models;

public class MemberModel {
    private String id;
    private String name;
    private double point;

    public MemberModel() {
    }

    public MemberModel(String id, String name) {
        this.id = id;
        this.name = name;
        point = 50.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
