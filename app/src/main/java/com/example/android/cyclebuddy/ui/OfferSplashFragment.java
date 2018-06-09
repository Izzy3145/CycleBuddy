package com.example.android.cyclebuddy.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.cyclebuddy.R;

public class OfferSplashFragment extends Fragment{

    public OfferSplashFragment() {
        // Required empty public constructor
    }

    public static OfferSplashFragment newInstance() {
        OfferSplashFragment fragment = new OfferSplashFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offer_splash, container, false);
    }
}
