package com.example.nano_nfc_sms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.browser.trusted.TrustedWebActivityIntentBuilder;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.androidbrowserhelper.trusted.TwaLauncher;

public class MainActivity extends AppCompatActivity {
    private static final int PORT = 7000;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = findViewById(R.id.start);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(getApplicationContext(), NanoService.class));
                MyHTTPD.mcontext=getApplicationContext();
                startActivity(new Intent(getApplicationContext(),Web.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView textIpaddr = (TextView) findViewById(R.id.ipaddr);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        textIpaddr.setText("Please access! http://" + formatedIpAddress + ":" + PORT);
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    public void test(View v){
       /* Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);*/
        Intent intent = new Intent(this, Web.class);
        startActivity(intent);
    }
    public void stop(View v){
        stopService(new Intent(this, NanoService.class));
    }
    @Override
    public void onStop() {
        super.onStop();
       // stopService(new Intent(this, NanoService.class));
    }

    public void nfc(View v){
        startActivity(new Intent(getApplicationContext(),Writeuuid.class));
    }
    public void read(View v){
        startActivity(new Intent(getApplicationContext(),Nfc_reader.class));
    }
}