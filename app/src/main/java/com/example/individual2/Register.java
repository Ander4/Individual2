package com.example.individual2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
    }

    public void onRegister(View view) {
        EditText editText = findViewById(R.id.usernameR);
        String nombre = editText.getText().toString();

        EditText editText2 = findViewById(R.id.passwordR);
        String pass = editText2.getText().toString();
        Data datos = new Data.Builder().putString("nombre",nombre).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetWorker.class).setInputData(datos).build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            String result = workInfo.getOutputData().getString("datos");
                            System.out.println("Resultado");
                            System.out.println(result);
                            System.out.println("[" + nombre + "]");
                            System.out.println(result.equals("[" + nombre + "]"));

                            if (!result.equals("[" + nombre + "]")) {

                                System.out.println("Hay un nombre igual");
                                Data datos2 = new Data.Builder().putString("nombre", nombre).putString("pass", pass).build();
                                OneTimeWorkRequest otwr2 = new OneTimeWorkRequest.Builder(InsertWorker.class).setInputData(datos2).build();
                                WorkManager.getInstance(Register.this).enqueue(otwr2);

                                Intent i = new Intent(Register.this, MainActivity.class);
                                startActivityForResult(i, 66);

                            }

                        }
                    }
                });

        WorkManager.getInstance(this).enqueue(otwr);
    }
}