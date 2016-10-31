package com.github.hyr0318.mediaselect_library.ui;

import java.io.Serializable;

/**
 * Description:
 * 作者：hyr on 2016/10/28 16:28
 * 邮箱：2045446584@qq.com
 */
public class Photo implements Serializable {

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
}

