package com.example.thuchanhhou;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DoiMkActivity extends AppCompatActivity {
    EditText edtNewPass;
    Button btnChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doi_mk);
        init();
        changePass();
    }

    void init() {
        edtNewPass = findViewById(R.id.new_password);
        btnChange = findViewById(R.id.btnChange);
    }

    void changePass() {
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtNewPass.getText().toString().isEmpty()) {
                    Toast.makeText(DoiMkActivity.this, "Bạn chưa nhập mk mới", Toast.LENGTH_SHORT).show();
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
                doChangePass();
            }
        }
    }

    void doChangePass() {
        Map<String, String> postData = new HashMap<>();
        postData.put("old_pass", "hoannc");
        postData.put("new_pass", edtNewPass.getText().toString());
        HttpPostAsyncTask task = new HttpPostAsyncTask(postData);
        task.execute("https://miai.vn/sample/updatepass.php");
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public class HttpPostAsyncTask extends AsyncTask<String, Void, String> {
        // This is the JSON body of the post
        JSONObject postData;
        // This is a constructor that allows you to pass in the JSON body
        public HttpPostAsyncTask(Map<String, String> postData) {
            if (postData != null) {
                this.postData = new JSONObject(postData);
            }
        }

        // This is a function that we are overriding from AsyncTask. It takes Strings as parameters because that is what we defined for the parameters of our async task
        @Override
        protected String doInBackground(String... params) {

            try {
                // This is getting the url from the string we passed in
                URL url = new URL(params[0]);
                // Create the urlConnection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestMethod("POST");
                // OPTIONAL - Sets an authorization header
                urlConnection.setRequestProperty("Authorization", "someAuthString");
                // Send the post body
                if (this.postData != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());
                    writer.write(postData.toString());
                    writer.flush();
                }

                int statusCode = urlConnection.getResponseCode();

                if (statusCode ==  200) {

                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());

                    String response = convertInputStreamToString(inputStream);
                    //Log.d("res", response);
                    //JSONObject jsonObject = new JSONObject(response);
                    //String res = jsonObject.getString("result_message");
                    return  response;
                    //Toast.makeText(DoiMkActivity.this, "result_message: " + res, Toast.LENGTH_SHORT).show();
                } else {
                    // Status code is not 200
                    // Do something to handle the error
                }

            } catch (Exception e) {
                Log.d("ERR: ", e.getLocalizedMessage());
            }
            return null;
        }

        protected void onPostExecute(String result) {
            // xu ly result -> JSON
            if(!result.isEmpty()) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String res = jsonObject.getString("result_message");
                    Toast.makeText(DoiMkActivity.this, "result_message: " + res, Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {

            }
        }
    }
}
