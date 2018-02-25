package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hp.foodiesserverside.Interface.ItemClickListener;
import com.example.hp.foodiesserverside.Service.ListenOrder;
import com.example.hp.foodiesserverside.ViewHolder.MenuViewHolder;
import com.example.hp.foodiesserverside.adapter.CategoryAdapter;
import com.example.hp.foodiesserverside.model.Category;
import com.example.hp.foodiesserverside.model.cat;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    FirebaseRecyclerAdapter<cat, MenuViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);


        toolbar.setTitle("Menu Management");
        setSupportActionBar(toolbar);
        dbRef = FirebaseDatabase.getInstance().getReference("Category");

        recView = (RecyclerView) findViewById(R.id.recyclerView);

        recView.setLayoutManager(new LinearLayoutManager(this));

        recView.setAdapter(firebaseRecyclerAdapter);


        getCategories();

        Intent intent = new Intent(Home.this, ListenOrder.class);
        startService(intent);


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
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Delete")) {

            deleteItem(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey());

            Toast.makeText(this, String.valueOf(item.getItemId()), Toast.LENGTH_SHORT).show();
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
                Category category = new Category();
                category.setName(cateogryName);
                category.setImage(categoryImageUri);

                dbRef.push().setValue(category).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                            Log.e("ImageUpload", uri.toString());
                            categoryImageUri = uri.toString();
                            decider = true;
                            Toast.makeText(Home.this, categoryImageUri, Toast.LENGTH_SHORT).show();

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

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<cat, MenuViewHolder>(
                cat.class,
                R.layout.item_view,
                MenuViewHolder.class,
                dbRef
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, cat model, int position) {
                viewHolder.foodName.setText(model.getName());
                Picasso.with(Home.this).load(model.getImage()).fit().into(viewHolder.foodImage);
                final cat local = model;


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

}
