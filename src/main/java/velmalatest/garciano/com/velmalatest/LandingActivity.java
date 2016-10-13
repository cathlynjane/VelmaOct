package velmalatest.garciano.com.velmalatest;

/**
 * Created by jeanneviegarciano on 7/20/2016.
 */


import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.tibolte.agendacalendarview.AgendaCalendarView;
import com.github.tibolte.agendacalendarview.CalendarManager;
import com.github.tibolte.agendacalendarview.CalendarPickerController;
import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.github.tibolte.agendacalendarview.models.DayItem;
import com.github.tibolte.agendacalendarview.models.WeekItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import velmalatest.garciano.com.velmalatest.apiclient.DrawableCalendarEvent;
import velmalatest.garciano.com.velmalatest.apiclient.DrawableEventRenderer;
import velmalatest.garciano.com.velmalatest.apiclient.MyEvent;
import velmalatest.garciano.com.velmalatest.apiclient.Test;

//import com.alamkanak.weekview.DateTimeInterpreter;
//import com.alamkanak.weekview.MonthLoader;
//import com.alamkanak.weekview.WeekView;
//import com.alamkanak.weekview.WeekViewEvent;


public class LandingActivity extends AppCompatActivity implements CalendarPickerController, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<People.LoadPeopleResult> {

    //WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener

    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;


    private boolean is_signInBtn_clicked;
    private int request_code;
    private FloatingActionButton fabButton;

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private static final int CREATE_EVENT = 0;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;



//    private WeekView mWeekView;
//
//    List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();


    FloatingActionButton fab;

    AgendaCalendarView mAgendaCalendarView;
    List<CalendarEvent> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildNewGoogleApiClient();
        setContentView(R.layout.activity_activity_landing);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        FirebaseMessaging.getInstance().subscribeToTopic("Food");
        FirebaseInstanceId.getInstance().getToken();


        new LandingActivity.sendNotification().execute();

        mAgendaCalendarView = (AgendaCalendarView) findViewById(R.id.agenda_calendar_view);

        // getSupportActionBar().setDisplayShowTitleEnabled(false);

//        Calendar cal = Calendar.getInstance();
//        Intent intent = new Intent(Intent.ACTION_EDIT);
//        intent.setType("vnd.android.cursor.item/event");
//        intent.putExtra("beginTime", cal.getTimeInMillis());
//        intent.putExtra("allDay", true);
//        intent.putExtra("rrule", "FREQ=YEARLY");
//        intent.putExtra("endTime", cal.getTimeInMillis() + 60 * 60 * 1000);
//        intent.putExtra("title", "A Test Event from android app");
//        startActivity(intent);

//        mWeekView = (WeekView) findViewById(R.id.weekView);
//        // Show a toast message about the touched event.
//        mWeekView.setOnEventClickListener(this);
//        // The week view has infinite scrolling horizontally. We have to provide the events of a
//        // month every time the month changes on the week view.
//        mWeekView.setMonthChangeListener(this);
//        // Set long press listener for events.
//        mWeekView.setEventLongPressListener(this);
//        // Set up a date time interpreter to interpret how the date and time will be formatted in
//        // the week view. This is optional.

        fab = (FloatingActionButton) findViewById(R.id.fabButton);

        fab.setOnClickListener(this);

        //setupDateTimeInterpreter(false);


        //region AgendaCalendarView
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();

        //minDate - this line of code kay mao ni if pila ka months before karun na month ganahan ka na imu e display.
        minDate.add(Calendar.MONTH, -7);
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        maxDate.add(Calendar.YEAR, 1);

//        List<CalendarEvent> eventList = new ArrayList<>();
        mockList(eventList);
//
        mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);

//
//        CalendarManager calendarManager = CalendarManager.getInstance(getApplicationContext());
//        calendarManager.buildCal(minDate, maxDate, Locale.getDefault(), new DayItem(), new WeekItem());
//        calendarManager.loadEvents(eventList, new BaseCalendarEvent());
//
//
//        List<CalendarEvent> readyEvents = calendarManager.getEvents();
//        List<DayItem> readyDays = calendarManager.getDays();
//        List<WeekItem> readyWeeks = calendarManager.getWeeks();
//       mAgendaCalendarView.init(Locale.getDefault(), readyWeeks, readyDays, readyEvents, this);
//        mAgendaCalendarView.addEventRenderer(new DrawableEventRenderer());


        //endregion
    }

    // region Interface - CalendarPickerController
    @Override
    public void onDaySelected(DayItem dayItem) {

    }

    @Override
    public void onEventSelected(final CalendarEvent event) {

        //this toast display the details of the event
        Toast.makeText(getApplicationContext(), String.format("Selected event: %s", event /*event.getTitle()*/), Toast.LENGTH_LONG).show();

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setIcon(R.drawable.alarm);
        alertBuilder.setTitle("Are you sure you want to delete this event?");

        alertBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        alertBuilder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
//                eventList.remove(event);

                dialog.dismiss();
            }
        });
        alertBuilder.show();
//          Log.d(LOG_TAG, String.format("Selected event: %s", event));
    }

    @Override
    public void onScrollToDate(Calendar calendar) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
        }
    }

    // endregion

    private void mockList(List<CalendarEvent> eventList) {

        Calendar startTime1 = Calendar.getInstance();
        Calendar endTime1 = Calendar.getInstance();
        endTime1.add(Calendar.DAY_OF_YEAR, 0);
        BaseCalendarEvent event1 = new BaseCalendarEvent("Event 1", "A wonderful journey!", "Mambaling",
                ContextCompat.getColor(this, R.color.colorPrimary), startTime1, endTime1, true);
        eventList.add(event1);

//        Calendar startTime2 = Calendar.getInstance();
//        startTime2.add(Calendar.DAY_OF_YEAR, 1);
//        Calendar endTime2 = Calendar.getInstance();
//        endTime2.add(Calendar.DAY_OF_YEAR, 1);
//        BaseCalendarEvent event2 = new BaseCalendarEvent("Event 2", "A beautiful small town", "Basak Pardo",
//                ContextCompat.getColor(this, R.color.colorPrimaryDark), startTime2, endTime2, true);
//        eventList.add(event2);

        // Example on how to provide your own layout
        Calendar startTime3 = Calendar.getInstance();
        Calendar endTime3 = Calendar.getInstance();
        startTime3.set(Calendar.HOUR_OF_DAY, 14);
        startTime3.set(Calendar.MINUTE, 0);
        endTime3.set(Calendar.HOUR_OF_DAY, 15);
        endTime3.set(Calendar.MINUTE, 0);
        DrawableCalendarEvent event3 = new DrawableCalendarEvent("Event 3", "", "Pardo",
                ContextCompat.getColor(this, R.color.colorAccent), startTime3, endTime3, false, R.drawable.common_ic_googleplayservices);
        eventList.add(event3);

        Calendar startTime4 = Calendar.getInstance();
        Calendar endTime4 = Calendar.getInstance();
        endTime4.add(Calendar.DATE, 0);
//        endTime4.add(Calendar.AUGUST, 1);
        BaseCalendarEvent event4 = new BaseCalendarEvent("Blessed", "heyy!", "cebu",
                ContextCompat.getColor(this, R.color.colorPrimary), startTime4, endTime4, true);
        eventList.add(event4);
    }

    private void buildNewGoogleApiClient() {

        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }

    protected void onStart() {
        super.onStart();
//        google_api_client.connect();

    }

    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    protected void onResume() {
        super.onResume();
        if (google_api_client.isConnected()) {
            google_api_client.connect();

        }
    }


    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        prefs.edit().putBoolean("isLoggedIn", true).commit();
//        Intent i = new Intent(LandingActivity.this, LandingActivity.class);
//        startActivity(i);
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        google_api_client.connect();

    }

    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();

            }
        }
    }


    @Override
    public void onClick(View view) {

        // Toast.makeText(getBaseContext(), "" + view, Toast.LENGTH_LONG).show();

        if (view == fab) {
            Intent intent = new Intent(LandingActivity.this, AddEventActivity.class);
            startActivity(intent);

//            Calendar minDate = Calendar.getInstance();
//            Calendar maxDate = Calendar.getInstance();
//
//            //minDate - this line of code kay mao ni if pila ka months before karun na month ganahan ka na imu e display.
//            minDate.add(Calendar.MONTH, -7);
//            minDate.set(Calendar.DAY_OF_MONTH, 1);
//            maxDate.add(Calendar.YEAR, 1);
//
//            Calendar startTime2 = Calendar.getInstance();
//            startTime2.add(Calendar.DAY_OF_YEAR, 1);
//            Calendar endTime2 = Calendar.getInstance();
//            endTime2.add(Calendar.DAY_OF_YEAR, 1);
//            BaseCalendarEvent event2 = new BaseCalendarEvent("Event 2", "A beautiful small town", "Basak Pardo",
//                    ContextCompat.getColor(this, R.color.colorPrimaryDark), startTime2, endTime2, true);
//            eventList.add(event2);
//
//            mAgendaCalendarView.init(eventList, minDate, maxDate, Locale.getDefault(), this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        // Check which request we're responding to
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_signInBtn_clicked = false;

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }

        if (requestCode == CREATE_EVENT) {

            Toast.makeText(getBaseContext(), "Here0", Toast.LENGTH_SHORT).show();
            MyEvent myevent = null;

            if (responseCode == RESULT_OK) {

                Toast.makeText(getBaseContext(), "Here1", Toast.LENGTH_SHORT).show();

                Bundle res = intent.getExtras();
                String name = res.getString("name");
                String eventDescription = res.getString("eventDescription");
                String eventLocation = res.getString("eventLocation");
                String startDate = res.getString("startDate");
                String startTime = res.getString("startTime");
                String endDate = res.getString("endDate");
                String endTime = res.getString("param_result");
                String notify = res.getString("endTime");

                myevent = new MyEvent(name, eventDescription, eventLocation, startDate, endDate, startTime, endTime, notify);

                Date sdate = null;
                Date edate = null;
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    sdate = format.parse(startDate);
                    edate = format.parse(endDate);
                } catch (Exception e) {

                }

                //String intMonth = (String) android.text.format.DateFormat.format("MM", date); //06
                //String year = (String) android.text.format.DateFormat.format("yyyy", date); //2013


                Calendar stime = Calendar.getInstance();
                stime.set(Calendar.HOUR_OF_DAY, 3);
                stime.set(Calendar.MINUTE, 0);
                stime.set(Calendar.MONTH, 9 - 1);
                stime.set(Calendar.YEAR, 2016);
                Calendar etime = Calendar.getInstance();
                etime.add(Calendar.HOUR, 1);
                etime.set(Calendar.MONTH, 9 - 1);
                //  WeekViewEvent event = new WeekViewEvent(1, name, stime, etime);
                //event.setColor(getResources().getColor(R.color.event_color_01));
                //   events.add(event);


//                Calendar sttime = Calendar.getInstance();
//                sttime.set(Calendar.HOUR_OF_DAY, 3);
//                sttime.set(Calendar.MINUTE, 30);
//                sttime.set(Calendar.MONTH, 9 - 1);
//                sttime.set(Calendar.YEAR, 2016);
//                Calendar ettime = (Calendar) sttime.clone();
//                ettime.set(Calendar.HOUR_OF_DAY, 4);
//                ettime.set(Calendar.MINUTE, 30);
//                ettime.set(Calendar.MONTH, 9 - 1);
//                event = new WeekViewEvent(10, getEventTitle(sttime), sttime, ettime);
//                event.setColor(Color.parseColor("#000000"));
//                events.add(event);


                // Create a new event.
                //  WeekViewEvent event = new WeekViewEvent(20, name, sdate, edate);
                //  events.add(event);


                // Refresh the week view. onMonthChange will be called again.
                //    mWeekView.notifyDatasetChanged();

            }

        }

    }


    //    private void buidNewGoogleApiClient() {
//
//        google_api_client = new GoogleApiClient.Builder(this)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(Plus.API, Plus.PlusOptions.builder().build())
//                .addScope(Plus.SCOPE_PLUS_LOGIN)
//                .addScope(Plus.SCOPE_PLUS_PROFILE)
//                .build();
//    }
//
//
//
//    protected void onStart() {
//        super.onStart();
//        google_api_client.connect();
//
//    }
//
//    protected void onStop() {
//        super.onStop();
//        if (google_api_client.isConnected()) {
//            google_api_client.disconnect();
//        }
//    }
//
//    protected void onResume(){
//        super.onResume();
//        if (google_api_client.isConnected()) {
//            google_api_client.connect();
//        }
//    }
//
//
//    @Override
//    public void onConnectionFailed(ConnectionResult result) {
//        if (!result.hasResolution()) {
//            google_api_availability.getErrorDialog(this, result.getErrorCode(), request_code).show();
//            return;
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int responseCode,
//                                    Intent intent) {
//        // Check which request we're responding to
//        if (requestCode == SIGN_IN_CODE) {
//            request_code = requestCode;
//            if (responseCode != RESULT_OK) {
//                is_signInBtn_clicked = false;
//            }
//
//            is_intent_inprogress = false;
//
//            if (!google_api_client.isConnecting()) {
//                google_api_client.connect();
//            }
//        }
//
//    }
//
//
//    private void gPlusSignOut() {
//        if (google_api_client.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(google_api_client);
//            google_api_client.disconnect();
//            google_api_client.connect();
//
//        }
//    }
//
//    @Override
//    public void onConnected(Bundle arg0) {
//        is_signInBtn_clicked = false;
//
//    }
//
//    @Override
//    public void onConnectionSuspended(int arg0) {
//        google_api_client.connect();
//
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //  setupDateTimeInterpreter(id == R.id.action_monthly_view);
//
//
////        if(id == R.id.action_logout){
////        gPlusSignOut();
////        }

//        switch (id) {
////            case R.id.action_logout: {
////
////                gPlusSignOut();
////                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
////                prefs.edit().putBoolean("isLoggedOut", false).apply();
////                prefs.edit().putBoolean("isLoggedIn", false).apply();
////                Intent i = new Intent(LandingActivity.this, LoginActivity.class);
////                startActivity(i);
////
////
////                return true;
////            }
//            case R.id.action_monthly_view: {
//                return true;
//            }
//            case R.id.action_today: {
//                //  mWeekView.goToToday();
//                return true;
//            }
//            case R.id.action_day_view: {
////                if (mWeekViewType != TYPE_DAY_VIEW) {
////                    item.setChecked(!item.isChecked());
////                    mWeekViewType = TYPE_DAY_VIEW;
////                    mWeekView.setNumberOfVisibleDays(1);
////
////                    // Lets change some dimensions to best fit the view.
////                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
////                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
////                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
////                }
//
//                return true;
//            }
//            case R.id.action_three_day_view:
////                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
////                    item.setChecked(!item.isChecked());
////                    mWeekViewType = TYPE_THREE_DAY_VIEW;
////                    mWeekView.setNumberOfVisibleDays(3);
////
////                    // Lets change some dimensions to best fit the view.
////                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
////                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
////                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
////                }
//                return true;
//            case R.id.action_week_view:
////                if (mWeekViewType != TYPE_WEEK_VIEW) {
////                    item.setChecked(!item.isChecked());
////                    mWeekViewType = TYPE_WEEK_VIEW;
////                    mWeekView.setNumberOfVisibleDays(7);
////
////                    // Lets change some dimensions to best fit the view.
////                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
////                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
////                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
////                }
//                return true;
//        }
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_logout) {
////            gPlusSignOut();
////            Intent logoutIntent = new Intent(LandingActivity.this, LoginActivity.class);
////            startActivity(logoutIntent);
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    private void setupDateTimeInterpreter(final boolean shortDate) {
//        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
//            @Override
//            public String interpretDate(Calendar date) {
//                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
//                String weekday = weekdayNameFormat.format(date.getTime());
//                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());
//
//                // All android api level do not have a standard way of getting the first letter of
//                // the week day name. Hence we get the first char programmatically.
//                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
//                if (shortDate)
//                    weekday = String.valueOf(weekday.charAt(0));
//                return weekday.toUpperCase() + format.format(date.getTime());
//            }
//
//            @Override
//            public String interpretTime(int hour) {
//                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
//            }
//        });
//    }


//    @Override
//    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

    // Populate the week view with some events.


    // Toast.makeText(getBaseContext(), "Hi", Toast.LENGTH_LONG).show();

//        Calendar startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth - 1);
//        startTime.set(Calendar.YEAR, newYear);
//        Calendar endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR, 1);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        WeekViewEvent event = new WeekViewEvent(1, getEventTitle(startTime), startTime, endTime);
//        //event.setColor(getResources().getColor(R.color.event_color_01));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 4);
//        endTime.set(Calendar.MINUTE, 30);
//        endTime.set(Calendar.MONTH, newMonth-1);
//        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 4);
//        startTime.set(Calendar.MINUTE, 20);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.set(Calendar.HOUR_OF_DAY, 5);
//        endTime.set(Calendar.MINUTE, 0);
//        event = new WeekViewEvent(10, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_03));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 30);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 2);
//        endTime.set(Calendar.MONTH, newMonth-1);
//        event = new WeekViewEvent(2, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.HOUR_OF_DAY, 5);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        startTime.add(Calendar.DATE, 1);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        endTime.set(Calendar.MONTH, newMonth - 1);
//        event = new WeekViewEvent(3, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_03));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 15);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(4, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_04));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, 1);
//        startTime.set(Calendar.HOUR_OF_DAY, 3);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_01));
//        events.add(event);
//
//        startTime = Calendar.getInstance();
//        startTime.set(Calendar.DAY_OF_MONTH, startTime.getActualMaximum(Calendar.DAY_OF_MONTH));
//        startTime.set(Calendar.HOUR_OF_DAY, 15);
//        startTime.set(Calendar.MINUTE, 0);
//        startTime.set(Calendar.MONTH, newMonth-1);
//        startTime.set(Calendar.YEAR, newYear);
//        endTime = (Calendar) startTime.clone();
//        endTime.add(Calendar.HOUR_OF_DAY, 3);
//        event = new WeekViewEvent(5, getEventTitle(startTime), startTime, endTime);
//        event.setColor(getResources().getColor(R.color.event_color_02));
//        events.add(event);

//        return events;
//    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

//    @Override
//    public void onEventClick(WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
//        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onEmptyViewLongPress(Calendar time) {
//        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
//    }
//
//    public WeekView getWeekView() {
//        return mWeekView;
//    }


    private void gPlusSignOut() {
        if (google_api_client.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(google_api_client);
            google_api_client.disconnect();


//                Auth.GoogleSignInApi.signOut(google_api_client).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(Status status) {
////                                Plus.AccountApi.clearDefaultAccount(google_api_client);
////                                google_api_client.disconnect();
//                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
//                                prefs.edit().putBoolean("isLoggedIn", false).commit();
//                                Intent intent = new Intent(LandingActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                            }
//                        });

        }
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        if (!connectionResult.hasResolution()) {
            google_api_availability.getErrorDialog(this, connectionResult.getErrorCode(), request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

//            connection_result = result;

//            if (is_signInBtn_clicked) {
//
//                resolveSignInError();
//            }
        }

    }


    @Override
    public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {

    }

    public class sendNotification extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("topic","Food")
                    .add("message","Hello.")
                    .add("title","Trial Velma")
//                .add("from","Jeanne")
                    .build();

            Request request = new Request.Builder()
                    //  .addHeader("Accept", "application/json")// .addHeader("Content-Type", "text/html")
                    // .url("http://192.168.197.1/fcmphp/register.php?")
                    .url("http://dev2-commit.mybudgetload.com:8282/mpa_api/send_notification.asp")
                    .post(body)
                    .build();
            Log.d("sd", "sd" + request);
            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;

        }

    }
}
