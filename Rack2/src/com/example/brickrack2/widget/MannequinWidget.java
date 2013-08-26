package com.example.brickrack2.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.brickrack2.R;
import com.example.brickrack2.Rack;
import com.example.brickrack2.controls.MannequinControls;

public class MannequinWidget extends RelativeLayout {

    private ImageView clothImageView;
    private MannequinControls manneControls;

    public MannequinWidget(Context ctx) {
        this(ctx, null, Rack.STYLE_WIDGET);

    }

    public MannequinWidget(Context ctx, AttributeSet as) {
        this(ctx, as, Rack.STYLE_WIDGET);
    }

    public MannequinWidget(Context ctx, AttributeSet as, int it) {
        super(ctx, as, it);

        LayoutInflater inflater = LayoutInflater.from(ctx);
        if (inflater != null) {
            inflater.inflate(com.example.brickrack2.R.layout.mannequin_widget_layout, this, true);
        }

        clothImageView = (ImageView) findViewById(R.id.cloth);
        manneControls = (MannequinControls) findViewById(R.id.mannequin_controls);
    }

    public void setOnClickCameraListener(OnClickListener l) {
        manneControls.setOnClickListener(l);
    }

    public void setOnClickDeleteListener(OnClickListener l) {
        manneControls.setDeleteOnClickListener(l);
    }

}
