package com.github.hyr0318.mediaselect_library.ui;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Description:
 * 作者：hyr on 2016/10/28 16:28
 * 邮箱：2045446584@qq.com
 */
public class Photo implements Parcelable {

    private int id;
    private String path;
    private String duration;
    private boolean select;

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;//类型

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Photo(int id, String path, String da) {
        this.id = id;
        this.path = path;
        this.duration = da;
    }

    public Photo() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Photo)) return false;

        Photo photo = (Photo) o;

        return id == photo.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    @Override public int describeContents() { return 0; }


    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.path);
        dest.writeString(this.duration);
        dest.writeByte(this.select ? (byte) 1 :(byte) 0);
        dest.writeInt(this.type);
    }


    protected Photo(Parcel in) {
        this.id = in.readInt();
        this.path = in.readString();
        this.duration = in.readString();
        this.select = in.readByte() != 0;
        this.type = in.readInt();
    }


    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override public Photo createFromParcel(Parcel source) {return new Photo(source);}


        @Override public Photo[] newArray(int size) {return new Photo[size];}
    };
}

