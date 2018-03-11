package com.swalla.campusdock.Activities;

import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.swalla.campusdock.R;
import com.swalla.campusdock.Utils.LocalStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class PreviewImage extends AppCompatActivity {
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.HolyBlack);
        setContentView(R.layout.activity_preview_image);
        imageView = findViewById(R.id.photo_view);
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "CampusDock");
        final File f = new File(folder, (String)LocalStore.getObject("previewImage"));
        if(f.exists())
            Glide.with(this).load(f).into(imageView);
    }
}
