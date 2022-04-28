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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class Register extends AppCompatActivity {

    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

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

        System.out.println("token: " + token);

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

        Data datos = new Data.Builder().putString("nombre",nombre).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetUserWorker.class).setInputData(datos).build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            String result = workInfo.getOutputData().getString("datos");
                            System.out.println("Resultado");
                            System.out.println(result);
                            System.out.println("[" + nombre + "]");
                            //System.out.println(result.equals("[" + nombre + "]"));
                            System.out.println(result);

                            if (!result.equals("[" + nombre + "]")) {

                                System.out.println("No Hay un nombre igual");
                                Data datos2 = new Data.Builder().putString("nombre", nombre).putString("pass", pass).build();
                                OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(InsertWorker.class).setInputData(datos2).build();
                                WorkManager.getInstance(Register.this).enqueue(otwr2);

                                Data datos3 = new Data.Builder().putString("id",token).putString("nombre",nombre).build();
                                OneTimeWorkRequest otwr3 = new OneTimeWorkRequest.Builder(FCMWorker.class).setInputData(datos3).build();
                                WorkManager.getInstance(Register.this).enqueue(otwr3);

                                Intent i = new Intent(Register.this, MainActivity.class);
                                startActivityForResult(i, 66);

                            }

                        }
                    }
                });

        WorkManager.getInstance(this).enqueue(otwr);
    }
}