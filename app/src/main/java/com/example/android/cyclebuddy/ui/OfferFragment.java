package com.example.android.cyclebuddy.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.helpers.AreasOfLondon;
import com.example.android.cyclebuddy.helpers.Constants;
import com.example.android.cyclebuddy.model.OfferedRoute;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


//fragment that allows user to offer to be a buddy on a route, saving data to Firebase database

public class OfferFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.of_offer_button)
    Button ofOfferButton;
    @BindView(R.id.offer_from_edit_text)
    AutoCompleteTextView ofFromEditText;
    @BindView(R.id.offer_to_edit_text)
    AutoCompleteTextView ofToEditText;
    @BindView(R.id.of_duration_edit_text)
    EditText ofDurationEditText;
    @BindView(R.id.be_a_buddy)
    TextView ofBeABuddyTv;
    @BindView(R.id.returnToggle)
    ToggleButton returnToggle;

    FragmentManager fm;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOfferDatabaseReference;
    private String mFrom;
    private String mTo;
    private int mDuration;
    private String mSharedPrefUserID;
    private InterstitialAd mInterstitialAd;

    public OfferFragment() {
        // Required empty public constructor
    }

    public static OfferFragment newInstance() {
        OfferFragment fragment = new OfferFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getFragmentManager();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOfferDatabaseReference = mFirebaseDatabase.getReference().child(Constants.OFFERED_PATH);

        //get userID from sharedpreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPrefUserID = sharedPreferences.getString(getString(R.string.preference_user_ID),
                getResources().getString(R.string.unsuccessful));

        //initialise ad
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        ButterKnife.bind(this, view);
        ofOfferButton.setOnClickListener(this);
        setUpAutoCompleteTextView();
        return view;
    }

    @Override
    public void onClick(View view) {
        //data validation
        if (ofFromEditText.getText().toString().equals("") || ofToEditText.getText().toString().equals("")
                || ofDurationEditText.getText().toString().equals("")) {
            //data validation
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.populate));
            builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            //load ad
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                Timber.d("The interstitial wasn't loaded yet.");
            }

            //save offered route to database
            mFrom = ofFromEditText.getText().toString();
            mTo = ofToEditText.getText().toString();
            mDuration = Integer.parseInt(ofDurationEditText.getText().toString());

            OfferedRoute newOfferedRoute = new OfferedRoute(mFrom, mTo, mDuration, mSharedPrefUserID);
            mOfferDatabaseReference.push().setValue(newOfferedRoute);

            if (returnToggle.isChecked()) {
                OfferedRoute offeredReturnRoute = new OfferedRoute(mTo, mFrom, mDuration, mSharedPrefUserID);
                mOfferDatabaseReference.push().setValue(offeredReturnRoute);
            }

            //and then display the offer splash fragment
            Fragment osFragment = OfferSplashFragment.newInstance();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, osFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void setUpAutoCompleteTextView() {
        //get list of all areas and postcodes in London
        String[] areasOfLondon = AreasOfLondon.areasAndPostcodes;
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, areasOfLondon);
        ofFromEditText.setAdapter(adapter);
        ofToEditText.setAdapter(adapter);
    }
}
