package com.bill.cli;

public class MyLaunch {
    public boolean loadRemoteConfig() {
        try {
            // pretend to read data from network
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            //ignored
        }
        return false;
    }
}
