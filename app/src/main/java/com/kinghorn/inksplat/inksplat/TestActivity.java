package com.kinghorn.inksplat.inksplat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import junit.framework.Test;

import java.io.File;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test_layout);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 1);
        }else{
            ActivityCompat.requestPermissions(TestActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case 1:
                    startActivityForResult(new InkSplatActivity.InksplatBuilder(getApplicationContext(), data.getData(),Uri.fromFile(new File("/sdcard/sample.jpg")),this).build(),2);
                    break;
                case 2:
                    //Here we want to get the return value that will be the uri to the temporary image that was saved in the cache directory.
                    if(data.hasExtra("InksplatFile")){
                        //If we got a temporary file back , then we want to set the image to the file.
                        ImageView i = (ImageView) findViewById(R.id.imageView3);
                        i.setImageBitmap(BitmapFactory.decodeFile(data.getStringExtra("InksplatFile")));
                    }else{

                    }
                    break;
            }
        }
    }
}
