package com.swalla.campusdock.Classes;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.swalla.campusdock.R;

import java.io.Serializable;

@Entity
public class Event implements Serializable {
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "created_by")
    private String created_by;

    @ColumnInfo(name = "eventName")
    private String eventName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "entryFee")
    private String entryFee;

    @ColumnInfo(name = "timeAndVenue")
    private String timeAndVenue;

    @ColumnInfo(name = "organizer")
    private String organizer;

    @ColumnInfo(name = "category")
    private String category;

    @PrimaryKey
    @ColumnInfo(name = "url")
    @NonNull
    private String url;

    @ColumnInfo(name = "banner")
    private int banner = R.mipmap.test_poster; // banner in case of no image found!

    @ColumnInfo(name = "enrolled")
    private boolean enrolled;

    public Event(){}

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public Event(String id, String name, String description, String date, String organiser, String category, String url, String created_by) {
        this.id = id;
        this.eventName = name;
        this.description = description;
        this.date = date;
        this.organizer = organiser;
        this.category = category;
        this.url = url;
        this.created_by = created_by;
    }

    public void setEnrolled(boolean enrolled) {
        this.enrolled = enrolled;
    }

    public boolean isEnrolled() {

        return enrolled;
    }

    public int getBanner() {
        return banner;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setBanner(int banner) {
        this.banner = banner;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
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

    public String getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(String entryFee) {
        this.entryFee = entryFee;
    }

    public String getTimeAndVenue() {
        return timeAndVenue;
    }

    public void setTimeAndVenue(String timeAndVenue) {
        this.timeAndVenue = timeAndVenue;
    }

}
