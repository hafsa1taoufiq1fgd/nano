package com.example.nano_nfc_sms;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.trusted.TrustedWebActivityIntentBuilder;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.androidbrowserhelper.trusted.TwaLauncher;
import android.os.Bundle;

public class Web extends AppCompatActivity {
    Uri t = Uri.parse("http://192.168.2.230:4200/");
    TwaLauncher launcher;
    private boolean mBrowserWasLaunched;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        launchTwa(t);
        System.out.println("\n Launch TWA");
    }

    public void launchTwa(Uri uri) {
        TrustedWebActivityIntentBuilder builder = new TrustedWebActivityIntentBuilder(uri);
        launcher= new TwaLauncher(this);
        //launcher.launch(builder,null,null);
        launcher.launch(builder, null, null, null);
        mBrowserWasLaunched = true;
        //launcher.launch(uri);
        //launcher.launch();
        //launcher.launch(builder, null, null);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mBrowserWasLaunched) {
            finish(); // The user closed the Trusted Web Activity and ended up here.
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (launcher != null) {
            launcher.destroy();
        }
    }
}