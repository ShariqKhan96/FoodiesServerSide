package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hp.foodiesserverside.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import info.hoang8f.widget.FButton;

public class SigninActivity extends AppCompatActivity {

    MaterialEditText phoneNumber;
    MaterialEditText password;

    FButton signIn;
    DatabaseReference dbRef;
    AlertDialog alertDialog;
    String passToVerify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        phoneNumber = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.password);
        signIn = findViewById(R.id.signIn);

        alertDialog = new SpotsDialog(this);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();
                if (!TextUtils.isEmpty(phoneNumber.getText().toString()) && !TextUtils.isEmpty(password.getText().toString())) {
                    dbRef = FirebaseDatabase.getInstance().getReference("User").child(phoneNumber.getText().toString());
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //  Toast.makeText(SigninActivity.this, dataSnapshot.child("Phone").getValue().toString(), Toast.LENGTH_SHORT).show();

                            Log.e("key", dataSnapshot.getKey());

                            User user = dataSnapshot.getValue(User.class);

                            if (user != null) {
                                if (user.isStaff.equals("true") && user.Password.equals(password.getText().toString())) {
                                    SharedPreferences.Editor edit = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                                    edit.putString("name", user.name);
                                    edit.putString("phone", phoneNumber.getText().toString());
                                    edit.putString("code", user.secureCode);
                                    //edit.putString("password", dataSnapshot.child(phoneNumber.getText().toString()).child("Password").getValue().toString());
                                    edit.putString("isStaff", user.isStaff);
                                    edit.apply();

                                    Intent intent = new Intent(SigninActivity.this, Home.class);
                                    passToVerify = user.Password;
                                    intent.putExtra("password", passToVerify);
                                    intent.putExtra("phone", phoneNumber.getText().toString());
                                    intent.putExtra("name", user.name);
                                    intent.putExtra("code", user.secureCode);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(SigninActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SigninActivity.this, "No user found!", Toast.LENGTH_SHORT).show();
                            }

//                            else {
//                                Toast.makeText(SigninActivity.this, "Invalid Phone number Or not Admin!", Toast.LENGTH_SHORT).show();
//                            }
                            alertDialog.dismiss();
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            alertDialog.dismiss();
                        }

                    });
                } else {
                    Toast.makeText(SigninActivity.this, "Password or Phone Number Is Empty!", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }


            }
        });
    }
}
