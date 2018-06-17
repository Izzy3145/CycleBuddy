package com.example.android.cyclebuddy.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.cyclebuddy.R;
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
public class OfferFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.of_offer_button) Button ofOfferButton;
    @BindView(R.id.offer_from_edit_text) EditText ofFromEditText;
    @BindView(R.id.offer_via_edit_text) EditText ofViaEditText;
    @BindView(R.id.offer_to_edit_text) EditText ofToEditText;
    @BindView(R.id.of_duration_edit_text) EditText ofDurationEditText;


    FragmentManager fm;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOfferDatabaseReference;

    private String mFrom;
    private String mTo;
    private String mVia;
    private int mDuration;
    private String mSharedPrefUserID;


//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    private String mParam1;
//    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public OfferFragment() {
        // Required empty public constructor
    }

    public static OfferFragment newInstance() {
        OfferFragment fragment = new OfferFragment();
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *  param1 Parameter 1.
     *  param2 Parameter 2.
     * @return A new instance of fragment OfferFragment.
     */
//    public static OfferFragment newInstance(String param1, String param2) {
//        OfferFragment fragment = new OfferFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getFragmentManager();
        //get reference to the database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        //get a reference to the correct child in the database
        mOfferDatabaseReference = mFirebaseDatabase.getReference().child("Offered");
        //get userID from sharedpreferences
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        mSharedPrefUserID = sharedPreferences.getString("USER_ID","");
        if (mSharedPrefUserID.equals("") ){
        } else {
            ofFromEditText.setText(mSharedPrefUserID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        ButterKnife.bind(this, view);
        ofOfferButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        //save offered route to database
        mFrom = ofFromEditText.getText().toString();
        mVia = ofViaEditText.getText().toString();
        mTo = ofToEditText.getText().toString();
        mDuration = Integer.parseInt(ofDurationEditText.getText().toString());

        OfferedRoute newOfferedRoute = new OfferedRoute(mFrom, mVia, mTo, mDuration);
        mOfferDatabaseReference.push().setValue(newOfferedRoute);

        //and then display the offer splash fragment
        Fragment osFragment = OfferSplashFragment.newInstance();
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, osFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /*// TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
