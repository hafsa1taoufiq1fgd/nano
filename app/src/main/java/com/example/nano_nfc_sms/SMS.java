package com.example.nano_nfc_sms;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class SMS {
    static int min = 1000;
    static int max = 9999;

    public SMS(){ }

    public static String SendSMS(String number){
        final int random = new Random().nextInt((max - min) + 1) + min;
        SmsManager.getDefault().sendTextMessage(number,null,"The Verfication code : "+random,null,null);
        String codeJson="{\"code\":\""+random+"\"}";
        return codeJson;
    }
}
