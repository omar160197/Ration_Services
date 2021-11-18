package com.appz.qrcode.seller_tasks.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemModel implements Parcelable {
    public static final Parcelable.Creator<ItemModel> CREATOR = new Parcelable.Creator<ItemModel>() {
        @Override
        public ItemModel createFromParcel(Parcel source) {
            return new ItemModel(source);
        }

        @Override
        public ItemModel[] newArray(int size) {
            return new ItemModel[size];
        }
    };
    private String id;
    private String name;
    private String img_url;
    private double point;
    private int number_units;

    public ItemModel(String name, String img_url, double point, int number_units) {
        this.name = name;
        this.img_url = img_url;
        this.point = point;
        this.number_units = number_units;
    }

    public ItemModel(String id, String name, String img_url, double point, int number_units) {
        this.id = id;
        this.name = name;
        this.img_url = img_url;
        this.point = point;
        this.number_units = number_units;
    }

    public ItemModel() {
    }

    protected ItemModel(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.img_url = in.readString();
        this.point = in.readDouble();
        this.number_units = in.readInt();
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

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public int getNumber_units() {
        return number_units;
    }

    public void setNumber_units(int number_units) {
        this.number_units = number_units;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.img_url);
        dest.writeDouble(this.point);
        dest.writeInt(this.number_units);
    }
}
