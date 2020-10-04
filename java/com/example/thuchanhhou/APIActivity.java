package com.example.thuchanhhou;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class APIActivity extends AppCompatActivity {
    Spinner spCountry;
    ArrayAdapter countryAdapter;
    TextView tvInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);

        countryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        tvInfo = findViewById(R.id.tvInfo);
        //check quyen truy cap internet
        spCountry = findViewById(R.id.spCountry);
        spCountry.setAdapter(countryAdapter);

        checkPermission();
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
                callGetCountry();
            }
        }

    }

    void getCountryInfo() {
        getCountryList task = new getCountryList(this.countryAdapter);
    }

    void callGetCountry() {
        // tao moi 1 luong con
        getCountryList task = new getCountryList(this.countryAdapter);

        // thuc thi -> truyen vao url
        task.execute("https://api.covid19api.com/countries");
    }

    public  class getCountryList extends AsyncTask<String, Void, String> {
        private ArrayAdapter<String> localCountryAdapter;
        //1 . ham tao
        public  getCountryList(ArrayAdapter<String> spCountry) {
            this.localCountryAdapter = spCountry;
        }
        //2. Xu ly nen
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

        //.3 sau khi nhan du du lieu tu api
        @Override
        protected void onPostExecute(String result) {
            // xu ly result -> JSON
            if(!result.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    localCountryAdapter.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        // lay ra json ocject tai vi tri i
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        localCountryAdapter.add(jsonObject.getString("Country"));
                    }
                    localCountryAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {

            }
        }
    }
}
