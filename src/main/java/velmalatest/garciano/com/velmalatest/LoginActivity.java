package velmalatest.garciano.com.velmalatest;

/**
 * Created by jeanneviegarciano on 7/20/2016.
 */


import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People.LoadPeopleResult;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import velmalatest.garciano.com.velmalatest.apiclient.User;


public class LoginActivity extends AppCompatActivity implements OnConnectionFailedListener, View.OnClickListener, ConnectionCallbacks, ResultCallback<LoadPeopleResult> {

    // These declarations are for google plus sign in
    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    SignInButton signIn_btn;
    private static final int SIGN_IN_CODE = 0;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    ProgressDialog progress_dialog;
    // up to here

    Context mcontext;

    //These are the declarations for getting the token for push notification implementation
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressBar mRegistrationProgressBar;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;
    //the declaration for push notif ends here

//     private Firebase mRootRef;
//    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this function calls the google_api_client
        buidNewGoogleApiClient();

        setContentView(R.layout.activity_main);
        //Customize sign-in button.a red button may be displayed when Google+ scopes are requested
        custimizeSignBtn();
        //handles the onclick for sign in button
        setBtnClickListeners();
        progress_dialog = new ProgressDialog(this);
        progress_dialog.setMessage("Signing in....");


//        FirebaseMessaging.getInstance().subscribeToTopic("Food");
//        FirebaseInstanceId.getInstance().getToken();

        // sendNotification();
//        new sendNotification().execute();
//        mRootRef = new Firebase("https://velma-143505.firebaseio.com/Users");
//        mDatabase = FirebaseDatabase.getInstance().getReference("https://velma-143505.firebaseio.com/");

//        mcontext = this;
//
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mcontext);
//        Boolean isLoggedIn = prefs.getBoolean("isLoggedIn", true);
//        Boolean isLoggedOut = prefs.getBoolean("isLoggedOut", true);
//
//        if(isLoggedIn){
//            // "Landing";
//            this.finish();
//            Intent i = new Intent(LoginActivity.this,LandingActivity.class);
//            startActivity(i);
//        }
//        else if(isLoggedOut){
//
//            Intent i = new Intent(LoginActivity.this,LoginActivity.class);
//            startActivity(i);
//            // "Login";
//        }


//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(context);
//                boolean sentToken = sharedPreferences
//                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
//                if (sentToken) {
//                    mInformationTextView.setText(getString(R.string.gcm_send_message));
//                } else {
//                    mInformationTextView.setText(getString(R.string.token_error_message));
//                }
//            }
//        };
//
//        registerReceiver();
//
//        if (checkPlayServices()) {
//            // Start IntentService to register this application with GCM.
//            Intent intent = new Intent(this, RegistrationIntentService.class);
//            startService(intent);
//        }
//
    }

    /*
    create and  initialize GoogleApiClient object to use Google Plus Api.
    While initializing the GoogleApiClient object, request the Plus.SCOPE_PLUS_LOGIN scope.
    */

    //These function is for the look of the sign in button
    private void custimizeSignBtn() {
        signIn_btn = (SignInButton) findViewById(R.id.sign_in_button);
        signIn_btn.setSize(SignInButton.SIZE_STANDARD);
        signIn_btn.setScopes(new Scope[]{Plus.SCOPE_PLUS_LOGIN});
    }   //endregion


    //This function is calling the google plus api
    private void buidNewGoogleApiClient() {
        google_api_client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }//endregion

    private void setBtnClickListeners() {
        signIn_btn.setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
    }

    protected void onStart() {
        super.onStart();
        google_api_client.connect();

    }

    protected void onStop() {
        super.onStop();
        if (google_api_client.isConnected()) {
            google_api_client.disconnect();
        }
    }

    protected void onResume() {
        super.onResume();
//        registerReceiver();
        if (google_api_client.isConnected()) {
            google_api_client.connect();
            changeUI(true);
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(this, result.getErrorCode(), request_code).show();
            return;
        }

        if (!is_intent_inprogress) {
            connection_result = result;
            if (is_signInBtn_clicked) {
                resolveSignInError();
            }
        }

    }

    /*
      Will receive the activity result and check which request we are responding to

     */
    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        // Check which request we're responding to
        if (requestCode == SIGN_IN_CODE) {
            request_code = requestCode;
            if (responseCode != RESULT_OK) {
                is_signInBtn_clicked = false;
                progress_dialog.dismiss();

            }

            is_intent_inprogress = false;

            if (!google_api_client.isConnecting()) {
                google_api_client.connect();
            }
        }

    }

    @Override
    public void onConnected(Bundle arg0) {
        is_signInBtn_clicked = false;
        // Get user's information and set it into the layout
        getProfileInfo();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("isLoggedIn", true).commit();
        Intent i = new Intent(LoginActivity.this, LandingActivity.class);
        startActivity(i);

//        getProfileInfo();
//
        // Update the UI after signin
        changeUI(true);

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        google_api_client.connect();
        changeUI(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:

                checkInternetConnection();
                
//                FirebaseMessaging.getInstance().subscribeToTopic("Food");
//                FirebaseInstanceId.getInstance().getToken();
//
//
//                new sendNotification().execute();

//                android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
//                        Settings.Secure.ANDROID_ID);
//                int duration = Toast.LENGTH_LONG;
//                Toast toast = Toast.makeText(getBaseContext(), android_id, duration);
//                toast.show();
                progress_dialog.dismiss();
                break;
        }
    }

    //This function is for checking if the mobile phone has internet connection. The user can't sign in if there's no internet connection.
    private void checkInternetConnection() {
        String answer;

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                answer = "You are connected to a wifi Network";
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG).show();
                gPlusSignIn();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                answer = "You are connected to a Mobile Network";
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG).show();
                gPlusSignIn();
            } else {
                answer = "No internet connectivity";
                Toast.makeText(getApplicationContext(), answer, Toast.LENGTH_LONG).show();
            }


        }
    }//endregion


    //This is for checking if the user is already connected to google plus api.
    private void gPlusSignIn() {
        if (!google_api_client.isConnecting()) {
            /*----------*/
//            google_api_client.connect();
//            Plus.AccountApi.getAccountName(google_api_client);
//            Toast.makeText(getApplicationContext(), emailAddr, Toast.LENGTH_SHORT).show();
            /*----------*/
            Log.d("user connected", "connected");
            is_signInBtn_clicked = true;
            progress_dialog.show();
            resolveSignInError();
//            showNotification();


        }
    }//endregion

    //This is called if there's an error in connecting to google plus api
    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(this, SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();

            }
        }
    }//endregion


    /*
     get user's information name, email, profile pic,Date of birth,tag line and about me
     */

    private void getProfileInfo() {
//        Toast.makeText(this, "start sign process", Toast.LENGTH_SHORT).show();
        try {

            if (Plus.PeopleApi.getCurrentPerson(google_api_client) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(google_api_client);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                String email = Plus.AccountApi.getAccountName(google_api_client);
//                Toast.makeText(getApplicationContext(), personName, Toast.LENGTH_LONG).show();
//                Log.d("Login",personName);
//
                setPersonalInfo(currentPerson);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     set the User information into the views defined in the layout
     */

    private void setPersonalInfo(Person currentPerson) {

        String personName = currentPerson.getDisplayName();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String email = Plus.AccountApi.getAccountName(google_api_client);
        String name = "name";
//        TextView   user_name = (TextView) findViewById(R.id.userName);
//        user_name.setText("Name: "+personName);
//        TextView gemail_id = (TextView)findViewById(R.id.emailId);
//        gemail_id.setText("Email Id: " +email);
        Log.d("Login",personName + email);

//        Firebase childRef = mRootRef.child(name);
//        childRef.setValue(personName);

        
//        String key = mDatabase.child("posts").push().getKey();
//        User user = new User(personName,email);
//        Map<String, Object> postValues = user.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/User" + key, postValues);
//        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

//        mDatabase.updateChildren(childUpdates);
    }

    /*
     By default the profile pic url gives 50x50 px image.
     If you need a bigger image we have to change the query parameter value from 50 to the size you want
    */

//    private void setProfilePic(String profile_pic){
//        profile_pic = profile_pic.substring(0,
//                profile_pic.length() - 2)
//                + PROFILE_PIC_SIZE;
//        ImageView    user_picture = (ImageView)findViewById(R.id.profile_pic);
//        new LoadProfilePic(user_picture).execute(profile_pic);
//    }

    /*
     Show and hide of the Views according to the user login status
     */

    private void changeUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);

//            findViewById(R.id.action_settings).setVisibility(View.VISIBLE);
        } else {

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.fab).setVisibility(View.GONE);
//            findViewById(R.id.action_settings).setVisibility(View.GONE);
        }
    }

    @Override
    public void onResult(LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            ArrayList<String> list = new ArrayList<String>();
            ArrayList<String> img_list= new ArrayList<String>();
            try {
                int count = personBuffer.getCount();

                for (int i = 0; i < count; i++) {
                    list.add(personBuffer.get(i).getDisplayName());
                    img_list.add(personBuffer.get(i).getImage().getUrl());
                }
                Intent intent = new Intent(LoginActivity.this,FriendActivity.class);
                intent.putStringArrayListExtra("friendsName",list);
                intent.putStringArrayListExtra("friendsPic",img_list);
                startActivity(intent);
            } finally {
                personBuffer.release();
            }
        } else {
            Log.e("circle error", "Error requesting visible circles: " + peopleData.getStatus());
        }
    }

//
//   /*
//    Perform background operation asynchronously, to load user profile picture with new dimensions from the modified url
//    */
//
//    private class LoadProfilePic extends AsyncTask<String, Void, Bitmap> {
//        ImageView bitmap_img;
//
//        public LoadProfilePic(ImageView bitmap_img) {
//            this.bitmap_img = bitmap_img;
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String url = urls[0];
//            Bitmap new_icon = null;
//            try {
//                InputStream in_stream = new java.net.URL(url).openStream();
//                new_icon = BitmapFactory.decodeStream(in_stream);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage());
//                e.printStackTrace();
//            }
//            return new_icon;
//        }
//
//        protected void onPostExecute(Bitmap result_img) {
//
//            bitmap_img.setImageBitmap(result_img);
//        }
//    }

    //This if for showing the notification for mobile and android watch
private void showNotification() {
    String eventDescription = "Manage your time in a smartest way";

    android.support.v4.app.NotificationCompat.BigTextStyle bigStyle = new android.support.v4.app.NotificationCompat.BigTextStyle();

    bigStyle.bigText(eventDescription);

    Notification notification = new android.support.v4.app.NotificationCompat.Builder(getApplication())
            .setSmallIcon(R.drawable.velmalogo)
            .setContentTitle("Welcome to Velma")
            .setContentText(eventDescription).setStyle(bigStyle)
            .setDefaults(Notification.DEFAULT_SOUND)
            .setVibrate(new long[]{100,2000,500,2000})
            .extend(new android.support.v4.app.NotificationCompat.WearableExtender().setHintShowBackgroundOnly(true))
            .build();
    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
    int notificationId = 1;
    notificationManager.notify(notificationId, notification);

//    Intent intent = new Intent(LoginActivity.this, LandingActivity.class);
//    startActivity(intent);
//    resolveSignInError();
}

    //This is for push notification codes

//    private void registerReceiver(){
//        if(!isReceiverRegistered) {
//            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
//                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
//            isReceiverRegistered = true;
//        }
//    }

    //push notification code ends here

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    //Checking if phone has google play services ends here



    public class sendNotification extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("topic","Food")
                    .add("message","Welcome to Velma. Manage your time in a smart way.")
                    .add("title","Food Galore")
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
