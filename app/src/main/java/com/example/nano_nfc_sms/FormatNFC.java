package com.example.nano_nfc_sms;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
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
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;
public class FormatNFC {
    public static Boolean isFormated=false;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    //Context context;
    TextView tvNFCContent;
    TextView NumSerie;
    TextView Technologies;
    TextView CardType;
    TextView IsWritable;
    TextView ReadOnly;
    TextView Tail;
    Tag myTag;
    private String TAG = "TAG";
    String[][] techLists;


    public FormatNFC(){

    }

    public String Init(Context context,Intent i){
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null) {
            //Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();

            return "This device doesn't support NFC";
        }
        pendingIntent = PendingIntent.getActivity(context, 0, new Intent(i).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
        return "done ";
    }
}
