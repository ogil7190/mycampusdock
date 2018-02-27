package com.swalla.campusdock.Classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

@Entity
public class Bulletin implements Serializable{
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "bulletinName")
    private String bulletinName;

    @ColumnInfo(name = "bulletinDescription")
    private String bulletinDescription;

    public Bulletin(){}
    public Bulletin(String id, String eventName, String description, String url) {
        this.id = id;
        this.bulletinName = eventName;
        this.bulletinDescription = description;
        this.url = url;
    }

    @ColumnInfo(name = "url")
    private String url;

    public void setId(String id) {
        this.id = id;
    }

    public void setBulletinName(String bulletinName) {
        this.bulletinName = bulletinName;
    }

    public void setBulletinDescription(String bulletinDescription) {
        this.bulletinDescription = bulletinDescription;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getBulletinName() {
        return bulletinName;
    }

    public String getBulletinDescription() {
        return bulletinDescription;
    }

    public String getUrl() {
        return url;
    }
}
