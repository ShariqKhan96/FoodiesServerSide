package com.example.hp.foodiesserverside.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hp.foodiesserverside.R;
import com.example.hp.foodiesserverside.model.Order;

import java.util.List;

/**
 * Created by hp on 2/28/2018.
 */

class MyViewHolder extends RecyclerView.ViewHolder {
    TextView prodouctName, productQuantity, productPrice, productDiscount;

    public MyViewHolder(View itemView) {
        super(itemView);
        prodouctName = itemView.findViewById(R.id.product_name);
        productDiscount = itemView.findViewById(R.id.procut_discount);
        productQuantity = itemView.findViewById(R.id.product_quantity);
        productPrice = itemView.findViewById(R.id.procut_price);
    }
}

public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<Order> myOrders;

    public OrderDetailAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_item_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Order order = myOrders.get(position);
        holder.productQuantity.setText("Quantity : "+order.quantity);
        holder.productPrice.setText("Price : "+order.price);
        holder.productDiscount.setText("Discount : " +order.discount);
        holder.prodouctName.setText("Name : "+order.name);

    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
