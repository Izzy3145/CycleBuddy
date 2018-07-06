package com.example.android.cyclebuddy.ui;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.ViewProfileActivity;
import com.example.android.cyclebuddy.adapters.SearchResultsAdapter;
import com.example.android.cyclebuddy.model.OfferedRoute;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

//fragment that shows a list of found routes (following a search), as returned from the database

public class SearchListFragment extends Fragment implements
        SearchResultsAdapter.SearchResultsAdapterListener {

    @BindView(R.id.results_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.rv_empty_view)
    TextView mEmptyView;
    private FragmentManager fm;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchResultsAdapter mAdapter;
    private ArrayList<OfferedRoute> foundRoutesList;

    private static final String DATASET_1 = "dataSet1";
    private static final String SELECTED_ROUTE = "selectedRoute";
    private static final String PASSED_BUNDLE = "passed bundle";

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);
        ButterKnife.bind(this, view);

        //set up recyclerView
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (foundRoutesList == null || foundRoutesList.size() == 0) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mAdapter = new SearchResultsAdapter(getContext(), foundRoutesList, this);
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;

    }

    @Override
    public void onClickMethod(ArrayList<OfferedRoute> dataset, int position) {
        //open View Profile
        OfferedRoute selectedRoute = dataset.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_ROUTE, selectedRoute);
        Intent openProfileActivity = new Intent(getActivity(), ViewProfileActivity.class);
        openProfileActivity.putExtra(PASSED_BUNDLE, bundle);
        startActivity(openProfileActivity);

    }
}
