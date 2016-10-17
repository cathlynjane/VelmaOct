package velmalatest.garciano.com.velmalatest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.PersonBuffer;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by jeanneviegarciano on 8/10/2016.
 */
public class OnboardingFragment3 extends Fragment implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, ResultCallback<People.LoadPeopleResult> {

    GoogleApiClient google_api_client;
    GoogleApiAvailability google_api_availability;
    SignInButton signIn_btn;
    private static final int SIGN_IN_CODE = 0;
    private static final int PROFILE_PIC_SIZE = 120;
    private ConnectionResult connection_result;
    private boolean is_intent_inprogress;
    private boolean is_signInBtn_clicked;
    private int request_code;
    ProgressDialog progress_dialog;


    View rootView;
    public static TextView text;
    public static Button inviteFriends;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.onboarding_screen3, container, false);

        buidNewGoogleApiClient();

        text = (TextView)rootView.findViewById(R.id.invite);
        inviteFriends = (Button) rootView.findViewById(R.id.invitepeople);
        inviteFriends.setOnClickListener(this);

        return rootView;


    }

    private void buidNewGoogleApiClient(){

        google_api_client =  new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();
    }


    @Override
    public void onConnected(Bundle bundle) {
Plus.PeopleApi.loadVisible(google_api_client, null).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        google_api_client.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {
            google_api_availability.getErrorDialog(getActivity(), result.getErrorCode(),request_code).show();
            return;
        }

        if (!is_intent_inprogress) {

            connection_result = result;

            if (is_signInBtn_clicked) {

                resolveSignInError();
            }
        }

    }


    private void resolveSignInError() {
        if (connection_result.hasResolution()) {
            try {
                is_intent_inprogress = true;
                connection_result.startResolutionForResult(getActivity(), SIGN_IN_CODE);
                Log.d("resolve error", "sign in error resolved");
            } catch (IntentSender.SendIntentException e) {
                is_intent_inprogress = false;
                google_api_client.connect();
            }
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
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
                Intent intent = new Intent( getActivity(),FriendActivity.class);
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


   /*
    Perform background operation asynchronously, to load user profile picture with new dimensions from the modified url
    */

    private class LoadProfilePic extends AsyncTask<String, Void, Bitmap> {
        ImageView bitmap_img;

        public LoadProfilePic(ImageView bitmap_img) {
            this.bitmap_img = bitmap_img;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap new_icon = null;
            try {
                InputStream in_stream = new java.net.URL(url).openStream();
                new_icon = BitmapFactory.decodeStream(in_stream);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return new_icon;
        }

        protected void onPostExecute(Bitmap result_img) {

            bitmap_img.setImageBitmap(result_img);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == inviteFriends) {
//            Toast.makeText(getActivity(), "G+ Friend List", Toast.LENGTH_LONG).show();
            Plus.PeopleApi.loadVisible(google_api_client, null)
                    .setResultCallback(this);
//            Toast.makeText(getActivity(), "G+ Friend List", Toast.LENGTH_LONG).show();
        }

    }

}
