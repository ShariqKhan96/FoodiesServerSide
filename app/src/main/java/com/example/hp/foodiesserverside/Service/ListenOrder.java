//package com.example.hp.foodiesserverside.Service;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.IBinder;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.example.hp.foodiesserverside.Common.Common;
//import com.example.hp.foodiesserverside.OrderActivity;
//import com.example.hp.foodiesserverside.R;
//import com.example.hp.foodiesserverside.model.Requests;
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.Random;
//
//public class ListenOrder extends Service implements ChildEventListener {
//    DatabaseReference databaseReference;
//
//    public ListenOrder() {
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        databaseReference = FirebaseDatabase.getInstance().getReference("Requests");
//
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        databaseReference.addChildEventListener(this);
//        Log.e("Service", "Servicetarted");
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//
//        //Triggering event for notify if new order received and status of request is 0 means order just placed....
//
//        Requests requests = dataSnapshot.getValue(Requests.class);
////        Log.e("ChildAddedService", s);
//        showNotification(dataSnapshot.getKey(), requests);
//
//    }
//
//    @Override
//    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//
//    }
//
//    private void showNotification(String key, Requests requests) {
//        Intent intent = new Intent(getBaseContext(), OrderActivity.class);
//        PendingIntent currentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, 0);
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());
//        builder.setAutoCancel(true)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setTicker("Shariq Khan Food App")
//                .setContentInfo("New Order")
//                .setContentText("Order Received from # " + key)
//                .setContentIntent(currentIntent)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentInfo("Info");
//
//        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        int randomId = new Random().nextInt(9999 - 1) + 1;
//        notificationManager.notify(randomId, builder.build());
//
//
//    }
//
//    @Override
//    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//    }
//
//    @Override
//    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//    }
//
//    @Override
//    public void onCancelled(DatabaseError databaseError) {
//
//    }
//}
