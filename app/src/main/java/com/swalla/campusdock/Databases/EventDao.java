package com.swalla.campusdock.Databases;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.Classes.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM  event")
    List<Event> getAllEvents();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Event user);

    @Update
    void update(Event user);

    @Delete
    void delete(Event user);

    @Query("SELECT * FROM event WHERE id = :id")
    Event getEvent(String id);

    @Query("DELETE FROM event")
    void nukeTable();
}
