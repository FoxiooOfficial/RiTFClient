package com.foxioo.ritfclient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GalleryActivity extends Activity
{
    /** Global */
    int _TIMEOUT_VALUE;
    String _URL_HOST;
    int _LIMIT_POST;
    String _COLOR_BG;

    /** Objects */
    private ApiClient apiClient;
    private Button button_back;
    private EditText edittext_tags;
    private Button button_search;
    private GridView grid_view;

    private ImageAdapter adapter;
    private ArrayList<Bitmap> currlist = new ArrayList<Bitmap>();
    private HashMap<String, Bitmap> thumbcache = new HashMap<String, Bitmap>();

    private ArrayList<PostItem> imageItems = new ArrayList<PostItem>();

    /** from JSON, download thumbnails and show on gallery */
    private class FetchImagesTask extends AsyncTask<String, Integer, ArrayList<Bitmap>>
    {
        private ProgressDialog proc;
        private String err;
        private ArrayList<PostItem> postitems;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            proc = new ProgressDialog(GalleryActivity.this);
            proc.setMessage("Downloading thumbnails...");
            proc.setCancelable(false);

            proc.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... params) {
            String tags = params[0];
            ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

            try
            {
                String encode = URLEncoder.encode(tags, "UTF-8");
                String endpoint = "api/get/?tags=" + encode + "&limit=" + _LIMIT_POST + "&page=" + GlobalVariables._CURRENT_PAGE;
                String jsonresp = apiClient.get(endpoint);

                if (jsonresp == null) {
                    err = "No response from the host";
                    return null;
                }

                JSONObject json = new JSONObject(jsonresp);
                JSONArray results = json.getJSONArray("results");
                final int total = results.length();

                postitems = new ArrayList<PostItem>();

                    // retrieve each thumbnail url from json to add them to the download list
                    ArrayList<String> thumburls = new ArrayList<String>();
                    for (int i = 0; i < total; i++)
                    {
                        JSONObject item = results.getJSONObject(i);

                        /** post info */
                        int id = item.optInt("id", 0);
                        String author = item.optString("author", "");
                        String description = item.optString("description", "");

                        String type = item.optString("type", "");
                        String url = item.optString("url", "");
                        String thumb = item.optString("thumb", "");

                        JSONArray posttags = item.optJSONArray("tags");

                        int width = item.optInt("width", 0);
                        int height = item.optInt("height", 0);
                        int duration = item.optInt("duration", 0);
                        Boolean deleted = item.optBoolean("deleted", false);

                            // splitting tags
                            String[] posttagssplit;
                            if (posttags != null && posttags.length() > 0)
                            {
                                posttagssplit = new String[posttags.length()];

                                for (int j = 0; j < posttags.length(); j++)
                                {
                                    posttagssplit[j] = posttags.optString(j, "");
                                }
                            }
                            else {
                                posttagssplit = new String[0];
                            }

                        postitems.add(new PostItem(id, author, description, type, url, thumb, posttagssplit, width, height, duration, deleted));

                        String thumburl = "";
                        if (thumb.length() != 0) {
                            thumburl = _URL_HOST + thumb.substring(1);
                        }
                        thumburls.add(thumburl);
                    }

                // download thumbnails from the list
                ExecutorService pool = Executors.newFixedThreadPool(GlobalVariables._CPU_THREADS);
                final ArrayList<Bitmap> thumbtemp = new ArrayList<Bitmap>();

                for (int i = 0; i < total; i++) {
                    thumbtemp.add(null);
                }

                for (int i = 0; i < total; i++)
                {
                    final int index = i;
                    final String url = thumburls.get(i);
                    pool.submit(new Runnable() {
                        @Override
                        public void run()
                        {
                            Bitmap bmp = thumbcache.get(url);
                            if (bmp == null)
                                {
                                String path = url.substring(_URL_HOST.length());
                                byte[] data = apiClient.getbytes(path);

                                if (data != null)
                                {
                                    bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if (bmp != null) {
                                        thumbcache.put(url, bmp);
                                    }
                                }
                            }

                            thumbtemp.set(index, bmp);
                            publishProgress(index + 1, total);
                        }
                    });
                }

                pool.shutdown();
                pool.awaitTermination(_TIMEOUT_VALUE, TimeUnit.SECONDS); // timeout

                for (Bitmap b : thumbtemp)
                {
                    if (b != null) {
                        bitmaps.add(b);
                    }
                }

                if (bitmaps.isEmpty()) {
                    err = "No thumbnails could be downloaded";
                    return null;
                }

                return bitmaps;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                err = "Error: " + e.getMessage();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int loaded = values[0];
            int total = values[1];
            proc.setMessage("Downloading " + loaded + "/" + total + "...");
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> result) {
            proc.dismiss();

            if (result != null)
            {
                currlist.clear();
                currlist.addAll(result);
                adapter.notifyDataSetChanged();

                imageItems.clear();
                if(postitems != null)
                    imageItems.addAll(postitems);
            } 
            else {
                Toast.makeText(GalleryActivity.this, err, Toast.LENGTH_LONG).show();
            }
        }
    }

    /** init */
    private void initApiClient() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        _URL_HOST = prefs.getString("settings_url_host", GlobalVariables._URL_HOST);
        _TIMEOUT_VALUE = prefs.getInt("settings_timeout", GlobalVariables._TIMEOUT_VALUE);
        _LIMIT_POST = prefs.getInt("settings_limit_post", GlobalVariables._LIMIT_POST);
        _COLOR_BG = prefs.getString("settings_color_bg", GlobalVariables._COLOR_BG);

            try {
                getWindow().getDecorView().setBackgroundColor(Color.parseColor(_COLOR_BG));
            }
            catch (IllegalArgumentException e) {
                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            }

        if (apiClient == null) {
            apiClient = new ApiClient(_URL_HOST, _TIMEOUT_VALUE);
        }
        else {
            apiClient.setUrl(_URL_HOST);
            apiClient.setTimeout(_TIMEOUT_VALUE);
        }
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        button_back = (Button)findViewById(R.id.button_back);

        edittext_tags = (EditText)findViewById(R.id.edittext_tags);
        edittext_tags.setText(GlobalVariables._TAGS);

        button_search = (Button)findViewById(R.id.button_search);
        grid_view = (GridView)findViewById(R.id.gridview);

        initApiClient(); 

        //if(edittext_tags.getText().toString().length() != 0) {
            new FetchImagesTask().execute(edittext_tags.getText().toString().trim());
        //}

        // back
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariables._TAGS = edittext_tags.getText().toString();
                finish();
            }
        });

        // search
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tags = edittext_tags.getText().toString().trim();
                
                /* if (tags.length() == 0) {
                    Toast.makeText(GalleryActivity.this, "Please enter tags", Toast.LENGTH_SHORT).show();
                    return;
                } */

                new FetchImagesTask().execute(tags);
            }
        });

        adapter = new ImageAdapter(this, currlist);
        grid_view.setAdapter(adapter);

        grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < imageItems.size())
                {
                    PostItem item = imageItems.get(position);
                    
                    Intent intent = new Intent(GalleryActivity.this, ViewerActivity.class);
                    intent.putExtra("postItem", item);
                    startActivity(intent);
                }
            }
        });
    }
}