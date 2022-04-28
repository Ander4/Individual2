package com.example.individual2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SetFoto2Worker extends Worker {

    public SetFoto2Worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/aeiros001/WEB/updateFoto2.php";
        HttpURLConnection urlConnection;

        String nombre = getInputData().getString("nombre");
        String imagen = getInputData().getString("foto");
        Uri uriImagen = Uri.parse(imagen);
        Bitmap bitmapFoto = null;
        try {
            bitmapFoto = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriImagen);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int anchoDestino = 180;
        int altoDestino = 180;
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

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fototransformada = stream.toByteArray();
        String fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("nombre", nombre)
                .appendQueryParameter("foto", fotoen64);
        String parametros = builder.build().getEncodedQuery();

        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();
            System.out.println(parametros);
            int statusCode = urlConnection.getResponseCode();
            System.out.println(statusCode);
            if (statusCode == 200) {
                return ListenableWorker.Result.success();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ListenableWorker.Result.failure();
    }

}
