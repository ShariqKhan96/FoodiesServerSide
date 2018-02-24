package com.example.hp.foodiesserverside.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.hp.foodiesserverside.Common.Common;
import com.example.hp.foodiesserverside.OrderActivity;
import com.example.hp.foodiesserverside.model.Requests;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ListenOrder extends Service implements ChildEventListener {
    DatabaseReference databaseReference;

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
    return  null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    databaseReference = FirebaseDatabase.getInstance().getReference("Requests");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        databaseReference.addChildEventListener(this);
        Log.e("Service", "Servicetarted");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        //Triggering event for notify
        Requests requests = dataSnapshot.getValue(Requests.class);
        Log.e("ChildChangedService", s);
        showNotification(dataSnapshot.getKey(), requests);

    }

    private void showNotification(String key, Requests requests) {
    Intent intent = new Intent(getBaseContext(), OrderActivity.class);
        PendingIntent currentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Shariq Khan Food App")
                .setContentInfo("Your Order was Updated")
                .setContentText("Order #"+key+" Updated to status "+ Common.convertcodeToStatus(requests.status))
                .setContentIntent(currentIntent)
                .setContentInfo("Info");

        NotificationManager notificationManager = (NotificationManager)getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());


    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
