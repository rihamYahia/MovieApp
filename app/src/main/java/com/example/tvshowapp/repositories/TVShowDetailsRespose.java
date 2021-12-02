package com.example.tvshowapp.repositories;

import com.example.tvshowapp.models.TVShowDetails;
import com.google.gson.annotations.SerializedName;

public class TVShowDetailsRespose {

    @SerializedName("tvShow")
    private TVShowDetails tvShowDetails;

    public TVShowDetails getTvShowDetails() {
        return tvShowDetails;
    }
}
