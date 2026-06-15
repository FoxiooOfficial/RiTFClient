package com.foxioo.ritfclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;

public class SettingsActivity extends Activity
{
    /** Objects */
    private EditText edit_url;
    private EditText edit_timeout;
    private Button button_save;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        edit_url = (EditText) findViewById(R.id.edittext_url);
        edit_timeout = (EditText) findViewById(R.id.edittext_timeout);
        button_save = (Button) findViewById(R.id.button_save);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        edit_url.setText(prefs.getString("settings_url_host", "http://192.168.33.20:5000/"));
        edit_timeout.setText(String.valueOf(prefs.getInt("settings_timeout", 3000)));

        // saving variables
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("settings_url_host", edit_url.getText().toString());
                editor.putInt("settings_timeout", Integer.parseInt(edit_timeout.getText().toString()));

                editor.commit();

                finish();
            }
        });
    }
}
