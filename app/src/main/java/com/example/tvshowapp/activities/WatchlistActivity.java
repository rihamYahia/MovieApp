package com.example.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.example.tvshowapp.R;
import com.example.tvshowapp.adapters.WatchlistAdapter;
import com.example.tvshowapp.databinding.ActivityWatchlistBinding;
import com.example.tvshowapp.listener.WatchlistListener;
import com.example.tvshowapp.models.TVShow;
import com.example.tvshowapp.utilities.TempDataHolder;
import com.example.tvshowapp.viewmodels.WatchlistViewModel;

import java.util.ArrayList;
import java.util.List;


import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class WatchlistActivity extends AppCompatActivity implements WatchlistListener {

    private ActivityWatchlistBinding activityWatchlistBinding;
    private WatchlistViewModel viewModel;
    private WatchlistAdapter watchlistAdapter;
    private List<TVShow> watchlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWatchlistBinding = DataBindingUtil.setContentView(this,R.layout.activity_watchlist);
        doInitialization();

    }

    private void doInitialization()
    {
        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);
        activityWatchlistBinding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        watchlist = new ArrayList<>();
        loadWatchlist();
    }

    private void loadWatchlist()
    {
        activityWatchlistBinding.setIsLoading(true);
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(viewModel.loadWatchlist().subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(tvShows -> {
            activityWatchlistBinding.setIsLoading(false);
            if(watchlist.size() > 0 )
            {
                watchlist.clear();
            }
            watchlist.addAll(tvShows);
            watchlistAdapter = new WatchlistAdapter(watchlist,this);
            activityWatchlistBinding.watchlistRecyclerView.setAdapter(watchlistAdapter);
            activityWatchlistBinding.watchlistRecyclerView.setVisibility(View.VISIBLE);
            compositeDisposable.dispose();
        }));

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(TempDataHolder.IS_WATCHLIST_UPDATED)
        {
            loadWatchlist();
            TempDataHolder.IS_WATCHLIST_UPDATED = false;
        }

    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(),TVShowDetailsActivity.class);
        intent.putExtra("tvShow" , tvShow);
        startActivity(intent);
    }

    @Override
    public void removeTVShowFromWatchlist(TVShow tvShow, int position) {
        CompositeDisposable compositeDisposableForDelete = new CompositeDisposable();
        compositeDisposableForDelete.add(viewModel.removeTVShowFromWatchlist(tvShow)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(() ->{
            watchlist.remove(position);
            watchlistAdapter.notifyItemRemoved(position);
            watchlistAdapter.notifyItemRangeChanged(position,watchlistAdapter.getItemCount());
            compositeDisposableForDelete.dispose();
        }));

    }
}