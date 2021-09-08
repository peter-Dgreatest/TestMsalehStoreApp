package com.itcrusaders.msaleh.non_admin.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.itcrusaders.msaleh.database.DeliveryDAO;
import com.itcrusaders.msaleh.R;

import java.util.ArrayList;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.StaffDeliveriesViewHolder>
        implements Filterable {

    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    ArrayList<DeliveryDAO> import_items,import_items_filtered;
    Context mContext;


    public DeliveryAdapter(Context mContext,
                           ArrayList<DeliveryDAO> import_items,
                           StaffDeliveriesOnClickHandler clickHandler) {
        this.import_items = new ArrayList<>();
        this.import_items=import_items;
        this.import_items_filtered=import_items;
        this.handler = clickHandler;
        this.mContext=mContext;
    }

    public interface StaffDeliveriesOnClickHandler{
        void clickHandler(String Params);
    }

    StaffDeliveriesOnClickHandler handler;

    @NonNull
    @Override
    public StaffDeliveriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();


        int layoutIdforItem = R.layout.delivery_view;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        return new StaffDeliveriesViewHolder(inflater.inflate(layoutIdforItem,parent,
                shouldAttachToParentImmediately));
    }

    @Override
    public void onBindViewHolder(@NonNull StaffDeliveriesViewHolder holder, int position) {
        DeliveryDAO importItemsDAO = import_items_filtered.get(position);
        // Log.e("Filtered : ",import_items_filtered.get(position).getProduct_name()+";;");

        holder.TvDLocation.setText(import_items_filtered.get(position).getPickupaddress());
        holder.TvReceiver.setText(importItemsDAO.getReceivername());
        holder.TvDate.setText(importItemsDAO.getDateCreated());


        holder.LayoutContainer.setTag(position);

    }

    @Override
    public int getItemCount() {
        try {
            return import_items_filtered.size();
        }catch (Exception e){
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    import_items_filtered = import_items;
                } else {
                    ArrayList<DeliveryDAO> filteredlist = new ArrayList<>();
                    for (DeliveryDAO row : import_items) {
                        if (row.getReceivername().toLowerCase().contains(charString.toLowerCase())
                                || row.getPickupaddress().toLowerCase().contains(charString.toLowerCase())
                                || row.getDateCreated().toLowerCase().contains(charString.toLowerCase())) {
                            filteredlist.add(row);
                        }
                    }
                    import_items_filtered = filteredlist;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = import_items_filtered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                import_items_filtered = (ArrayList<DeliveryDAO>) results.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };

    }


    public void setMessageData(ArrayList<DeliveryDAO> import_items){
        //this.import_items= new ArrayList<>();
        this.import_items = import_items;
        this.import_items_filtered = import_items;
        notifyDataSetChanged();
    }

    class StaffDeliveriesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView TvDate;
        TextView TvDLocation, TvReceiver;
        LinearLayout LayoutContainer;

        public StaffDeliveriesViewHolder(@NonNull View itemView) {
            super(itemView);
            TvDate = itemView.findViewById(R.id.deliveryDateTag);
            TvDLocation = itemView.findViewById(R.id.deliveryLocationTag);
            TvReceiver = itemView.findViewById(R.id.deliveryReceiverTag);
            LayoutContainer = itemView.findViewById(R.id.deliveryDiv);

            LayoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handler.clickHandler(LayoutContainer.getTag().toString());
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            handler.clickHandler(LayoutContainer.getTag().toString());
        }
    }
}
