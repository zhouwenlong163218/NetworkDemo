package com.example.network;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button URL,Ok,three,sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        URL = findViewById(R.id.btn_URL);
        Ok = findViewById(R.id.btn_OkHttp);
        three = findViewById(R.id.btn_Http);
        sms = findViewById(R.id.btn_sms);

        URL.setOnClickListener(this);
        Ok.setOnClickListener(this);
        three.setOnClickListener(this);
        sms.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.btn_URL:
                intent = new Intent(MainActivity.this,HttpURLActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_OkHttp:
                intent = new Intent(MainActivity.this,HttpOkActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_Http:
                intent = new Intent(MainActivity.this,ThreeHttpActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_sms:
                intent = new Intent(MainActivity.this,SMSActivity.class);
                startActivity(intent);
                break;
        }
    }
}
