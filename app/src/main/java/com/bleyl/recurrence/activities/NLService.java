package com.bleyl.recurrence.activities;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ai.wit.sdk.IWitListener;
import ai.wit.sdk.Wit;
import ai.wit.sdk.model.WitOutcome;

/**
 * Created by Lau Jun Ning on 10-May-17.
 */

public class NLService extends NotificationListenerService implements IWitListener{

    double check1, check2;
    String textAgenda,textDateTime, text_Text,CONTACT;
    JSONArray arr;
    Wit _wit;
    private String TAG = this.getClass().getSimpleName();
    Date date , dateInstance;


//    private NLServiceReceiver nlservicereciver;
    @Override
    public void onCreate() {
        super.onCreate();

        String accessToken = "AHFZMJOREAT3T24N4XYFFBYCRWBLDRLQ";
        _wit = new Wit(accessToken, this);
        _wit.enableContextLocation(getApplicationContext());



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(nlservicereciver);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        Log.i(TAG,"********** ******************************** onNotificationPosted");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().toString() + "\t" + sbn.getPackageName()+" TAG is = "+TAG+"    "+sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString());

        CONTACT =sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();

        String notiContent = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
        _wit.captureTextIntent(notiContent);


////        ************Below code was the code to autoadd notification into the apps ****Very Important
//        Intent i = new Intent(this,AutoAddNotification.class);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        i.putExtra("TITLE_I_NEED",sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString());
//        i.putExtra("CONTENT_I_NEED",sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString());
//        startActivity(i);



//        *****this code is to post out notification details
//        final String message = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString()+" is inside "+sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TITLE).toString();
//
//        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
//            }
//        });




    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i(TAG,"********** onNOtificationRemoved");
        Log.i(TAG,"ID :" + sbn.getId() + "\t" + sbn.getNotification().tickerText +"\t" + sbn.getPackageName());
//        Intent i = new  Intent("com.example.laujunning.testnotificationlistener.NOTIFICATION_LISTENER_EXAMPLE");
//        i.putExtra("notification_event","onNotificationRemoved :" + sbn.getPackageName() + "\n");

//        sendBroadcast(i);
//        new Handler(Looper.getMainLooper()).post(new Runnable() { // Tried new Handler(Looper.myLopper()) also
//            @Override
//            public void run() {
//                Toast.makeText(getApplicationContext(), "If remove is over here", Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public void witDidGraspIntent(ArrayList<WitOutcome> arrayList, String s, Error error) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (error != null) {
            Toast.makeText(this,"Error in Wit graspIntent",Toast.LENGTH_LONG).show();
//            jsonView.setText(error.getLocalizedMessage());
            return ;
        }

        String jsonOutput = gson.toJson(arrayList);


            //arr is JSONArray that use to store jsonOutput which is in array format as u can check from the result with square bracket *[]
//             obj = new JSONObject(jsonOutput);
        try {
            arr = new JSONArray(jsonOutput);


            check1 = arr.getJSONObject(0).getJSONObject("entities").getJSONArray("datetime").getJSONObject(0).getDouble("confidence");
            check2 = arr.getJSONObject(0).getJSONObject("entities").getJSONArray("agenda_entry").getJSONObject(0).getDouble("confidence");
        }catch(Exception e)
        {
            e.printStackTrace();
        }

            if(check1> 0.8000000 &&  check2> 0.5000000){
                for (int i = 0; i < arr.length(); i++)
                {

                    try {
                        //Here i choose getString to get "_text" element from the JSONObject with the 1st item inside the JSONArray
                        textAgenda = arr.getJSONObject(i).getJSONObject("entities").getJSONArray("agenda_entry").getJSONObject(i).getString("value");
                        textDateTime = arr.getJSONObject(i).getJSONObject("entities").getJSONArray("datetime").getJSONObject(i).getString("value");
                        text_Text = arr.getJSONObject(i).getString("_text");

                        int xdatetime = arr.getJSONObject(i).getJSONObject("entities").getJSONArray("datetime").length();
                        int xagenda = arr.getJSONObject(i).getJSONObject("entities").getJSONArray("agenda_entry").length();
                        double dAgenda = arr.getJSONObject(i).getJSONObject("entities").getJSONArray("agenda_entry").getJSONObject(i).getDouble("confidence");
                        double dDT = arr.getJSONObject(i).getJSONObject("entities").getJSONArray("datetime").getJSONObject(i).getDouble("confidence");

                        for (int x = 1; x < xdatetime; x++) {
                            if (dDT <= arr.getJSONObject(i).getJSONObject("entities").getJSONArray("datetime").getJSONObject(x).getDouble("confidence")) {
                                textDateTime = arr.getJSONObject(i).getJSONObject("entities").getJSONArray("datetime").getJSONObject(x).getString("value");
                            }
                        }

                        for (int x = 1; x < xagenda; x++) {
                            if (dAgenda <= arr.getJSONObject(i).getJSONObject("entities").getJSONArray("agenda_entry").getJSONObject(x).getDouble("confidence")) {
                                textAgenda = arr.getJSONObject(i).getJSONObject("entities").getJSONArray("agenda_entry").getJSONObject(x).getString("value");
                            }
                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }


                Calendar cal = Calendar.getInstance();
                dateInstance = cal.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                try {
                   date = sdf.parse(textDateTime);
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                if(date.after(dateInstance)){
                    Toast.makeText(this,"GRAB WHAT I CAN !~~~~~~~~~~~~textAgenda>> "+textAgenda+"~~~~~~text_Text>>>>"+text_Text+"~~~~~~~textDateTime>>>>"+textDateTime,Toast.LENGTH_LONG).show();

                    Intent i = new Intent(this,AutoAddNotification.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("TITLE_I_NEED",textAgenda);
                    i.putExtra("CONTENT_I_NEED",text_Text);
                    i.putExtra("DATETIME_I_NEED",textDateTime);
                    i.putExtra("CONTACT_I_NEED",CONTACT);
                    startActivity(i);

                    check1 = 0;
                    check2 =0;
                }


            }





//        Log.i(TAG,"********************* textAgenda>>>>>>>>>>>>>> :"+textAgenda);
//        Log.i(TAG,"********************* text_Text>>>>>>>>>>>>>>> :"+text_Text);
//        Log.i(TAG,"********************* textDateTime>>>>>>>>>>>>>>: "+textDateTime);



    }

    @Override
    public void witDidStartListening() {

    }

    @Override
    public void witDidStopListening() {

    }

    @Override
    public void witActivityDetectorStarted() {

    }

    @Override
    public String witGenerateMessageId() {
        return null;
    }

//    class NLServiceReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if(intent.getStringExtra("command").equals("clearall")){
//                NLService.this.cancelAllNotifications();
//            }
//            else if(intent.getStringExtra("command").equals("list")){
//                Intent i1 = new  Intent("com.example.laujunning.testnotificationlistener.NOTIFICATION_LISTENER_EXAMPLE");
//                i1.putExtra("notification_event","=====================");
//                sendBroadcast(i1);
//                int i=1;
//                for (StatusBarNotification sbn : NLService.this.getActiveNotifications()) {
//                    Intent i2 = new  Intent("com.example.laujunning.testnotificationlistener.NOTIFICATION_LISTENER_EXAMPLE");
//                    i2.putExtra("notification_event",i +" " + sbn.getPackageName() +"\n");
//                    sendBroadcast(i2);
//                    i++;
//                }
//                Intent i3 = new  Intent("com.example.laujunning.testnotificationlistener.NOTIFICATION_LISTENER_EXAMPLE");
//                i3.putExtra("notification_event","===== Notification List ====");
//                sendBroadcast(i3);
//
//            }
//
//        }
//    }

}