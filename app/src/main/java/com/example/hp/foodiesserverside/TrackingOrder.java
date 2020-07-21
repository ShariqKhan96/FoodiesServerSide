package com.example.hp.foodiesserverside;

import android.app.ProgressDialog;
import android.graphics.Color;

import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.example.hp.foodiesserverside.Common.Common;
import com.example.hp.foodiesserverside.model.Location;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback, RoutingListener {

    private GoogleMap mMap;
    double destinationLat;
    double destinationLng;
    String[] toSplit;
    LatLng startPoint;
    LatLng endPoint;
    DatabaseReference shipperRef;
    private List<Polyline> polylines = new ArrayList<>();
    private ProgressDialog progressDialog;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        toSplit = Common.currentRequest.latLng.split(",");
        destinationLat = Double.valueOf(toSplit[0]);
        destinationLng = Double.valueOf(toSplit[1]);
        endPoint = new LatLng(destinationLat, destinationLng);
        startPoint = new LatLng(Common.currentLat, Common.currentLng);
        shipperRef = FirebaseDatabase.getInstance().getReference("ShipperLocation").child(Common.currentRequest.assigned_to.phone);
        Log.e("start", startPoint.toString());
        Log.e("end", endPoint.toString());

        //mMap.addMarker(new MarkerOptions().position(startPoint).title("Source"));
        mMap.addMarker(new MarkerOptions().position(endPoint).title("Destination"));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPoint, 15));
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(startPoint);
        builder.include(endPoint);
        LatLngBounds bounds = builder.build();
        final int width = getResources().getDisplayMetrics().widthPixels;
        final int height = getResources().getDisplayMetrics().heightPixels;
        final int minMetric = Math.min(width, height);
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, 100);
        mMap.animateCamera(cu);

        track();

        //drawPath();


    }

    private void track() {

        shipperRef.addValueEventListener(valueEventListener);


    }

    private void addMarkers(Location latLng) {
        mMap.clear();
        endPoint = new LatLng(destinationLat, destinationLng);
        mMap.addMarker(new MarkerOptions().position(endPoint).title("Destination"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title("Rider"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLng.latitude, latLng.longitude), 13));
        Log.e("tracking", "tracking");

    }

    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e("ExceptionRouting", e.getMessage());
        progressDialog.dismiss();

    }

    @Override
    public void onRoutingStart() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }


        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            //
            // int colorIndex = i % COLORS.length;

            Log.e("i", String.valueOf(route.size()));


            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(Color.RED);
            polyOptions.width(10);
            polyOptions.addAll(route.get(i).getPoints());

            List<LatLng> routeList = route.get(i).getPoints();
//            Geocoder geocoder = new Geocoder(TrackingOrder.this, Locale.getDefault());
//
//            try {
//                List<Address> nameOfLocality = geocoder.getFromLocation(routeList.get(0).latitude, routeList.get(0).longitude, 1);
//                String state = nameOfLocality.get(0).getLocality();
//                //  String ss = nameOfLocality.get(0).getAddressLine(2);
//                String sss = nameOfLocality.get(0).getAddressLine(0);
//                Log.e("s", state);
//                //  Log.e("ss", ss);
//                Log.e("sss", sss);
//
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }


            // Log.e("points", String.valueOf(routeList.size()));

            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
            //route.get(i).getDistanceValue();

//            if (!decideRouteType)
//            {
//                text1.setText(route.get(i).getDistanceText());
//                text2.setText(route.get(i).getDurationText());
//
//            }
            Log.e("RouteValue", "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue());

        }
        // progressDialog.dismiss();

        progressDialog.dismiss();
    }

    @Override
    public void onRoutingCancelled() {
        progressDialog.dismiss();
    }

    public void drawPath() {
        Log.e("RoutingBegins", "Draw");
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(TrackingOrder.this)
                .waypoints(startPoint, endPoint)
                .build();
        routing.execute();


        //here asynctask shuru hojaiga package ka then api call hogi internally...
    }

    @Override
    protected void onStart() {
        super.onStart();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                Location latLng = dataSnapshot.getValue(com.example.hp.foodiesserverside.model.Location.class);
                try {
                    addMarkers(latLng);
                } catch (Exception e) {
                    Toast.makeText(TrackingOrder.this, "Wait for shipper to start trip!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        shipperRef = FirebaseDatabase.getInstance().getReference("ShipperLocation").child(Common.currentRequest.assigned_to.phone);
        if (valueEventListener != null)
            shipperRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        shipperRef.removeEventListener(valueEventListener);
    }
}

