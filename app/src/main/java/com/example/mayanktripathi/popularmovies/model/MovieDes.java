package com.example.mayanktripathi.popularmovies.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mayanktripathi.popularmovies.Adapter.reviewAdapter;
import com.example.mayanktripathi.popularmovies.Activity.MainActivity;
import com.example.mayanktripathi.popularmovies.MoviedbApi.MovieSearchApi;
import com.example.mayanktripathi.popularmovies.R;
import com.example.mayanktripathi.popularmovies.MoviedbApi.TheMovieDbApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by mayanktripathi on 19/12/16.
 */

public class MovieDes extends AppCompatActivity {


    final String TAG = MainActivity.class.getSimpleName();
    String API_KEY = "2b47a29cda3b623cc10069fd23476ea9";
    final String poster_URL = "http://image.tmdb.org/t/p/w1000";
    final String URL = "http://image.tmdb.org/t/p/w300";
    Call<MovieSearch> call;
    Call<MovieSearch> callreview;
    public String id;
    public String videokey;
    public String reviewstext;
    private ShareActionProvider mShareActionProvider;
    Intent shareIntent;


    private Context context;
    private RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;

    List<String> reviewlist = new ArrayList<>();



    TextView title, description, rating, realeasedate, language , review;
    ImageView poster, headposter;
    CollapsingToolbarLayout toolbar;
    RatingBar ratingBar;


    TheMovieDbApi apiService =
            MovieSearchApi.getClient().create(TheMovieDbApi.class);

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getMenuInflater().inflate(R.menu.menu_des , menu);
        MenuItem item = menu.findItem(R.id.action_share);
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=" );
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(shareIntent);

        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_des);


        toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        title = (TextView) findViewById(R.id.movieDetailTitle);
        language = (TextView) findViewById(R.id.language);
        realeasedate = (TextView) findViewById(R.id.releaseDate);
        poster = (ImageView) findViewById(R.id.posterImageDetail);
        headposter = (ImageView) findViewById(R.id.backdrop);
        description = (TextView) findViewById(R.id.movieSummary);
        rating = (TextView) findViewById(R.id.ratingtext);
        ratingBar  = (RatingBar) findViewById(R.id.ratingBar1);


        context = getApplicationContext();


        Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tool);



        recyclerView = (RecyclerView) findViewById(R.id.reviewRv);
        recylerViewLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(recylerViewLayoutManager);
        recyclerViewAdapter = new reviewAdapter(context, reviewlist);
        recyclerView.setAdapter(recyclerViewAdapter);



        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            }
        });

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();
        final int pos = bundle.getInt("position");

        Log.v("main activity", "pos =  " + pos);


        if(MainActivity.isPopluar)
            call = apiService.getpopular(API_KEY);
        else if (MainActivity.isUpcoming)
            call = apiService.getupcoming(API_KEY);
        else if (MainActivity.isSearch)
            call = apiService.getsearch(API_KEY, MainActivity.searchquery);
        else {
            call = apiService.getresult(API_KEY);
        }


        call.enqueue(new Callback<MovieSearch>() {
                         @Override
                         public void onResponse(Call<MovieSearch> call, Response<MovieSearch> response) {

                             Log.d(TAG, "Number of movies received: " + response.body().getResults().size());

                             String title_movie = response.body().getResults().get(pos).getTitle();
                             String des = response.body().getResults().get(pos).getDescription();
                             String poster_movie = response.body().getResults().get(pos).getPosterUrl();
                             String release = response.body().getResults().get(pos).getRealasedate();
                             String img = response.body().getResults().get(pos).getImgUrl();
                             String lang = response.body().getResults().get(pos).getLanguage();
                             String rate = response.body().getResults().get(pos).getRating();
                             id = response.body().getResults().get(pos).getId();

                             String sharetitle = title_movie.replace(" ", "+");

                             release = release.replace("-", "");


                             DateFormat target = new SimpleDateFormat("MMMM yyyy");
                             DateFormat original = new SimpleDateFormat("yyyyMMdd" , Locale.ENGLISH);
                             Date date = null;
                             try {
                                 date = original.parse(release);
                             } catch (ParseException e) {
                                 e.printStackTrace();
                             }
                             String formattedDate = target.format(date);

                             // 20120821

                             Log.v(TAG , formattedDate);


                             shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/results?search_query="+sharetitle );
                             getreviews(id);

                             Log.v(TAG, id);
                             img = URL + img;
                             poster_movie = poster_URL + poster_movie;

                             toolbar.setTitle(title_movie);

                             rating.setText(rate);
                             language.setText(lang);
                             title.setText(title_movie);
                             description.setText(des);
                             realeasedate.setText(formattedDate);
                             Glide.with(getBaseContext())
                                     .load(poster_movie)
                                     .error(R.mipmap.ic_launcher)
                                     .crossFade()
                                     .into(headposter);

                             Glide.with(getBaseContext())
                                     .load(img)
                                     .error(R.mipmap.ic_launcher)
                                     .crossFade()
                                     .into(poster);


                         }

                         @Override
                         public void onFailure(Call<MovieSearch> call, Throwable t) {

                             Log.e(TAG, t.toString());


                         }

                     }

        );


        headposter.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {


                                              call = apiService.getvideo(id, API_KEY);
                                              call.enqueue(new Callback<MovieSearch>() {
                                                  @Override
                                                  public void onResponse(Call<MovieSearch> call, Response<MovieSearch> response) {

                                                      Log.v(TAG, response.body().getResults().size() + "  no");

                                                      videokey = response.body().getResults().get(0).getKey();
                                                      Log.v(TAG, videokey);

                                                      String url = "https://www.youtube.com/watch?v=" + videokey;



                                                      shareIntent.putExtra(Intent.EXTRA_TEXT, url);
                                                     // mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);



                                                      Uri uri = Uri.parse(url);

                                                      // create an intent builder
                                                      CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

                                                      // Begin customizing
                                                      // set toolbar colors
                                                      intentBuilder.setToolbarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                                                      intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

                                                      // set start and exit animations
                                                      //intentBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
                                                      intentBuilder.setExitAnimations(getApplicationContext(), android.R.anim.slide_in_left,
                                                              android.R.anim.slide_out_right);

                                                      // build custom tabs intent
                                                      CustomTabsIntent customTabsIntent = intentBuilder.build();

                                                      // launch the url
                                                      customTabsIntent.launchUrl(MovieDes.this, uri);
                                                  }

                                                  @Override
                                                  public void onFailure(Call<MovieSearch> call, Throwable t) {

                                                      Log.e(TAG, t.toString());

                                                  }
                                              });

                                          }
                                      }
        );
    }

    public void getreviews(String ids) {

        callreview = apiService.getreviews(ids, API_KEY);

        callreview.enqueue(new Callback<MovieSearch>() {
            @Override
            public void onResponse(Call<MovieSearch> call, Response<MovieSearch> response) {

                Log.v(TAG, response.toString());
                Log.v(TAG, response.body().getResults().size() + "  size");



                for(int i = 0 ; i<response.body().getResults().size() ; i++)
                {
                    reviewstext = response.body().getResults().get(i).getReviews();


                    Log.v(TAG , reviewstext);

                     reviewlist.add(reviewstext);
                     recyclerViewAdapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onFailure(Call<MovieSearch> call, Throwable t) {

                Log.e(TAG, t.toString() + " failed response");

            }
        });


    }


}
