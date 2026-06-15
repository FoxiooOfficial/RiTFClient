package com.foxioo.ritfclient;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;

import org.json.JSONObject;

public class MainActivity extends Activity
{
    /** Global */
    final int _TIMEOUT_VALUE = 5000;
    String URL_HOST = "http://192.168.33.20:5000/";

    /** Objects */
    private ApiClient api_client;
    
    private TextView text_status;
    private Button button_refresh_api;
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_search);
        
        api_client = new ApiClient(URL_HOST, _TIMEOUT_VALUE);
        
        text_status = (TextView)findViewById(R.id.text_status);
        
        button_refresh_api = (Button)findViewById(R.id.button_refresh_api);
        button_refresh_api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_status.setText("Connecting...");
                new API_CheckStatus().execute();
            }
        });
        

        new API_CheckStatus().execute();
    }

    /** this functions check if there is connection to target host */
    private class API_CheckStatus extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            button_refresh_api.setEnabled(false);
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
