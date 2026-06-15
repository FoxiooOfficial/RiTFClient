package com.foxioo.ritfclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

import org.json.JSONObject;

public class MainActivity extends Activity
{
    /** Global */
    int _TIMEOUT_VALUE = 5000;
    String URL_HOST = "http://192.168.33.20:5000/";

    /** Objects */
    private ApiClient api_client;
    
    private TextView text_status;
    private Button button_refresh_api;
    private Button button_settings;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);
        
        //api_client = new ApiClient(URL_HOST, _TIMEOUT_VALUE);
        
        // status text displaying its connected or not
        text_status = (TextView)findViewById(R.id.text_status);
        
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

        //new API_CheckStatus().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        URL_HOST = prefs.getString("settings_url_host", "http://192.168.33.20:5000/");
        _TIMEOUT_VALUE = prefs.getInt("settings_timeout", 3000);

        // updating api class variables
        if (api_client == null)
        {
            api_client = new ApiClient(URL_HOST, _TIMEOUT_VALUE);
        }
        else
        {
            api_client.setUrl(URL_HOST);
            api_client.setTimeout(_TIMEOUT_VALUE);
        }

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
        }

        @Override
        protected Boolean doInBackground(Void... voids)
        {
            button_refresh_api.setEnabled(false);
            String jsonResponse = api_client.get("api/status");

            if (jsonResponse == null) {
                return false;
            }

            try {
                JSONObject json = new JSONObject(jsonResponse);
                int status = json.getInt("status");

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
            button_refresh_api.setEnabled(true);

            if (ok) {
                text_status.setText("Connected!");
            } else {
                text_status.setText("Oh uh! Host is unavailable due to a timeout.");
            }
        }
    }
}
