package com.drukido.vrun.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.drukido.vrun.R;

import java.io.FileInputStream;

public class PhotoViewerActivity extends AppCompatActivity {

    public static final String EXTRA_PHOTO_KEY = "photoExtra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);

        Bitmap bmp = null;
        String filename = getIntent().getStringExtra(EXTRA_PHOTO_KEY);
        try {
            FileInputStream is = this.openFileInput(filename);
            bmp = BitmapFactory.decodeStream(is);
            is.close();
            ((ImageView)findViewById(R.id.photoViewer_imageView)).setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
        }
    }
}
