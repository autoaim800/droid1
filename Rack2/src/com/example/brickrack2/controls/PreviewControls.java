package com.example.brickrack2.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.brickrack2.R;
import com.example.brickrack2.Rack;

@SuppressLint("NewApi")
public class PreviewControls extends LinearLayout {

    private ImageView nextImageView;
    private ImageView prevImageView;
    private ImageView snagImageView;

    public PreviewControls(Context ctx) {
        this(ctx, null, Rack.STYLE_WIDGET);
    }

    public PreviewControls(Context ctx, AttributeSet attrs) {
        this(ctx, attrs, Rack.STYLE_WIDGET);
    }

    public PreviewControls(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);

        LayoutInflater inflater = LayoutInflater.from(ctx);
        if (inflater != null) {
            inflater.inflate(R.layout.preview_controls_layout, this, true);
        }
        prevImageView = (ImageView) findViewById(R.id.imageViewPrevMask);
        nextImageView = (ImageView) findViewById(R.id.imageViewNextMask);
        snagImageView = (ImageView) findViewById(R.id.imageViewSnag);
    }

    public void setOnPrevClickListener(View.OnClickListener l) {
        prevImageView.setOnClickListener(l);
    }

    public void setOnNextClickListener(View.OnClickListener l) {
        nextImageView.setOnClickListener(l);
    }

    public void setOnSnagClickListener(View.OnClickListener l) {
        snagImageView.setOnClickListener(l);
    }
}
