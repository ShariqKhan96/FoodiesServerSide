package com.example.hp.foodiesserverside.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hp.foodiesserverside.R;
import com.example.hp.foodiesserverside.OrderDetails;
import com.example.hp.foodiesserverside.model.Category;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by hp on 2/13/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private ArrayList<Category> arrayList = new ArrayList<>();
    private Context context;

    public CategoryAdapter(ArrayList<Category> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        return new CategoryAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Category object = arrayList.get(position);
        holder.foodName.setText(object.getName());
        Picasso.with(context).load(object.getImage()).into(holder.foodImage);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrderDetails.class);
                intent.putExtra("foodKey", object.getId());
                context.startActivity(intent);
            }
        });
        holder.view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Select action");
                menu.add(0,Integer.valueOf(object.getId()),holder.getAdapterPosition(), "Update");
                menu.add(0,Integer.valueOf(object.getId()),holder.getAdapterPosition(), "Delete");



            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView foodName;
        private ImageView foodImage;
        private View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            foodImage = itemView.findViewById(R.id.food_image);
            foodName = itemView.findViewById(R.id.food_namme);
        }


    }
}
