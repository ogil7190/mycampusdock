package com.swalla.campusdock.Classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class Bulletin implements Serializable{
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "bulletinName")
    private String bulletinName;

    @ColumnInfo(name = "created_by")
    private String created_by;

    @ColumnInfo(name = "created_on")
    private String created_on;

    @ColumnInfo(name = "expires_on")
    private String expires_on;

    @ColumnInfo(name = "bulletinDescription")
    private String bulletinDescription;

    @ColumnInfo(name = "files")
    private String files;

    public Bulletin(){}
    public Bulletin(String id, String bulletinName, String bulletinDescription, String files, String created_by, String created_on, String expires_on) {
        this.id = id;
        this.bulletinName = bulletinName;
        this.bulletinDescription = bulletinDescription;
        this.files = files;
        this.created_by = created_by;
        this.created_on = created_on.substring(0,10);
        this.expires_on = expires_on.substring(0,10);
    }

    public static Bulletin parseFromJSON(JSONObject obj) throws JSONException{
        ArrayList<String> files = new ArrayList<>();
        JSONArray ar = obj.getJSONArray("url");

        for(int i=0; i<ar.length(); i++){
            files.add(ar.getString(i));
        }

        return new Bulletin(obj.getString("bulletin_id"), obj.getString("name"), obj.getString("description").replace("\r\n", "<br>"), android.text.TextUtils.join(",", files), obj.getString("created_by"), obj.getString("date"), obj.getString("expire_date"));
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getCreated_by() {

        return created_by;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public void setExpires_on(String expires_on) {
        this.expires_on = expires_on;
    }

    public String getCreated_on() {
        return created_on;
    }

    public String getExpires_on() {
        return expires_on;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setBulletinName(String bulletinName) {
        this.bulletinName = bulletinName;
    }

    public void setBulletinDescription(String bulletinDescription) {
        this.bulletinDescription = bulletinDescription;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public String getFiles() {
        return files;
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
}
