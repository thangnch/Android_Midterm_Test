package com.example.thuchanhhou;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LayLaiMkActivity extends AppCompatActivity {
    EditText edtEmail;
    Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lay_lai_mk);
        init();
        rePassword();
    }

    void init() {
        edtEmail = findViewById(R.id.edtEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
    }

    void rePassword() {
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtEmail.getText().toString().isEmpty()) {
                    Toast.makeText(LayLaiMkActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                }
                else {
                    checkPermission();
                }
            }
        });
    }

    void checkPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int has_internet = checkSelfPermission(Manifest.permission.INTERNET);

            ArrayList<String> permissions = new ArrayList<String>();
            // kiem tra xem da cap internet
            if(has_internet != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.INTERNET);
            }

            if(!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 200);
            }
            else {
                // lay du lieu tu api vao spinner
                resetPassword();
            }
        }

    }

    void resetPassword() {
        LayLaiMK task = new LayLaiMK();
        task.execute("https://miai.vn/sample/resetpass.php?email="+ edtEmail.getText().toString());
    }

    public class LayLaiMK extends AsyncTask<String, Void, String> {

        public  LayLaiMK() {
            super();
        }

        @Override
        protected String doInBackground(String... params) {
            String textUrl = params[0];

            InputStream in = null;
            BufferedReader br= null;
            try {
                URL url = new URL(textUrl);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("GET");
                httpConn.connect();

                int resCode = httpConn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpConn.getInputStream();
                    br= new BufferedReader(new InputStreamReader(in));

                    StringBuilder sb= new StringBuilder();
                    String s;
                    while((s= br.readLine())!= null) {
                        sb.append(s);
                        sb.append("\n");
                    }
                    return sb.toString();
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String result) {
            // xu ly result -> JSON
            if(!result.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.getString("result_message");
                    Toast.makeText(LayLaiMkActivity.this, "result_message: " + res, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {

            }
        }
    }
}
