package com.example.android.cyclebuddy.ui;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.TextView;

import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.helpers.SearchResultsAdapter;
import com.example.android.cyclebuddy.model.OfferedRoute;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {} interface
 * to handle interaction events.
 * Use the {@link SearchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchListFragment extends Fragment implements
        SearchResultsAdapter.SearchResultsAdapterListener{

   // private OnFragmentInteractionListener mListener;
   @BindView(R.id.results_recycler_view) RecyclerView mRecyclerView;
   @BindView(R.id.rv_empty_view) TextView mEmptyView;
   private FragmentManager fm;
   private RecyclerView.LayoutManager mLayoutManager;
   private SearchResultsAdapter mAdapter;

    private static final String DATASET_1 = "dataSet1";
    private static final String SELECTED_ROUTE = "selectedRoute";

    private ArrayList<OfferedRoute> foundRoutesList;

    public SearchListFragment() {
        // Required empty public constructor
    }

    public static SearchListFragment newInstance() {
        SearchListFragment fragment = new SearchListFragment();
        return fragment;
    }

    public static SearchListFragment newInstance(ArrayList<OfferedRoute> foundRoutes) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(DATASET_1, foundRoutes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getFragmentManager();
        if (getArguments() != null) {
            foundRoutesList = getArguments().getParcelableArrayList(DATASET_1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(Html.fromHtml("<font color='#FFFFFF'> Search </font>"));

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);
        ButterKnife.bind(this,view);

        //set up recyclerView
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(foundRoutesList == null || foundRoutesList.size() == 0){
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            //set dataset to adapter
            mAdapter = new SearchResultsAdapter(getContext(), foundRoutesList, this);
            //mAdapter.setStepsForNextView(mSteps);
            //set adapter to recycler view
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;

    }

    @Override
    public void onClickMethod(ArrayList<OfferedRoute> dataset, int position) {
        OfferedRoute selectedRoute = dataset.get(position);

        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_ROUTE, selectedRoute);

        android.app.Fragment ssFragment = SearchSplashFragment.newInstance();
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        ssFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, ssFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
