package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.SidePropagation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.hp.foodiesserverside.Interface.ItemClickListener;
import com.example.hp.foodiesserverside.ViewHolder.FoodViewHolder;
import com.example.hp.foodiesserverside.model.Category;
import com.example.hp.foodiesserverside.model.foods;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;
import io.codetail.animation.ArcAnimator;
import io.codetail.animation.Side;

public class Foods extends AppCompatActivity {

    RecyclerView recyclerView;
    String key;
    DatabaseReference dbRef;
    FirebaseRecyclerAdapter<foods, FoodViewHolder> firebaseRecyclerAdapter;
    FloatingActionButton fab;
    StorageReference storageReference;
    private final int PICK_IMAGE = 1;
    MaterialEditText edtFoodDiscount;
    MaterialEditText edtFoodDescription;
    MaterialEditText edtFoodName;
    MaterialEditText edtFoodPrice;
    FButton select;
    private String foodName;
    private String categoryImageUri;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods);

        key = getIntent().getStringExtra("CategoryId");
        dbRef = FirebaseDatabase.getInstance().getReference("Foods");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
//                ArcAnimator.createArcShift(fab, 550, 850, 65, Side.LEFT)
//                        .setDuration(2000)
//                        .start();
                showAlertDialog();
            }
        });


        loadFoodList();


    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please fill the information completely");
        builder.setTitle("Add new Category");

        storageReference = FirebaseStorage.getInstance().getReference();

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_food_dialog, null);

        builder.setView(view);
        builder.setIcon(R.drawable.ic_cloud_upload_black_24dp);

        edtFoodName = view.findViewById(R.id.food_name);
        edtFoodDescription = view.findViewById(R.id.food_description);
        edtFoodDiscount = view.findViewById(R.id.food_discount);
        edtFoodPrice = view.findViewById(R.id.food_price);

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


        builder.setPositiveButton("Add Food", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {

                foodName = edtFoodName.getText().toString();
                uploadImage();
//                foods food = new foods();
//                food.Name = foodName;
//                food.Image = categoryImageUri;
//                food.MenuId = key;
//                food.Description = edtFoodDescription.getText().toString();
//                food.Discount = edtFoodDiscount.getText().toString();
//
//                DatabaseReference forPushId = FirebaseDatabase.getInstance().getReference("Foods").push();
//                String push_key = forPushId.getKey();
//                DatabaseReference insert = FirebaseDatabase.getInstance().getReference("Foods");
//
//                Map map = new HashMap();
//                map.put("Name", foodName);
//                Log.e("sending", categoryImageUri);
//                map.put("Image", categoryImageUri);
//                map.put("MenuId", key);
//                map.put("Discount", edtFoodDiscount.getText().toString());
//                map.put("Description", edtFoodDescription.getText().toString());
//
//
//                Map referenceMap = new HashMap();
//                referenceMap.put(push_key, map);
//
//                insert.updateChildren(referenceMap, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
//                        if (databaseError != null)
//                        {
//                            Log.e("DatabaseError", databaseError.getMessage());
//                        }
//                    }
//
//
//                });

//                insert.push().setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            //  Toast.makeText(Home.this, task.toString(), Toast.LENGTH_SHORT).show();
//                            Log.e("Successfull", "Success");
//                            dialog.dismiss();
//                        } else {
//                            Toast.makeText(Foods.this, task.toString(), Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                            Log.e("UnSuccessfull", "UnSuccess");
//                        }
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("Exception", e.getMessage());
//                    }
//                });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {

            imageUri = data.getData();
            select.setText("Image Selected");
        }
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

                            foods food = new foods();
                            food.Name = foodName;
                            food.Image = categoryImageUri;
                            food.MenuId = key;
                            food.Description = edtFoodDescription.getText().toString();
                            food.Discount = edtFoodDiscount.getText().toString();

                            DatabaseReference forPushId = FirebaseDatabase.getInstance().getReference("Foods").push();
                            String push_key = forPushId.getKey();
                            DatabaseReference insert = FirebaseDatabase.getInstance().getReference("Foods");

                            Map map = new HashMap();
                            map.put("Name", foodName);
                            Log.e("sending", categoryImageUri);
                            map.put("Image", categoryImageUri);
                            map.put("MenuId", key);
                            map.put("Discount", edtFoodDiscount.getText().toString());
                            map.put("Description", edtFoodDescription.getText().toString());


                            Map referenceMap = new HashMap();
                            referenceMap.put(push_key, map);

                            insert.updateChildren(referenceMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        Log.e("DatabaseError", databaseError.getMessage());
                                    }
                                }


                            });


                            Toast.makeText(Foods.this, categoryImageUri, Toast.LENGTH_SHORT).show();

                        }

                    });

                }
            });

        }
    }

    private void loadFoodList() {
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<foods, FoodViewHolder>(foods.class, R.layout.item_view,
                FoodViewHolder.class,
                dbRef.orderByChild("MenuId").equalTo(key)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, foods model, int position) {

                viewHolder.foodName.setText(model.Name);
                Picasso.with(Foods.this).load(model.Image).into(viewHolder.foodImage);

                final foods Model = model;

                viewHolder.onItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(Foods.this, Model.Name, Toast.LENGTH_SHORT).show();

                    }
                });
            }
        };
        firebaseRecyclerAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
