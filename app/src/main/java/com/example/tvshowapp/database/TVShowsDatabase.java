package com.example.tvshowapp.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tvshowapp.dao.TVShowDao;
import com.example.tvshowapp.models.TVShow;

@Database(entities = TVShow.class , version = 1  , exportSchema = false)
public abstract class TVShowsDatabase extends RoomDatabase {

    private static TVShowsDatabase tvShowDatabase;

    public static synchronized TVShowsDatabase getTvShowDatabase(Context context)
    {
        if(tvShowDatabase == null)
        {
            tvShowDatabase = Room.databaseBuilder(context,TVShowsDatabase.class,"tv_shows_db").build();
        }
        return tvShowDatabase;
    }

    public abstract TVShowDao tvShowDao();

}
