package com.example.brickrack2;

import java.io.File;
import java.util.List;

public class Holder {
    private static File[] clothFiles;
    private static int currentCategory;

    private static boolean needInitHolder = true;

    private static boolean needInitSdCard = true;

    private static PictureProcessor pictureProcessor;
    private static List<File> thumbFileList;
    private static int currentMaskCount;
    private static int currentMaskId;
    private static File[] currentMaskFiles;
    private static MaskConf[] currentMaskConfs;

    public static File[] getClothFiles() {
        return clothFiles;
    }

    public static int getCurrentCategoryId() {
        return currentCategory;
    }

    public static PictureProcessor getPictureProcessor() {
        return pictureProcessor;
    }

    public static List<File> getThumbFileList() {
        return thumbFileList;
    }

    public static boolean isNeedInitHolder() {
        return needInitHolder;
    }

    public static boolean isNeedInitSdCard() {
        return needInitSdCard;
    }

    public static void setClothFiles(File[] files) {
        clothFiles = files;

    }

    public static void setCurrentCategory(int catId) {
        currentCategory = catId;
    }

    public static void setThumbFileList(List<File> list) {
        thumbFileList = list;

    }

    public static int getCurrentMaskCount() {
        return currentMaskCount;
    }

    public static int getCurrentMaskId() {
        return currentMaskId;
    }

    public static void setCurrentMaskId(int maskId) {
        currentMaskId = maskId;
    }

    public static void setCurrentMaskFiles(File[] maskFiles) {
        currentMaskFiles = maskFiles;
        currentMaskCount = maskFiles.length;
    }

    public static File[] getCurrentMasks() {
        return currentMaskFiles;
    }

    public static MaskConf getCurrentMaskConf() {
        return currentMaskConfs[currentMaskId];
    }

    public static void setCurrentMaskConfs(MaskConf[] confs) {
        currentMaskConfs = confs;
    }
}
