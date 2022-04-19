package com.example.individual2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Galeria extends AppCompatActivity {

    Uri uriimagen = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        if (getIntent().getExtras() != null) {
            String mensaje= getIntent().getExtras().getString("mensaje");
            String fecha= getIntent().getExtras().getString("fecha");

        }
    }

    public void onEntrar(View view) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefich = "IMG_" + timeStamp + "_";
        File directorio=this.getFilesDir();
        File fichImg = null;

        try {
            fichImg = File.createTempFile(nombrefich, ".jpg",directorio);
            //uriimagen = FileProvider.getUriForFile(this, "com.example.tema17ejercicio1.provider", fichImg);
        } catch (IOException e) {
            e.printStackTrace();
        }

        uriimagen = FileProvider.getUriForFile(this, "com.example.tema17ejercicio1.provider", fichImg);

        Intent elIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        elIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
        startActivityForResult(elIntent, 4);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4 && resultCode == RESULT_OK) {
            ImageView elImageView = findViewById(R.id.imageView);
            elImageView.setImageURI(uriimagen);
        }
    }
}
