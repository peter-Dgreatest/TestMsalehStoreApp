package com.itcrusaders.msaleh.Adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.itcrusaders.msaleh.database.InventoryDAO;
import com.itcrusaders.msaleh.R;

import java.util.ArrayList;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryViewHolder> implements Filterable {


    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    ArrayList<InventoryDAO> import_items,import_items_filtered;
    Context mContext;


    public InventoryAdapter(Context mContext,
                            ArrayList<InventoryDAO> import_items,
                            InventoryAdapterOnClickHandler clickHandler) {
        this.import_items = new ArrayList<>();
        this.import_items=import_items;
        this.import_items_filtered=import_items;
        this.handler = clickHandler;
        this.mContext=mContext;
    }

    public interface InventoryAdapterOnClickHandler{
        void clickHandler(String Params);
    }

    InventoryAdapterOnClickHandler handler;

    @NonNull
    @Override
    public InventoryAdapter.InventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();


        int layoutIdforItem = R.layout.inventory_loader;

        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;

        return new InventoryAdapter.InventoryViewHolder(inflater.inflate(layoutIdforItem,parent,
                shouldAttachToParentImmediately));
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryAdapter.InventoryViewHolder holder, int position) {
        InventoryDAO importItemsDAO = import_items_filtered.get(position);
        // Log.e("Filtered : ",import_items_filtered.get(position).getProduct_name()+";;");

        holder.TVProductName.setText(import_items_filtered.get(position).getName());
        holder.Tvbarcode.setText(importItemsDAO.getBarcode());
        holder.Tvbranch.setText(importItemsDAO.getBranch());
        holder.TvDate.setText(importItemsDAO.getDateEntered());
        if(importItemsDAO.getStock().equalsIgnoreCase("0")) {
            holder.Tvstock.setBackgroundColor(Color.parseColor("#FF12A519"));
            holder.Tvstock.setText("in stock");
        }else{
            holder.Tvstock.setText("out of stock");
            holder.Tvstock.setBackgroundResource(R.drawable.buttonshapebackground2);
        }


        holder.LayoutContainer.setTag(importItemsDAO.getBarcode());

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
                    ArrayList<InventoryDAO> filteredlist = new ArrayList<>();
                    for (InventoryDAO row : import_items) {
                        if (row.getBarcode().toLowerCase().contains(charString.toLowerCase())
                                || row.getBranch().toLowerCase().contains(charString.toLowerCase())
                                || row.getName().toLowerCase().contains(charString.toLowerCase())) {
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
                import_items_filtered = (ArrayList<InventoryDAO>) results.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };

    }


    public void setMessageData(ArrayList<InventoryDAO> import_items){
        //this.import_items= new ArrayList<>();
        this.import_items = import_items;
        this.import_items_filtered = import_items;
        notifyDataSetChanged();
    }

    public class InventoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView TvDate;
        TextView TVProductName, Tvbarcode,Tvbranch,Tvstock;
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
