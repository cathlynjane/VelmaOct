package velmalatest.garciano.com.velmalatest;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

/**
 * Created by jeanneviegarciano on 8/16/2016.
 */
public class TutorialActivity extends FragmentActivity {

    private ViewPager pager;
    private SmartTabLayout indicator;
    private Button skipbutton;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tutorial);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //prefs.edit().putBoolean("isFirstRun", true).commit();
        Boolean isFirstRun = prefs.getBoolean("isFirstRun", false);
        prefs.edit().putBoolean("isFirstRun", true).commit();

        showNotification();

        pager = (ViewPager)findViewById(R.id.pager);
        indicator = (SmartTabLayout)findViewById(R.id.indicator);
        skipbutton = (Button)findViewById(R.id.skip);
        next = (Button)findViewById(R.id.next);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0 : return new TutorialFragment1();
                    case 1 : return new TutorialFragment2();
                    case 2 : return new TutorialFragment3();
                    default: return null;
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
                if(position == 2){
                    skipbutton.setVisibility(View.GONE);
                    next.setText("Done");
                }
                else {
                    skipbutton.setVisibility(View.VISIBLE);
                    next.setText("Next");
                }
            }

        });

        skipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishOnboarding();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pager.getCurrentItem() == 2){
                    finishOnboarding();
                } else {
                    pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                }
            }
        });
    }

    private void finishOnboarding() {
//        SharedPreferences preferences =
//                getSharedPreferences("my_preferences", MODE_PRIVATE);
//
//        preferences.edit()
//                .putBoolean("onboarding_complete",false).apply();

        Intent main = new Intent(this, LoginActivity.class);
        startActivity(main);

        finish();
    }

    private void showNotification() {
        String eventDescription = "Manage your time in a smartest way";

        android.support.v4.app.NotificationCompat.BigTextStyle bigStyle = new android.support.v4.app.NotificationCompat.BigTextStyle();
        bigStyle.bigText(eventDescription);

        Notification notification = new android.support.v4.app.NotificationCompat.Builder(getApplication())
                .setSmallIcon(R.drawable.velmalogo)
                .setContentTitle("Welcome to Velma")
                .setContentText(eventDescription).setStyle(bigStyle)
                .extend(new android.support.v4.app.NotificationCompat.WearableExtender().setHintShowBackgroundOnly(true))
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
        int notificationId = 1;
        notificationManager.notify(notificationId, notification);

    }
}
