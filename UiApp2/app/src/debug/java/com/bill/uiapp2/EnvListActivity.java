package com.bill.uiapp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bill.uiapp2.utils.StringHelper;
import com.bill.uiapp2.widgets.StringListView;

import java.util.logging.Logger;

/**
 * A full screen activity of environment list
 */
public class EnvListActivity extends AppCompatActivity {

    private StringListView envListView;
    String[] urlArray = new String[]{"url1", "url2"};
    private EditText urlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_env_list);
        
        initList();
        urlEditText = (EditText)findViewById(R.id.newUrlEditText);
//        ((Button)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                handleOnClickAddButton(view);
//            }
//        });
    }

    private void initList() {
        envListView = (StringListView)findViewById(R.id.env_list_view);
        envListView.setStringArray(urlArray);
        envListView.setOnStringItemClickListener(new StringListView.OnStringItemClickListener(){
            @Override
            public void onStringItemClick(String value) {
                handleUrlClicked(value);
            }
        });
    }

    private void handleOnClickAddButton(View view){
        urlArray = StringHelper.appendToStringArray(urlArray, urlEditText.getText().toString());
        envListView.setStringArray(urlArray);
    }

    private void handleUrlClicked(String value) {
        Intent i = new Intent(this, SplashActivity.class);
        // i.put extra url=value
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
