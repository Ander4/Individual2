package com.example.individual2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

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
    int numFoto = 1;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        user = (String) b.get("user");

    }

//    private void getImages(){
//
//        Data datos = new Data.Builder().putString("nombre",user.getText().toString()).putString("pass",pass.getText().toString()).build();
//        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetWorker.class).setInputData(datos).build();
//        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
//                .observe(this, new Observer<WorkInfo>() {
//                    @Override
//                    public void onChanged(WorkInfo workInfo) {
//                        if(workInfo != null && workInfo.getState().isFinished()){
//
//                            String result = workInfo.getOutputData().getString("datos");
//                            System.out.println("Resultado");
//                            System.out.println(result);
//
//                            if (result.equals("["+user.getText().toString()+"]")) {
//
//                                Intent i = new Intent(MainActivity.this, Galeria.class);
//                                startActivityForResult(i, 66);
//
//
//                            }
//
//                        }
//                    }
//                });
//        WorkManager.getInstance(this).enqueue(otwr);
//
//    }

    private void setNumFoto(){

        ImageView elImageView = findViewById(R.id.imageView);
        ImageView elImageView2 = findViewById(R.id.imageView2);
        ImageView elImageView3 = findViewById(R.id.imageView3);
        ImageView elImageView4 = findViewById(R.id.imageView4);

        if (elImageView.getDrawable() == null) {

            numFoto = 1;
            System.out.println("Es null la foto1");

        } else if (elImageView2.getDrawable() == null) {

            numFoto = 2;
            System.out.println("Es null la foto2");

        } else if (elImageView3.getDrawable() == null) {

            numFoto = 3;
            System.out.println("Es null la foto3");

        } else if (elImageView4.getDrawable() == null) {

            numFoto = 4;
            System.out.println("Es null la foto4");

        }

    }

    public void onEntrar(View view) {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefich = "IMG_" + timeStamp + "_";
        File directorio=this.getFilesDir();
        File fichImg = null;

        try {
            fichImg = File.createTempFile(nombrefich, ".jpg",directorio);
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

            setNumFoto();

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

            setImages(rotatedBitmap);
        }
    }

    private void setImages(Bitmap bitmap){

        switch (numFoto){

            case 1: {

                ImageView elImageView = findViewById(R.id.imageView);
                elImageView.setImageBitmap(bitmap);
                Data datos = new Data.Builder().putString("nombre",user).putString("foto", uriimagen.toString()).build();
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(SetFoto1Worker.class).setInputData(datos).build();
                WorkManager.getInstance(this).enqueue(otwr);
                break;
            }

            case 2: {

                ImageView elImageView = findViewById(R.id.imageView2);
                elImageView.setImageBitmap(bitmap);
                break;

            }

            case 3: {

                ImageView elImageView = findViewById(R.id.imageView3);
                elImageView.setImageBitmap(bitmap);
                break;

            }

            case 4: {

                ImageView elImageView = findViewById(R.id.imageView4);
                elImageView.setImageBitmap(bitmap);
                break;

            }

        }

    }

}
