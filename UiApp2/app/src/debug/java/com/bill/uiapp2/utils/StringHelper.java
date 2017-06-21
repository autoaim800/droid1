package com.bill.uiapp2.utils;

/**
 * what's the purpose of this file
 */
public class StringHelper {
    public static String[] appendToStringArray(String[] urlArray, String s) {
        String[] pending = new String[urlArray.length + 1];
        for(int i = 0; i < urlArray.length; i ++){
            pending[i] = urlArray[i];
        }
        pending[urlArray.length] = s;
        return pending;
    }
}
