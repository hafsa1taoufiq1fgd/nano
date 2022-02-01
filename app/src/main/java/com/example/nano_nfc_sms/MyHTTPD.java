package com.example.nano_nfc_sms;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Random;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class MyHTTPD extends  NanoHTTPD{
    public static Context mcontext;
    // MainActivity mActivity;
    String num;
    /**
     * logger to log to.
     */
    //private static final Logger LOG = Logger.getLogger(MyHTTPD.class.getName());
    public MyHTTPD() {
        super(7000);
    }

    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        final HashMap<String, String> map = new HashMap<String, String>();

        final String json = map.get("postData");
        String uri = session.getUri();
        System.out.println(session.getHeaders());
        //System.out.println(session.toString());
        System.out.println("uri : " + uri);
        System.out.println("Method:" + method.toString());
        //System.out.println((method.toString()).equals("POST"));

        if ((method.toString()).equals(("OPTIONS"))) {
            System.out.println("options hhhhh ");
            NanoHTTPD.Response resp = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json",
                    "{name:'options'}");
            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Access-Control-Allow-Headers", "*");
            resp.addHeader("Access-Control-Allow-Credentials", "true");
            resp.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS");
            //String t="hh";
            return resp;
        }
        if ((method.toString()).equals("POST")) {
            try {
                session.parseBody(map);
                System.out.println("--------- Body----------");
                num = map.get("postData");
                System.out.println(num);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ResponseException e) {
                e.printStackTrace();
            }

            if (uri.equals("/nfc")) {
                Nfc.putNull();
                // mcontext=mActivity.getApplicationContext();
                System.out.println(mcontext.toString());
                Intent i = new Intent();
                i.setClass(mcontext, Nfc_reader.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(i);
                //testm.Remove();
                while (!Nfc.AllValuesSet()) {
                    try {
                        Thread.sleep(1000);

                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
           /* Intent intent=new Intent(mcontext, MyService.class);
            mcontext.startService(intent);
            while(MyService.running){
                try
                {
                    Thread.sleep(1000);

                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }*/

                //startService(new Intent(mcontext, MyService2.class));
                System.out.println(Nfc.JsonFormat());
                NanoHTTPD.Response resp = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json",
                        Nfc.JsonFormat());
                resp.addHeader("Access-Control-Allow-Origin", "*");
                resp.addHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with,Accept");
                resp.addHeader("Access-Control-Allow-Credentials", "true");
                resp.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS");
                return resp;

            }
            if (uri.equals("/sms")) {
                String verf_code = SMS.SendSMS(num);
                System.out.println(verf_code);
                NanoHTTPD.Response resp = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json",
                        verf_code);
                resp.addHeader("Access-Control-Allow-Origin", "*");
                resp.addHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with,Accept");
                resp.addHeader("Access-Control-Allow-Credentials", "true");
                resp.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS");
                return resp;
            } else {
                NanoHTTPD.Response resp = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json",
                        "esleee");
                resp.addHeader("Access-Control-Allow-Origin", "*");
                resp.addHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with,Accept");
                resp.addHeader("Access-Control-Allow-Credentials", "true");
                resp.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS");
                return resp;
            }
        } else {
           /* File file = new File("/assets/marker-icon.png");
            FileInputStream fis = null;
            Activity mActivity;


            try {
                fis = new FileInputStream(String.valueOf(mcontext.getAssets().open("marker-icon.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return newFixedLengthResponse(Response.Status.OK,"image/png",fis, file.length() );*/

            //Response resp=new Response(Response.Status.OK,"application/json",iss);
            NanoHTTPD.Response resp = newFixedLengthResponse(NanoHTTPD.Response.Status.OK, "application/json",
                    "{name:'hafsa'}");
            resp.addHeader("Access-Control-Allow-Origin", "*");
            resp.addHeader("Access-Control-Allow-Headers", "Content-Type, x-requested-with,Accept");
            resp.addHeader("Access-Control-Allow-Credentials", "true");
            resp.addHeader("Access-Control-Allow-Methods", "GET,POST,PUT,OPTIONS");
            //String t="hh";*/
            return resp;
        }


    }

}
