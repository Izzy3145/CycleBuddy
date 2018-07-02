package com.example.android.cyclebuddy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.ViewProfileActivity;
import com.example.android.cyclebuddy.model.OfferedRoute;

import butterknife.BindView;
import butterknife.ButterKnife;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link SearchSplashFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link SearchSplashFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class SearchSplashFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.whole_search_splash) LinearLayout wholeSearchSplash;
    @BindView(R.id.search_splash_ok) Button splashOkButton;
    private Bundle mReceivedBundle;
    private static final String PASSED_BUNDLE = "passed bundle";


    public SearchSplashFragment() {
        // Required empty public constructor
    }

    public static SearchSplashFragment newInstance() {
        SearchSplashFragment fragment = new SearchSplashFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceivedBundle = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_splash, container, false);
        ButterKnife.bind(this,view);
        wholeSearchSplash.setOnClickListener(this);
        splashOkButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        Intent openProfileActivity = new Intent(getActivity(), ViewProfileActivity.class);
        openProfileActivity.putExtra(PASSED_BUNDLE, mReceivedBundle);
        startActivity(openProfileActivity);
    }

}
