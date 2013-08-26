package com.example.brickrack2;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.brickrack2.widget.MannequinWidget;
import com.example.brickrack2.widget.NavigatorWidget;

public class WardrobeActivity extends Activity {

    /**
     * a task to dress the mannequin
     */
    public class DressUpTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

    public class MockRollerImageAdapter extends BaseAdapter {

        private WardrobeActivity activity;
        private LayoutInflater inflater;

        public MockRollerImageAdapter(WardrobeActivity parentActivity) {
            Log.d(TAG, "initiating mock roller image adapter ...");

            activity = parentActivity;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 10;
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
            thumb.setImageResource(R.drawable.top);
            return vi;
        }
    }

    /**
     * task to read file names from sd-card, preparations, then paint roller
     * 
     */
    public class UpdateRollerTask extends AsyncTask<Integer, Void, Void> {

        private static final String TAG = Rack.TAG;

        @Override
        protected Void doInBackground(Integer... params) {

            initSdCardFolder();

            initHolder();

            int catId = params[0];

            readThumbCloth(catId);

            return null;
        }

        /**
         * read from /mnt/sdcard/brick/rack_v2
         */
        private void initHolder() {
            // TODO Auto-generated method stub
        }

        /**
         * make /mnt/sdcard/brick unzip res/folders.zip to /mnt/sdcard/brick/
         */
        private void initSdCardFolder() {
            if (Rack.dirRack.exists()) {
                Log.d(TAG, String.format("found %s", Rack.dirRack.getPath()));
                return;
            }

            if (!Rack.dirBrick.exists()) {
                Rack.dirBrick.mkdir();
            }

            unzipRackFolders(Rack.NAME_FOLDERS_ZIP, Rack.dirBrick);

        }

        private void unzipRackFolders(String zipName, File outDir) {
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
                            File dir = new File(outDir, filename);
                            // if (dir.exists()) {
                            // continue;
                            // }
                            Log.d(TAG, String.format("unzip dir: %s", filename));
                            dir.mkdir();

                        } else {
                            File file = new File(outDir, filename);

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

        @Override
        protected void onPostExecute(Void result) {
            paintRoller();
            super.onPostExecute(result);
        }

        /**
         * load thumbs and clothes of given category to holder
         * 
         * @param catId
         */
        private void readThumbCloth(int catId) {
            File thumbDir = Rack.buildThumbDir(catId);
            String[] thumbNames = thumbDir.list(new ThumbFileNameFilter());
            if (thumbNames == null) {
                Log.d(TAG, "null thumb for category: " + catId);
                return;
            }

            File clothDir = Rack.buildClothDir(catId);
            String[] clothNames = clothDir.list(new ClothFileNameFilter());
            if (clothNames == null) {
                Log.d(TAG, "null cloth for category: " + catId);
                return;
            }

            // same name of file can be found in both directories
            Set<String> clothNameSet = new HashSet<String>();
            for (String name : clothNames) {
                String firstName = name.substring(0, name.length() - 4);
                clothNameSet.add(firstName);
            }

            List<String> firstNameList = new ArrayList<String>();

            for (String name : thumbNames) {
                String firstName = name.substring(0, name.length() - 4);
                if (clothNameSet.contains(firstName)) {
                    firstNameList.add(firstName);
                } else {
                    Log.d(TAG, "no found cloth for thumb: " + firstName);
                }
            }
            File[] clothFiles = new File[firstNameList.size()];
            List<File> thumbFileList = new ArrayList<File>();

            for (int i = 0; i < firstNameList.size(); i++) {

                String firstName = firstNameList.get(i);

                Log.d(TAG, "adding thumb: " + firstName);

                String clothFileName = String.format("%s%s", firstName, Rack.EXT_CLOTH);
                String thumbFileName = String.format("%s%s", firstName, Rack.EXT_THUMB);

                clothFiles[i] = new File(clothDir, clothFileName);
                thumbFileList.add(new File(thumbDir, thumbFileName));
            }

            Holder.setClothFiles(clothFiles);
            Holder.setThumbFileList(thumbFileList);
        }

    }

    public static final String TAG = Rack.TAG;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private MannequinWidget mannequin;

    private Context mCtx;

    private int mPosition;

    private NavigatorWidget navigator;

    private ListView roller;

    /**
     * apply dress from given position of roller
     * 
     * @param position
     *            an integer of index from roller
     */
    private void applyDress(int position) {

        Log.d(TAG, "applying dress from roller: " + position);
        if (position == mPosition) {
            return;
        }

        mPosition = position;

        DressUpTask task = new DressUpTask();
        task.execute();
    }

    /**
     * delete current roller item's thumb and cloth file
     */
    private void deleteCurrentRollerItem() {
        // TODO Auto-generated method stub
        Log.d(TAG, "deleting current roller item...");
    }

    private void initUI() {

        roller = (ListView) findViewById(R.id.wardrobe_roller);
        registerForContextMenu(roller);

        roller.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adv, View v, int position, long id) {
                applyDress(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adv) {
                // TODO Auto-generated method stub
                Log.d(TAG, "nothing selected on roller");
            }

        });

        roller.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adv, View v, int position, long id) {
                applyDress(position);
            }

        });

        navigator = (NavigatorWidget) findViewById(R.id.wardrobe_navigator);
        navigator.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG,
                        String.format("click view-id: %s desc: %s", view.getId(),
                                view.getContentDescription()));

                int catId = 0;
                try {
                    catId = Integer.parseInt(view.getContentDescription().toString());
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                switchCategory(catId);
            }
        });

        mannequin = (MannequinWidget) findViewById(R.id.wardrobe_mannequin);
        mannequin.setLayoutParams(new LinearLayout.LayoutParams(Rack.WIDTH_MANNEQUIN,
                Rack.HEIGHT_MANNEQUIN));

        mannequin.setOnClickCameraListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                invokeCamera();
            }
        });

        mannequin.setOnClickDeleteListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                undressMannequin();
            }
        });

        // navigator.setOnTouchListener(new View.OnTouchListener() {
        // @Override
        // public boolean onTouch(View view, MotionEvent event) {
        // Log.d(TAG,
        // String.format("touch view-id: %s desc: %s", view.getId(),
        // view.getContentDescription()));
        // return false;
        // }
        // });
    }

    /**
     * sending intent to start preview
     */
    private void invokeCamera() {
        // TODO Auto-generated method stub
        Log.d(TAG, "invoking camera for preview...");

        Intent it = new Intent();
        it.setClass(mCtx, PreviewActivity.class);
        this.startActivityForResult(it, Rack.CODE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemDelete:
                deleteCurrentRollerItem();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);

        mCtx = getApplicationContext();

        initUI();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        getMenuInflater().inflate(R.menu.roller_long_click_menu, menu);

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wardrobe, menu);
        return true;
    }

    @Override
    protected void onResume() {
        switchCategory(0);
        super.onResume();
    }

    /**
     * this method is called after resource has been prepared to holder by
     * async-task
     */
    protected void paintRoller() {
        if (Holder.getThumbFileList() != null && Holder.getThumbFileList().size() > 0) {
            roller.setAdapter(new RollerImageAdapter(this));
        } else {
            // TODO remove the else clourse later
            roller.setAdapter(new MockRollerImageAdapter(this));
        }
    }

    /**
     * this method is called after preparation by async-task, this method
     * actually put things to wardrobe
     */
    public void paintWardrobe() {
        // TODO to be impl
        // show navigator
        // show roller
        // show mannequin
        // show controls
    }

    /**
     * this method is called by the cat-touch-icon
     */
    private void switchCategory(int catId) {
        Log.d(TAG, "switching to category: " + catId);
        Holder.setCurrentCategory(catId);

        Log.d(TAG, "updating roller for category: " + catId);
        UpdateRollerTask task = new UpdateRollerTask();
        task.execute(catId);
    }

    /**
     * dress mannequin, this method is called by image button listener
     */
    private void undressMannequin() {
        // TODO Auto-generated method stub
        Log.d(TAG, "undressing mannequin...");
    }

}
