package com.example.network;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class SMSActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etPhone;
    private EditText etVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        etPhone = findViewById(R.id.et_phone);
        etVerifyCode = findViewById(R.id.et_verify_code);

        Button btnVerify = findViewById(R.id.btn_verify);
        btnVerify.setOnClickListener(this);

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                break;
            case R.id.btn_login:
                break;
        }
    }
}

