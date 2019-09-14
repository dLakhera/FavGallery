package com.example.favgallery;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.favgallery.GridAdapter.gridAdapter;

public class MainActivity extends AppCompatActivity {
    private static final int Permission_Request = 0;
    private static final int Result_Load_Image = 1;
    private static final int Result_Load_Video = 2;
    boolean doubleBackToExitPressedOnce = false;
    private String TAG = "MainActivity";
    private GridView gridView;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPref = getSharedPreferences("URI_LIST", MODE_PRIVATE);
        gridAdapter = new GridAdapter(getApplicationContext(), sharedPref);
        gridView = findViewById(R.id.Grid);
        gridView.setAdapter(gridAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FullScreen.class);
                intent.putExtra("Location", gridAdapter.getPath(position));
                startActivity(intent);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting Permission To Read External Storage");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Permission_Request);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting Permission To Write External Storage");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Permission_Request);
        }

        updateView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateView();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public void addImage(View V) {
        Log.d(TAG, "addImage");
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, Result_Load_Image);
    }

    public void addVideo(View V) {
        Log.d(TAG, "addVideo");
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, Result_Load_Video);
    }

    public void ClearAll(View view) {
        Log.d(TAG, "ClearAll");
        sharedPref.edit().clear().apply();
        gridAdapter.clearImageIds();
        updateView();
    }

    private void updateView() {
        gridAdapter.notifyDataSetChanged();
        gridView.setAdapter(gridAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case Permission_Request:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Sorry! We Don't have the required permission", Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case Result_Load_Image:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage;
                        selectedImage = data.getData();
                        if (selectedImage == null)
                            break;
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        if (picturePath != null) {
                            gridAdapter.addImageIds(picturePath);

                        }
                    } catch (NullPointerException E) {
                        break;
                    }
                }
                break;
            case Result_Load_Video:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedVideo = data.getData();
                        if (selectedVideo == null)
                            break;
                        String[] filePathColumn = {MediaStore.Video.Media.DATA};
                        Cursor cursor = getContentResolver().query(selectedVideo, filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String videoPath = cursor.getString(columnIndex);
                        cursor.close();
                        if (videoPath != null) {
                            gridAdapter.addImageIds(videoPath);
                        }
                    } catch (NullPointerException E) {
                        break;
                    }
                }
                break;
        }
        gridAdapter.removeDuplicates();
    }
}
