package com.example.serviceexample1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    TextView tv_results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        tv_results = findViewById(R.id.tv_results);
        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            tv_results.setText(tv_results.getText()+"\n"+"result:"+intent.getDoubleExtra("result",0)
            +"start ID:"+intent.getIntExtra("startid",0));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getExtras()!=null){
            tv_results.setText(tv_results.getText()+"\n"+"result:"+intent.getDoubleExtra("result",0)
                    +"start ID:"+intent.getIntExtra("startid",0));

        }

    }
}
