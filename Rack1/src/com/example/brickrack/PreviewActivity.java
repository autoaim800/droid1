package com.example.brickrack;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * preview activity before saving
 * 
 */
public class PreviewActivity extends Activity {

    private final class CatButtonOnClickHandler implements View.OnClickListener {

        private int mCatId;

        public CatButtonOnClickHandler(int catId) {
            mCatId = catId;
        }

        @Override
        public void onClick(View v) {
            switchCategory(mCatId);
        }
    }

    /**
     * this picture to as raw picture, even to be deleted later
     */
    class SavePhotoTask extends AsyncTask<byte[], String, String> {

        @Override
        protected String doInBackground(byte[]... jpeg) {

            savingRawPicture(jpeg);

            return (null);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "result of picture saved received");
            invokeReview();
        }

        private void savingRawPicture(byte[]... jpeg) {
            File photo = Holder.rawPicFile;

            Log.d(TAG, String.format("saving raw picture to %s", photo.getAbsoluteFile()));

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo);

                fos.write(jpeg[0]);
                fos.close();

            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }
        }

    }

    private static final String TAG = Const.TAG_LOGGER;

    private Camera camera = null;

    private boolean cameraConfigured = false;

    private View cancelButton;

    private boolean inPreview = false;

    private ImageView mask;

    private int mCat1Id = 0;
    private int mCat2Count = 0;

    private int mCat2Id = 0;

    private ImageView nextButton;

    Camera.PictureCallback photoCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePhotoTask().execute(data);
            camera.startPreview();
            inPreview = true;
        }
    };

    private ImageView prevButton;

    private SurfaceView preview = null;

    private SurfaceHolder previewHolder = null;

    private ImageView snagItButton;

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // initPreview(width, height);
            initPreview(wantedWidth, wantedHeight);
            startPreview();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };

    private int wantedHeight = Const.PIC_HEIGHT;

    private int wantedWidth = Const.PIC_WIDTH;

    /**
     * initiate mask by mCatOneId and mCatTwoId;
     */
    private void applyMask() {
        Log.d(TAG, String.format("applying mask %s %s to preview", mCat1Id, mCat2Id));
        mask = (ImageView) findViewById(R.id.preview_mask);
        mask.setImageURI(buildMaskFile());
    }

    private Uri buildMaskFile() {
        File dp = new File(Holder.maskDir, String.valueOf(mCat1Id));
        File fp = new File(dp, Holder.getMaskNames()[mCat2Id]);
        return Uri.fromFile(fp);
    }

    protected void cancelPreview() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            Log.d(TAG, String.format("supprt width=%s height=%s", size.width, size.height));

            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea < resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }

    private void initControls() {
        cancelButton = (ImageView) findViewById(R.id.preview_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelPreview();
            }
        });

        snagItButton = (ImageView) findViewById(R.id.preview_snagit);
        snagItButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                takePicture();
            }
        });

        prevButton = (ImageView) findViewById(R.id.preview_mask_previous);
        nextButton = (ImageView) findViewById(R.id.preview_mask_next);

        prevButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                prevMask();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nextMask();
            }
        });

    }

    private void initNavigator() {
        for (int i = 0; i < Const.CAT_MAX; i++) {
            ImageView btn = (ImageView) findViewById(Const.CAT_PV_IDS[i]);
            btn.setOnClickListener(new CatButtonOnClickHandler(i));
        }
    }

    private void initPreview(int width, int height) {
        Log.d(TAG, String.format("initiating preview width=%s height=%s cat1=%s cat2=%s\n", width,
                height, mCat1Id, mCat2Id));

        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
                Toast.makeText(PreviewActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size vsize = getBestPreviewSize(width, height, parameters);
                Camera.Size psize = getSmallestPictureSize(parameters);

                if (vsize != null && psize != null) {

                    // parameters.setPreviewSize(vsize.width, vsize.height);
                    Log.d(TAG, String.format("setting preview size width=%s height=%s",
                            wantedWidth, wantedHeight));
                    parameters.setPreviewSize(wantedWidth, wantedHeight);

                    // parameters.setPictureSize(psize.width, psize.height);
                    Log.d(TAG, String.format("setting capture size width=%s height=%s",
                            wantedWidth, wantedHeight));
                    parameters.setPictureSize(wantedWidth, wantedHeight);

                    parameters.setPictureFormat(ImageFormat.JPEG);
                    camera.setParameters(parameters);

                    cameraConfigured = true;
                }
            }
        }
    }

    private void initViewFinder() {
        preview = (SurfaceView) findViewById(R.id.cam_preview);

        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    protected void nextMask() {
        Log.d(TAG, "switching to next mask");
        mCat2Id++;
        if (mCat2Id >= mCat2Count) {
            mCat2Id = 0;
        }
        applyMask();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                cancelPreview();
                return;

            case Activity.RESULT_OK:
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();

            default:
                return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cam_preview);

        Intent it = this.getIntent();
        Bundle bundle = it.getExtras();

        mCat1Id = bundle.getInt(Const.KEY_CAT_1_ID);

        switchCategory(mCat1Id);

        initViewFinder();

        initNavigator();

        initControls();

        applyMask();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.activity_menu_cam_preview, menu);
        return (super.onCreateOptionsMenu(menu));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.camera) {
            takePicture();
        }

        return (super.onOptionsItemSelected(item));
    }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera = null;
        inPreview = false;

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            Camera.CameraInfo info = new Camera.CameraInfo();

            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, info);

                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    camera = Camera.open(i);
                }
            }
        }

        if (camera == null) {
            camera = Camera.open();
        }

        startPreview();
    }

    protected void prevMask() {
        // TODO Auto-generated method stub
        Log.d(TAG, "switch to previous mask");
        mCat2Id--;
        if (mCat2Id < 0) {
            mCat2Id = mCat2Count - 1;
        }
        applyMask();
    }

    private void startPreview() {
        Log.d(TAG, "starting preview ...");

        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    /**
     * collect camera image send to next activity for review
     */
    protected void invokeReview() {
        Log.d(TAG, "intenting review ...");
        Intent it = new Intent();
        it.setClass(getApplicationContext(), ReviewActivity.class);

        it.putExtra(Const.KEY_WANTED_WIDTH, wantedWidth);
        it.putExtra(Const.KEY_WANTED_HEIGHT, wantedHeight);

        it.putExtra(Const.KEY_CAT_1_ID, mCat1Id);
        it.putExtra(Const.KEY_CAT_2_ID, mCat2Id);

        startActivityForResult(it, Const.CODE_4_RE_VIEW);
        // startActivity(it);
    }

    protected void switchCategory(int newCatId) {

        Log.d(TAG, String.format("switch mask category to %s", newCatId));

        if (mCat2Count > 0 && newCatId == mCat1Id) {
            // change to the same category?
            return;
        }

        mCat1Id = newCatId;

        // load masks
        File dp = new File(Holder.maskDir, String.valueOf(mCat1Id));
        Log.d(TAG, "listing mask dir:" + dp);
        String[] names = dp.list();

        mCat2Id = 0;
        if (names != null) {
            mCat2Count = names.length;
            Log.d(TAG, String.format("found %d mask/s", names.length));
            Holder.setMaskNames(names);
            applyMask();
        }

    }

    /**
     * this method only takes picture, which is done in its AsyncTask
     */
    private void takePicture() {
        if (inPreview) {
            Log.d(TAG, "taking picture");
            camera.takePicture(null, null, photoCallback);
            inPreview = false;
        }
    }
}
