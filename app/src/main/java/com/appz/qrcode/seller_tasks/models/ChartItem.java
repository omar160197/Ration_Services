package com.appz.qrcode.seller_tasks.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ChartItem implements Parcelable {
    public static final Parcelable.Creator<ChartItem> CREATOR = new Parcelable.Creator<ChartItem>() {
        @Override
        public ChartItem createFromParcel(Parcel source) {
            return new ChartItem(source);
        }

        @Override
        public ChartItem[] newArray(int size) {
            return new ChartItem[size];
        }
    };
    private String img_url;
    private String id;
    private String name;
    private int num_selected;
    private double point;

    public ChartItem(String img_url, String id, String name, int num_selected, double point) {
        this.img_url = img_url;
        this.id = id;
        this.name = name;
        this.num_selected = num_selected;
        this.point = point;
    }


    protected ChartItem(Parcel in) {
        this.img_url = in.readString();
        this.id = in.readString();
        this.name = in.readString();
        this.num_selected = in.readInt();
        this.point = in.readDouble();
    }

    @Override
    public String toString() {
        return "ChartItem{" +
                "img_url='" + img_url + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", num_selected=" + num_selected +
                ", point=" + point +
                '}';
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
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

    public int getNum_selected() { return num_selected; }

    public void setNum_selected(int num_selected) {
        this.num_selected = num_selected;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.img_url);
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.num_selected);
        dest.writeDouble(this.point);
    }
}
