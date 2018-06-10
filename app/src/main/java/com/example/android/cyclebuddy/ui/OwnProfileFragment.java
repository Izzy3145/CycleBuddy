package com.example.android.cyclebuddy.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.cyclebuddy.R;

public class OwnProfileFragment extends Fragment{

    public OwnProfileFragment(){}

    public static OwnProfileFragment newInstance(){
        OwnProfileFragment ownProfileFragment = new OwnProfileFragment();
        return ownProfileFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}
