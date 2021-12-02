package com.example.tvshowapp.listener;

import com.example.tvshowapp.models.TVShow;

public interface WatchlistListener {
    void onTVShowClicked (TVShow tvShow);

    void removeTVShowFromWatchlist (TVShow tvShow , int position);

}
