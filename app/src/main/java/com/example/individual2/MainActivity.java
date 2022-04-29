package com.example.individual2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onEntrar(View view) {

        // Definir los parametros
        EditText et;
        EditText et2;
        String nombre;
        String pass;

        // Comprobar la orientación del dispositivo
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // Si está en LANDSCAPE conseguir los datos de su layout

            // Conseguir el usuario introducido
            et = findViewById(R.id.usernameL);
            nombre = et.getText().toString();
            // Conseguir la contraseña introducida
            et2 = findViewById(R.id.passwordL);
            pass = et2.getText().toString();


        } else {

            // Si está en PORTRAIT conseguir los datos de su layout

            // Conseguir el usuario introducido
            et = findViewById(R.id.username);
            nombre = et.getText().toString();
            // Conseguir la contraseña introducida
            et2 = findViewById(R.id.password);
            pass = et2.getText().toString();

        }

        // Llamar al worker para comprobar la información con los datos recibidos
        Data datos = new Data.Builder().putString("nombre",nombre).putString("pass",pass).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetWorker.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            // Conseguir el resultado del worker
                            String result = workInfo.getOutputData().getString("datos");

                            // Comprobar el resultado
                            if (result.equals("["+nombre+"]")) {

                                // Si el resultado coincide con el nombre Cambiar al activity Galeria
                                Intent i = new Intent(MainActivity.this, Galeria.class);
                                i.putExtra("user",nombre);
                                startActivityForResult(i, 66);

                            } else {

                                // Si el resultado no coincide hacer un toast indicándolo
                                Toast toast = Toast.makeText(MainActivity.this, "El usuario " + nombre + " y la contraseña no coinciden", Toast.LENGTH_SHORT);
                                toast.show();

                            }

                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    public void onRegister(View v){

        // Al darle al boton register cambiar al activity Register
        Intent i = new Intent(this, Register.class);
        startActivityForResult(i, 66);

    }

}