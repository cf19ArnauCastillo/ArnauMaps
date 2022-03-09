package com.example.arnaumaps.Slider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.example.arnaumaps.Glide.GlideApp;
import com.example.arnaumaps.R;

import java.util.ArrayList;
import java.util.Objects;

public class SlidingAdapter extends PagerAdapter {
    private ArrayList<String> urls;
    private Context context;

    public SlidingAdapter(Context context, ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    public int getCount() {
        return urls.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = LayoutInflater.from(view.getContext()).inflate(R.layout.item_image, view, false);
        final ImageView imageView = imageLayout.findViewById(R.id.image);

        GlideApp.with(context)
                .load(urls.get(position))
                .into(imageView);

        Objects.requireNonNull(view).addView(imageLayout);

        return imageLayout;
    }
}