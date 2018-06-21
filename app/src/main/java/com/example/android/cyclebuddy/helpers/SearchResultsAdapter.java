package com.example.android.cyclebuddy.helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.android.cyclebuddy.R;
import com.example.android.cyclebuddy.model.OfferedRoute;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private ArrayList<OfferedRoute> mDataset;
    private Context mContext;
    private SearchResultsAdapterListener mClickHandler;

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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        OfferedRoute offeredRoute = mDataset.get(position);
        holder.resultFromTv.setText(offeredRoute.getFrom());
        holder.resultToTv.setText(offeredRoute.getTo());
        holder.resultDurationTv.setText(String.valueOf(offeredRoute.getDuration()));
        holder.resultUserIDTv.setText(offeredRoute.getUserID());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

//    public void setStepsForNextView(List<Step> selectedSteps){
//        mSteps = selectedSteps;
//        notifyDataSetChanged();
//    }

    //create interface that requires onClick Method to be implemented
    public interface SearchResultsAdapterListener {
        void onClickMethod(ArrayList<OfferedRoute> dataset, int position);
    }

    //create custom viewholder
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.result_from) TextView resultFromTv;
        @BindView(R.id.result_to) TextView resultToTv;
        @BindView(R.id.result_duration) TextView resultDurationTv;
        @BindView(R.id.result_userID) TextView resultUserIDTv;

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
