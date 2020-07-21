package com.example.hp.foodiesserverside.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hp.foodiesserverside.R;
import com.example.hp.foodiesserverside.ViewHolder.BannerViewHolder;
import com.example.hp.foodiesserverside.model.Banner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hp on 3/7/2018.
 */

public class BannerAdapter extends RecyclerView.Adapter<BannerViewHolder> {

    private ArrayList<Banner> arrayList = new ArrayList<>();
    private Context context;

    public BannerAdapter(ArrayList<Banner> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item_view, parent, false);
        return new BannerViewHolder(view, arrayList);

    }

    @Override
    public void onBindViewHolder(BannerViewHolder holder, int position) {

        Banner banner = arrayList.get(position);
        holder.foodName.setText(banner.name);
        Picasso.with(context).load(banner.image).into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
