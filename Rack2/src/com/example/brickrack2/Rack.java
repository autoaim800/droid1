package com.example.brickrack2;

import java.io.File;

import android.os.Environment;
import android.util.AttributeSet;

public class Rack {

    public static final int[] CAT_DESCS = { 0, 1, 2, 3, 4 };
    public static final int[] CAT_IDS = { R.id.cat0, R.id.cat1, R.id.cat2, R.id.cat3, R.id.cat4 };
    public static final int CAT_MAX = 5;
    public static final int CODE_REQUEST = 200;

    public static final File dirBrick;
    public static final File dirClothes;
    public static final File dirMasks;
    public static final File dirRack;
    public static final File dirThumbs;

    public static final String EXT_CLOTH = ".png";
    public static final String EXT_THUMB = ".jpg";
    public static final int HEIGHT_CAMERA = 320;

    public static final int HEIGHT_MANNEQUIN = 480;
    public static final int HEIGHT_PICTURE = 320;

    public static final int HEIGHT_PREVIEW = 480;
    public static final int STYLE_WIDGET = 0;
    public static final String TAG = "Rack2";

    public static final int WIDTH_CAMERA = 240;
    public static final int WIDTH_MANNEQUIN = 360;

    public static final int WIDTH_PICTURE = 240;
    public static final int WIDTH_PREVIEW = 360;

    public static final File fileRawJpeg;

    public static final String NAME_FOLDERS_ZIP = "rackfolders.zip";
    public static final String EXT_MASK = ".png";

    static {
        dirBrick = new File(Environment.getExternalStorageDirectory(), "brick");
        dirRack = new File(dirBrick, "rack_v2");
        dirThumbs = new File(dirRack, "thumbs");
        dirMasks = new File(dirRack, "masks");
        dirClothes = new File(dirRack, "clothes");
        fileRawJpeg = new File(dirRack, "raw.jpg");
    }

    public static File buildClothDir(int catId) {
        return new File(dirThumbs, String.format("%s%s", catId, EXT_CLOTH));
    }

    public static File buildThumbDir(int catId) {
        return new File(dirThumbs, String.format("%s%s", catId, EXT_THUMB));
    }

}
