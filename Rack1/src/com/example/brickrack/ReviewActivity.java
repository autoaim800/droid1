package com.example.brickrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * review activity after saved
 **/
public class ReviewActivity extends Activity {

    public class AcceptPhotoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            // mv picture from tmp -> wardrobe/cat1/cat2/timestamp.png
            File tmpFile = Holder.tmpPicFile;
            File destFile = buildDestFile();

            Log.d(TAG, "moving to wardrobe...");
            if (tmpFile.renameTo(destFile)) {
                Log.d(TAG, "moved ...");
            } else {
                Log.d(TAG, "failed to move ...");
            }

            Log.d(TAG, "generating thumbnail...");
            // create thumb nail
            Bitmap thumbBmp = ThumbnailUtils.extractThumbnail(outBmp, Const.THUMB_WIDTH,
                    Const.THUMB_HEIGHT);

            saveBmpAsJpg(thumbBmp, buildThumbFile());

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // generate thumbnail
            Log.d(TAG, "post accept-task");

            Log.d(TAG, "intenting to parent...");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

    }

    class ProcessPhotoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            savePic2Tmp();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            showReview();

        }
    }

    private static final String TAG = Const.TAG_LOGGER;
    private ImageView acceptButton;
    private ImageView cancelButton;
    private int mCat1Id;
    private int mCat2Id;
    private Bitmap outBmp;

    private String rawTimestamp;
    private ImageView retakeButton;
    private ImageView review;

    private int wantedHeight;
    private int wantedWidth;

    /**
     * move cache photo to real location, back to wardrobe
     */
    protected void acceptPicture() {

        Toast.makeText(getApplicationContext(), "saving ...", Toast.LENGTH_LONG).show();

        Log.d(TAG, "creating aceept-picture task...");
        AcceptPhotoTask apt = new AcceptPhotoTask();
        apt.execute();

    }

    /**
     * build name for cloth picture file
     * 
     * @return an object of File
     */
    private File buildDestFile() {

        File cat1Dir = new File(Holder.wardrobeDir, String.valueOf(mCat1Id));
        // File cat2Dir = new File(cat1Dir, String.valueOf(mCatTwoId));

        if (!cat1Dir.exists()) {
            cat1Dir.mkdirs();
        }

        return new File(cat1Dir, String.format("%s.png", rawTimestamp));
    }

    /**
     * build a File object using category1-id, category2-id
     * 
     * @return
     */
    private File buildMaskFile() {
        // TODO hard code mask to pants
        File dp = new File(Holder.maskDir, String.valueOf(mCat1Id));
        return new File(dp, Holder.getMaskNames()[mCat2Id]);
    }

    /**
     * build name for thumb nail picture file
     * 
     * @return an object of File
     */
    private File buildThumbFile() {
        File cat1Dir = new File(Holder.thumbDir, String.valueOf(mCat1Id));

        if (!cat1Dir.exists()) {
            cat1Dir.mkdirs();
        }

        return new File(cat1Dir, String.format("%s.jpg", rawTimestamp));
    }

    /**
     * delete cache photo, back to wardrobe
     */
    protected void cancelPicture() {
        deleteCachePhoto();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);

        finish();
    }

    private void deleteCachePhoto() {
        // TODO Auto-generated method stub
    }

    private void initControls() {
        retakeButton = (ImageView) findViewById(R.id.review_retake);
        retakeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                retakePicture();
            }
        });
        cancelButton = (ImageView) findViewById(R.id.review_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelPicture();
            }
        });

        acceptButton = (ImageView) findViewById(R.id.review_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                acceptPicture();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_review);

        Intent it = this.getIntent();
        Bundle extra = it.getExtras();

        wantedWidth = extra.getInt(Const.KEY_WANTED_WIDTH);
        wantedHeight = extra.getInt(Const.KEY_WANTED_HEIGHT);

        mCat1Id = extra.getInt(Const.KEY_CAT_1_ID);
        mCat2Id = extra.getInt(Const.KEY_CAT_2_ID);

        review = (ImageView) findViewById(R.id.review);

        // File photo = new File(Environment.getExternalStorageDirectory(),
        // RackConst.FP_CAV);
        //
        // if (photo.exists()) {
        // Uri uri = Uri.fromFile(photo);
        // review.setImageURI(uri);
        // } else {
        // Log.d(TAG, "no found picture: " + photo.getAbsolutePath());
        // }

        initControls();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.cam_review, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ProcessPhotoTask pt = new ProcessPhotoTask();
        pt.execute();
    }

    private void resizeCopy() {

        File rawFile = new File(Environment.getExternalStorageDirectory(), Const.FILE_NAME_RAW);
        File cavFile = new File(Environment.getExternalStorageDirectory(), Const.FILE_NAME_CAV);
        File maskFile = new File(Environment.getExternalStorageDirectory(), Const.FP_MASK_PANTS);

        Bitmap bmp = BitmapFactory.decodeFile(rawFile.getAbsolutePath());

        int origWidth = bmp.getWidth();
        int origHeight = bmp.getHeight();

        if (origWidth > wantedHeight) {
            Log.d(TAG, "crop raw to cav-file");

            int offsetWidth = (origWidth - wantedHeight) / 2;
            int offsetHeight = (origHeight - wantedHeight) / 2;

            Bitmap bmp2 = Bitmap.createBitmap(bmp, offsetWidth, offsetHeight, wantedHeight,
                    wantedHeight);
            OutputStream fos = null;
            try {
                fos = new FileOutputStream(cavFile);
                bmp2.compress(CompressFormat.PNG, 80, fos);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    // ignored
                }
            }
        } else {
            Log.d(TAG, "copy raw to cav-file");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(cavFile);
                bmp.compress(CompressFormat.PNG, 80, fos);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    // ignored
                }
            }
        }
    }

    /**
     * delete cache photo, back to preview
     */
    protected void retakePicture() {
        deleteCachePhoto();
        // Intent it = new Intent();
        // it.setClass(getBaseContext(), CamPreviewActivity.class);
        // startActivity(it);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_FIRST_USER + 1, returnIntent);

        finish();
    }

    private void saveBmpAsJpg(Bitmap thumbBmp, File tmpThumbPicFile) {
        saveBmpToFile(thumbBmp, tmpThumbPicFile, CompressFormat.JPEG, 100);
    }

    private void saveBmpAsPng(Bitmap outBmp, File tmpFile) {

        int ratio = 80;
        CompressFormat method = CompressFormat.PNG;

        saveBmpToFile(outBmp, tmpFile, method, ratio);
    }

    private void saveBmpToFile(Bitmap inBmp, File outFile, CompressFormat compressMethod,
            int compressRatio) {
        FileOutputStream fos = null;
        try {
            Log.d(TAG, String.format("saving to %s ...", outFile.getAbsolutePath()));

            fos = new FileOutputStream(outFile);

            inBmp.compress(compressMethod, compressRatio, fos);

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                // ignored
            }
        }
    }

    /**
     * save raw .jpg to cropped transparent .png file
     */
    private void savePic2Tmp() {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_hhmmss");
        rawTimestamp = df.format(new Date());

        // load raw
        File rawFile = Holder.rawPicFile;
        File tmpFile = Holder.tmpPicFile;
        // buildOutputFile();
        File maskFile = buildMaskFile();

        // resizeCopy();

        Log.d(TAG, "decoding raw picture...");
        Bitmap rawBmp = BitmapFactory.decodeFile(rawFile.getAbsolutePath());
        Log.d(TAG,
                String.format("raw picture width=%s height=%s %s", rawBmp.getWidth(),
                        rawBmp.getHeight(), rawFile.getAbsolutePath()));

        Log.d(TAG, "decoding mask picture...");
        Bitmap maskBmp = BitmapFactory.decodeFile(maskFile.getAbsolutePath());
        Log.d(TAG,
                String.format("mask picture width=%s height=%s %s", maskBmp.getWidth(),
                        maskBmp.getHeight(), maskFile.getAbsolutePath()));

        int width = rawBmp.getWidth();
        int height = rawBmp.getHeight();

        // Bitmap outBmp = Bitmap.createBitmap(width, height, conf);
        InputStream is = this.getResources().openRawResource(R.drawable.nocolor);

        // can not create new picture transparent, have to based on a
        // transparent template file
        Bitmap tplBmp = BitmapFactory.decodeStream(is);
        Log.d(TAG,
                String.format("template picture width=%s height=%s", tplBmp.getWidth(),
                        tplBmp.getHeight()));

        outBmp = tplBmp.copy(tplBmp.getConfig(), true);
        Log.d(TAG,
                String.format("output picture width=%s height=%s", outBmp.getWidth(),
                        outBmp.getHeight()));

        for (int coordX = 0; coordX < width; coordX++) {
            for (int coordY = 0; coordY < height; coordY++) {
                int mp = maskBmp.getPixel(coordX, coordY);
                if (mp == Color.TRANSPARENT) {
                    outBmp.setPixel(coordX, coordY, rawBmp.getPixel(coordX, coordY));
                }
            }
        }

        saveBmpAsPng(outBmp, tmpFile);
    }

    protected void showReview() {

        Log.d(TAG, "showing review ...");

        // reload picture
        Log.d(TAG, "overlaying temp file...");
        File targ = Holder.tmpPicFile;
        Log.d(TAG, "overlaying mask file...");
        File mask = buildMaskFile();

        Drawable[] layers = new Drawable[2];
        // load mannequin
        // layers[0] = Drawable.createFromPath(targ.getAbsolutePath());
        layers[0] = getResources().getDrawable(R.drawable.male);
        layers[1] = Drawable.createFromPath(targ.getAbsolutePath());
        layers[1].setAlpha(200);
        LayerDrawable layerDrawable = new LayerDrawable(layers);
        review.setImageDrawable(layerDrawable);

        // review.setImageURI(Uri.fromFile(targ));
    }

}
