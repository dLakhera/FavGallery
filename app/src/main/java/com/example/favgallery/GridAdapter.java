package com.example.favgallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import java.io.File;
import java.lang.reflect.Array;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GridAdapter extends BaseAdapter {

    static GridAdapter gridAdapter;

    private int imageCount = 0;
    private Context context;
    private ArrayList<String> iDs = new ArrayList<>();
    private SharedPreferences sharedPreferences;

    GridAdapter(Context context, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.context = context;
        Map<String, ?> entries = sharedPreferences.getAll();
        Set<String> keys = entries.keySet();
        for (String key : keys) {
            iDs.add(key);
            imageCount++;
        }
    }

    void removeDuplicates()
    {
        ArrayList<String> list = iDs;
        Set<String> set = new LinkedHashSet<>(list);
        list.clear();
        list.addAll(set);
        iDs=list;
        imageCount = iDs.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view != null)
            return view;
        String path = iDs.get(i);
        ImageView imageView = new ImageView((context));
        if (!new File(path).exists()) {
            imageCount--;
            iDs.remove(i);
            sharedPreferences.edit().remove(path).apply();
            imageView.setImageResource(R.drawable.download);/////////////////////////////
            return imageView;
        }
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        imageView.setLayoutParams(new GridView.LayoutParams(screenWidth/3, screenWidth/3));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        /*try {
            imageView.setImageBitmap(context.getContentResolver().loadThumbnail(Uri.parse(path),new Size(512, 512), null));
        }catch(IOException e){
            e.getStackTrace();
        }*/
        if (URLConnection.guessContentTypeFromName(iDs.get(i)).startsWith("video")) {
            imageView.setImageBitmap(ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MICRO_KIND));
        } else {
            imageView.setImageBitmap(BitmapFactory.decodeFile(path));
        }
        return imageView;
    }

    //Functions Used
    void addImageIds(String path) {
        try {
            if (new File(path).exists()) {
                iDs.add(path);
                imageCount++;
                sharedPreferences.edit().putString(path, path).apply();
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }

    void clearImageIds() {
        iDs.clear();imageCount=0;
    }

    void deletePath(String path) {
        sharedPreferences.edit().remove(path).apply();
        iDs.remove(path);
        Log.d("Delete", path);
        imageCount--;
    }

    String getPath(int position) {
        return iDs.get(position);
    }

    //Unused Functions
    @Override
    public int getCount() {
        return imageCount;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
