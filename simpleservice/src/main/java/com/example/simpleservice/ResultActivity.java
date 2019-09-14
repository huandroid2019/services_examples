package com.example.simpleservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        if(data!=null){
            ((TextView)findViewById(R.id.tv_data)).setText(data);
        }
    }
}
