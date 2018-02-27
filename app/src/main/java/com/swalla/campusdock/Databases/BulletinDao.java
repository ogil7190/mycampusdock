package com.swalla.campusdock.Databases;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.swalla.campusdock.Classes.Bulletin;

import java.util.List;

/**
 * Created by minion on 27/02/2018 AD.
 */
@Dao
public interface BulletinDao {
    @Query("SELECT * FROM  bulletin")
    List<Bulletin> getAllClassEvents();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Bulletin user);

    @Update
    void update(Bulletin user);

    @Delete
    void delete(Bulletin user);

    @Query("DELETE FROM bulletin")
    void nukeTable();
}
