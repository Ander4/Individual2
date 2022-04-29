package com.example.individual2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class GetFoto3Worker extends Worker {

    public GetFoto3Worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Definir la direccion del php
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/aeiros001/WEB/getFoto3.php";
        HttpURLConnection urlConnection;

        // Conseguir los datos
        String user = getInputData().getString("nombre");

        // Crear la uri
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("nombre", user);
        String parametros = builder.build().getEncodedQuery();


        try {

            // Abrir la conexión
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
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {

                // Si el codigo es 200(RESULT OK) recoger la foto
                Bitmap elBitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
                System.out.println(elBitmap);
                try {

                    Scanner s = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";

                    // Guardar el Bitmap de la foto en un ficehro para poder leerls despues
                    OutputStreamWriter fichero = new OutputStreamWriter(getApplicationContext().openFileOutput("foto.txt",
                            Context.MODE_PRIVATE));
                    fichero.write(result);
                    fichero.close();

                    // Devolver result success
                    return Result.success();

                } catch (IOException e){

                    e.printStackTrace();

                }

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }

}
