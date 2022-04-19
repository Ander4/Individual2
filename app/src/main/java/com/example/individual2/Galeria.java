package com.example.individual2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.Image;
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

            Bitmap bitmapFoto = null;
            try {
                bitmapFoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uriimagen);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int anchoDestino = elImageView.getWidth();
            int altoDestino = elImageView.getHeight();
            int anchoImagen = bitmapFoto.getWidth();
            int altoImagen = bitmapFoto.getHeight();
            float ratioImagen = (float) anchoImagen / (float) altoImagen;
            float ratioDestino = (float) anchoDestino / (float) altoDestino;
            int anchoFinal = anchoDestino;
            int altoFinal = altoDestino;
            System.out.println(anchoFinal + " " + altoFinal);
            if (ratioDestino > ratioImagen) {
                anchoFinal = (int) ((float)altoDestino * ratioImagen);
            } else {
                altoFinal = (int) ((float)anchoDestino / ratioImagen);
            }
            Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);

            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapredimensionado, anchoFinal, altoFinal, true);

            Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

            elImageView.setImageBitmap(rotatedBitmap);

            ImageView img = findViewById(R.id.imageView2);
            img.setImageURI(uriimagen);
        }
    }
}
