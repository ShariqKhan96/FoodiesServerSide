package com.example.hp.foodiesserverside.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.foodiesserverside.Interface.ItemClickListener;
import com.example.hp.foodiesserverside.R;

import info.hoang8f.widget.FButton;

public class ShipperViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener,
        View.OnClickListener {

    public TextView name;
    public TextView phno;
    public ImageView active;
    private ItemClickListener itemClickListener;
  public  FButton assign;

    public ShipperViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        phno = itemView.findViewById(R.id.phno);
        active = itemView.findViewById(R.id.active);
        assign = itemView.findViewById(R.id.assign);
        assign.setOnClickListener(this);
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
        menu.setHeaderTitle("Perform");
        menu.add(0, 0, getAdapterPosition(), "Activate");
        menu.add(0, 1, getAdapterPosition(), "Deactivate");

    }
}
