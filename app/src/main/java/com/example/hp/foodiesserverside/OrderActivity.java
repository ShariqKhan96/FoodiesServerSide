package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hp.foodiesserverside.Common.Common;
import com.example.hp.foodiesserverside.Interface.ItemClickListener;
import com.example.hp.foodiesserverside.Remote.APIService;
import com.example.hp.foodiesserverside.Remote.RetrofitClient;
import com.example.hp.foodiesserverside.ViewHolder.OrderViewHolder;
import com.example.hp.foodiesserverside.model.MyResponse;
import com.example.hp.foodiesserverside.model.Notification;
import com.example.hp.foodiesserverside.model.Request;
import com.example.hp.foodiesserverside.model.Sender;
import com.example.hp.foodiesserverside.model.Token;
import com.example.hp.foodiesserverside.model.User;
import com.example.hp.foodiesserverside.model.foods;
import com.example.hp.foodiesserverside.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrderActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> firebaseRecyclerAdapter;
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

    @Override
    protected void onStart() {
        super.onStart();
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    private void loadOrders() {
        Utils.getInstance().showLoader(this);
        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(databaseReference, Request.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                options
        ) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false));
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Utils.getInstance().dismissLoader();
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, int position, @NonNull Request model) {

                viewHolder.orderName.setText(model.name);
                viewHolder.orderPhone.setText(model.phone);
                viewHolder.orderStatus.setText(Utils.convertcodeToStatus(model.status));
                viewHolder.orderAddress.setText(model.address);

                if (model.assigned_to != null) {
                    viewHolder.shipper.setVisibility(View.GONE);
                    viewHolder.oderAssignedTo.setVisibility(View.VISIBLE);
                    viewHolder.oderAssignedTo.setText("Assigned To :" + model.assigned_to.getName());
                } else {
                    viewHolder.shipper.setVisibility(View.VISIBLE);
                    viewHolder.oderAssignedTo.setVisibility(View.GONE);
                }
                if (!model.estimate_time.equals("null")) {
                    viewHolder.estimateTime.setVisibility(View.VISIBLE);
                    viewHolder.estimateTime.setText("Delivery Time :" + model.estimate_time);
                    //viewHolder.oderAssignedTo.setVisibility(View.VISIBLE);
                    //viewHolder.oderAssignedTo.setText("Assigned To :" + model.assigned_to.getName());
                } else
                    viewHolder.estimateTime.setVisibility(View.GONE);


                viewHolder.onItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });

                viewHolder.showDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(OrderActivity.this, OrderDetails.class);
                        intent.putExtra("orderId", firebaseRecyclerAdapter.getRef(position).getKey());

                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
                viewHolder.directions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(OrderActivity.this, TrackingOrder.class);
                        Common.currentRequest = model;

                        if (model.punch_status != null) {
                            if (model.punch_status.equals("1")) {
//                            FirebaseDatabase.getInstance().getReference("Shippers").child(model.assigned_to.phone).child("assignments")
//                                    .child(model.orderId)
//                                    .child("status").addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    String value = dataSnapshot.getValue().toString();
//                                    Log.e("value", value);
//                                    if (value.equals("2")) {
//                                        Toast.makeText(OrderActivity.this, "Shipper delivered the parcel. No tracking available.", Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//
//
//                                    if (value.equals("1"))
//                                        startActivity(intent);
//                                    else
//                                        Toast.makeText(OrderActivity.this, "Shipper has not started the trip yet!", Toast.LENGTH_SHORT).show();
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            });
                                startActivity(intent);
                            } else
                                Toast.makeText(OrderActivity.this, "Tracking not available at the moment!", Toast.LENGTH_SHORT).show();

                        } else
                            Toast.makeText(OrderActivity.this, "Tracking not available at the moment!", Toast.LENGTH_SHORT).show();
                    }

                });
                viewHolder.shipper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(OrderActivity.this, ShipperActivity.class);
                        Common.currentRequest = model;
                        Log.e("id", model.orderId + " " + model.latLng);
                        intent.putExtra("from", "assign");
                        startActivity(intent);

                    }
                });

            }

//            @Override
//            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position)
//            {
//
//                viewHolder.orderName.setText(model.name);
//                viewHolder.orderPhone.setText(model.phone);
//                viewHolder.orderStatus.setText(Utils.convertcodeToStatus(model.status));
//                viewHolder.orderAddress.setText(model.address);
//
//                if (model.assigned_to != null) {
//                    viewHolder.shipper.setVisibility(View.GONE);
//                    viewHolder.oderAssignedTo.setVisibility(View.VISIBLE);
//                    viewHolder.oderAssignedTo.setText("Assigned To :" + model.assigned_to.getName());
//                } else {
//                    viewHolder.shipper.setVisibility(View.VISIBLE);
//                    viewHolder.oderAssignedTo.setVisibility(View.GONE);
//                }
//                if (!model.estimate_time.equals("null")) {
//                    viewHolder.estimateTime.setVisibility(View.VISIBLE);
//                    viewHolder.estimateTime.setText("Delivery Time :" + model.estimate_time);
//                    //viewHolder.oderAssignedTo.setVisibility(View.VISIBLE);
//                    //viewHolder.oderAssignedTo.setText("Assigned To :" + model.assigned_to.getName());
//                } else
//                    viewHolder.estimateTime.setVisibility(View.GONE);
//
//
//                viewHolder.onItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//
//                    }
//                });
//
//                viewHolder.showDetails.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent intent = new Intent(OrderActivity.this, OrderDetails.class);
//                        intent.putExtra("orderId", firebaseRecyclerAdapter.getRef(position).getKey());
//
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                    }
//                });
//                viewHolder.directions.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//
//                        Intent intent = new Intent(OrderActivity.this, TrackingOrder.class);
//                        Common.currentRequest = model;
//
//                        if (model.punch_status != null) {
//                            if (model.punch_status.equals("1")) {
////                            FirebaseDatabase.getInstance().getReference("Shippers").child(model.assigned_to.phone).child("assignments")
////                                    .child(model.orderId)
////                                    .child("status").addListenerForSingleValueEvent(new ValueEventListener() {
////                                @Override
////                                public void onDataChange(DataSnapshot dataSnapshot) {
////                                    String value = dataSnapshot.getValue().toString();
////                                    Log.e("value", value);
////                                    if (value.equals("2")) {
////                                        Toast.makeText(OrderActivity.this, "Shipper delivered the parcel. No tracking available.", Toast.LENGTH_SHORT).show();
////                                        return;
////                                    }
////
////
////                                    if (value.equals("1"))
////                                        startActivity(intent);
////                                    else
////                                        Toast.makeText(OrderActivity.this, "Shipper has not started the trip yet!", Toast.LENGTH_SHORT).show();
////                                }
////
////                                @Override
////                                public void onCancelled(DatabaseError databaseError) {
////
////                                }
////                            });
//                                startActivity(intent);
//                            } else
//                                Toast.makeText(OrderActivity.this, "Tracking not available at the moment!", Toast.LENGTH_SHORT).show();
//
//                        } else
//                            Toast.makeText(OrderActivity.this, "Tracking not available at the moment!", Toast.LENGTH_SHORT).show();
//                    }
//
//                });
//                viewHolder.shipper.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//
//                        Intent intent = new Intent(OrderActivity.this, ShipperActivity.class);
//                        Common.currentRequest = model;
//                        Log.e("id", model.orderId + " " + model.latLng);
//                        intent.putExtra("from", "assign");
//                        startActivity(intent);
//
//                    }
//                });
//
//            }
        };

        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @NonNull
    private String convertcodeToStatus(String status) {
        if (status.equals("0"))
            return "Order Pending";
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
            //deleteOrder(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey());
            showEstimateTimeDialog(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey(), firebaseRecyclerAdapter.getItem(item.getOrder()));
        }

        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {

        databaseReference.child(key).removeValue();
    }

    private void showUpdateDialog(String key, final Request item) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please Choose status");

        View view = this.getLayoutInflater().inflate(R.layout.update_order_layout, null);
        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Pending", "Accept", "Reject", "Shipping", "Delivered"); //01234


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

    private void showEstimateTimeDialog(String key, final Request item) {


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Estimate Delivery Time");

        final View view1 = LayoutInflater.from(this).inflate(R.layout.estimate_time_dialog, null);
        alertDialog.setView(view1);

        final MaterialEditText homeAddress = view1.findViewById(R.id.home_address);

        homeAddress.setText(item.estimate_time);


        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int mint = c.get(Calendar.MINUTE);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

            //return_date = dayOfMonth + "-" + (month + 1) + "-" + year;
            //start.setText(new SimpleDateFormat("yyyy-MM-dd").format(Utils.getInstance().getDateFromString(year, month, dayOfMonth)));
            new TimePickerDialog(
                    this, (timePicker, hourOfDay, minute) -> {
                String min = minute < 10 ? "0" + minute : minute + "";
                String hr = hourOfDay < 10 ? "0" + hourOfDay : hourOfDay + "";

                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Utils.getInstance().getDateFromString(year, month, dayOfMonth));
                String time = hr + ":" + min;

                item.estimate_time = date + " " + time;
                homeAddress.setText(item.estimate_time);
                //Log.e("text", spinner.getText().toString());
                databaseReference.child(key).setValue(item);
                firebaseRecyclerAdapter.notifyDataSetChanged();


                Retrofit retrofit = RetrofitClient.getClinet(Common.FCM_BASE_URL);
                APIService apiService = retrofit.create(APIService.class);
                FirebaseDatabase.getInstance().getReference("User")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    User user = data.getValue(User.class);
                                    if (user.Phone.equals(item.phone)) {

                                        if (user.token != null) {
                                            String message = "Your order # " + item.orderId + " will be delivered to you at " + item.estimate_time;
                                            String title = "ESTIMATE DELIVERY TIME";
                                            apiService.sendNotification(new Sender(user.token.token, new Notification(message, title), null))
                                                    .enqueue(new Callback<MyResponse>() {
                                                        @Override
                                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                            Log.e("code", response.code() + "");
                                                            if (response.isSuccessful()) {
                                                                //Toast.makeText(context, "Promotion sent successfully!", Toast.LENGTH_SHORT).show();

                                                            }
//                                                                            else
//                                                                                Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }

                                                        @Override
                                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                                        }
                                                    });
                                        }

                                    } else {

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                //apiService.sendNotification(new Sender())


//            to_time = hourOfDay + ":" + min;
//            to.setText(to_time);
//            try {
//                //Log.e("date", Calendar.getInstance().get(Calendar.YEAR) + "-" + to_time);
//               // Date toDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).parse(dateFormat.format(Calendar.getInstance().getTime()) + " " + to_time);
//            } catch (ParseException e) {
//                Toast.makeText(this, "Time Error", Toast.LENGTH_SHORT).show();
//            }
            },
                    hour, mint, true).show();

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);

        datePickerDialog.show();


    }

    private void notifyOrderStatusToUser(final String localKey, final Request item) {
        Log.e("localKey", localKey);
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("User");
        tokenRef.orderByKey().equalTo(item.phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnaps : dataSnapshot.getChildren()) {
//                    Token token = new Token(postSnaps.child("token").getValue().toString(),
//                            Boolean.valueOf(postSnaps.child("isServerToken").getValue().toString()));
                    User user = postSnaps.getValue(User.class);
                    if (user.token != null) {
                        Notification notification = new Notification(String.
                                format("Status of your order #'%s' has updated to '%s'", localKey, Utils.convertcodeToStatus(item.status)), "ORDER UPDATE");
                        Sender content = new Sender(user.token.token, notification, null);
                        Log.e("SenderName", content.to);


                        mService.sendNotification(content).enqueue(new Callback<MyResponse>() {
                            @Override
                            public void onResponse(Call<MyResponse> call, retrofit2.Response<MyResponse> response) {
                                Log.e("Retrofit Response!", "Hello inside");
                                if (response.body().success == 1) {
                                    Toast.makeText(OrderActivity.this, "Order updated successfully!", Toast.LENGTH_SHORT).show();
//                                    finish();
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
