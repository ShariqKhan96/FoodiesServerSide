package com.example.hp.foodiesserverside.ViewHolder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.foodiesserverside.R;
import com.example.hp.foodiesserverside.model.Banner;

import java.util.ArrayList;

/**
 * Created by hp on 3/3/2018.
 */

public class BannerViewHolder extends RecyclerView.ViewHolder implements
        View.OnCreateContextMenuListener,
        View.OnClickListener {

    public TextView foodName;
    public ImageView foodImage;
    private ArrayList<Banner> arrayList = new ArrayList<>();


    private View view;

    public BannerViewHolder(View itemView, ArrayList<Banner> arrayList) {
        super(itemView);
        this.arrayList = arrayList;
        foodImage = itemView.findViewById(R.id.food_image);
        foodName = itemView.findViewById(R.id.food_namme);

        itemView.setOnClickListener(this);

        itemView.setOnCreateContextMenuListener(this);
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Perform");
        Banner banner = arrayList.get(getAdapterPosition());

        menu.add(0, Integer.parseInt(banner.id), getAdapterPosition(), "Delete");

        //menu.add(0, 1, getAdapterPosition(), "Add");
    }
}