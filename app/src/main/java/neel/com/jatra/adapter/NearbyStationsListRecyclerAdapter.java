package neel.com.jatra.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import neel.com.jatra.R;
import neel.com.jatra.model.StationInfo;

public class NearbyStationsListRecyclerAdapter extends RecyclerView.Adapter<NearbyStationsListRecyclerAdapter.NearbyStationsListViewHolder> {

    private ArrayList<StationInfo> mStationsArrayList;

    public NearbyStationsListRecyclerAdapter(ArrayList<StationInfo> stationsArrayList) {
        mStationsArrayList = stationsArrayList;
    }

    @NonNull
    @Override
    public NearbyStationsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stations,null,false);

        NearbyStationsListViewHolder holder = new NearbyStationsListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyStationsListViewHolder holder, int position) {

        holder.bindViewHolder(position);
    }

    @Override
    public int getItemCount() {

        return mStationsArrayList.size();

    }

    public class NearbyStationsListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mStationTitle;
        private TextView mStationDistanceTv;

        public NearbyStationsListViewHolder(View itemView) {
            super(itemView);

            mStationTitle = itemView.findViewById(R.id.tv_list_item_stations_title);
            mStationDistanceTv = itemView.findViewById(R.id.tv_list_item_stations_distance);

            itemView.setOnClickListener(this);

        }


        public void bindViewHolder(int position){

                String distanceMetric;
                mStationTitle.setText(mStationsArrayList.get(position).getTitle());

                float distance = mStationsArrayList.get(position).getDistance();

                if (distance > 1000) {
                    distanceMetric = String.valueOf(distance / 1000) + "km";
                } else {
                    distanceMetric = String.valueOf(distance) + "m";
                }

                mStationDistanceTv.setText(distanceMetric);

            }

        @Override
        public void onClick(View view) {



        }
    }

}
