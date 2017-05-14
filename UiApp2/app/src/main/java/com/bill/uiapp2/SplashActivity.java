package com.bill.uiapp2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bill.cli.MyLaunch;

/**
 * This activity prepares all needs before launching real ui; <code>MyLaunch</code> is initialized here too.
 * This activity does not get a chance to be launched for the second time during a launch session.
 */
public class SplashActivity extends AppCompatActivity {

    private MyLaunch launch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        launch = new MyLaunch();

        new PrepareTask().execute();
    }

    private class PrepareTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            launch.loadRemoteConfig();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            handleRemoteConfigSuccess();
        }
    }

    private void handleRemoteConfigSuccess() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }

}
