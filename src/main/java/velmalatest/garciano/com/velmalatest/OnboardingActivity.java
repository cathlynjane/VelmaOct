package velmalatest.garciano.com.velmalatest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by jeanneviegarciano on 8/10/2016.
 */
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager pager;
    private SmartTabLayout indicator;
    public Button skip;
    public Button BtnAddEvent;
    public EditText event;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        context = this;

        event = (EditText) findViewById(R.id.eventname);
        pager = (ViewPager) findViewById(R.id.pager);
        indicator = (SmartTabLayout) findViewById(R.id.indicator);
        BtnAddEvent = (Button) findViewById(R.id.btnAddEvent);

        BtnAddEvent.setVisibility(View.GONE);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new OnboardingFragment1();
                    case 1:
                        return new OnboardingFragment2();
                    case 2:
                        return new OnboardingFragment3();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };

        pager.setAdapter(adapter);

        indicator.setViewPager(pager);

        indicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    BtnAddEvent.setVisibility(View.GONE);
                } else if (position == 1) {
                    BtnAddEvent.setVisibility(View.GONE);
                } else {
                    BtnAddEvent.setVisibility(View.VISIBLE);
                }

            }

        });


        BtnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String name = event.getText().toString();
                final String eventDescription = OnboardingFragment1.descrip.getText().toString();
                final String eventLocation = OnboardingFragment1.locate.getText().toString();
                final String startDate = OnboardingFragment2.dateStart.getText().toString();
                final String endDate = OnboardingFragment2.dateEnd.getText().toString();
                final String startTime = OnboardingFragment2.timeStart.getText().toString();
                final String endTime = OnboardingFragment2.timeEnd.getText().toString();
                final String notify = OnboardingFragment2.alarming.getText().toString();


                if (name.isEmpty()) {
                    Toast.makeText(context, "Invalid Event Name", Toast.LENGTH_SHORT).show();
                } else if (eventDescription.isEmpty()) {
                    Toast.makeText(context, "Invalid Event Description", Toast.LENGTH_SHORT).show();
                }
// else if (eventLocation.isEmpty()) {
//                    Toast.makeText(context, "Invalid Event Location", Toast.LENGTH_SHORT).show();
//                }
                else if (startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    Toast.makeText(context, "Please add Starting date and completion date.", Toast.LENGTH_SHORT).show();
                } else {
                    //MyEvent myevent = null;
                    //myevent = new MyEvent(name, eventDescription, eventLocation, startDate, endDate, startTime, endTime, notify);

                    Bundle bundle = new Bundle();
                    bundle.putString("name", name);
                    bundle.putString("eventDescription", eventDescription);
                    bundle.putString("eventLocation", eventLocation);
                    bundle.putString("startDate", startDate);
                    bundle.putString("startTime", startTime);
                    bundle.putString("endDate", endDate);
                    bundle.putString("endTime", endTime);
                    bundle.putString("notify", notify);


                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();


                }


            }
        });
    }

}
