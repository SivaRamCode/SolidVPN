package com.siva.vpn.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.siva.vpn.R;
import com.siva.vpn.models.ServerDetail;
import java.util.List;

public class LocationRecyclerAdapter extends RecyclerView.Adapter<LocationRecyclerAdapter.LocationRecyclerViewHolder> {

    private List<ServerDetail> S_Data = null;
    private OnItemListener itemListener;
    private Context context;

    public LocationRecyclerAdapter(Context context, List<ServerDetail> serverDetails, OnItemListener listener)
    {
        S_Data = serverDetails;
        itemListener = listener;
        this.context=context;
    }



    @NonNull
    @Override
    public LocationRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.location_list_item,parent,false);
        return new LocationRecyclerViewHolder(view, itemListener);
    }


    @Override
    public void onBindViewHolder(@NonNull LocationRecyclerViewHolder holder, int position) {


        holder.LocationName.setText(S_Data.get(position).getServername());
        if(S_Data.get(position).getIsPaid().equals("true")) {
            holder.isPaid_tv.setVisibility(View.VISIBLE);
        }else{
            holder.isPaid_tv.setVisibility(View.GONE);
            holder.signal_iv.setImageResource(R.drawable.medium_signals);
        }

        if(S_Data.get(position).getIsDefault().toLowerCase().equals("true")){
            holder.Flag.setImageResource(R.drawable.f_0);
        }else {
            Glide.with(context).load(S_Data.get(position).getServerflag())
                    .placeholder(R.drawable.f_0)
                    .into(holder.Flag);
        }

        if(DataManager.ADMOB_ENABLE && S_Data.get(position).getReward_server().equals("true")){
            holder.ads_ll.setVisibility(View.VISIBLE);
        }else{
            holder.ads_ll.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return S_Data.size();
    }

    public class LocationRecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView  Flag,isPaid_tv,signal_iv;
        TextView LocationName;
        LinearLayout ads_ll;
        public LocationRecyclerViewHolder(@NonNull View itemView, final OnItemListener listener) {
            super(itemView);
            Flag = (ImageView)itemView.findViewById(R.id.country_flag);
            signal_iv = (ImageView)itemView.findViewById(R.id.signal_iv);
            isPaid_tv = (ImageView)itemView.findViewById(R.id.free_iv);
            ads_ll = (LinearLayout) itemView.findViewById(R.id.ads_ll);
            LocationName = (TextView)itemView.findViewById(R.id.location_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        listener.OnItemClick(getAdapterPosition());



                }
            });

        }
    }

    public interface OnItemListener{
        void OnItemClick(int index);
    }

}
