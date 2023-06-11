package com.example.h071211028_finallmobile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.h071211028_finallmobile.network.Const;
import com.example.h071211028_finallmobile.utils.ActionBarTitle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ActionBarTitle {

    private String layoutName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bnv_main);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.menu_item_tv_now_playing);
    }

    @Override
    public void setActionBarTitle(String title) {
        View view = getLayoutInflater().inflate(R.layout.action_bar,null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
        );

        TextView titleBar = view.findViewById(R.id.tv_ab_title);
        titleBar.setText(title);

        getSupportActionBar().setCustomView(view, params);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.menu_item_tv_now_playing:
                layoutName = Const.NOW_PLAYING;
                setActionBarTitle(layoutName);
                fragment = new MovieFragment(layoutName);
                break;
            case R.id.menu_item_tv_upcoming:
                layoutName = Const.UPCOMING;
                setActionBarTitle(layoutName);
                fragment = new MovieFragment(layoutName);
                break;
            case R.id.menu_item_tv_popular:
                layoutName = Const.POPULAR;
                setActionBarTitle(layoutName);
                fragment = new MovieFragment(layoutName);
                break;
            case R.id.menu_item_tv_favorites:
                layoutName = Const.FAVORITES;
                setActionBarTitle(layoutName);
                fragment = new FavoriteFragment();
                break;
        }

        if(fragment!=null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_main,fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}