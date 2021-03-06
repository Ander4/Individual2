package com.example.individual2;

import android.content.Context;
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
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class GetWorker extends Worker {
    public GetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        // Definir la direccion del php
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/aeiros001/WEB/select.php";
        HttpURLConnection urlConnection;

        // Conseguir los datos
        String user = getInputData().getString("nombre");
        String pass = getInputData().getString("pass");

        // Crear la uri
        Uri.Builder builder = new Uri.Builder()
                .appendQueryParameter("nombre", user)
                .appendQueryParameter("pass", pass);
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

                // Si el statusCode es 200(OK), Conseguir el resultado del query
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                System.out.println("Resultado del Query: " + user);
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<String> lista = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++)
                {
                    String nombre = jsonArray.getJSONObject(i).getString("nombre");
                    lista.add(nombre);
                }
                inputStream.close();

                // Crear el parametro resultados para devolverlo
                Data resultados = new Data.Builder()
                        .putString("datos",lista.toString())
                        .build();
                System.out.println(resultados);

                // Devolver resultSuccess y el resultado
                return Result.success(resultados);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
