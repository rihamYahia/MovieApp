package com.example.tvshowapp.responses;

import com.example.tvshowapp.models.TVShow;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TVShowsResponse {
    @SerializedName("page")
    private int page;

    @SerializedName("pages")
    private int totalpages;

    @SerializedName("tv_shows")
    private List<TVShow> tvShows;

    public int getPage() {
        return page;
    }

    public int getTotalpages() {
        return totalpages;
    }

    public List<TVShow> getTvShows() {
        return tvShows;
    }
}
