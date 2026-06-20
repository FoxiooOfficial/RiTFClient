package com.foxioo.ritfclient;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;


public class ViewerActivity extends Activity
{
    /** Global */
    int _TIMEOUT_VALUE;
    String _URL_HOST;
    //int _LIMIT_POST;
    int _QUALITY;
    String _COLOR_BG;
    String _COLOR_TEXT;

    /** Objects */
    private ApiClient apiClient;
    
    private Button button_back; // back
    private Button button_download; // download
    private Button button_copy_metadata; // copy metadata

    private ImageView image_view; // main image showing
    private Bitmap curr_image; // main image data

    private TextView text_info; // post details

    private String curr_type = "";
    private int curr_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer);
        
        image_view = (ImageView)findViewById(R.id.image_view);
        text_info = (TextView)findViewById(R.id.text_info);

        button_back = (Button)findViewById(R.id.button_back);
        button_download = (Button)findViewById(R.id.button_download);
        button_copy_metadata = (Button)findViewById(R.id.button_copy_metadata);

        // exit
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        _URL_HOST = prefs.getString("settings_url_host", GlobalVariables._URL_HOST);
        _TIMEOUT_VALUE = prefs.getInt("settings_timeout", GlobalVariables._TIMEOUT_VALUE);
        _COLOR_BG = prefs.getString("settings_color_bg", GlobalVariables._COLOR_BG);
        _QUALITY = prefs.getInt("settings_quality_download", GlobalVariables._QUALITY);
        _COLOR_TEXT = prefs.getString("settings_color_text", GlobalVariables._COLOR_TEXT);

            // change color bg
            try {
                getWindow().getDecorView().setBackgroundColor(Color.parseColor(_COLOR_BG));
                text_info.setTextColor(Color.parseColor(_COLOR_TEXT));
            } 
            catch (IllegalArgumentException e) {
                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            }

        PostItem item = (PostItem) getIntent().getSerializableExtra("postItem");
        if (item == null) {
            Toast.makeText(this, "No image data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // post data
        String info =   "Author(s): " + item.author +
                        "\nDescription: " + item.description +
                        "\nID: " + item.id +
                        "\n" +
                        "\nMedia type: " + item.type +
                        "\nMedia URL: " + _URL_HOST + item.url.substring(1, item.url.length()) +
                        "\nThumbnail URL: " + _URL_HOST + item.thumb.substring(1, item.thumb.length()) +
                        "\nSize: " + item.width + " x " + item.height +
                        "\n" +
                        "\nDuration (in seconds): " + item.duration +
                        "\nHas it been deleted?: " + item.deleted +
                        "\n" +
                        "\nTags: " + item.gettags();

        text_info.setText(info);

        curr_id = item.id;
        curr_type = item.type;

        // download
        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(curr_type.equals("image"))
                {
                    if(curr_image != null)
                        new SaveImageTask().execute(curr_image);
                    else
                        Toast.makeText(ViewerActivity.this, "Image not loaded", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(ViewerActivity.this, "Failed to load media", Toast.LENGTH_SHORT).show();
                //else if(curr_type.equals("video")) {
                    // TODO
                //}
            }
        });

        // copy metadata
        button_copy_metadata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text_info.getText());
                Toast.makeText(ViewerActivity.this, "Metadata has been copied to the clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        apiClient = new ApiClient(_URL_HOST, _TIMEOUT_VALUE); // download image
        new LoadFullImageTask().execute(item.url);
    }

    private class LoadFullImageTask extends AsyncTask<String, Void, Bitmap> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(ViewerActivity.this);
            progress.setMessage("Loading image...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imgpath = params[0];
            if (imgpath.startsWith("/"))
                imgpath = imgpath.substring(1);

            byte[] data = apiClient.getbytes(imgpath);
            if (data == null) // on fail, null
                return null;

            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }

        @Override
        protected void onPostExecute(Bitmap bmp) {
            progress.dismiss();

            if (bmp != null) {
                curr_image = bmp;
                image_view.setImageBitmap(bmp);
            } 
            else {
                Toast.makeText(ViewerActivity.this, "Unable to download the image", Toast.LENGTH_LONG).show();
            }
        }
    }

   private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
        private ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(ViewerActivity.this);
            progress.setMessage("Saving image...");
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            if (bitmap == null) return null;

            File folder = new File(Environment.getExternalStorageDirectory(), "RITFClient");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filename = "ritf_" + curr_id + "_" + _QUALITY + ".jpg";
            File file = new File(folder, filename);

            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, _QUALITY, out); 
                out.flush();
                out.close();

                return file.getAbsolutePath();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String filePath) {
            progress.dismiss();

            if (filePath != null) {
                Toast.makeText(ViewerActivity.this, "Saved to: " + filePath, Toast.LENGTH_LONG).show();

                MediaScannerConnection.scanFile(ViewerActivity.this,
                        new String[]{filePath}, null, null);
            } else {
                Toast.makeText(ViewerActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
