package com.bill.uiapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.logging.Logger;

/**
 * A full screen activity of environment list
 */
public class EnvListActivity extends AppCompatActivity {

    private ListView envListView;
    String[] urlArray = new String[]{"url1", "url2"};
    private EditText urlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env_list);
        
        initList();
        urlEditText = (EditText)findViewById(R.id.newUrlEditText);
    }

    private void initList() {
        envListView = (ListView)findViewById(R.id.env_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                urlArray);
        envListView.setAdapter(adapter);

        envListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                handleUrlLongClicked(position);
                return false;
            }
        });

        envListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                handleUrlSelected(position);
            }
        });

        envListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                handleUrlSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // do nothing
            }
        });
    }

    private void handleUrlLongClicked(int position) {
        initList();
    }

    private void handleOnClickLaunchButton(View view){
    }

    private void handleUrlSelected(int position) {
        info(String.format("clicked on %s", position));
        if (position < 0 || position > urlArray.length){
            return;
        }
        String url = urlArray[position];
        info("starting intent for splash-activity");
        Intent i = new Intent(this, SplashActivity.class);
        startActivity(i);
    }

    private void info(String s) {
        Logger.getLogger("Ui2").info(s);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
    }



}
