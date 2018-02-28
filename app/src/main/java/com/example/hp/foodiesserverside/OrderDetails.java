package com.example.hp.foodiesserverside;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.hp.foodiesserverside.ViewHolder.OrderDetailAdapter;
import com.example.hp.foodiesserverside.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderDetails extends AppCompatActivity {

    TextView orderid, ordercomment, orderaddress, orderphone, ordertotal;
    String order_id_value;
    RecyclerView lstOrders;
    DatabaseReference databaseReference;
    DatabaseReference forTotal;
    List<Order> orderList = new ArrayList<>();
    OrderDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_food);
        lstOrders = findViewById(R.id.food_recyclerview);


        if (getIntent() != null)
            order_id_value = getIntent().getStringExtra("orderId");


        initTextViews();
        forTotal = FirebaseDatabase.getInstance().getReference("Requests").child(order_id_value);
        databaseReference = FirebaseDatabase.getInstance().getReference("Requests").child(order_id_value).child("orders");
        RecyclerView.LayoutManager maanger = new LinearLayoutManager(this);
        adapter = new OrderDetailAdapter(orderList);
        lstOrders.setAdapter(adapter);
        lstOrders.setLayoutManager(maanger);

        forTotal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ordercomment.setText(dataSnapshot.child("comment").getValue().toString());
                ordertotal.setText(dataSnapshot.child("total").getValue().toString());
                orderaddress.setText(dataSnapshot.child("address").getValue().toString());
                orderphone.setText(dataSnapshot.child("phone").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()
                        ) {
                    Order order = ds.getValue(Order.class);
                    String name = ds.child("name").getValue().toString();

                    Double localTotal = Double.valueOf(ds.child("quantity").getValue().toString())*(Double.valueOf(ds.child("price").getValue().toString()));

                    orderList.add(new Order(name, order.discount, String.valueOf(localTotal), order.quantity));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void initTextViews() {
        orderid = findViewById(R.id.order_id);
        orderid.setText(order_id_value);
        ordercomment = findViewById(R.id.order_comment);
        orderaddress = findViewById(R.id.order_address);
        ordertotal = findViewById(R.id.order_total);
        orderphone = findViewById(R.id.order_phone);

    }
}
