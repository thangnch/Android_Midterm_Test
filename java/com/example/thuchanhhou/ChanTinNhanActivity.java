package com.example.thuchanhhou;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ChanTinNhanActivity extends AppCompatActivity {
    EditText edtBlock;
    Button btnBlock;
    int dem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chan_tin_nhan);
        init();
        clickBlock();
    }

    void init() {
        edtBlock = findViewById(R.id.edtBlockMes);
        btnBlock = findViewById(R.id.btnBlock);
    }

    void clickBlock() {
        btnBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // kiem tra quyen
                checkPermission();
            }
        });
    }


    void checkPermission() {
        // 1 neu la android < android M
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            registerSMS();
        }
        else { //neu la android >= android M thi kiem tra quyen nhan - doc tin nhan chua
            int has_sms_receiver = checkSelfPermission(Manifest.permission.RECEIVE_MMS);
            int has_read_sms = checkSelfPermission(Manifest.permission.READ_SMS);

            ArrayList<String> permissions = new ArrayList<String>();
            // kiem tra xem da cap phep nhan tin chua
            if(has_sms_receiver != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.RECEIVE_MMS);
            }

            if(has_read_sms != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_SMS);
            }

            if(!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), 200);
                registerSMS();
            }
            else {
                // neu khong co j can xin
                registerSMS();
            }
        }
        // dang ky Broadcast Receiver de nhan sms
        //2.
    }

    void registerSMS() {
        IntentFilter smsfilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(new SMSReveicer(), smsfilter);

    }

    public class SMSReveicer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    // get sms objects
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus.length == 0) {
                        return;
                    }
                    // large message might be broken into many
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        sb.append(messages[i].getMessageBody());
                    }
                    //String sender = messages[0].getOriginatingAddress();
                    String message = sb.toString();

                    //1. kiem tra noi dung tin nhan
                    //2.hien thi thong bao
                    processSMS(message);

                    // prevent any other broadcast receivers from receiving broadcast
                    if(dem > 0)
                        abortBroadcast();
                }
            }
        }
    }

    void processSMS(String mes) {
        // tach chuoi nhap
        String[] arrBlock = edtBlock.getText().toString().split(",");

        for (int i = 0; i < arrBlock.length; i++) {
            if(mes.indexOf(arrBlock[i]) >= 0) {
                dem++;
            }
        }
        Toast.makeText(this, "Da chan : " + dem +" tin nhan", Toast.LENGTH_SHORT).show();
    }
}
