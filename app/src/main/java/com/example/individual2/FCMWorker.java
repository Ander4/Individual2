package com.example.individual2;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FCMWorker extends Worker {

    public FCMWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {

        // Definir la direccion del php
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/aeiros001/WEB/fcm.php";
        HttpURLConnection urlConnection;

        // Conseguir los datos
        String id = getInputData().getString("id");
        String user = getInputData().getString("nombre");

        // Crear la uri
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("id", id)
                .appendQueryParameter("nombre",user);
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
            System.out.println(parametros);
            int statusCode = urlConnection.getResponseCode();
            System.out.println(statusCode);
            if (statusCode == 200) {

                // Si el codigo es 200(RESULT OK) devolver result success

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
