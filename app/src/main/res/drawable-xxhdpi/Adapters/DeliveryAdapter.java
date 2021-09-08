package com.itcrusaders.msaleh.Adapters;

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

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.InventoryViewHolder> implements Filterable {


    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    ArrayList<DeliveryDAO> import_items, import_items_filtered;
    Context mContext;


    public DeliveryAdapter(Context mContext,
                           ArrayList<DeliveryDAO> import_items,
                           DeliveryAdapter.DeliveryAdapterOnClickHandler clickHandler) {
        this.import_items = new ArrayList<>();
        this.import_items = import_items;
        this.import_items_filtered = import_items;
        this.handler = clickHandler;
        this.mContext = mContext;
    }

    public interface DeliveryAdapterOnClickHandler {
        void clickHandler(String Params);
    }

    DeliveryAdapter.DeliveryAdapterOnClickHandler handler;

    @NonNull
    @Override
    public DeliveryAdapter.InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();


        int layoutIdforItem = R.layout.delivery_loader;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        return new DeliveryAdapter.InventoryViewHolder(inflater.inflate(layoutIdforItem, parent,
                shouldAttachToParentImmediately));
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryAdapter.InventoryViewHolder holder, int position) {
        DeliveryDAO importItemsDAO = import_items_filtered.get(position);
        // Log.e("Filtered : ",import_items_filtered.get(position).getProduct_name()+";;");

        holder.TVProductName.setText(import_items_filtered.get(position).getPickupaddress());
        holder.Tvbarcode.setText(importItemsDAO.getClientname());
        holder.Tvbranch.setText(importItemsDAO.getPickupbranch());
        holder.TvDate.setText(importItemsDAO.getDateCreated());



        holder.LayoutContainer.setTag(position);

    }

    @Override
    public int getItemCount() {
        try {
            return import_items_filtered.size();
        } catch (Exception e) {
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
                        if (row.getClientname().toLowerCase().contains(charString.toLowerCase())
                                || row.getPickupbranch().toLowerCase().contains(charString.toLowerCase())
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


    public void setMessageData(ArrayList<DeliveryDAO> import_items) {
        //this.import_items= new ArrayList<>();
        this.import_items = import_items;
        this.import_items_filtered = import_items;
        notifyDataSetChanged();
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView TvDate;
        TextView TVProductName, Tvbarcode, Tvbranch, Tvstock;
        LinearLayout LayoutContainer;

        public InventoryViewHolder(@NonNull View itemView) {
            super(itemView);
            TvDate = itemView.findViewById(R.id.tv_pdate);
            TVProductName = itemView.findViewById(R.id.tv_inventory_name);
            Tvbranch = itemView.findViewById(R.id.et_product_branch);
            Tvbarcode = itemView.findViewById(R.id.tv_barcode);
            Tvstock = itemView.findViewById(R.id.et_stock_info);
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