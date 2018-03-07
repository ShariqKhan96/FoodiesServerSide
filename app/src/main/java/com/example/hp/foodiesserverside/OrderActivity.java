package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hp.foodiesserverside.Common.Common;
import com.example.hp.foodiesserverside.Remote.APIService;
import com.example.hp.foodiesserverside.ViewHolder.OrderViewHolder;
import com.example.hp.foodiesserverside.model.MyResponse;
import com.example.hp.foodiesserverside.model.Notification;
import com.example.hp.foodiesserverside.model.Requests;
import com.example.hp.foodiesserverside.model.Sender;
import com.example.hp.foodiesserverside.model.Token;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import retrofit2.Call;
import retrofit2.Callback;

public class OrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Requests, OrderViewHolder> firebaseRecyclerAdapter;
    DatabaseReference databaseReference;
    MaterialSpinner spinner;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order);

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        mService = Common.getFCMClient();


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
            protected void populateViewHolder(OrderViewHolder viewHolder, final Requests model, final int position) {

                viewHolder.orderName.setText(model.name);
                viewHolder.orderPhone.setText(model.phone);
                viewHolder.orderStatus.setText(convertcodeToStatus(model.status));
                viewHolder.orderAddress.setText(model.address);

                viewHolder.showDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(OrderActivity.this, OrderDetails.class);
                        intent.putExtra("orderId", firebaseRecyclerAdapter.getRef(position).getKey());

                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });

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

                notifyOrderStatusToUser(localKey, item);
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

    private void notifyOrderStatusToUser(final String localKey, final Requests item) {
        Log.e("localKey", localKey);
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens");
        tokenRef.orderByKey().equalTo(item.phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnaps : dataSnapshot.getChildren()) {
                    Token token = new Token(postSnaps.child("token").getValue().toString(),
                            Boolean.valueOf(postSnaps.child("isServerToken").getValue().toString()));
                    Notification notification = new Notification(String.
                            format("Your order '%s' status was updated by '%s' to '%s'", localKey, Home.PHONE, item.status), "Shariq Khan");
                    Sender content = new Sender(token.token, notification);
                    Log.e("SenderName", content.to);


                    mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                        @Override
                        public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                            Log.e("Retrofit Response!", "Hello inside");
                            if (response.body().success == 1) {
                                Toast.makeText(OrderActivity.this, "Order updated successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(OrderActivity.this, "Order updated but failed to notify!", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("RETROFITERROR", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
