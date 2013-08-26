package com.example.brickrack2;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.example.brickrack2.controls.PreviewControls;
import com.example.brickrack2.widget.NavigatorWidget;

public class PreviewActivity extends Activity {

    public class ApplyMaskTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... params) {
            int catId = params[0];

            int maskId = params[1];

            // need to reload masks from SD card
            readMaskFiles(catId);

            // load task from SD card folder
            // put data to Holder
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            MaskConf conf = Holder.getCurrentMaskConf();

            paintMask(Holder.getCurrentMaskId(), conf.getLeftOffset(), conf.getTopOffset());

            super.onPostExecute(result);
        }

        /**
         * read masks from SD card for given category id, read offset settings,
         * store the result to Holder
         * 
         * @param catId
         *            an integer of category id
         */
        private void readMaskFiles(int catId) {
            // read mask picture files
            File catDir = new File(Rack.dirMasks, String.valueOf(catId));
            if (catDir.exists()) {
                // new MaskFileNameFilter()
                File[] maskFiles = catDir.listFiles();
                Holder.setCurrentMaskFiles(maskFiles);
                Log.d(TAG, String.format("found %d mask/s for category:%d dir:%s",
                        maskFiles.length, catId, catDir.getPath()));

            } else {
                Log.e(TAG, "no found mask dir for category: " + catId);
            }

            // read mask configure files
            MaskConf[] confs = new MaskConf[Holder.getCurrentMaskCount()];
            for (int i = 0; i < confs.length; i++) {
                confs[i] = new MaskConf(0, 0);
            }

            // store them to Holder
            Holder.setCurrentMaskConfs(confs);
        }
    }

    private class CameraTakePictureCallback implements Camera.PictureCallback {

        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePhotoTask().execute(data);
            camera.startPreview();
            inPreview = true;
        }

    }

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpeg) {
            File photo = Rack.fileRawJpeg;

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());

                fos.write(jpeg[0]);
                fos.close();
            } catch (java.io.IOException e) {
                Log.e(TAG, "Exception in photoCallback", e);
            }

            return photo.getPath();
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            Log.d(TAG, "post save photo task, result: " + result);

            super.onPostExecute(result);
        }

    }

    private static final String TAG = Rack.TAG;

    private Camera camera = null;
    private boolean cameraConfigured = false;
    private PreviewControls controls;

    private boolean inPreview = false;

    private ImageView mask;

    private NavigatorWidget navigator;

    private Camera.PictureCallback pictureCallback = new CameraTakePictureCallback();

    private SurfaceView preview = null;

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, String.format("preview surface changed to %sx%s", width, height));
            initPreview(width, height);
            attachCameraToSurfaceView();
        }

        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
            Log.d(TAG, "surface is created");
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
            Log.d(TAG, "surface is destroyed");
        }
    };

    private SurfaceHolder surfaceHolder = null;

    private boolean maskLoaded;

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // new MenuInflater(this).inflate(R.menu.options, menu);
    //
    // return (super.onCreateOptionsMenu(menu));
    // }
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // if (item.getItemId() == R.id.camera) {
    // if (inPreview) {
    // camera.takePicture(null, null, photoCallback);
    // inPreview = false;
    // }
    // }
    //
    // return (super.onOptionsItemSelected(item));
    // }

    private void attachCameraToSurfaceView() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
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

    private void initPreview(int width, int height) {
        if (camera != null && surfaceHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size previewSize = getBestPreviewSize(width, height, parameters);
                Camera.Size captureSize = getSmallestPictureSize(parameters);

                Log.d(TAG, String.format("previewing in %sx%s capturing in %sx%s",
                        previewSize.width, previewSize.height, captureSize.width,
                        captureSize.height));

                if (previewSize != null && captureSize != null) {
                    parameters.setPreviewSize(previewSize.width, previewSize.height);
                    parameters.setPictureSize(captureSize.width, captureSize.height);
                    parameters.setPictureFormat(ImageFormat.JPEG);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    private void iniUi() {
        preview = (SurfaceView) findViewById(R.id.cam_preview);
        surfaceHolder = preview.getHolder();
        surfaceHolder.addCallback(surfaceCallback);

        // preview.setLayoutParams(new
        // RelativeLayout.LayoutParams(Rack.WIDTH_PREVIEW,
        // Rack.HEIGHT_PREVIEW));

        navigator = (NavigatorWidget) findViewById(R.id.preview_navigator);
        navigator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    int catId = Integer.parseInt(v.getContentDescription().toString());
                    switchMask(catId, 0);
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        controls = (PreviewControls) findViewById(R.id.preview_controls);
        controls.setOnPrevClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevMask();
            }
        });
        controls.setOnNextClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextMask();
            }
        });

        controls.setOnSnagClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snagIt();
            }
        });

        mask = (ImageView) findViewById(R.id.preview_mask);

        switchMask(Holder.getCurrentCategoryId(), Holder.getCurrentMaskId());

    }

    private void nextMask() {
        Log.d(TAG, "switching to next mask...");
        int maskId = Holder.getCurrentMaskId();
        maskId++;
        if (maskId >= Holder.getCurrentMaskCount()) {
            maskId = 0;
        }
        Holder.setCurrentMaskId(maskId);

        switchMask(Holder.getCurrentCategoryId(), Holder.getCurrentMaskId());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        iniUi();
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

        attachCameraToSurfaceView();
    }

    private void paintMask(int currentMaskId, int leftOffset, int topOffset) {
        File[] maskFiles = Holder.getCurrentMasks();
        mask.setImageURI(Uri.fromFile(maskFiles[currentMaskId]));
    }

    /**
     * change mask to previous one (if there has any)
     */
    private void prevMask() {
        Log.d(TAG, "switching to previous mask...");
        int maskId = Holder.getCurrentMaskId();
        maskId--;
        if (maskId < 0) {
            maskId = Holder.getCurrentMaskCount() - 1;
        }
        Holder.setCurrentMaskId(maskId);
        switchMask(Holder.getCurrentCategoryId(), Holder.getCurrentMaskId());
    }

    private void snagIt() {
        if (inPreview) {
            Log.d(TAG, "taking picture...");
            camera.takePicture(null, null, pictureCallback);
            inPreview = false;
        } else {
            Log.d(TAG, "not in preview, could NOT take any picture");
        }
    }

    /**
     * preview view-finder invokes changing category, or initiate category for
     * onCreate
     * 
     * @param catId
     *            an integer of category id
     * @param maskId
     *            an integer of 2nd-category id for the mask file
     */
    private void switchMask(int catId, int maskId) {
        Log.d(TAG, String.format("switching mask to %s/%s...", catId, maskId));

        if (maskLoaded && catId == Holder.getCurrentCategoryId()) {
            return;
        }

        Holder.setCurrentCategory(catId);

        ApplyMaskTask task = new ApplyMaskTask();
        task.execute(catId, maskId);

        maskLoaded = true;

    }
}
