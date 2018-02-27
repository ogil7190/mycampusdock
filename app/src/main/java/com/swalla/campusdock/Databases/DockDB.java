package com.swalla.campusdock.Databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.swalla.campusdock.Classes.Bulletin;
import com.swalla.campusdock.Classes.Event;

/**
 * Created by minion on 26/02/2018 AD.
 */
@Database(entities = {Event.class, Bulletin.class},version = 1)
public abstract  class DockDB extends RoomDatabase{
    private static DockDB instance;

    public static DockDB getIntsance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context, DockDB.class,"Ogil")
                    .allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract EventDao getEventDao();
    public abstract BulletinDao getBulletinDao();
}
