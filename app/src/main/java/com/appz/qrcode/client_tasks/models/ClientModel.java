package com.appz.qrcode.client_tasks.models;

import java.util.List;

public class ClientModel {
    private String id;
    private String name;
    private double point;
    private List<MemberModel> memberModels;

    public ClientModel() {
    }

    public ClientModel(String id, String name, List<MemberModel> memberModels) {
        this.id = id;
        this.name = name;
        this.memberModels = memberModels;
    }

    public ClientModel(String id, String name) {
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

    public void setPoint(double point) {
        this.point = point;
    }

    public List<MemberModel> getMemberModels() {
        return memberModels;
    }

    public void setMemberModels(List<MemberModel> memberModels) {
        this.memberModels = memberModels;
    }
}
