package com.example.hp.foodiesserverside.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.foodiesserverside.Interface.ItemClickListener;
import com.example.hp.foodiesserverside.R;

/**
 * Created by hp on 2/21/2018.
 */

public class MenuViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener,
        View.OnClickListener {

    public TextView foodName;
    public ImageView foodImage;
    private ItemClickListener itemClickListener;

    private View view;

    public MenuViewHolder(View itemView) {
        super(itemView);
        foodImage = itemView.findViewById(R.id.food_image);
        foodName = itemView.findViewById(R.id.food_namme);

        itemView.setOnClickListener(this);
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
        menu.add(0,0,getAdapterPosition(), "Delete");
//        menu.add(0,1,getAdapterPosition(), "Add Category");
    }
}
