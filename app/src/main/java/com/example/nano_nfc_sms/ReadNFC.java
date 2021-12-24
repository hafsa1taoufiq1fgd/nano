package com.example.nano_nfc_sms;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;


import java.io.UnsupportedEncodingException;

public class ReadNFC extends AppCompatActivity {

    private String TAG = "TAG";
    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;
    private NfcAdapter nfcAdapter;
    boolean writeMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_read_nfc);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
        }
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        IntentFilter NdefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        System.out.println("TagDetect" + tagDetected);

        writeTagFilters = new IntentFilter[]{tagDetected, techDetected, NdefDetected};
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        NdefMessage[] msgs = null;

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
                buildTagViews(msgs);
                System.out.println(msgs);

            } else {
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{
                        record
                });
                msgs = new NdefMessage[]{
                        msg
                };
                System.out.println("2");
                Toast.makeText(this, "Unknown tag type:" + msgs, Toast.LENGTH_LONG);
            }
            if(rawMsgs==null){
                error("Failed to Read the tag.");
            }


        }


    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;
        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        if(payload.length!=0){
            System.out.println("payload"+payload.length);

            for (int i=0;i<payload.length;i++) {
                System.out.println("payload"+payload[i]);
            }
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0063;

            try {
                text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.toString());
            }
            Nfc.setUUID(text);
            toast("The UUID : \n\n"+text);


        }else{
            error("The card is empty");
        }

    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        readFromIntent(intent);

    }

    @Override
    public void onPause() {
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume() {
        super.onResume();
        WriteModeOn();
    }


    private void WriteModeOn() {
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff() {
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void toast(String text) {
        new AlertDialog.Builder(this)
                .setTitle("Result")
                .setMessage(text)
                .setPositiveButton("OK",null)
                .show();
    }

    private void error(String text) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(text)
                .setPositiveButton("OK",null)
                .show();
    }
}