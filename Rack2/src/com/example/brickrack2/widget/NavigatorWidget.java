package com.example.brickrack2.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.example.brickrack2.R;
import com.example.brickrack2.Rack;

public class NavigatorWidget extends HorizontalScrollView {

    protected static final String TAG = Rack.TAG;

    private ImageView[] catImageViews;
    private Context mCtx;

    public NavigatorWidget(Context ctx) {
        this(ctx, null, Rack.STYLE_WIDGET);
    }

    public NavigatorWidget(Context ctx, AttributeSet as) {
        this(ctx, as, Rack.STYLE_WIDGET);
    }

    public NavigatorWidget(Context ctx, AttributeSet as, int styleId) {
        super(ctx, as, styleId);

        mCtx = ctx;

        initUi();
    }

    // @Override
    // public void setOnTouchListener(OnTouchListener l) {
    // for (int i = 0; i < Helper.CAT_MAX; i++) {
    // catImageViews[i].setOnTouchListener(l);
    // }
    // super.setOnTouchListener(l);
    // }

    private void initUi() {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        if (inflater != null) {
            inflater.inflate(R.layout.navigator_widget_layout, this, true);
        }

        catImageViews = new ImageView[Rack.CAT_MAX];

        for (int i = 0; i < Rack.CAT_MAX; i++) {

            catImageViews[i] = (ImageView) findViewById(Rack.CAT_IDS[i]);

            // final int catId = i;
            // catImageViews[i].setOnClickListener(new OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // Log.d(TAG, String.format("category %d is clicked", catId));
            // Holder.setCurrentCategory(catId);
            // }
            // });
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {

        for (int i = 0; i < Rack.CAT_MAX; i++) {
            catImageViews[i].setOnClickListener(l);
        }
        super.setOnClickListener(l);
    }

}
