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


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        ActivityManager am= (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            if (am.isBackgroundRestricted()==true){
//                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE},1);
//            }
//        }

//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            return;
//                        }
//                        String token = task.getResult();
//                    }
//                });

    }

    public void onEntrar(View view) {

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

        Data datos = new Data.Builder().putString("nombre",nombre).putString("pass",pass).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetWorker.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            String result = workInfo.getOutputData().getString("datos");
                            System.out.println("Resultado");
                            System.out.println(result);

                            if (result.equals("["+nombre+"]")) {

                                Intent i = new Intent(MainActivity.this, Galeria.class);
                                i.putExtra("user",nombre);
                                startActivityForResult(i, 66);

                            }

                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    public void onRegister(View v){

        Intent i = new Intent(this, Register.class);
        startActivityForResult(i, 66);

    }

}