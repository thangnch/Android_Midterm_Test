package com.example.thuchanhhou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DangNhapActivity extends AppCompatActivity {
    TextView tvValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dang_nhap);
        Intent intent = getIntent();
        String value = intent.getStringExtra("username");
        tvValue = findViewById(R.id.textView);
        tvValue.setText("chào mừng " + value + "đến với ngân hàng");
    }
}
