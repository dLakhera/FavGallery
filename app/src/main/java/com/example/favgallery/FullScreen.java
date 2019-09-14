package com.example.favgallery;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.net.URLConnection;

import static com.example.favgallery.GridAdapter.gridAdapter;

public class FullScreen extends AppCompatActivity {

    private String path;
    private boolean isImage = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                path = null;
            } else {
                path = extras.getString("Location");
            }
        } else {
            path = savedInstanceState.getString("Location");
        }

        ImageView imageView = findViewById(R.id.ImageView);
        if (URLConnection.guessContentTypeFromName(path).startsWith("video")) {
            {
                isImage = false;
                VideoView videoView = findViewById(R.id.VideoView);
                try {
                    videoView.setVideoURI(Uri.parse(path));
                    videoView.setVisibility(View.VISIBLE);
                    MediaController mediaController = new MediaController(this);
                    mediaController.setAnchorView(videoView);
                    videoView.setMediaController(mediaController);

                } catch (Exception e) {
                    e.printStackTrace();
                    imageView.setImageResource(R.drawable.download);
                    imageView.setVisibility(View.VISIBLE);
                    Log.e("Error", e.getMessage());
                }
            }

        } else {
            imageView.setVisibility(View.VISIBLE);
            try {
                imageView.setImageBitmap(BitmapFactory.decodeFile(path));
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.download);
                Log.e("Error", e.getMessage());
            }
        }
    }

    public void DeleteThis(View view) {
        gridAdapter.deletePath(path);
        File file = new File(path);
        if (file.delete())
            Toast.makeText(this, "File Deleted Successfully", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Unable to Delete", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("Location",path);
    }

    public void RemoveThis(View view) {
        gridAdapter.deletePath(path);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void Share(View V){
        Intent intent = new Intent(Intent.ACTION_SEND);
        if(isImage)
            intent.setType("image/jpeg");
        else
            intent.setType("video/mp4");
        intent.putExtra(Intent.EXTRA_SUBJECT,"FavGallery");
        intent.putExtra(Intent.EXTRA_TEXT,"\n\nShared from FavGallery App");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        startActivity(Intent.createChooser(intent,"Share Using:"));
    }

    public void openDefault(View v){
        if(isImage)
            startActivity(new Intent().setDataAndType(Uri.parse(path), "image/jpeg"));
        else
            startActivity(new Intent().setDataAndType(Uri.parse(path), "video/*"));
    }

}
