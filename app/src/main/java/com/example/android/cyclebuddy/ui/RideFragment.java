package com.example.android.cyclebuddy.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.cyclebuddy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

//the opening or home fragment, with buttons leading to SearchFragment and OfferFragment

public class RideFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.rf_search_button)
    Button searchButton;
    @BindView(R.id.rf_offer_button)
    Button offerButton;
    private FragmentManager fm;
    private OnNavigationItemChanged mCallback;

    public RideFragment() {
        // Required empty public constructor
    }

    public static RideFragment newInstance() {
        RideFragment fragment = new RideFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getFragmentManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ride, container, false);
        ButterKnife.bind(this, view);
        searchButton.setOnClickListener(this);
        offerButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rf_search_button:
                mCallback.changeHighlightedIcon(R.id.navigation_search);
                break;
            case R.id.rf_offer_button:
                mCallback.changeHighlightedIcon(R.id.navigation_offer);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationItemChanged) {
            mCallback = (OnNavigationItemChanged) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnNavigationItemChanged {
        void changeHighlightedIcon(int menuItemId);
    }
}
