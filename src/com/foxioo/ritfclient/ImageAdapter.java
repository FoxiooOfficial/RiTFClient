package com.foxioo.ritfclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter
{
    private Context context;
    private ArrayList<Bitmap> bitmaps;

    public ImageAdapter(Context context, ArrayList<Bitmap> bitmaps)
    {
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @Override
    public int getCount() {
        return (bitmaps == null) ? 0 : bitmaps.size();
    }

    @Override
    public Object getItem(int pos) {
        return bitmaps.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent)
    {
        ImageView image_view;
        if (view == null)
        {
            image_view = new ImageView(context);

            float density = context.getResources().getDisplayMetrics().density;
            int pixelheight = (int)(GlobalVariables._IMAGE_PIXEL_HIGH_DENSITY * density + 0.5f);

            image_view.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixelheight));

            image_view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image_view.setPadding(GlobalVariables._IMAGE_PADDING, GlobalVariables._IMAGE_PADDING, GlobalVariables._IMAGE_PADDING, GlobalVariables._IMAGE_PADDING);
        }
        else {
            image_view = (ImageView)view;
        }

        image_view.setImageBitmap(bitmaps.get(pos));
        return image_view;
    }
}