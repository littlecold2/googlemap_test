package com.example.min.googlemaptest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by MIN on 2017-08-08.
 */

public class Urltextview extends AppCompatActivity {
    // public static Context mContext;
    TextView tv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.downloadview);
        tv = (TextView) findViewById(R.id.urltext);
        Intent intent = getIntent();
        String uText = intent.getStringExtra("url");
        tv.setText(uText);

    }
}
