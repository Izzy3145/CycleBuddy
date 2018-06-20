package com.example.android.cyclebuddy.ui;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.helpers.SearchResultsAdapter;
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
   private FragmentManager fm;
   private RecyclerView.LayoutManager mLayoutManager;
   private SearchResultsAdapter mAdapter;

    private static final String DATASET_1 = "dataSet1";
    private String[] arrayListForRv;

    public SearchListFragment() {
        // Required empty public constructor
    }

    public static SearchListFragment newInstance() {
        SearchListFragment fragment = new SearchListFragment();
        return fragment;
    }


    public static SearchListFragment newInstance(String[] dataset) {
        SearchListFragment fragment = new SearchListFragment();
        Bundle args = new Bundle();
        args.putStringArray(DATASET_1, dataset);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getFragmentManager();
        if (getArguments() != null) {
            arrayListForRv = getArguments().getStringArray(DATASET_1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);
        ButterKnife.bind(this,view);

        //set up recyclerview
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //set dataset to adapter
        mAdapter = new SearchResultsAdapter(getContext(), arrayListForRv, this);
        //mAdapter.setStepsForNextView(mSteps);
        //set adapter to recycler view
        mRecyclerView.setAdapter(mAdapter);
        return view;

    }

    @Override
    public void onClickMethod(String[] dataset, int position) {
        android.app.Fragment ssFragment = SearchSplashFragment.newInstance();
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, ssFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }
}
