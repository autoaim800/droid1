/**
 * 
 */
package com.example.brickrack;

import java.io.File;
import java.util.List;

import android.os.Environment;

/**
 * a class holds all data for current user and folders
 * 
 * @author b01-3
 * 
 */
public class Holder {

    public static final File brickDir;
    private static int cameraHeight = 640;

    private static int cameraWidth = 480;
    private static boolean empty = true;

    public static final File emptyPicFile;

    public static final String emptyPicPath;

    /**
     * here stores thumbs for current category
     */
    private static List<String> fileNames;
    public static final File maskDir;
    private static String[] maskNames;
    private static boolean needFolderCheck = true;
    public static final File rackDir;

    public static final File rawPicFile;

    public static final File thumbDir;

    public static final File tmpPicFile;

    private static int userGender = Const.GENDER_MALE;

    private static float userSize = 10;

    public static final File wardrobeDir;

    static {
        brickDir = new File(Environment.getExternalStorageDirectory(), Const.DIR_NAME_BRICK);
        rackDir = new File(brickDir, Const.DIR_NAME_RACK);
        thumbDir = new File(rackDir, Const.DIR_NAME_THUMB);
        maskDir = new File(rackDir, Const.DIR_NAME_MASK);

        emptyPicFile = new File(rackDir, Const.FILE_NAME_NO_COLOR);
        emptyPicPath = emptyPicFile.getAbsolutePath();

        wardrobeDir = new File(rackDir, Const.DIR_NAME_WARDROBE);
        rawPicFile = new File(rackDir, Const.FILE_NAME_RAW);
        tmpPicFile = new File(rackDir, Const.FILE_NAME_CAV);

    }

    public static List<String> getFileNames() {
        return fileNames;
    }

    public static String[] getMaskNames() {
        return maskNames;
    }

    public static boolean isEmpty() {
        return empty;
    }

    public static boolean isNeedFolderCheck() {
        return needFolderCheck;
    }

    public static void setEmpty(boolean isEmpty) {
        empty = isEmpty;
    }

    public static void setFileNames(List<String> pending) {
        fileNames = pending;

    }

    public static void setMaskNames(String[] names) {
        maskNames = names;

    }

    public static void setNeedFolderCheck(boolean checked) {
        needFolderCheck = checked;
    }

}
