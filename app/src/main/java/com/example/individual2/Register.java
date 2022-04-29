package com.example.individual2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Register extends AppCompatActivity {

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Conseguir el token del Firebase
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }
                        token = task.getResult();
                    }
                });
    }

    public void onRegister(View view) {

        // Definir parametros
        EditText et;
        EditText et2;
        String nombre;
        String pass;

        // Comprobar la orientación del dispositivo
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // Si está en LANDSCAPE conseguir los datos de su layout

            // Conseguir el usuario introducido
            et = findViewById(R.id.usernameRL);
            nombre = et.getText().toString();
            // Conseguir la contraseña introducida
            et2 = findViewById(R.id.passwordRL);
            pass = et2.getText().toString();


        } else {

            // Si está en PORTRAIT conseguir los datos de su layout

            // Conseguir el usuario introducido
            et = findViewById(R.id.usernameR);
            nombre = et.getText().toString();
            // Conseguir la contraseña introducida
            et2 = findViewById(R.id.passwordR);
            pass = et2.getText().toString();

        }

        // Llamar al worker para comprobar la información con los datos recibidos
        Data datos = new Data.Builder().putString("nombre",nombre).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetUserWorker.class).setInputData(datos).build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            // Conseguir el resultado del worker
                            String result = workInfo.getOutputData().getString("datos");

                            // Comprobar el resultado
                            if (!result.equals("[" + nombre + "]")) {

                                // Si el resultado no coincide con el nombre llamar al worker `para Insertarlo en la base de datos
                                Data datos2 = new Data.Builder().putString("nombre", nombre).putString("pass", pass).build();
                                OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(InsertWorker.class).setInputData(datos2).build();
                                WorkManager.getInstance(Register.this).enqueue(otwr2);

                                // Llamar al worker para enviar el mensaje FCM, el cual enviará una notificación avisando
                                // de que se ha añadido el usuario a la base de datos
                                Data datos3 = new Data.Builder().putString("id",token).putString("nombre",nombre).build();
                                OneTimeWorkRequest otwr3 = new OneTimeWorkRequest.Builder(FCMWorker.class).setInputData(datos3).build();
                                WorkManager.getInstance(Register.this).enqueue(otwr3);

                                // Cambiar al MainActivity
                                Intent i = new Intent(Register.this, MainActivity.class);
                                startActivityForResult(i, 66);
                                finish();

                            }else {

                                // Si el resultado coincide hacer un toast indicándolo
                                Toast toast = Toast.makeText(Register.this, "El usuario " + nombre + " ya está registrado", Toast.LENGTH_SHORT);
                                toast.show();

                            }

                        }
                    }
                });

        WorkManager.getInstance(this).enqueue(otwr);
    }
}