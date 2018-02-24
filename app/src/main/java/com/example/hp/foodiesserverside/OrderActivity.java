package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hp.foodiesserverside.ViewHolder.OrderViewHolder;
import com.example.hp.foodiesserverside.model.Order;
import com.example.hp.foodiesserverside.model.Requests;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class OrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Requests, OrderViewHolder> firebaseRecyclerAdapter;
    DatabaseReference databaseReference;
    MaterialSpinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
        recyclerView.setAdapter(firebaseRecyclerAdapter);

        loadOrders();
    }

    private void loadOrders() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, OrderViewHolder>(
                Requests.class,
                R.layout.order_item,
                OrderViewHolder.class,
                databaseReference
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Requests model, int position) {

                viewHolder.orderName.setText(model.name);
                viewHolder.orderPhone.setText(model.phone);
                viewHolder.orderStatus.setText(convertcodeToStatus(model.status));
                viewHolder.orderAddress.setText(model.address);
            }
        };

        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @NonNull
    private String convertcodeToStatus(String status) {
        if (status.equals("0"))
            return "Order Placed";
        else if (status.equals("1"))
            return "On My Way!";
        else
            return "Shipped";

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if (item.getItemId() == 0) {
            showUpdateDialog(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey(), firebaseRecyclerAdapter.getItem(item.getOrder()));

        } else {
            deleteOrder(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {

        databaseReference.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Requests item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please Choose status");

        View view = this.getLayoutInflater().inflate(R.layout.update_order_layout, null);
        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed", "On my Way", "Shipped");


        alertDialog.setView(view);
        final String localKey = key;

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                ;
                item.status = String.valueOf(spinner.getSelectedIndex());
                databaseReference.child(localKey).setValue(item);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();

    }
}
