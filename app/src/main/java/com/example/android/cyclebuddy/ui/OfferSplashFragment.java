package com.example.android.cyclebuddy.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.android.cyclebuddy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfferSplashFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.whole_offer_splash) LinearLayout wholeOfferSplash;
    private FragmentManager fm;

    public OfferSplashFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getFragmentManager();
    }

    public static OfferSplashFragment newInstance() {
        OfferSplashFragment fragment = new OfferSplashFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(Html.fromHtml("<font color='#FFFFFF'> Offer A Ride </font>"));
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_splash, container, false);
        ButterKnife.bind(this,view);
        wholeOfferSplash.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        android.app.Fragment slFragment = OfferFragment.newInstance();
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, slFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
