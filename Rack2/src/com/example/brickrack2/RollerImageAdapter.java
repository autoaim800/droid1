package com.example.brickrack2;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class RollerImageAdapter extends BaseAdapter {

    private WardrobeActivity activity;
    private LayoutInflater inflater;
    private List<File> thumbFileList;

    public RollerImageAdapter(WardrobeActivity parentActivity) {
        activity = parentActivity;
        thumbFileList = Holder.getThumbFileList();
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return thumbFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.roller_list_layout, null);
        }

        ImageView thumb = (ImageView) vi.findViewById(R.id.imageViewNavIcon);

        File thumbFile = this.thumbFileList.get(position);
        thumb.setImageURI(Uri.fromFile(thumbFile));
        return vi;
    }
}
