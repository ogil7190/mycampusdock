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

    public Event(){}

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Event(String id, String name, String description, String date, String endDate, String url, String created_by) {
        this.id = id;
        this.eventName = name;
        this.description = description;
        this.date = date.substring(0, 6);
        this.endDate = endDate.substring(0, 6);
        this.url = url;
        this.created_by = created_by;
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

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public static Event parseFromJSON(JSONObject obj) throws JSONException{
        return new Event(obj.getString("event_id"), obj.getString("name"), obj.getString("description").replace("\r\n", "<br>"), obj.getString("date"), obj.getString("endDate"), obj.getString("url"), obj.getString("created_by"));
    }

    public Event updateEvent(JSONObject obj) throws JSONException{
        this.eventName = this.eventName + " ● ";
        this.description = obj.getString("event_description").replace("\r\n", "<br>");
        this.date = obj.getString("event_start_date").substring(0, 6);
        this.endDate = obj.getString("event_end_date").substring(0, 6);
        try {
            this.url = obj.getString("event_image_url");
            NotiUtil.getBitmapFromURL(this.url);
        } catch(Exception e){

        }
        return this;
    }

}
