package com.foxioo.ritfclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;

import org.json.JSONObject;

public class MainActivity extends Activity
{
    /** Global */
    int _TIMEOUT_VALUE;
    String _URL_HOST;
    String _COLOR_BG;

    /** Host variables */
    long count = 0;
    int hostversion = 0;

    /** Objects */
    private ApiClient api_client;
    
    private Button button_search;

    private TextView text_status;
    private TextView text_count;

    private Button button_refresh_api;
    private Button button_settings;
    private Button button_about;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);
        
        //api_client = new ApiClient(URL_HOST, _TIMEOUT_VALUE);
        
        // go to gallery
        button_search = (Button)findViewById(R.id.button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        // status text displaying its connected or not
        text_status = (TextView)findViewById(R.id.text_status);
        text_count = (TextView)findViewById(R.id.text_count);
        
        // refreshing connection
        button_refresh_api = (Button)findViewById(R.id.button_refresh_api);
        button_refresh_api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_status.setText("Connecting...");
                new API_CheckStatus().execute();
            }
        });
        
        // just settings
        button_settings = (Button)findViewById(R.id.button_settings);
        button_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        // about project
        button_about = (Button)findViewById(R.id.button_about);
        button_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        //new API_CheckStatus().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        _URL_HOST = prefs.getString("settings_url_host", GlobalVariables._URL_HOST);
        _TIMEOUT_VALUE = prefs.getInt("settings_timeout", GlobalVariables._TIMEOUT_VALUE);
        _COLOR_BG = prefs.getString("settings_color_bg", GlobalVariables._COLOR_BG);

        // updating api class variables
        if (api_client == null) {
            api_client = new ApiClient(_URL_HOST, _TIMEOUT_VALUE);
        }
        else {
            api_client.setUrl(_URL_HOST);
            api_client.setTimeout(_TIMEOUT_VALUE);
        }

        // change color bg
        try {
            getWindow().getDecorView().setBackgroundColor(Color.parseColor(_COLOR_BG));
        } 
        catch (IllegalArgumentException e) {
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        }

        // check api status
        new API_CheckStatus().execute();
    }

    /** this functions check if there is connection to target host */
    private class API_CheckStatus extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            button_refresh_api.setEnabled(false);
            text_status.setText("Connecting...");
            text_count.setText("");
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            //button_refresh_api.setEnabled(false);
            String jsonResponse = api_client.get("api/status");

            if (jsonResponse == null) {
                return false;
            }

            try {
                JSONObject json = new JSONObject(jsonResponse);
                int status = json.getInt("status");
                count = json.getLong("count");
                hostversion = json.getInt("hostversion");

                return status == 1;
            }
            catch (Exception e)
            {
                e.printStackTrace();

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean ok)
        {
            super.onPostExecute(ok);
            if (hostversion != GlobalVariables._CLIENT_VERSION) // check if version matches; if not then uhh... show warning!
                Toast.makeText(MainActivity.this, "Version Mismatch!\nExpected " + GlobalVariables._CLIENT_VERSION + " but host sent " + hostversion, Toast.LENGTH_LONG).show();

            button_refresh_api.setEnabled(true);
            
            if (ok) { // connected to the host
                text_status.setText("Connected!\n");
                text_count.setText(Long.toString(count));
            }
            else { // connectedn't to the host
                text_status.setText("Oh uh! Host is unavailable due to a timeout.");
                text_count.setText("");
            }
        }
    }
}
