package com.bill.uiapp2.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bill.uiapp2.SplashActivity;

import java.util.ArrayList;

/**
 * A list view of full row strings
 */
public class StringListView extends ListView {
    private final Context mContent;
    private ArrayList<OnStringItemClickListener> mOnClickListners;
    private String[] urlArray;

    public StringListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContent = context;

        setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                // not sure what to do with long click
                // handleUrlLongClicked(position);
                return false;
            }
        });

        setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                handleUrlSelected(position);
            }
        });

        setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                handleUrlSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });
    }

    public void setStringArray(String[] urls) {
        this.urlArray = urls;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContent, android.R.layout.simple_list_item_1,
                urlArray);
        setAdapter(adapter);
        // should set new or reuse existing one?
        // then adapter.notifyDataSetChanged();
    }

    private void handleUrlSelected(int position) {

        if (position < 0 || position > urlArray.length){
            return;
        }
        String url = urlArray[position];
        for (OnStringItemClickListener lis:mOnClickListners){
            lis.onStringItemClick(url);
        }
    }

    public void setOnStringItemClickListener(OnStringItemClickListener newOnStringItemClickListener) {
        if (null == mOnClickListners) {
            mOnClickListners = new ArrayList<OnStringItemClickListener>();
        }
        mOnClickListners.add(newOnStringItemClickListener);
    }

    public interface OnStringItemClickListener {
        void onStringItemClick(String value);
    }
}
