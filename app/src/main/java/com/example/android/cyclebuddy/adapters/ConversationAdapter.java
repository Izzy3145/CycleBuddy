package com.example.android.cyclebuddy.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.model.Message;
import com.example.android.cyclebuddy.model.MessageSummary;
import com.example.android.cyclebuddy.model.OfferedRoute;
import com.example.android.cyclebuddy.model.UserProfile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private ArrayList<Message> mDataset;
    private Context mContext;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;

    //constructor
    public ConversationAdapter(Context context, ArrayList<Message> dataSet) {
        mContext = context;
        mDataset = dataSet;
    }

    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate layout to contain viewholders on creation
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item,
                parent, false);
        //pass the view to the viewholder
        ConversationAdapter.ViewHolder viewHolder = new ConversationAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message foundMessage = mDataset.get(position);
        holder.messageTv.setText(foundMessage.getMessage());
    }


    @Override
    public int getItemCount() {
        if(mDataset != null){
            return mDataset.size();
        } else {
            return 0;
        }
    }

    //create custom viewholder
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.message)
        TextView messageTv;

        //create ViewHolder constructor
        private ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
