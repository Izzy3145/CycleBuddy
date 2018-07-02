package com.example.android.cyclebuddy.ui;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.adapters.MessageListAdapter;
import com.example.android.cyclebuddy.model.MessageSummary;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * { MessageListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MessageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessageListFragment extends Fragment implements
        MessageListAdapter.MessageListAdapterListener{

    @BindView(R.id.messages_overview_recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.rv_messages_empty_view)
    TextView mEmptyView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MessageListAdapter mAdapter;
    private ArrayList<MessageSummary> messageList;
    private FragmentManager fm;


    //TODO: connect this to the database, make this a list fragment, implementing onClickListener
    //TODO: and leading to message detail depending on userID
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public MessageListFragment() {
        // Required empty public constructor
    }

    public static MessageListFragment newInstance() {
        MessageListFragment fragment = new MessageListFragment();
        return fragment;
    }


//    // TODO: Put list of historical MessageSummary objects in here
//    public static MessageListFragment newInstance(String param1, String param2) {
//        MessageListFragment fragment = new MessageListFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getActivity().getFragmentManager();
        MessageSummary onlyMessageSummary = new MessageSummary(null, "Jake Goodman", "I love you, you're great");
        messageList = new ArrayList<MessageSummary>();
        messageList.add(onlyMessageSummary);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);
        ButterKnife.bind(this,view);

        //set up recyclerView
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(messageList == null || messageList.size() == 0){
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            mAdapter = new MessageListAdapter(getContext(), messageList, this);
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }

    @Override
    public void onClickMethod(ArrayList<MessageSummary> dataset, int position) {
        android.app.Fragment convoFragment = ConversationFragment.newInstance();
        FragmentTransaction fragmentTransaction=fm.beginTransaction();
        //ssFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_container, convoFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
/*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    *//**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     *//*
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
