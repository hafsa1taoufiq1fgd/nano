package com.example.nano_nfc_sms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
public class FormateNFC extends Activity {
    private String TAG = "TAG";
    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;
    private NfcAdapter nfcAdapter;
    Tag myTag;
    String[][] techLists;
    boolean writeMode;
    Nfc nfc=new Nfc();
    AlertDialog alert;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formate_nfc);
        MyHTTPD.mcontext=this;
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getSupportActionBar().hide();
        //setContentView(R.layout.activity_main);
        //Toast.makeText(this, "Plz Put the NFC ", Toast.LENGTH_LONG).show();
        /*this.alert=new AlertDialog.Builder(this)
                .setTitle(" Notification ")
                .setMessage("Plz put the NFC  ")
                .setPositiveButton("OK",null)
                .show();
*/
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter NdefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

//        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
//        System.out.println("TagDetect" + tagDetected);
        techLists = new  String[][] {
                new String[] { NdefFormatable.class.getName() }
        };
        System.out.println(Ndef.class.getName());
        System.out.println(NdefFormatable.class.getName());
        writeTagFilters = new IntentFilter[]{NdefDetected,tagDetected,techDetected};
    }
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            NfcA nfcA = NfcA.get(myTag);
            if (nfcA != null) {
                try {
                    nfcA.connect();
                    nfcA.transceive(new byte[]{
                            (byte) 0xA2,
                            (byte) 0x03,
                            (byte) 0xE1, (byte) 0x10, (byte) 0x06, (byte) 0x00
                    });
                    nfcA.transceive(new byte[] {
                            (byte)0xA2,
                            (byte)0x04,
                            (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x00
                    });
                    nfcA.transceive(new byte[] {
                            (byte)0xA2,
                            (byte)0x04,
                            (byte)0x03, (byte)0x04, (byte)0xD8, (byte)0x00
                    });
                    nfcA.transceive(new byte[] {
                            (byte)0xA2,
                            (byte)0x05,
                            (byte)0x00, (byte)0x00, (byte)0xFE, (byte)0x00
                    });
                System.out.println("\n \n formatte nmi ");
                    nfc.setFormate();
                    //toast(" The card is Formatted.");
                    //finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("\n \n wa la deja formatter  ");
                    nfc.setFormate();
                    //finish();
                    //error("Failed to format the tag,or the tag is protected");

                }
            }

        }
    }

    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0, 0);
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onRestart() {
        super.onRestart();

    }
    private void error(String text) {
        new AlertDialog.Builder(this)
                .setTitle("Erreur")
                .setMessage(text)
                .setPositiveButton("OK",null)
                .show();
    }
    private void toast(String text) {
        new AlertDialog.Builder(this)
                .setTitle("Result")
                .setMessage(text)
                .setPositiveButton("OK",null)
                .show();
    }



}