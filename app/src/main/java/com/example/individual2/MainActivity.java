package com.example.individual2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
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

        ActivityManager am= (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (am.isBackgroundRestricted()==true){
                System.out.println("No esta el permiso concedido");
            }
        }

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
        EditText user = findViewById(R.id.username);
        EditText pass = findViewById(R.id.password);

        System.out.println(user.getText().toString());

        Data datos = new Data.Builder().putString("nombre",user.getText().toString()).putString("pass",pass.getText().toString()).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetWorker.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            String result = workInfo.getOutputData().getString("datos");
                            System.out.println("Resultado");
                            System.out.println(result);

                            if (result.equals("["+user.getText().toString()+"]")) {

                                Intent i = new Intent(MainActivity.this, Galeria.class);
                                startActivityForResult(i, 66);


                            }

                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    public void onRegister(View v){

        //Log.i("Recorrido","Paso por onRegister MainActivity");
        Intent i = new Intent(this, Register.class);
        startActivityForResult(i, 66);

    }

}