package com.example.android.cyclebuddy.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
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
import com.example.android.cyclebuddy.model.OfferedRoute;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { OfferFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OfferFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OfferFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.of_offer_button)
    Button ofOfferButton;
    @BindView(R.id.offer_from_edit_text)
    AutoCompleteTextView ofFromEditText;
    @BindView(R.id.offer_via_edit_text)
    AutoCompleteTextView ofViaEditText;
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
    private String mVia;
    private int mDuration;
    private String mSharedPrefUserID;

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
        //get reference to the database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //get a reference to the correct child in the database
        mOfferDatabaseReference = mFirebaseDatabase.getReference().child("Offered");

        //get userID from sharedpreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPrefUserID = sharedPreferences.getString(getString(R.string.preference_user_ID),
                "unsuccessful");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getActivity().setTitle(Html.fromHtml("<font color='#FFFFFF'> Offer </font>"));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        ButterKnife.bind(this, view);
        ofOfferButton.setOnClickListener(this);

        //get list of all areas and postcodes in London
        String[] areasOfLondon = AreasOfLondon.areasAndPostcodes;
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, areasOfLondon);
        ofFromEditText.setAdapter(adapter);
        ofViaEditText.setAdapter(adapter);
        ofToEditText.setAdapter(adapter);

        return view;
    }

    @Override
    public void onClick(View view) {
        //save offered route to database
        mFrom = ofFromEditText.getText().toString();
        mVia = ofViaEditText.getText().toString();
        mTo = ofToEditText.getText().toString();
        mDuration = Integer.parseInt(ofDurationEditText.getText().toString());

        OfferedRoute newOfferedRoute = new OfferedRoute(mFrom, mVia, mTo, mDuration, mSharedPrefUserID);
        mOfferDatabaseReference.push().setValue(newOfferedRoute);

        if (returnToggle.isChecked()) {
            OfferedRoute offeredReturnRoute = new OfferedRoute(mTo, mVia, mFrom, mDuration, mSharedPrefUserID);
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
