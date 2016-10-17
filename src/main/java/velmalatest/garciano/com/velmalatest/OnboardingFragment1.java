package velmalatest.garciano.com.velmalatest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

/**
 * Created by jeanneviegarciano on 8/10/2016.
 */
public class OnboardingFragment1 extends Fragment implements View.OnClickListener{

//    private static final int RESULT_OK = 0;
    View rootView;
    public static TextView des;
    public static EditText descrip;
    public static TextView loc;
    public static EditText locate;
    int PLACE_PICKER_REQUEST = 1;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.onboarding_screen1, container, false);

        des = (TextView)rootView.findViewById(R.id.description);
        descrip = (EditText)rootView.findViewById(R.id.descriptionText);
        loc = (TextView)rootView.findViewById(R.id.location);
        locate = (EditText)rootView.findViewById(R.id.locationText);
        locate.setHintTextColor(getResources().getColor(R.color.colorPrimary));
        locate.setOnClickListener(this);

        return rootView;


    }


    @Override
    public void onClick(View view) {
        if(view == locate)
        {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            Intent intent;

            try {
                intent = builder.build(getActivity());
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == PLACE_PICKER_REQUEST)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                Place place = PlacePicker.getPlace(data, getActivity());
                String address = String.format("%s", place.getAddress());
                locate.setText(address);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
