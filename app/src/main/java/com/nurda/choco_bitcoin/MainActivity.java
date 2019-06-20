package com.nurda.choco_bitcoin;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.nurda.choco_bitcoin.fragments.ExchangeFragment;
import com.nurda.choco_bitcoin.fragments.HistoryFragment;
import com.nurda.choco_bitcoin.fragments.InfoFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(MainActivity.this);

        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.frameMain,new InfoFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(MainActivity.this);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        switch (menuItem.getItemId()){
            case R.id.nav_info:
                Log.d(TAG, "onNavigationItemSelected: ");
                ft.replace(R.id.frameMain, new InfoFragment());
                break;
            case R.id.nav_history:
                Log.d(TAG, "onNavigationItemSelected: ");
                ft.replace(R.id.frameMain, new HistoryFragment());
                break;
            case R.id.nav_exchange:
                Log.d(TAG, "onNavigationItemSelected: ");
                ft.replace(R.id.frameMain, new ExchangeFragment());
                break;
        }
        ft.commit();
        return true;
    }
}
