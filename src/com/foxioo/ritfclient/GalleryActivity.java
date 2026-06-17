package com.foxioo.ritfclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.view.View;

public class GalleryActivity extends Activity
{
    /** Global */
    int _TIMEOUT_VALUE;
    String _URL_HOST;
    int _LIMIT_POST;
    String _COLOR_BG;

    /** Objects */
    private Button button_back; // exit settings
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        button_back = (Button)findViewById(R.id.button_back);

        // back
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        _URL_HOST = prefs.getString("settings_url_host", GlobalVariables._URL_HOST);
        _TIMEOUT_VALUE = prefs.getInt("settings_timeout", GlobalVariables._TIMEOUT_VALUE);
        _LIMIT_POST = prefs.getInt("settings_limit_post", GlobalVariables._LIMIT_POST);
        _COLOR_BG = prefs.getString("settings_color_bg", GlobalVariables._COLOR_BG);

        // change color bg
        try {
            getWindow().getDecorView().setBackgroundColor(Color.parseColor(_COLOR_BG));
        } 
        catch (IllegalArgumentException e) {
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }
    }
}
