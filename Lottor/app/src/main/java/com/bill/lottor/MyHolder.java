package com.bill.lottor;

import com.bill.lottor.models.MyAppl;

public class MyHolder {
    private static MyAppl myAppl = new MyAppl();

    public static MyAppl getMyAppl() {
        return myAppl;
    }

    private MyHolder() {
    }
}
