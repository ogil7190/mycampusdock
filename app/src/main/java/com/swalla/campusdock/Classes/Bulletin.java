package com.swalla.campusdock.Classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

import static com.swalla.campusdock.Utils.Config.Bulletin.STR_BULLETIN_CREATOR;
import static com.swalla.campusdock.Utils.Config.Bulletin.STR_BULLETIN_DESCRIPTION;
import static com.swalla.campusdock.Utils.Config.Bulletin.STR_BULLETIN_EXPIRY;
import static com.swalla.campusdock.Utils.Config.Bulletin.STR_BULLETIN_FILES;
import static com.swalla.campusdock.Utils.Config.Bulletin.STR_BULLETIN_ID;
import static com.swalla.campusdock.Utils.Config.Bulletin.STR_BULLETIN_TIME;
import static com.swalla.campusdock.Utils.Config.Bulletin.STR_BULLETIN_TITLE;

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
        this.created_on = created_on;
        this.expires_on = expires_on;
    }

    public static Bulletin parseFromJSON(JSONObject obj) throws JSONException{
        ArrayList<String> files = new ArrayList<>();
        try {
            JSONArray ar = new JSONArray(obj.getString(STR_BULLETIN_FILES));
            for (int i = 0; i < ar.length(); i++) {
                files.add(ar.getString(i));
            }
        }
        catch (JSONException e){

        }
        return new Bulletin(obj.getString(STR_BULLETIN_ID), obj.getString(STR_BULLETIN_TITLE), obj.getString(STR_BULLETIN_DESCRIPTION).replace("\r\n", "<br>"), android.text.TextUtils.join(",", files), obj.getString(STR_BULLETIN_CREATOR), obj.getString(STR_BULLETIN_TIME), obj.getString(STR_BULLETIN_EXPIRY));
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
