package com.example.hp.foodiesserverside.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.foodiesserverside.Interface.ItemClickListener;
import com.example.hp.foodiesserverside.R;

import info.hoang8f.widget.FButton;

/**
 * Created by hp on 2/23/2018.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
        View.OnCreateContextMenuListener {


    public TextView orderName;
    public TextView estimateTime;
    public TextView orderStatus;
    public TextView orderPhone;
    public TextView oderAssignedTo;
    public TextView orderAddress;
    public ImageView showDetails;
    public FButton directions;
    public FButton shipper;
    private ItemClickListener itemClickListener;

    private View view;

    public OrderViewHolder(View itemView) {
        super(itemView);
        orderName = itemView.findViewById(R.id.order_name);
        orderStatus = itemView.findViewById(R.id.order_status);
        estimateTime = itemView.findViewById(R.id.expected_time);
        oderAssignedTo = itemView.findViewById(R.id.order_assigned_to);
        orderAddress = itemView.findViewById(R.id.order_address);
        orderPhone = itemView.findViewById(R.id.order_phone);
        showDetails = itemView.findViewById(R.id.showDetails);
        directions = itemView.findViewById(R.id.directions);
        shipper = itemView.findViewById(R.id.shipper);
        showDetails.setRotation(showDetails.getRotation() - 90);


        directions.setOnClickListener(this);
        shipper.setOnClickListener(this);
        showDetails.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void onItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public void onClick(View v) {

        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Actions");

        // menu.add(0, 0, getAdapterPosition(), "Update");
        menu.add(0, 0, getAdapterPosition(), "Update Status");
        menu.add(0, 1, getAdapterPosition(), "Delivery Time");
        //menu.add(0, 1, getAdapterPosition(), "Cancel");
    }
}
