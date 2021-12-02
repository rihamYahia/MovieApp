package com.example.tvshowapp.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tvshowapp.database.TVShowsDatabase;
import com.example.tvshowapp.models.TVShow;
import com.example.tvshowapp.repositories.TVShowDetailsRespose;
import com.example.tvshowapp.repositories.TVShowDetailsRespository;

import io.reactivex.Completable;
import io.reactivex.Flowable;

public class TVShowDetailsViewModel extends AndroidViewModel {

    private TVShowDetailsRespository tvShowDetailsRepository;
    private TVShowsDatabase tvShowsDatabase;
    public TVShowDetailsViewModel (@NonNull Application application)
    {
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRespository();
        tvShowsDatabase = TVShowsDatabase.getTvShowDatabase(application);
    }

    public LiveData<TVShowDetailsRespose> getTVShowDetails(String tvShowId)
    {
        return tvShowDetailsRepository.getTVShowDetails(tvShowId);
    }

    public Completable addToWatchlist(TVShow tvShow)
    {
        return tvShowsDatabase.tvShowDao().addToWatchlist(tvShow);
    }

    public Flowable<TVShow> getTVShowFromWatchlist(String tvShowId)
    {
        return tvShowsDatabase.tvShowDao().getTVShowFromWatchlist(tvShowId);
    }

    public Completable removeTVShowFromWatchlist(TVShow tvShow)
    {
        return  tvShowsDatabase.tvShowDao().removeFromWatchlist(tvShow);
    }
}
