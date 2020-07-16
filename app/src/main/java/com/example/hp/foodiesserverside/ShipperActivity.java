package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hp.foodiesserverside.Common.Common;
import com.example.hp.foodiesserverside.Interface.ItemClickListener;
import com.example.hp.foodiesserverside.ViewHolder.MenuViewHolder;
import com.example.hp.foodiesserverside.ViewHolder.ShipperViewHolder;
import com.example.hp.foodiesserverside.model.Category;
import com.example.hp.foodiesserverside.model.Shipper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.rengwuxian.materialedittext.MaterialEditText;

public class ShipperActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton floatingActionButton;
    FirebaseRecyclerAdapter<Shipper, ShipperViewHolder> firebaseRecyclerAdapter;
    DatabaseReference shipperRef;
    String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipper);
        recyclerView = findViewById(R.id.shippers);
        from = getIntent().getStringExtra("from");
        floatingActionButton = findViewById(R.id.add_shipper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        shipperRef = FirebaseDatabase.getInstance().getReference("Shippers");
        loadShippers();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please fill the information completely");
        builder.setTitle("Add new Shipper");


        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_shipper_dialog, null);

        builder.setView(view);
        builder.setIcon(R.drawable.ic_cloud_upload_black_24dp);

        final MaterialEditText name, phone, password;
        name = view.findViewById(R.id.categoryName);
        phone = view.findViewById(R.id.phone);
        password = view.findViewById(R.id.password);
        //upload = view.findViewById(R.id.upload);
//        select = view.findViewById(R.id.select);
//
//        select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
//
//            }
//        });


        builder.setPositiveButton("Add Shipper", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                //cateogryName = edtCategoryName.getText().toString();
                // uploadImage();


                Shipper shipper = new Shipper("1", name.getText().toString(), password.getText().toString(), phone.getText().toString());
                FirebaseDatabase.getInstance().getReference("Shippers").child(phone.getText().toString())
                        .setValue(shipper);

                firebaseRecyclerAdapter.notifyDataSetChanged();


                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();


    }

    private void loadShippers() {

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Shipper, ShipperViewHolder>(Shipper.class,
                R.layout.shipper_view,
                ShipperViewHolder.class,
                shipperRef) {
            @Override
            protected void populateViewHolder(ShipperViewHolder viewHolder, final Shipper model, int position) {
                if (from.equals("assign"))
                    viewHolder.assign.setVisibility(View.VISIBLE);
                else viewHolder.assign.setVisibility(View.GONE);
                viewHolder.name.setText(model.getName());
                viewHolder.phno.setText(model.getPhone());
                if (model.active.equals("1")) {
                    viewHolder.active.setImageDrawable(getDrawable(R.drawable.round_green));
                } else {
                    viewHolder.active.setImageDrawable(getDrawable(R.drawable.round_red));
                }
                viewHolder.onItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

//                        Intent intent = new Intent(Home.this, Foods.class);
//                        intent.putExtra("CategoryId", firebaseRecyclerAdapter.getRef(position).getKey());
//                        startActivity(intent);
                    }
                });
                viewHolder.assign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (model.active.equals("1")) {
                            //Common.currentRequest.assigned_to = model;
                            FirebaseDatabase.getInstance().getReference("Shippers")
                                    .child(model.phone)
                                    .child("assignments")
                                    .child(Common.currentRequest.orderId)
                                    .setValue(Common.currentRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference("Requests").child(Common.currentRequest.orderId)
                                                .child("assigned_to").setValue(model);
                                        Toast.makeText(ShipperActivity.this, "Assignment completed!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ShipperActivity.this, "This shipper is not active!", Toast.LENGTH_SHORT).show();
                        }


                    }
                });


            }

        };
        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter);


//        shipperRef = FirebaseDatabase.getInstance().getReference("Shippers");
//        shipperRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0) {
//active
            String key = firebaseRecyclerAdapter.getRef(item.getOrder()).getKey();
            FirebaseDatabase.getInstance().getReference("Shippers").child(key).child("active").setValue("1");
        } else {
            //deactive

            String key = firebaseRecyclerAdapter.getRef(item.getOrder()).getKey();
            FirebaseDatabase.getInstance().getReference("Shippers").child(key).child("active").setValue("0");
        }

        firebaseRecyclerAdapter.notifyDataSetChanged();
        return super.onContextItemSelected(item);
    }
}