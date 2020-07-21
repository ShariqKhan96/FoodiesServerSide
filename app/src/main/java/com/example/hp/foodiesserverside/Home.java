package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.hp.foodiesserverside.Remote.APIService;
import com.example.hp.foodiesserverside.model.MyResponse;
import com.example.hp.foodiesserverside.model.Notification;
import com.example.hp.foodiesserverside.model.Sender;
import com.example.hp.foodiesserverside.model.User;
import com.example.hp.foodiesserverside.utils.Utils;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hp.foodiesserverside.Common.Common;
import com.example.hp.foodiesserverside.Interface.ItemClickListener;

import com.example.hp.foodiesserverside.ViewHolder.MenuViewHolder;
import com.example.hp.foodiesserverside.adapter.CategoryAdapter;
import com.example.hp.foodiesserverside.model.Category;
import com.example.hp.foodiesserverside.model.Token;
import com.example.hp.foodiesserverside.model.cat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.transitionseverywhere.TransitionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    DatabaseReference dbRef;
    RecyclerView recView;
    CategoryAdapter adapter;
    private ArrayList<Category> arrayList = new ArrayList<>();
    Toolbar toolbar;
    DatabaseReference dbReference;
    FloatingActionButton fab;
    MaterialEditText edtCategoryName;
    FButton upload, select;
    String cateogryName;
    private final int PICK_IMAGE = 1;
    private StorageReference storageReference;
    private Uri imageUri;
    private String categoryImageUri;
    boolean decider = false;
    FirebaseRecyclerAdapter<Category, MenuViewHolder> firebaseRecyclerAdapter;


    //location
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;

    //dialog fields
    MaterialEditText Password, newPassword, confirmPassword;
    FButton check;

    //Database reference for change password
    String changePassVerify;
    DatabaseReference changePassReference;
    String phoneNumber;
    String newPass;
    String confirmNewPass;
    SharedPreferences prefs;
    public static String PHONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); //


        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);
        dbRef = FirebaseDatabase.getInstance().getReference("Category");
        changePassReference = FirebaseDatabase.getInstance().getReference("User").child(prefs.getString("phone", ""));
        PHONE = prefs.getString("phone", "");

        //init google api client and location


        changePassVerify = prefs.getString("password", "");
        Log.e("verifyPassword", changePassVerify);
        recView = (RecyclerView) findViewById(R.id.recyclerView);

        recView.setLayoutManager(new LinearLayoutManager(this));

        recView.setAdapter(firebaseRecyclerAdapter);


        getCategories();
//
//        Intent intent = new Intent(Home.this, ListenOrder.class);
//        startService(intent);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                updateToken(instanceIdResult.getToken());
            }
        });


    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Delete")) {

            deleteItem(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey());

            Toast.makeText(this, String.valueOf(item.getItemId()), Toast.LENGTH_SHORT).show();
        } else {
            showAlertDialog();
        }
        return true;
    }

    private void deleteItem(final String key) {
        dbRef.child(key).removeValue();
        dbReference = FirebaseDatabase.getInstance().getReference("Foods");
        dbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //  Toast.makeText(Home.this, String.valueOf(ds.getChildrenCount()), Toast.LENGTH_SHORT).show();
                    Log.e("Key", ds.getKey());
                    if (ds.child("MenuId").getValue().toString().equals(key)) {
                        dbReference.child(ds.getKey()).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    private void updateOrDelete(CharSequence title, cat itemId) {
        if (title.equals("Delete")) {

            dbRef.child(String.valueOf(itemId)).removeValue();
//            arrayList.remove(itemId);
//            adapter.notifyDataSetChanged();
        }

    }


    private void showMessageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setMessage("Please fill the information completely");
        builder.setTitle("Send Message");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.send_message_dialog, null);

        builder.setView(view);
        builder.setIcon(R.drawable.ic_cloud_upload_black_24dp);

        MaterialEditText messageEdt, titleEdt;
        messageEdt = view.findViewById(R.id.message);
        titleEdt = view.findViewById(R.id.title);

        builder.setPositiveButton("Send Message", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                if (!Utils.getInstance().anyFieldEmpty(new String[]{messageEdt.getText().toString(), titleEdt.getText().toString()})) {

                    APIService api = Common.getFCMClient();
                    FirebaseDatabase.getInstance().getReference("User")
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        User user = data.getValue(User.class);
                                        if (!user.Phone.equals(prefs.getString("phone", "")) && user.isStaff.equals("false")) {

                                            if (user.token != null) {
                                                api.sendNotification(new Sender(user.token.token, new Notification(messageEdt.getText().toString(), titleEdt.getText().toString()), null))
                                                        .enqueue(new Callback<MyResponse>() {
                                                            @Override
                                                            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                                                Log.e("code", response.code() + "");
                                                                if (response.isSuccessful()) {
                                                                    Toast.makeText(Home.this, "Promotion sent successfully!", Toast.LENGTH_SHORT).show();

                                                                } else
                                                                    Toast.makeText(Home.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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

                    //api.sendNotification(new Sender())
                } else {
                    Toast.makeText(Home.this, "Some field(s) empty!", Toast.LENGTH_SHORT).show();
                }
                //uploadImage();


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

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please fill the information completely");
        builder.setTitle("Add new Category");

        storageReference = FirebaseStorage.getInstance().getReference();

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_category_dialog, null);

        builder.setView(view);
        builder.setIcon(R.drawable.ic_cloud_upload_black_24dp);

        edtCategoryName = view.findViewById(R.id.categoryName);
        //upload = view.findViewById(R.id.upload);
        select = view.findViewById(R.id.select);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

            }
        });


        builder.setPositiveButton("Add Category", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                cateogryName = edtCategoryName.getText().toString();
                uploadImage();


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

    private void uploadImage() {

        if (imageUri != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Uploading");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/" + imageName);
            imageFolder.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    dialog.dismiss();

                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            categoryImageUri = uri.toString();

                            String id = dbRef.push().getKey();
                            Category category = new Category();
                            category.setName(cateogryName);
                            category.setImage(categoryImageUri);
                            category.setId(id);


                            dbRef.child(id).setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //  Toast.makeText(Home.this, task.toString(), Toast.LENGTH_SHORT).show();
                                        Log.e("Successfull", "Success");
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(Home.this, task.toString(), Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                        Log.e("UnSuccessfull", "UnSuccess");
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Exception", e.getMessage());
                                }
                            });


//                            decider = true;
//                            Toast.makeText(Home.this, categoryImageUri, Toast.LENGTH_SHORT).show();

                        }

                    });

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null) {
            imageUri = data.getData();
            select.setText("Image Selected");
        }
    }

    private void getCategories() {
        Utils.getInstance().showLoader(this);
        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(dbRef, Category.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(
                options) {
            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MenuViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false));
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                Utils.getInstance().dismissLoader();
            }

            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, int position, @NonNull Category model) {
                viewHolder.foodName.setText(model.getName());
                Picasso.with(Home.this).load(model.getImage()).placeholder(R.drawable.my_bg).into(viewHolder.foodImage);

//
//                final double viewWidthToBitmapWidthRatio = (double)viewHolder.foodImage.getWidth() / (double)bitmap.getWidth();
//                viewHolder.foodImage.getLayoutParams().height = (int) (bitmap.getHeight() * viewWidthToBitmapWidthRatio);
                viewHolder.onItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent intent = new Intent(Home.this, Foods.class);
                        intent.putExtra("CategoryId", firebaseRecyclerAdapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

        };
        firebaseRecyclerAdapter.notifyDataSetChanged();
        recView.setAdapter(firebaseRecyclerAdapter);

//        dbRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snaps : dataSnapshot.getChildren()) {
//                    Category category = new Category();
//                    category.setId(snaps.getKey());
//                    try {
//                        category.setImage(snaps.child("Image").getValue().toString());
//                    } catch (Exception e) {
//                        category.setImage("http://medifoods.my/wp-content/uploads/2015/03/cover-menu-westernsoup1.jpg");
//                    }
//
//                    try {
//                        category.setName(snaps.child("Name").getValue().toString());
//                    } catch (Exception e) {
//                        category.setName(snaps.child("name").getValue().toString());
//                    }
//                    arrayList.add(category);
//
//                }
//                adapter.notifyDataSetChanged();
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_orders) {
            startActivity(new Intent(Home.this, OrderActivity.class));
        } else if (id == R.id.nav_message) {
//            Intent intent = new Intent(Home.this, SendMessage.class);
//            startActivity(intent);
            showMessageDialog();
        } else if (id == R.id.nav_password) {
            changePasswordDialog();

        } else if (id == R.id.nav_signout) {
            SharedPreferences preferences = getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(Home.this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_banner_management) {
            Intent intent = new Intent(Home.this, BannerMng.class);
            startActivity(intent);

        } else if (R.id.nav_shippers == id) {
            Intent intent = new Intent(Home.this, ShipperActivity.class);
            intent.putExtra("from", "not_assign");
            startActivity(intent);
        }

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changePasswordDialog() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Change Password");

        final View view = LayoutInflater.from(this).inflate(R.layout.change_password_layout, null);
        alertDialog.setView(view);
        final ViewGroup changePassLayout = view.findViewById(R.id.confirm_layout);

        Password = view.findViewById(R.id.password);
        newPassword = view.findViewById(R.id.newpassword);
        confirmPassword = view.findViewById(R.id.confirm_new_password);
        check = view.findViewById(R.id.check);

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = Password.getText().toString();

                if (pass.equals(changePassVerify)) {


                    TransitionManager.beginDelayedTransition(changePassLayout);
                    check.setVisibility(View.GONE);
                    changePassLayout.setVisibility(View.VISIBLE);
                } else {
                    Password.setError("Wrong password!");
                }


            }
        });


        alertDialog.setIcon(R.drawable.ic_security_black_24dp);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!(TextUtils.isEmpty(Password.getText().toString())) && (!(TextUtils.isEmpty(newPassword.getText().toString()))) &&
                        (!(TextUtils.isEmpty(confirmPassword.getText().toString())))) {

                    newPass = newPassword.getText().toString();
                    confirmNewPass = confirmPassword.getText().toString();

                    if (newPass.equals(confirmNewPass)) {
                        Map<String, Object> passwordUpdateMap = new HashMap<>();
                        passwordUpdateMap.put("isStaff", "true");
                        passwordUpdateMap.put("Name", prefs.getString("name", ""));
                        passwordUpdateMap.put("Password", newPass);
                        passwordUpdateMap.put("Phone", prefs.getString("phone", ""));
                        passwordUpdateMap.put("security_code", prefs.getString("code", ""));

                        changePassReference.updateChildren(passwordUpdateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(Home.this, "Password Changed Successfully !!", Toast.LENGTH_SHORT).show();
                                    changePassVerify = newPass;
                                    SharedPreferences.Editor prefs = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                                    prefs.putString("password", newPass);
                                    prefs.apply();

                                } else
                                    Toast.makeText(Home.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }


                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
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


    private void updateToken(String token) {
        Log.e("token", token);
        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("User").child(Home.PHONE).child("token");
        Token toki = new Token(token, true);

        tokenRef.setValue(toki);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//                    if (location == null) {
//                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//
//                    } else {
//                        //If everything went fine lets get latitude and longitude
//                        currentLatitude = location.getLatitude();
//                        currentLongitude = location.getLongitude();
//
//                        Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
//                    }

                }

            } else {
                Log.e("NotGranted", "Permission not Granted");
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Home.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            Common.currentLat = location.getLatitude();
            Common.currentLng = location.getLongitude();

            Log.e("current LatLng", currentLatitude + " " + currentLongitude);
            //  Toast.makeText(this, currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                Log.e("onConnectedFailed", e.getMessage());

            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        Common.currentLat = location.getLatitude();
        Common.currentLng = location.getLongitude();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
}

