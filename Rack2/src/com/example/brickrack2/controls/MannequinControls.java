package com.example.brickrack2.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.brickrack2.R;
import com.example.brickrack2.Rack;

@SuppressLint("NewApi")
public class MannequinControls extends LinearLayout {

    private ImageView cameraImageView;
    private ImageView deleteImageView;

    public MannequinControls(Context context) {
        this(context, null, Rack.STYLE_WIDGET);
    }

    public MannequinControls(Context context, AttributeSet attrs) {
        this(context, attrs, Rack.STYLE_WIDGET);
    }

    public MannequinControls(Context ctx, AttributeSet attrs, int defStyle) {
        super(ctx, attrs, defStyle);

        LayoutInflater inflater = LayoutInflater.from(ctx);
        if (inflater != null) {
            inflater.inflate(R.layout.mannequin_controls_layout, this, true);
        }
        deleteImageView = (ImageView) findViewById(R.id.imageViewDelete);
        cameraImageView = (ImageView) findViewById(R.id.imageViewCamera);

    }

    public void setCameraOnClickListener(OnClickListener l) {
        cameraImageView.setOnClickListener(l);
    }

    public void setDeleteOnClickListener(OnClickListener l) {
        deleteImageView.setOnClickListener(l);
    }
}
