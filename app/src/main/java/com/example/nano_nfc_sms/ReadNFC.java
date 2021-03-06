package com.example.nano_nfc_sms;

import androidx.appcompat.app.AppCompatActivity;

import android.nfc.FormatException;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
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


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.regex.Pattern;

public class ReadNFC extends AppCompatActivity {

    private String TAG = "TAG";
    private PendingIntent pendingIntent;
    private IntentFilter[] writeTagFilters;
    private NfcAdapter nfcAdapter;
    boolean writeMode;
    Tag myTag;

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
            if(!Pattern.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", text)) {
                toast("UUID not valid ");
            }
            Nfc.setUUID(text);
            toast("The UUID : \n\n"+text);


        }else{
            error("The card is empty");
        }

    }
    /**  Write Tag */
    UUID uuid =UUID.randomUUID();;
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

    /******************************************************************************************/

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