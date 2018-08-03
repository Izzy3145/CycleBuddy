package com.riseapp.android.cyclebuddy.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.riseapp.android.cyclebuddy.R;

import butterknife.BindView;
import butterknife.ButterKnife;

//fragment that thanks the user for offering to be a buddy

public class OfferSplashFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.whole_offer_splash)
    ConstraintLayout wholeOfferSplash;
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
