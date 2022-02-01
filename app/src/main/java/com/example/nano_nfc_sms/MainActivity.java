package com.example.nano_nfc_sms;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.browser.trusted.TrustedWebActivityIntentBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.androidbrowserhelper.trusted.TwaLauncher;

public class MainActivity extends AppCompatActivity {
    private static final int PORT = 7000;
    private static final int PERMISSION_SEND_SMS = 123;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = findViewById(R.id.start);
        requestSmsPermission();
        //startService(new Intent(getApplicationContext(), NanoService.class));
        //MyHTTPD.mcontext=getApplicationContext();
        //startActivity(new Intent(getApplicationContext(),Web.class));
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

    @Override
    public AssetManager getAssets() {
        return super.getAssets();
    }

    public void nfc(View v){
        startActivity(new Intent(getApplicationContext(),Writeuuid.class));
    }
    public void read(View v){
        startActivity(new Intent(getApplicationContext(),Nfc_reader.class));
    }
    private void requestSmsPermission() {

        // check permission is given
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //request permission (see result in onRequestPermissionsResult() method)
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    PERMISSION_SEND_SMS);
            //onRequestPermissionsResult(PERMISSION_SEND_SMS,new String[]{Manifest.permission.SEND_SMS },PackageManager.)


        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_SEND_SMS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    //sendSms(phone, message);
                    return;
                } else {
                    // permission denied
                    requestSmsPermission();
                }
                return;
            }
        }
    }
}