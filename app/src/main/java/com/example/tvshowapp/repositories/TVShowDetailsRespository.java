package com.example.tvshowapp.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tvshowapp.network.ApiClient;
import com.example.tvshowapp.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TVShowDetailsRespository {

    private ApiService apiService;
    public TVShowDetailsRespository()
    {
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<TVShowDetailsRespose> getTVShowDetails(String tvShowId)
    {
        MutableLiveData<TVShowDetailsRespose> data = new MutableLiveData<>();
        apiService.getTVShowDetails(tvShowId).enqueue(new Callback<TVShowDetailsRespose>() {
            @Override
            public void onResponse(@NonNull Call<TVShowDetailsRespose> call,@NonNull Response<TVShowDetailsRespose> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<TVShowDetailsRespose> call,@NonNull Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

}
