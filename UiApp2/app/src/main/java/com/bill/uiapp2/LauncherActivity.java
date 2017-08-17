package com.bill.uiapp2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Do not extend any AppCompact or exception
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent in = new Intent(this, HomeActivity.class);
        // hide from back stack
        // cold launch to splash
        // warm launch to home
        startActivity(in);
    }

}
