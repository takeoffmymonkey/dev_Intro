package com.example.intro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.intro.R;
import com.example.intro.model.DbHelper;

public class MainActivity extends AppCompatActivity {
    private String TAG = "INTROVERT:" + getClass().getSimpleName();

    public static DbHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DbHelper(this);

        // Setting Pager
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        vpPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override // SCROLL_STATE_IDLE, S_S_DRAGGING, S_S_SETTLING
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Setting Adapter for Pager
        FragmentPagerAdapter adapterViewPager =
                new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_dump_events:
                dbHelper.dumpTable(dbHelper.EVENTS_TABLE);
                return true;
            case R.id.menu_dump_tags:
                dbHelper.dumpTable(dbHelper.TAGS_TABLE);
                return true;
            case R.id.menu_add:
                Intent intent = new Intent(MainActivity.this,
                        AddEditActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}