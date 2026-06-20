package com.foxioo.ritfclient;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.view.View;
import android.graphics.Color;

public class SettingsActivity extends Activity
{
    /** Objects */
    private EditText edit_url; // host URL;
    private EditText edit_timeout; // timeout (in milisecounds);
    private EditText edit_limit; // gallery display limit;
    private EditText edit_colorbg; // background color (in hex);
    private EditText edit_colortext; // background text (in hex);
    private EditText edit_qualitydownload; // quality scale for downloaded media

    private Button button_deafult; // reset to default
    private Button button_exit; // exit settings
    private Button button_save; // save settings
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        edit_url = (EditText)findViewById(R.id.edittext_url);
        edit_url.setText(prefs.getString("settings_url_host", GlobalVariables._URL_HOST));

        edit_timeout = (EditText)findViewById(R.id.edittext_timeout);
        edit_timeout.setText(String.valueOf(prefs.getInt("settings_timeout", GlobalVariables._TIMEOUT_VALUE)));

        edit_limit = (EditText)findViewById(R.id.edittext_limit);
        edit_limit.setText(String.valueOf(prefs.getInt("settings_limit_post", GlobalVariables._LIMIT_POST)));

        edit_colorbg = (EditText)findViewById(R.id.edittext_colorbg);
        edit_colorbg.setText(prefs.getString("settings_color_bg", GlobalVariables._COLOR_BG));

        edit_colortext = (EditText)findViewById(R.id.edittext_colortext);
        edit_colortext.setText(prefs.getString("settings_color_text", GlobalVariables._COLOR_TEXT));

        edit_qualitydownload = (EditText)findViewById(R.id.edittext_qualitydownload);
        edit_qualitydownload.setText(String.valueOf(prefs.getInt("settings_quality_download", GlobalVariables._QUALITY)));

        button_deafult = (Button)findViewById(R.id.button_deafult);
        button_exit = (Button)findViewById(R.id.button_exit);
        button_save = (Button)findViewById(R.id.button_save);


        // default variables
        button_deafult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("settings_url_host", GlobalVariables._URL_HOST);
                editor.putInt("settings_timeout", GlobalVariables._TIMEOUT_VALUE);
                editor.putInt("settings_limit_post", GlobalVariables._LIMIT_POST);
                editor.putString("settings_color_bg", GlobalVariables._COLOR_BG);
                editor.putString("settings_color_text", GlobalVariables._COLOR_TEXT);
                editor.putInt("settings_quality_download", GlobalVariables._QUALITY);

                editor.commit();

                Toast.makeText(SettingsActivity.this, "Restored to default settings", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // just exit
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // saving variables
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                    
                    // save variables
                    editor.putString("settings_url_host", edit_url.getText().toString()); // url

                    // timeout
                    int timeout_var;

                        try {
                            String timeout_str = edit_timeout.getText().toString();
                            
                            if (timeout_str.length() == 0) { 
                                edit_timeout.setError("Enter timeout value!");
                                return;
                            }
                            
                            timeout_var = Integer.parseInt(timeout_str);
                            timeout_var = Math.max(GlobalVariables._TIMEOUT_VALUE_MIN, Math.min(GlobalVariables._TIMEOUT_VALUE_MAX, timeout_var));
                        }
                        catch (NumberFormatException e) {
                            edit_timeout.setError("Invalid timeout value!");
                            return;
                        }

                    editor.putInt("settings_timeout", timeout_var); 

                    // limit posts
                    int limit_var;

                        try {
                            String limit_str = edit_limit.getText().toString();

                            if (limit_str.length() == 0) {
                                edit_limit.setError("Enter limit value!");
                                return;
                            }
                            
                            limit_var = Integer.parseInt(limit_str);
                            limit_var = Math.max(GlobalVariables._LIMIT_POST_MIN, Math.min(GlobalVariables._LIMIT_POST_MAX, limit_var));
                        }
                        catch (NumberFormatException e) {
                            edit_limit.setError("Invalid limit value!");
                            return;
                        }
                        
                    editor.putInt("settings_limit_post", limit_var);

                    // color background
                    String color_bg_var = edit_colorbg.getText().toString().trim();
                    
                        if (color_bg_var.length() == 0) {
                            edit_colorbg.setError("Enter HEX color!");
                            return;
                        }

                        if (!color_bg_var.startsWith("#")) {
                            color_bg_var = "#" + color_bg_var;
                        }

                        try {
                            Color.parseColor(color_bg_var); 
                        }
                        catch (IllegalArgumentException e) {
                            edit_colorbg.setError("Invalid HEX format for the background color!");
                            return; 
                        }

                    editor.putString("settings_color_bg", color_bg_var);

                    // color text
                    String color_text_var = edit_colortext.getText().toString().trim();
                    
                        if (color_text_var.length() == 0) {
                            edit_colortext.setError("Enter HEX color!");
                            return;
                        }

                        if (!color_text_var.startsWith("#")) {
                            color_text_var = "#" + color_text_var;
                        }

                        try {
                            Color.parseColor(color_text_var); 
                        }
                        catch (IllegalArgumentException e) {
                            edit_colortext.setError("Invalid HEX format for the text color!");
                            return; 
                        }

                    editor.putString("settings_color_text", color_text_var);
                
                    // quality
                    int quality_var;

                        try {
                            String quality_str = edit_qualitydownload.getText().toString();

                            if (quality_str.length() == 0) {
                                edit_qualitydownload.setError("Enter limit value!");
                                return;
                            }
                            
                            quality_var = Integer.parseInt(quality_str);
                            quality_var = Math.max(5, Math.min(100, quality_var));
                        }
                        catch (NumberFormatException e) {
                            edit_limit.setError("Invalid limit value!");
                            return;
                        }
                        
                    editor.putInt("settings_quality_download", quality_var);

                editor.commit();

                Toast.makeText(SettingsActivity.this, "Settings have been saved", Toast.LENGTH_SHORT).show();
                finish();
            }
            
        });
        
    }
}
