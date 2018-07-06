package com.example.android.cyclebuddy.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.helpers.AreasOfLondon;
import com.example.android.cyclebuddy.model.OfferedRoute;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

//fragment that allows user to search for routes/buddies available, querying the database

public class SearchFragment extends Fragment implements View.OnClickListener {

    private static String TAG = "LOG_TAG_FOR_SEARCH";
    @BindView(R.id.sf_search_button)
    Button sfSearchButton;
    @BindView(R.id.search_from_edit_text)
    AutoCompleteTextView sfFromEditText;
    @BindView(R.id.search_to_edit_text)
    AutoCompleteTextView sfToEditText;
    private FragmentManager fm;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mOfferDatabaseReference;
    private String searchFrom;
    private String searchTo;
    private String mCurrentUserID;
    private ArrayList<OfferedRoute> foundRoutesList;
    private InterstitialAd mInterstitialAd;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getFragmentManager();
        //get reference to the database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mOfferDatabaseReference = mFirebaseDatabase.getReference();
        mCurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //initialise ad
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, view);

        sfSearchButton.setOnClickListener(this);

        setUpAutoCompleteTextView();

        return view;
    }

    @Override
    public void onClick(View view) {
        //data validation
        if (sfFromEditText.getText().toString().equals("") || sfToEditText.getText().toString().equals("")) {
            //data validation
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please populate all fields");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
            //get the searched route
            searchFrom = sfFromEditText.getText().toString();
            searchTo = sfToEditText.getText().toString();

            //search the database for the searched route
            mOfferDatabaseReference.child("Offered").orderByChild("from").equalTo(searchFrom)
                    .addListenerForSingleValueEvent(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    foundRoutesList = new ArrayList<OfferedRoute>();
                                    for (DataSnapshot routesSnapshot : dataSnapshot.getChildren()) {
                                        OfferedRoute foundRoute = routesSnapshot.getValue(OfferedRoute.class);
                                        if (foundRoute != null && !foundRoute.getUserID().equals(mCurrentUserID)) {
                                            if (foundRoute.getTo().equals(searchTo)) {
                                                foundRoutesList.add(foundRoute);
                                            }
                                        }
                                        Log.d(TAG, "no of records of the search is " + foundRoutesList.size());
                                    }

                                    android.app.Fragment slFragment = SearchListFragment.newInstance(foundRoutesList);
                                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, slFragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
            }
        }

        private void setUpAutoCompleteTextView () {
            //get list of all areas and postcodes in London
            String[] areasOfLondon = AreasOfLondon.areasAndPostcodes;
            // Create the adapter and set it to the AutoCompleteTextView
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, areasOfLondon);
            sfFromEditText.setAdapter(adapter);
            sfToEditText.setAdapter(adapter);
        }
    }
