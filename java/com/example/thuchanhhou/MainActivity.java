package com.example.thuchanhhou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText edtUsername;
    EditText edtPassword;
    Button btnSignIn, btnRegister, btnPasswordLoss, btnChangPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        signIn();
        register();
        lossPass();
        changePass();
    }

    void init() {
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnRegister = findViewById(R.id.btnRegister);
        btnPasswordLoss = findViewById(R.id.btnPasswordLost);
        btnChangPass = findViewById(R.id.btnChangePass);
    }

    void signIn() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtUsername.getText().toString().equals("admin") && !edtPassword.getText().toString().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, DangNhapActivity.class);
                    intent.putExtra("username", edtUsername.getText().toString());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Sai tai khoan / mat khau", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void register() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.miai.vn/sample/reg.php";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    void lossPass() {
        btnPasswordLoss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LayLaiMkActivity.class);
                startActivity(intent);
            }
        });
    }

    void changePass() {
        btnChangPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DoiMkActivity.class);
                startActivity(intent);
            }
        });
    }


}
