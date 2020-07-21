package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.hp.foodiesserverside.Interface.ItemClickListener;
import com.example.hp.foodiesserverside.ViewHolder.MenuViewHolder;
import com.example.hp.foodiesserverside.adapter.BannerAdapter;
import com.example.hp.foodiesserverside.model.Banner;
import com.example.hp.foodiesserverside.model.Category;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.hoang8f.widget.FButton;

public class BannerMng extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    FirebaseRecyclerAdapter<Banner, MenuViewHolder> firebaseRecyclerAdapter;
    RecyclerView bannerList;
    DatabaseReference BannerDataBase;
    Uri imageUri;
    private MaterialEditText edtFoodName;
    private FButton select;
    private String foodName;
    private String categoryImageUri;
    private String cateogryName;
    StorageReference storageReference;
    BannerAdapter adapter;
    ArrayList<Banner> arrayList = new ArrayList<>();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null) {

            imageUri = data.getData();
            select.setText("Image Selected");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Delete")) {

            deleteItem(String.valueOf(item.getItemId()));

            Toast.makeText(this, String.valueOf(item.getItemId()), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void deleteItem(String key) {
        BannerDataBase.child(key).removeValue();
    }

    private void addDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please fill the information completely");
        builder.setTitle("Add new Banner Item");

        storageReference = FirebaseStorage.getInstance().getReference();

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.add_category_dialog, null);

        builder.setView(view);
        builder.setIcon(R.drawable.ic_cloud_upload_black_24dp);

        edtFoodName = view.findViewById(R.id.categoryName);
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


        builder.setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_mng);

        BannerDataBase = FirebaseDatabase.getInstance().getReference("Banner");

        storageReference = FirebaseStorage.getInstance().getReference();


        bannerList = findViewById(R.id.bannerlist);

        bannerList.setLayoutManager(new LinearLayoutManager(this));

        adapter = new BannerAdapter(arrayList, this);

        bannerList.setAdapter(adapter);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialog();
            }
        });

//
//        BannerDataBase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                arrayList.clear();
//                Log.e("onDataChanged", "");
//                for (DataSnapshot ds : dataSnapshot.getChildren()
//                        ) {
//                    Log.e("Key", ds.getKey());
//
//                    Banner banner = new Banner();
//
//                    banner.image = dataSnapshot.child("Image").getValue().toString();
//                    banner.name = dataSnapshot.child("Name").getValue().toString();
//                    arrayList.add(banner);
//                    bannerList.getAdapter().notifyDataSetChanged();
//                }
//
//                BannerDataBase.removeEventListener(this);
//                bannerList.getAdapter().notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


        BannerDataBase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Banner banner = new Banner();
                banner.id = s;
                banner.image = dataSnapshot.child("Image").getValue().toString();
                banner.name = dataSnapshot.child("Name").getValue().toString();
                arrayList.add(banner);
                bannerList.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
        });
    }
//
//    private void loadBannerItems() {
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Banner, MenuViewHolder>(
//                Banner.class,
//                R.layout.banner_item_view,
//                MenuViewHolder.class,
//                BannerDataBase
//        ) {
//            @Override
//            protected void populateViewHolder(MenuViewHolder viewHolder, Banner model, int position) {
//
//                viewHolder.foodName.setText(model.name);
//                Picasso.with(BannerMng.this).load(model.image).into(viewHolder.foodImage);
//                viewHolder.onItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//
//                    }
//                });
//            }
//
//        };
//        firebaseRecyclerAdapter.notifyDataSetChanged();
//        bannerList.setAdapter(firebaseRecyclerAdapter);
//    }

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

                            cateogryName = edtFoodName.getText().toString();

                            Category category = new Category();
                            category.setName(cateogryName);
                            category.setImage(categoryImageUri);

                            DatabaseReference forPushId = FirebaseDatabase.getInstance().getReference("Banner").push();
                            String push_key = forPushId.getKey();
                            DatabaseReference insert = FirebaseDatabase.getInstance().getReference("Banner");

                            Map map = new HashMap();
                            map.put("Name", foodName);
                            Log.e("sending", categoryImageUri);
                            map.put("Image", categoryImageUri);


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


                            Toast.makeText(BannerMng.this, categoryImageUri, Toast.LENGTH_SHORT).show();

                        }

                    });

                }
            });

        }
    }
}
