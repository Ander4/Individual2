package com.example.individual2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void onGet(View view) {
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetWorker.class).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            TextView textViewResult = findViewById(R.id.textView);
                            textViewResult.setText(workInfo.getOutputData().getString("datos"));
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
    public void onInsert(View view) {
        EditText editText = findViewById(R.id.etInsert);
        String pokimon = editText.getText().toString();
        Data datos = new Data.Builder().putString("pokimon",pokimon).build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(InsertWorker.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
    }
}