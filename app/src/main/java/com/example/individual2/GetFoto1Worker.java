package com.example.individual2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class GetFoto1Worker extends Worker {

    public GetFoto1Worker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/aeiros001/WEB/getFoto1.php";
        HttpURLConnection urlConnection;

        String user = getInputData().getString("nombre");

        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("nombre", user);
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
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                Bitmap elBitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
                System.out.println(elBitmap);
                try {
                    System.out.println("User del worker GETFOTO1: " + user);

                    Scanner s = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";
                    OutputStreamWriter fichero = new OutputStreamWriter(getApplicationContext().openFileOutput("foto.txt",
                            Context.MODE_PRIVATE));
                    fichero.write(result);
                    fichero.close();
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
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return Result.failure();
    }

}
