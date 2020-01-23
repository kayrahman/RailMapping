package neel.com.jatra.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import neel.com.jatra.R;
import neel.com.jatra.model.OnPopularPlaceItemClick;
import neel.com.jatra.model.PopularPlace;

public class PopularPlaceRecyclerAdapter extends RecyclerView.Adapter<PopularPlaceRecyclerAdapter.PopularPlaceViewHolder> {

    private ArrayList<PopularPlace> mPopularPlaces;
    private OnPopularPlaceItemClick mOnPopularPlaceItemClick;

    private Context mContext;

    public PopularPlaceRecyclerAdapter(ArrayList<PopularPlace> popularPlaces, Context context,OnPopularPlaceItemClick onPopularPlaceItemClick) {
        mPopularPlaces = popularPlaces;
        mOnPopularPlaceItemClick = onPopularPlaceItemClick;
        mContext = context;
    }




    @NonNull
    @Override
    public PopularPlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_popular_places,null,false);

        PopularPlaceViewHolder holder = new PopularPlaceViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PopularPlaceViewHolder holder, int position) {

        holder.bindViewHolder(position);

    }

    @Override
    public int getItemCount() {
        return mPopularPlaces.size();
    }

    public class PopularPlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mPlaceTitleTv;
        TextView mPlaceDescTv;
        ImageView mImageView;
        int mIndex;

        public PopularPlaceViewHolder(View itemView) {
            super(itemView);

            mPlaceTitleTv = itemView.findViewById(R.id.tv_list_item_popular_places_title);
            mPlaceDescTv = itemView.findViewById(R.id.tv_list_item_popular_places_desc);
            mImageView = itemView.findViewById(R.id.iv_list_item_popular_places);

            itemView.setOnClickListener(this);
        }


        public void bindViewHolder(int position){

            mIndex = position;
            mPlaceTitleTv.setText(mPopularPlaces.get(position).getTitle());
            mPlaceDescTv.setText(mPopularPlaces.get(position).getDescription());

            Picasso.get()
                    .load(mPopularPlaces.get(position).getPhoto_utl())
                    .into(mImageView);


        }

        @Override
        public void onClick(View view) {
            mOnPopularPlaceItemClick.onPopularPlaceItemClick(mIndex);
        }
    }


}
