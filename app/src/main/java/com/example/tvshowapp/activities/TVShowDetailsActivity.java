package com.example.tvshowapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tvshowapp.R;
import com.example.tvshowapp.adapters.EpisodesAdapter;
import com.example.tvshowapp.adapters.ImageSliderAdapter;
import com.example.tvshowapp.databinding.ActivityTvshowDetailsBinding;
import com.example.tvshowapp.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.tvshowapp.models.TVShow;
import com.example.tvshowapp.utilities.TempDataHolder;
import com.example.tvshowapp.viewmodels.TVShowDetailsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Locale;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

import io.reactivex.android.schedulers.AndroidSchedulers;


public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTvshowDetailsBinding activityTvshowDetailsBinding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBottomSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;
    private Boolean isTVShowAvailableInWatchlist = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityTvshowDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_tvshow_details);
        doInitialization();
    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        activityTvshowDetailsBinding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        checkTVShowInWatchlist();
        getTVShowDetails();
    }

    private void checkTVShowInWatchlist() {
        CompositeDisposable compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(tvShowDetailsViewModel.getTVShowFromWatchlist(String.valueOf(tvShow.getId()))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tvShow1 -> {
                    isTVShowAvailableInWatchlist = true;
                    activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                    compositeDisposable.dispose();
                }));
    }


    private void loadImageSlider(String[] sliderImages) {
        activityTvshowDetailsBinding.sliderViewPager.setOffscreenPageLimit(1);
        activityTvshowDetailsBinding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        activityTvshowDetailsBinding.sliderViewPager.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.viewFadingEdge.setVisibility(View.VISIBLE);
        setupSliderIndicators(sliderImages.length);
        activityTvshowDetailsBinding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            activityTvshowDetailsBinding.layoutSliderIndicators.addView(indicators[i]);
        }
        activityTvshowDetailsBinding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);

    }

    private void setCurrentSliderIndicator(int position) {
        int childCount = activityTvshowDetailsBinding.layoutSliderIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) activityTvshowDetailsBinding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background_slider_indicator_inactive));
            }

        }
    }

    private void loadBasicTVShowDetails() {
        activityTvshowDetailsBinding.setTvShowName(tvShow.getName());
        activityTvshowDetailsBinding.setNetworkCountry(tvShow.getNetwork() + "(" + tvShow.getCountry() + ")");
        activityTvshowDetailsBinding.setStatus(tvShow.getStatus());
        activityTvshowDetailsBinding.setStartedDate(tvShow.getStartDate());
        activityTvshowDetailsBinding.textName.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textNetworkCountry.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStatus.setVisibility(View.VISIBLE);
        activityTvshowDetailsBinding.textStarted.setVisibility(View.VISIBLE);

    }

    private void getTVShowDetails() {
        activityTvshowDetailsBinding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());
        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(this,
                tvShowDetailsRespose -> {
                    activityTvshowDetailsBinding.setIsLoading(false);
                    if (tvShowDetailsRespose.getTvShowDetails() != null) {
                        if (tvShowDetailsRespose.getTvShowDetails().getPictures() != null) {
                            loadImageSlider(tvShowDetailsRespose.getTvShowDetails().getPictures());
                        }
                        activityTvshowDetailsBinding.setTvShowImageURL(tvShowDetailsRespose.getTvShowDetails().getImagePath());
                        activityTvshowDetailsBinding.imageTVShow.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.setDescription(String.valueOf(HtmlCompat.fromHtml(tvShowDetailsRespose.getTvShowDetails().getDescription(), HtmlCompat.FROM_HTML_MODE_LEGACY)));
                        activityTvshowDetailsBinding.textDescription.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.textReadMore.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.textReadMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (activityTvshowDetailsBinding.textReadMore.getText().toString().equals("Read More")) {
                                    activityTvshowDetailsBinding.textDescription.setMaxLines(Integer.MAX_VALUE);
                                    activityTvshowDetailsBinding.textDescription.setEllipsize(null);
                                    activityTvshowDetailsBinding.textReadMore.setText("Read Less");
                                } else {
                                    activityTvshowDetailsBinding.textDescription.setMaxLines(4);
                                    activityTvshowDetailsBinding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                                    activityTvshowDetailsBinding.textReadMore.setText("Read More");
                                }
                            }
                        });
                        activityTvshowDetailsBinding.setRating(String.format(Locale.getDefault(), "%.2f", Double.parseDouble(tvShowDetailsRespose.getTvShowDetails().getRating())));
                        if (tvShowDetailsRespose.getTvShowDetails().getGenres() != null) {
                            activityTvshowDetailsBinding.setGenre(tvShowDetailsRespose.getTvShowDetails().getGenres()[0]);
                        } else {
                            activityTvshowDetailsBinding.setGenre("N/A");
                        }
                        activityTvshowDetailsBinding.setRuntime(tvShowDetailsRespose.getTvShowDetails().getRuntime() + "Min");
                        activityTvshowDetailsBinding.viewDivider1.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.layoutMisc.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.buttonWebsite.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(tvShowDetailsRespose.getTvShowDetails().getUrl()));
                                startActivity(intent);
                            }
                        });

                        activityTvshowDetailsBinding.buttonWebsite.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.buttonEpisodes.setVisibility(View.VISIBLE);
                        activityTvshowDetailsBinding.buttonEpisodes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (episodesBottomSheetDialog == null) {
                                    episodesBottomSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                                    layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                            LayoutInflater.from(TVShowDetailsActivity.this),
                                            R.layout.layout_episodes_bottom_sheet,
                                            findViewById(R.id.episodesContainer),
                                            false
                                    );

                                    episodesBottomSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                                    layoutEpisodesBottomSheetBinding.episodesRecyclerView.setAdapter(
                                            new EpisodesAdapter(tvShowDetailsRespose.getTvShowDetails().getEpisodes())
                                    );
                                    layoutEpisodesBottomSheetBinding.textTitle.setText(
                                            String.format("Episodes | %s ", tvShow.getName())
                                    );

                                    layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            episodesBottomSheetDialog.dismiss();
                                        }
                                    });
                                }

                                //  ---  optional section start ----//
                                FrameLayout frameLayout = episodesBottomSheetDialog.findViewById(
                                        com.google.android.material.R.id.design_bottom_sheet
                                );

                                if (frameLayout != null) {
                                    BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                                    bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                }

                                //  ---  optional section end ----//
                                episodesBottomSheetDialog.show();
                            }
                        });

                        activityTvshowDetailsBinding.imageWatchlist.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CompositeDisposable compositeDisposable = new CompositeDisposable();
                                if(isTVShowAvailableInWatchlist)
                                {
                                    compositeDisposable.add(tvShowDetailsViewModel.removeTVShowFromWatchlist(tvShow)
                                            .subscribeOn(Schedulers.computation())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() -> {
                                                isTVShowAvailableInWatchlist = false;
                                                TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                                activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_watchlist);
                                                Toast.makeText(getApplicationContext(), "remove from watchlist", Toast.LENGTH_SHORT).show();
                                                compositeDisposable.dispose();
                                            }));
                                }
                                else{
                                    compositeDisposable.add(tvShowDetailsViewModel.addToWatchlist(tvShow)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() ->{
                                                TempDataHolder.IS_WATCHLIST_UPDATED = true;
                                                activityTvshowDetailsBinding.imageWatchlist.setImageResource(R.drawable.ic_added);
                                                Toast.makeText(getApplicationContext(),"Added to Watchlist",Toast.LENGTH_SHORT).show();
                                                compositeDisposable.dispose();
                                            })
                                    );
                                }
                            }
                        });

                        activityTvshowDetailsBinding.imageWatchlist.setVisibility(View.VISIBLE);
                        loadBasicTVShowDetails();
                    }
                });


    }
}
 
