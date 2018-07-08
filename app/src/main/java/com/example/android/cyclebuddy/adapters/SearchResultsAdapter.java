package com.example.android.cyclebuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.helpers.CircularImageTransform;
import com.example.android.cyclebuddy.helpers.Constants;
import com.example.android.cyclebuddy.model.OfferedRoute;
import com.example.android.cyclebuddy.model.UserProfile;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private ArrayList<OfferedRoute> mDataset;
    private Context mContext;
    private SearchResultsAdapterListener mClickHandler;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private UserProfile mUserProfile;

    //constructor
    public SearchResultsAdapter(Context context, ArrayList<OfferedRoute> dataSet,
                                SearchResultsAdapterListener listener){
        mContext = context;
        mDataset = dataSet;
        mClickHandler = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate layout to contain viewholders on creation
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item,
                parent, false);
        //pass the view to the viewholder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        OfferedRoute offeredRoute = mDataset.get(position);
        holder.resultFromTv.setText(offeredRoute.getFrom());
        holder.resultToTv.setText(offeredRoute.getToString());

        String duration = String.valueOf(offeredRoute.getDuration());
        String journeyTime = mContext.getResources().getString(R.string.journey_time);
        String mins = mContext.getResources().getString(R.string.mins);
        String journeyTimeSummary = journeyTime + duration + mins;
        holder.resultDurationTv.setText(journeyTimeSummary);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference(Constants.USERS_PATH).child(offeredRoute.getUserID());
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference().child(Constants.IMAGES_PATH).child(offeredRoute.getUserID());

        //download all other values
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUserProfile = dataSnapshot.getValue(UserProfile.class);
                //set data to views
                if (mUserProfile != null) {
                    holder.resultUsernameTv.setText(mUserProfile.getUser());

                    if (mUserProfile.getPhotoUrl() == null || mUserProfile.getPhotoUrl().isEmpty()) {
                        Timber.v("No photo saved yet");
                    } else {
                        StorageReference downloadRef = mStorageReference.child(mUserProfile.getPhotoUrl());
                        Glide.with(mContext)
                                .using(new FirebaseImageLoader())
                                .load(downloadRef)
                                .placeholder(R.drawable.ic_add_a_photo)
                                .transform(new CircularImageTransform(mContext))
                                .into(holder.ImageViewRv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    //create interface that requires onClick Method to be implemented
    public interface SearchResultsAdapterListener {
        void onClickMethod(ArrayList<OfferedRoute> dataset, int position);
    }

    //create custom viewholder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.small_image_view_rv)
        ImageView ImageViewRv;
        @BindView(R.id.result_username) TextView resultUsernameTv;
        @BindView(R.id.result_from) TextView resultFromTv;
        @BindView(R.id.result_to) TextView resultToTv;
        @BindView(R.id.result_duration) TextView resultDurationTv;

        //create ViewHolder constructor
        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClickMethod(mDataset, adapterPosition);
        }
    }
}
