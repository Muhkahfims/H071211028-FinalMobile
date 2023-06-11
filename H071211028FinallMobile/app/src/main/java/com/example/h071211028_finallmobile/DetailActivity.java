package com.example.h071211028_finallmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.h071211028_finallmobile.adapter.CastAdapter;
import com.example.h071211028_finallmobile.adapter.GenreRecyclerAdapter;
import com.example.h071211028_finallmobile.database.AppDatabase;
import com.example.h071211028_finallmobile.database.entities.Favorite;
import com.example.h071211028_finallmobile.model.CastResponse;
import com.example.h071211028_finallmobile.model.Genre;
import com.example.h071211028_finallmobile.model.movie.Movie;
import com.example.h071211028_finallmobile.network.Const;
import com.example.h071211028_finallmobile.network.MovieApiClient;
import com.example.h071211028_finallmobile.network.MovieApiInterface;
import com.example.h071211028_finallmobile.utils.ActionBarTitle;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements ActionBarTitle {
    private TextView tvTitle, tvReleaseYear, tvDuration, tvDescription, tvRatingNumber;
    private RatingBar rating;
    private ImageView cover, posterDetail;
    private String id;
    private RecyclerView recyclerView, recyclerViewCast;
    private ArrayList<String> genres;

    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;
    private String favoriteTitle, favoriteImgUrl = "";

    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setTitle("");

        database = AppDatabase.getInstance(getApplicationContext());

        progressBar = findViewById(R.id.pb_main);
        progressBar.setVisibility(View.VISIBLE);

        constraintLayout = findViewById(R.id.constraintLayout);
        constraintLayout.setVisibility(View.GONE);

        id = getIntent().getStringExtra("ID");

        genres = new ArrayList<>();


        tvTitle = findViewById(R.id.tv_detail_title);
        tvReleaseYear = findViewById(R.id.tv_detail_release_year);
        tvDuration = findViewById(R.id.tv_detail_duration);
        rating = findViewById(R.id.rb_detail);
        cover = findViewById(R.id.iv_detail_cover);
        posterDetail = findViewById(R.id.iv_poster_detail);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvRatingNumber = findViewById(R.id.tv_detail_rating);

        loadData();
    }

    public void loadData() {
        MovieApiInterface movieApiInterface = MovieApiClient.getRetrofit().create(MovieApiInterface.class);
        Call<Movie> movieDetail = movieApiInterface.getMovie(id, Const.API_KEY);
        movieDetail.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if(response.isSuccessful() && response.body() != null){
                    Call<CastResponse> casts = movieApiInterface.getCast(id, Const.API_KEY);
                    casts.enqueue(new Callback<CastResponse>() {
                        @Override
                        public void onResponse(Call<CastResponse> call, Response<CastResponse> responseCast) {
                            if(responseCast.isSuccessful() && responseCast.body() !=null){
                                progressBar.setVisibility(View.GONE);
                                constraintLayout.setVisibility(View.VISIBLE);

                                setActivityContent(response.body(), responseCast.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<CastResponse> call, Throwable t) {

                        }
                    });
                }
                else {
                    Toast.makeText(DetailActivity.this, "Error OnResponse", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d("DetailActivity", "onFailure: " + t.getLocalizedMessage());
                Toast.makeText(DetailActivity.this, "Failed: "+ t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.menu_like:
                int movieId = Integer.parseInt(id);
                boolean exists = database.favoriteDao().isExists(movieId);

                if(exists){
                    Favorite favorite = database.favoriteDao().findById(movieId);
                    database.favoriteDao().delete(favorite).subscribe(() -> {
                        item.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_favorite_border));
                        Toast.makeText(this, "Removed From favorite", Toast.LENGTH_SHORT).show();
                    }, throwable -> {
                        Toast.makeText(this, "Operation Failed", Toast.LENGTH_SHORT).show();
                    });

                }else{
                    Favorite favorite = new Favorite(movieId,favoriteTitle, favoriteImgUrl);
                    database.favoriteDao().addFavorite(favorite).subscribe(() -> {
                        item.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_favorite));
                        item.getIcon().setColorFilter(getResources().getColor(R.color.active_tab_color), PorterDuff.Mode.SRC_ATOP);

                        Toast.makeText(this, "Added to Favorite", Toast.LENGTH_SHORT).show();
                    }, throwable -> {
                        Toast.makeText(this, "Failed To Add", Toast.LENGTH_SHORT).show();
                    });
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //
    public void setActionBarTitle(String title){
        View view = getLayoutInflater().inflate(R.layout.action_bar,null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        );

        TextView titleBar = view.findViewById(R.id.tv_ab_title);
        titleBar.setText(title);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setCustomView(view, params);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left);

    }


    private void setActivityContent(Movie movie, CastResponse castResponse){

        favoriteTitle = movie.getTitle();
        favoriteImgUrl = movie.getCover();

        Glide.with(DetailActivity.this)
                .load(Const.IMG_URL_200 + movie.getBackdrop())
                .into(cover);

        Glide.with(DetailActivity.this)
                .load(Const.IMG_URL_200 + movie.getCover())
                .apply(RequestOptions.bitmapTransform(new RoundedCorners(25)))
                .into(posterDetail);
        tvTitle.setText(movie.getTitle());
        tvReleaseYear.setText(movie.getReleaseDate().split("-")[0]);
        tvDuration.setText(movie.getDuration() + "min");
        rating.setRating((float)movie.getRating()/2);
        tvDescription.setText(movie.getDescription());
        tvRatingNumber.setText(String.valueOf(movie.getRating()));

        setGenres(movie.getGenres());

        recyclerView = findViewById(R.id.rv_genre);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(new GenreRecyclerAdapter(genres, this));
        setActionBarTitle(movie.getTitle());

        recyclerViewCast = findViewById(R.id.rv_cast);
        recyclerViewCast.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        recyclerViewCast.setAdapter(new CastAdapter(castResponse.getCasts()));

    }

    private void setGenres(List<Genre> genresList){
        for(int i = 0; i< genresList.size(); i++){
            genres.add(genresList.get(i).getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie_detail_toolbar, menu);

        int movieId = Integer.parseInt(id);
        boolean exists = database.favoriteDao().isExists(movieId);

        if(!exists){
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_favorite_border));
        }else{
            menu.getItem(0).setIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_favorite));
            menu.getItem(0).getIcon().setColorFilter(getResources().getColor(R.color.active_tab_color), PorterDuff.Mode.SRC_ATOP);
        }
        return true;
    }
}