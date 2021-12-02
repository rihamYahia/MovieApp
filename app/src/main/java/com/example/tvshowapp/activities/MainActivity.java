package com.example.tvshowapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.tvshowapp.R;
import com.example.tvshowapp.adapters.TVShowsAdapter;
import com.example.tvshowapp.databinding.ActivityMainBinding;
import com.example.tvshowapp.listener.TVShowsListener;
import com.example.tvshowapp.models.TVShow;
import com.example.tvshowapp.models.TVShowDetails;
import com.example.tvshowapp.responses.TVShowsResponse;
import com.example.tvshowapp.viewmodels.MostPopularTVShowsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TVShowsListener {

    private ActivityMainBinding activityMainBinding;
    private MostPopularTVShowsViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;

    public TVShowsResponse mostPopularTVShowsResponce;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        doInitialization();
    }

    private void doInitialization()
    {
        activityMainBinding.tvShowsRecyclerview.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(MostPopularTVShowsViewModel.class);
        tvShowsAdapter = new TVShowsAdapter(tvShows,this);
        activityMainBinding.tvShowsRecyclerview.setAdapter(tvShowsAdapter);
        activityMainBinding.tvShowsRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(! activityMainBinding.tvShowsRecyclerview.canScrollVertically(1)){
                    if(currentPage <= totalAvailablePages){
                        currentPage +=1;
                        getMostPopularTVShows();
                    }
                }
            }
        });
        activityMainBinding.imageWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext() , WatchlistActivity.class));
            }
        });
        activityMainBinding.imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SearchActivity.class));
            }
        });
        getMostPopularTVShows();
    }

    private void getMostPopularTVShows()
    {
        toggleLoading();
        viewModel.getMostPopularTVShows(currentPage).observe(this,mostPopularTVShowsResponce  ->{
            toggleLoading();
            if(mostPopularTVShowsResponce.getTvShows() != null)
            {
                totalAvailablePages = mostPopularTVShowsResponce.getTotalpages();
                if(mostPopularTVShowsResponce.getTvShows() != null)
                {
                    int oldCount = tvShows.size();
                    tvShows.addAll(mostPopularTVShowsResponce.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount,tvShows.size());
                }
            }
                }

        );
    }

    private void toggleLoading()
    {
        if(currentPage == 1){
            if(activityMainBinding.getIsLoading() != null && activityMainBinding.getIsLoading()){
                activityMainBinding.setIsLoading(false);
            } else{
                activityMainBinding.setIsLoading(true);
            }
        }else{
            if(activityMainBinding.getIsLoadingMore() != null && activityMainBinding.getIsLoadingMore()){
                activityMainBinding.setIsLoadingMore(false);
            }
            else{
                activityMainBinding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(),TVShowDetailsActivity.class);
         intent.putExtra("tvShow" , tvShow);
        startActivity(intent);
    }
}