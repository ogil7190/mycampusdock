package com.swalla.campusdock.Classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import android.util.Log;

import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.NotiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import static com.swalla.campusdock.Utils.Config.Event.STR_EVENT_CREATOR;
import static com.swalla.campusdock.Utils.Config.Event.STR_EVENT_DESCRIPTION;
import static com.swalla.campusdock.Utils.Config.Event.STR_EVENT_EXPIRY;
import static com.swalla.campusdock.Utils.Config.Event.STR_EVENT_ID;
import static com.swalla.campusdock.Utils.Config.Event.STR_EVENT_IMAGE;
import static com.swalla.campusdock.Utils.Config.Event.STR_EVENT_TAGS;
import static com.swalla.campusdock.Utils.Config.Event.STR_EVENT_TIME;
import static com.swalla.campusdock.Utils.Config.Event.STR_EVENT_TITLE;

@Entity
public class Event implements Serializable {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private String id;

    @ColumnInfo(name = "created_by")
    private String created_by;

    @ColumnInfo(name = "eventName")
    private String eventName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "endDate")
    private String endDate;

    @ColumnInfo(name = "url")
    private String url;

    @ColumnInfo(name = "enrolled")
    private boolean enrolled;

    @ColumnInfo(name = "isUpdated")
    private boolean isUpdated;

    @ColumnInfo(name = "tags")
    private String tags;

    public Event(){}

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Event(String id, String name, String description, String date, String endDate, String url, String tags, String created_by) {
        this.id = id;
        this.eventName = name;
        this.description = description;
        this.date = date;
        this.endDate = endDate;
        this.url = url;
        this.created_by = created_by;
        this.tags = tags;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isEnrolled() {
        return enrolled;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setUpdated(boolean updated) {
        isUpdated = updated;
    }

    public boolean isUpdated() {
        return isUpdated;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public static Event parseFromJSON(JSONObject obj) throws JSONException{
        return new Event(obj.getString(STR_EVENT_ID), obj.getString(STR_EVENT_TITLE), obj.getString(STR_EVENT_DESCRIPTION).replace("\r\n", "<br>"), obj.getString(STR_EVENT_TIME), obj.getString(STR_EVENT_EXPIRY), obj.getString(STR_EVENT_IMAGE), obj.getString(STR_EVENT_TAGS), obj.getString(STR_EVENT_CREATOR));
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getTags() {
        return tags;
    }

    public Event updateEvent(JSONObject obj) throws JSONException{
        this.isUpdated = true;
        this.description = obj.getString(STR_EVENT_DESCRIPTION).replace("\r\n", "<br>");
        this.date = obj.getString(STR_EVENT_TIME);
        this.endDate = obj.getString(STR_EVENT_EXPIRY);
        try {
            this.url = obj.getString(STR_EVENT_IMAGE);
            new NotiUtil().getBitmapFromURL(this.url);
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }
}
