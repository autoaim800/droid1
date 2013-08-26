package com.example.brickrack;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

    public class DressUpTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            Log.d(TAG, "merging layers...");
            Drawable[] layers = new Drawable[Const.CAT_MAX];
            for (int i = 0; i < Const.CAT_MAX; i++) {
                layers[i] = Drawable.createFromPath(clothPaths[i]);
            }
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
        }

    }

    /**
     * @author b01-3
     * 
     */
    public class ImageListAdapter extends BaseAdapter {

        private MainActivity activity;
        private LayoutInflater inflater;
        private List<String> nameList;

        public ImageListAdapter(MainActivity parentActivity, List<String> fileNameList) {
            nameList = fileNameList;
            activity = parentActivity;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        private Uri buildThumbUri(int catId, String thumbName) {
            File catDir = new File(Holder.thumbDir, String.valueOf(catId));
            File thumbFp = new File(catDir, String.format("%s%s", thumbName, Const.EXT_NAME_THUMB));
            return Uri.fromFile(thumbFp);
        }

        @Override
        public int getCount() {
            return nameList.size();
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
                vi = inflater.inflate(R.layout.list_view_image_roller, null);
            }

            // TextView title = (TextView)
            // vi.findViewById(R.id.textViewNavTitle);
            // do not need title for this case

            ImageView thumb = (ImageView) vi.findViewById(R.id.imageViewNavIcon);
            // thumb image

            thumb.setImageURI(buildThumbUri(mCatOneId, nameList.get(position)));
            // thumb.setImageResource(dataList.get(position));

            return vi;
        }
    }

    /**
     * @deprecated no reason
     * 
     */
    public class InitSdCardTask extends AsyncTask<Void, Void, Void> {

        private File brickRoot;
        private File rackRoot;
        private File sdRoot;

        @Override
        protected Void doInBackground(Void... params) {

            if (initBrickRoot()) {

                initRackFolder();

            }
            return null;
        }

        /**
         * @return false means no need to initiate the folder tree
         */
        private boolean initBrickRoot() {
            sdRoot = Environment.getExternalStorageDirectory();

            if (sdRoot.canWrite() && sdRoot.canRead()) {

                brickRoot = new File(sdRoot, Const.DIR_NAME_BRICK);

                if (!brickRoot.exists()) {
                    brickRoot.mkdir();
                }

                rackRoot = new File(brickRoot, Const.DIR_NAME_RACK);

                if (!rackRoot.exists()) {
                    rackRoot.mkdir();
                    return true;
                }

            }
            return false;
        }

        private void initRackFolder() {
            unzipRackFolders("rackfolders.zip");
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            initRoller();
        }

        private void unzipRackFolders(String zipName) {
            InputStream is = null;
            try {
                is = mCtx.getAssets().open(zipName);
                ZipInputStream zis = null;
                try {
                    ZipEntry ze;
                    zis = new ZipInputStream(new BufferedInputStream(is));

                    while ((ze = zis.getNextEntry()) != null) {

                        String filename = ze.getName();

                        if (ze.isDirectory()) {
                            Log.d(TAG, String.format("making dir: %s", filename));
                            File dir = new File(brickRoot, filename);
                            dir.mkdir();

                        } else {
                            Log.d(TAG, String.format("file: %s", filename));

                            File file = new File(brickRoot, filename);
                            FileOutputStream fout = new FileOutputStream(file);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();

                            byte[] buffer = new byte[1024];
                            int count;
                            while ((count = zis.read(buffer)) != -1) {
                                baos.write(buffer, 0, count);
                            }
                            byte[] bytes = baos.toByteArray();

                            fout.write(bytes);
                            fout.flush();
                            fout.close();

                            zis.closeEntry();
                        }
                    }
                } finally {
                    zis.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // ignored
                    }
                }
            }
        }
    }

    public class LoadRollerTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... catId) {

            // TODO initiate folder structure
            if (Holder.isNeedFolderCheck()) {
                if (!Holder.rackDir.exists()) {
                    initRackFolder();
                }
                Holder.setNeedFolderCheck(false);
            }

            if (Holder.isEmpty()) {

                // do something

                Holder.setEmpty(false);
            }

            // load picture from thumb folder
            loadThumb(catId[0]);

            return null;
        }

        private void initRackFolder() {
            unzipRackFolders("rackfolders.zip");
        }

        /**
         * real deal, load thumbs for specified category-id to Holder
         * 
         * @param oneCatId
         *            an integer of category id
         */
        private void loadThumb(int oneCatId) {

            Log.d(TAG, String.format("loading roller by cat:%s...", oneCatId));

            File dp = new File(Holder.thumbDir, String.valueOf(oneCatId));
            String[] files = dp.list();
            if (files == null) {
                Log.d(TAG, String.format("found null files for thumb %d", oneCatId));
                return;
            }
            List<String> pending = new ArrayList<String>();
            for (String name : files) {
                if (name.endsWith(Const.EXT_NAME_THUMB)) {
                    pending.add(name.substring(0, name.length() - 4));
                }
            }
            Holder.setFileNames(pending);
        }

        @Override
        protected void onPostExecute(Void result) {

            loadRoller();

            super.onPostExecute(result);
        }

        private void unzipRackFolders(String zipName) {
            Log.d(TAG, "unzipping rack folders...");

            InputStream is = null;
            try {
                is = mCtx.getAssets().open(zipName);
                ZipInputStream zis = null;
                try {
                    ZipEntry ze;
                    zis = new ZipInputStream(new BufferedInputStream(is));

                    while ((ze = zis.getNextEntry()) != null) {

                        String filename = ze.getName();

                        if (ze.isDirectory()) {
                            File dir = new File(Holder.brickDir, filename);
                            // if (dir.exists()) {
                            // continue;
                            // }
                            Log.d(TAG, String.format("unzip dir: %s", filename));
                            dir.mkdir();

                        } else {
                            File file = new File(Holder.brickDir, filename);

                            // if (file.exists()) {
                            // continue;
                            // }

                            Log.d(TAG, String.format("unzip file: %s", filename));
                            FileOutputStream fout = new FileOutputStream(file);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();

                            byte[] buffer = new byte[1024];
                            int count;
                            while ((count = zis.read(buffer)) != -1) {
                                baos.write(buffer, 0, count);
                            }
                            byte[] bytes = baos.toByteArray();

                            fout.write(bytes);
                            fout.flush();
                            fout.close();

                            zis.closeEntry();
                        }
                    }
                    // out of while

                } finally {
                    zis.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // ignored
                    }
                }
            }
        }

    }

    private static final String TAG = Const.TAG_LOGGER;

    private RelativeLayout avatar;
    private String[] clothPaths = { Holder.emptyPicPath, Holder.emptyPicPath, Holder.emptyPicPath,
            Holder.emptyPicPath, Holder.emptyPicPath };

    /**
     * current category id
     */
    private int mCatOneId = 0;

    private Drawable[] mClothLayers;

    private ImageView mClothView = null;

    private Context mCtx;
    private LayerDrawable mLayerDrawable;
    private int mRollerClickedPosition;

    private int mRollerLongClickedPosition;

    private ListView roller;

    private ListAdapter rollerAdapter;

    /**
     * build a file object for given category-id and roller-index
     * 
     * @param cat1Id
     *            an integer of category id
     * @param index
     *            an integer of roller selected index
     * @return an object of file
     */
    private File buildClothFile(int cat1Id, int index) {
        File catDir = new File(Holder.wardrobeDir, String.valueOf(cat1Id));
        List<String> fileList = Holder.getFileNames();
        if (fileList == null || fileList.size() < 1) {
            return null;
        }
        String fileName = fileList.get(index);
        File filePath = new File(catDir, String.format("%s%s", fileName, Const.EXT_NAME_CLOTH));

        return filePath;
    }

    /**
     * build a file object for given category-id and roller-index
     * 
     * @param cat1Id
     *            an integer of category id
     * @param index
     *            an integer of roller selected index
     * @return an object of file
     */
    private File buildThumbFile(int cat1Id, int index) {
        File catDir = new File(Holder.thumbDir, String.valueOf(cat1Id));
        List<String> fileList = Holder.getFileNames();
        if (fileList == null || fileList.size() < 1) {
            return null;
        }
        String fileName = fileList.get(index);
        File filePath = new File(catDir, String.format("%s%s", fileName, Const.EXT_NAME_THUMB));

        return filePath;
    }

    /**
     * item-id which is being long-clicked will be deleted
     */
    private void deleteCurrentItemFromRoller() {
        Log.d(TAG, String.format("delete cat: %d index: %d", mCatOneId, mRollerLongClickedPosition));

        if (mRollerLongClickedPosition < 0) {
            return;
        }

        File clothFile = buildClothFile(mCatOneId, mRollerLongClickedPosition);
        File thumbFile = buildThumbFile(mCatOneId, mRollerLongClickedPosition);

        if (clothFile.exists()) {
            Log.d(TAG, "deleting cloth " + clothFile.getAbsoluteFile());
            clothFile.delete();
        } else {
            Log.d(TAG, "no found cloth " + clothFile.getAbsoluteFile());
        }

        if (thumbFile.exists()) {
            Log.d(TAG, "deleting thumb " + thumbFile.getAbsolutePath());
            thumbFile.delete();
        } else {
            Log.d(TAG, "no found thumb " + thumbFile.getAbsoluteFile());
        }

        initRoller(mCatOneId);

    }

    private void initAvatar() {
        Log.d(TAG, "initiating avatar...");
        mClothLayers = new Drawable[Const.CAT_MAX];
        for (int i = 0; i < Const.CAT_MAX; i++) {
            // mClothLayers[i] = Drawable.createFromPath(clothPaths[i]);
            mClothLayers[i] = getResources().getDrawable(R.drawable.nocolor);
        }
        mLayerDrawable = new LayerDrawable(mClothLayers);
        mClothView.setImageDrawable(mLayerDrawable);
    }

    private void initControls() {

        ImageView iv = (ImageView) findViewById(R.id.wardrobe_camera);

        iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                invokePreview();
            }

        });

        ImageView delIv = (ImageView) findViewById(R.id.wardrobe_delete);
        delIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initAvatar();
            }
        });
    }

    private void initNavigator() {

        for (int i = 0; i < Const.CAT_MAX; i++) {
            final int ii = i;
            ImageView iv1 = (ImageView) findViewById(Const.CAT_WD_IDS[i]);
            iv1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initRoller(ii);
                }
            });
        }
        // out of for

    }

    private void initRoller() {
        initRoller(0);
    }

    /**
     * load the roller by category
     * 
     * @param selectedCatId
     *            a integer of category-id, starts from 0
     */
    private void initRoller(int selectedCatId) {

        mCatOneId = selectedCatId;

        Log.d(TAG, String.format("creating roller task", selectedCatId));

        LoadRollerTask rollerTask = new LoadRollerTask();
        rollerTask.execute(mCatOneId);

    }

    /**
     * load the roller with thumbs from Holder, must be called after/post
     * LoadRollerTask
     */
    protected void loadRoller() {
        List<String> fileList = Holder.getFileNames();
        if (fileList == null) {
            return;
        }
        rollerAdapter = new ImageListAdapter(this, fileList);
        roller.setAdapter(rollerAdapter);
    }

    private void loadUserConfigure() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_CANCELED:
                Log.d(TAG, "returned as Cancel");
                return;
            case Activity.RESULT_OK:
                Log.d(TAG, "returned as OK");
                initRoller(mCatOneId);
                return;
            default:
                return;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemDelete:
                deleteCurrentItemFromRoller();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mCtx = this.getApplicationContext();

        roller = (ListView) findViewById(R.id.roller);

        registerForContextMenu(roller);

        avatar = (RelativeLayout) findViewById(R.id.avatar);

        mClothView = (ImageView) findViewById(R.id.avatar_cloths);

        roller.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d(TAG, String.format("cat. %s roller %s is clicked", mCatOneId, position));

                updateClothPaths(mCatOneId, position);

                mRollerClickedPosition = position;

            }

        });

        roller.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position,
                    long id) {

                Log.d(TAG, String.format("cat. %s roller %s is long-clicked", mCatOneId, position));

                mRollerLongClickedPosition = position;

                return false;
            }

        });

        loadUserConfigure();

        initNavigator();
        initAvatar();

        initControls();

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        getMenuInflater().inflate(R.menu.roller_long_click_menu, menu);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /**
     * send intent to start preview
     */
    private void invokePreview() {
        Intent it = new Intent();
        it.setClass(mCtx, PreviewActivity.class);
        it.putExtra(Const.KEY_CAT_1_ID, mCatOneId);
        // startActivity(it);
        startActivityForResult(it, Const.CODE_4_PRE_VIEW);
    }

    /**
     * update global variable: clothUris according to giving category-id, this
     * method is called by on-item-click on roller
     * 
     */
    protected void updateClothPaths(int cat1Id, int rollerPosition) {

        Log.d(TAG, String.format("updating cloth %s %s...", cat1Id, rollerPosition));

        File dp = new File(Holder.wardrobeDir, String.valueOf(cat1Id));
        File fp = new File(dp, String.format("%s%s", Holder.getFileNames().get(rollerPosition),
                Const.EXT_NAME_CLOTH));

        // clothPaths[cat1Id] = fp.getAbsolutePath();

        Log.d(TAG, String.format("updating cloth layer %s %s", cat1Id, fp.getAbsolutePath()));
        // boolean ret = mLayerDrawable.setDrawableByLayerId(cat1Id,
        // Drawable.createFromPath(fp.getAbsolutePath()));
        // Log.d(TAG, String.format("updating result=%s", ret));

        mClothLayers[Const.Z_ORDER[cat1Id]] = Drawable.createFromPath(fp.getAbsolutePath());

        mLayerDrawable = new LayerDrawable(mClothLayers);
        mClothView.setImageDrawable(mLayerDrawable);

        Log.d(TAG, "updating cloth-view");
        mClothView.setImageDrawable(mLayerDrawable);
    }

}
