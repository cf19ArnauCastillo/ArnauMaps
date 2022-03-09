package com.example.arnaumaps.Slider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.arnaumaps.R;

import java.util.ArrayList;

public class SliderActivity extends AppCompatActivity {
    ArrayList<String> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        images = getIntent().getExtras().getStringArrayList("images");

        ViewPager viewPager = findViewById(R.id.vpager);
        SlidingAdapter slidingAdapter = new SlidingAdapter(getApplicationContext(), images);
        viewPager.setAdapter(slidingAdapter);
    }
}