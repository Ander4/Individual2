package com.example.individual2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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
        Data datos = new Data.Builder().putString("nombre",nombre).putString("pass",pass).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(InsertWorker.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
    }
}