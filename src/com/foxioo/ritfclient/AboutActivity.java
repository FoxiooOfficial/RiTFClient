package com.foxioo.ritfclient;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class AboutActivity extends Activity
{
    /** Objects */
    private Button button_back; // exit settings
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        button_back = (Button)findViewById(R.id.button_back);

        // back
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
