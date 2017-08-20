package com.bleyl.recurrence.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.bleyl.recurrence.database.DatabaseHelper;
import com.bleyl.recurrence.dialogs.AdvancedRepeatSelector;
import com.bleyl.recurrence.dialogs.DaysOfWeekSelector;
import com.bleyl.recurrence.dialogs.IconPicker;
import com.bleyl.recurrence.dialogs.RepeatSelector;
import com.bleyl.recurrence.models.Colour;
import com.bleyl.recurrence.models.Reminder;
import com.bleyl.recurrence.R;
import com.bleyl.recurrence.receivers.AlarmReceiver;
import com.bleyl.recurrence.utils.AlarmUtil;
import com.bleyl.recurrence.utils.AnimationUtil;
import com.bleyl.recurrence.utils.DateAndTimeUtil;
import com.bleyl.recurrence.utils.TextFormatUtil;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.R.attr.text;
import static com.bleyl.recurrence.R.id.toolbar;
import static java.security.AccessController.getContext;

/**
 * Created by Lau Jun Ning on 22-May-17.
 */

public class AutoAddNotification extends AppCompatActivity {


    boolean Repeat = false;
    private String icon;
    private String colour;
    private Calendar c;
    private boolean[] daysOfWeek = new boolean[7];
    private int timesShown = 0;
    private int timesToShow = 1;
    private int repeatType;
    private int id;
    private int interval = 1;
    private String TITLE_I_WANT;
    private String CONTENT_I_WANT;
    private String DATETIME_I_WANT;
    private String ContactPerson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        ButterKnife.bind(this);

        DatabaseHelper database = DatabaseHelper.getInstance(this);
        id = database.getLastNotificationId() + 1;
        database.close();


        icon = getString(R.string.default_icon_value);
        colour = getString(R.string.default_colour_value);
        repeatType = Reminder.DOES_NOT_REPEAT;



       Bundle b = getIntent().getExtras();


        if(b!= null)
        {
            TITLE_I_WANT = (String)b.get("TITLE_I_NEED");
            CONTENT_I_WANT = (String)b.get("CONTENT_I_NEED");
            DATETIME_I_WANT = (String)b.get("DATETIME_I_NEED");
            ContactPerson = (String)b.get("CONTACT_I_NEED");

//            Log.i("TAG","********************* TITLE_I_WANT>>>>>>>>>>>>>> :"+TITLE_I_WANT);
//            Log.i("TAG","********************* CONTENT_I_WANT>>>>>>>>>>>>>> :"+CONTENT_I_WANT);
//            Log.i("TAG","********************* DATETIME_I_WANT>>>>>>>>>>>>>> :"+DATETIME_I_WANT);

            //Comparing old notification
            DatabaseHelper db = DatabaseHelper.getInstance(getApplicationContext());
            List<Reminder> reminderList = database.getNotificationList(1);
            database.close();

            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            DateTime dt = formatter.parseDateTime(DATETIME_I_WANT); // You get a DateTime object
            dt=  dt.minusHours(8);
            // Create a new formatter with the pattern you want
            DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyyMMddHHmm");
            String dateStringInYourFormat = formatter2.print(dt); // format the DateTime to that pattern

            for(int q=0; q<reminderList.size(); q++){

               String gdt = reminderList.get(q).getDateAndTime();
                String ttl = reminderList.get(q).getTitle();
                Log.i("TAG","********************* gdt>>>>>>>>>>>>>> :"+gdt);
                Log.i("TAG","********************* ttl>>>>>>>>>>>>>> :"+ttl);
                Log.i("TAG","********************* dateStringInYourFormat>>>>>>>>>>>>>> :"+dateStringInYourFormat);
                Log.i("TAG","********************* tTITLE_I_WANTtl>>>>>>>>>>>>>> :"+TITLE_I_WANT);


                if(Objects.equals(dateStringInYourFormat, gdt) && Objects.equals(TITLE_I_WANT, ttl)){
                    Repeat = true;
                    //            Log.i("TAG","********************* CONTENT_I_WANT>>>>>>>>>>>>>> :"+CONTENT_I_WANT);
//            Log.i("TAG","********************* DATETIME_I_WANT>>>>>>>>>>>>>> :"+DATETIME_I_WANT);
                }

            }


            if(Repeat == false) {
                Log.i("TAG","********************* tTITLE_I_WANTtl>>>>>>>>>>>>>> :"+Repeat);
                saveNotification();
            }else{
                Repeat = false;
            }

        }else
        {
            Toast.makeText(getApplicationContext(), "There is no passing of title and content", Toast.LENGTH_LONG).show();
        }




    }



    public void saveNotification() {

//        String dateTime = "08/20/2017 08:00:00";
//        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
//        DateTime dt = dtf.parseDateTime(dateTime); // You get a DateTime object


        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        DateTime dt = formatter.parseDateTime(DATETIME_I_WANT); // You get a DateTime object
         dt=  dt.minusHours(8);
        // Create a new formatter with the pattern you want
        DateTimeFormatter formatter2 = DateTimeFormat.forPattern("yyyyMMddHHmm");
        String dateStringInYourFormat = formatter2.print(dt); // format the DateTime to that pattern

//        Toast.makeText(this,"GRAB WHAT I CAN ! >>>"+ dateStringInYourFormat,Toast.LENGTH_LONG).show();
//        Calendar c = Calendar.getInstance();
//        c.setTime(newDate);


        try {
            c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
//            c.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            c.setTime(sdf.parse(dateStringInYourFormat));// all done
        }catch(Exception e){
            e.printStackTrace();
        }

//            String test = "201708071800";
//            Date d = new SimpleDateFormat("yyyyMMddHHmm").parse(test);
//            Log.i("TAG",Long.toString(d.getTime())+" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+test);

//        Calendar c = Calendar.getInstance();
//        Date now = c.getTime();
//        c.setTime(now);
//        c.add(Calendar.DATE,1);
//        now = c.getTime();
//
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
//        String formattedDate = df.format(now);

        if (repeatType == Reminder.DOES_NOT_REPEAT) {
            timesToShow = timesShown + 1;
        }

        DatabaseHelper database = DatabaseHelper.getInstance(this);
        Reminder reminder = new Reminder()
                .setId(id)
                .setTitle(TITLE_I_WANT)
                .setContent(ContactPerson)
                .setDateAndTime(dateStringInYourFormat)
                .setRepeatType(repeatType)
                .setForeverState("False")
                .setNumberToShow(timesToShow)
                .setNumberShown(timesShown)
                .setIcon(icon)
                .setColour(colour)
                .setInterval(interval);

        database.addNotification(reminder);

        if (repeatType == Reminder.SPECIFIC_DAYS) {
            reminder.setDaysOfWeek(daysOfWeek);
            database.addDaysOfWeek(reminder);
        }

        database.close();
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        c.set(Calendar.SECOND, 0);
        AlarmUtil.setAlarm(this, alarmIntent, reminder.getId(), c);
        finish();
    }
}

