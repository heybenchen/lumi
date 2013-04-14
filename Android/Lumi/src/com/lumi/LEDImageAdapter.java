package com.lumi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
 
public class LEDImageAdapter extends BaseAdapter {
    private Context mContext;
 
    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off,
            R.drawable.led_off, R.drawable.led_off, R.drawable.led_off, R.drawable.led_off
    };
 
    // Constructor
    public LEDImageAdapter(Context c){
        mContext = c;
    }
 
    //@Override
    public int getCount() {
        return mThumbIds.length;
    }
 
    //@Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }
 
    //@Override
    public long getItemId(int position) {
        return 0;
    }
    
    //@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mThumbIds[position]); //TEMP: setting index to 0 since there is only one image
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(60, 60));
        return imageView;
    }

}