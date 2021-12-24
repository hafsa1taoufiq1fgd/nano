package com.example.nano_nfc_sms;

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
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import android.os.Bundle;

public class Writeuuid extends AppCompatActivity {
    Button Write;
    private Tag detectedTag;
    private String TAG = "TAG";
    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;
    private NfcAdapter nfcAdapter;
    Tag myTag;

    boolean writeMode;
    UUID uuid =UUID.randomUUID();;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_writeuuid);
        Write =findViewById(R.id.nfc);
        Write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    write(myTag);
                } catch (IOException e) {
                    error(e.getMessage());
                } catch (FormatException e) {
                    error(e.getMessage());
                }
            }
        });

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


    private void write(Tag myTag) throws IOException, FormatException {

        NdefRecord[] records = {createRecord(uuid.toString())};
        NdefMessage message = new NdefMessage(records);
        NfcA nfca = NfcA.get(myTag);
        System.out.println("nfca"+nfca);
        if (myTag == null) {
            error("Read the card again");
        }

        if (!nfca.isConnected()) {
            System.out.println("not connect");
            nfca.connect();
        }
        System.out.println("yes");


        System.out.println("uuid " + uuid);


        nfca.transceive(new byte[]{
                (byte) 0x1B,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF
        });
        nfca.transceive(new byte[]{(byte) 0xA2, (byte) (0x10 & 0x0FF), 0x00, 0x00, (byte) 0x00, (byte) 0xFF});

        nfca.close();
        int size = message.toByteArray().length;
        try {


            Ndef ndef = Ndef.get(myTag);
            System.out.println("ndef"+ndef);

            if (ndef != null) {
                System.out.println("Open");
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is read-only.", Toast.LENGTH_LONG).show();
                }
                if (ndef.getMaxSize() < size) {

                    error("Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.");

                }
                ndef.writeNdefMessage(message);
                toast("The UUID was successfully written.");

                long id = System.currentTimeMillis();
                ndef.close();
                System.out.println("Closed");

            }


        } catch (Exception e) {

            error("Failed to write tag ");

        }

        if(nfca.isConnected()){
            System.out.println("closed1");
            nfca.close();
        }
        NfcA nfca1 = NfcA.get(myTag);
        nfca1.connect();

        nfca1.transceive(new byte[] {(byte) 0xA2,(byte)(0x12 & 0x0FF),(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        nfca1.transceive(new byte[] {(byte) 0xA2,(byte)(0x13 & 0x0FF), (byte) 0xFF , (byte) 0xFF , (byte) 0x00, (byte) 0x00});
        nfca1.transceive(new byte[] {(byte) 0xA2,(byte)(0x10 & 0x0FF),  0x00,  0x00, (byte) 0x00, (byte) 0x04});

        nfca1.close();


    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        payload[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);


        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())||NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            MifareUltralight mu =MifareUltralight.get(myTag);
            try {
                mu.connect();

                byte[] response =mu.transceive(new byte[] {(byte) 0x30,(byte)(0x10 & 0x0FF)});

                if(response[3]!=4){
                    mu.transceive(new byte[]{
                            (byte) 0xA2,(byte)(0x12 & 0x0FF),
                            (byte)0x0FF, (byte)0x0FF, (byte)0x0FF, (byte)0x0FF});
                    mu.transceive(new byte[] {(byte) 0xA2,(byte)(0x10 & 0x0FF), (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04});
                }
                mu.close();
//
            } catch (IOException e) {
                e.printStackTrace();
            }
//
        }
    }

    @Override
    public void onPause(){
        super.onPause();
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