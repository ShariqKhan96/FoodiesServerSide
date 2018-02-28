package com.example.hp.foodiesserverside;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

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
                    dbRef = FirebaseDatabase.getInstance().getReference("User");
                    dbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //  Toast.makeText(SigninActivity.this, dataSnapshot.child("Phone").getValue().toString(), Toast.LENGTH_SHORT).show();

                            if (dataSnapshot.child(phoneNumber.getText().toString()).exists() &&
                                    dataSnapshot.child(phoneNumber.getText().toString()).child("isStaff").getValue().toString().equals("true")) {
                                if (dataSnapshot.child(phoneNumber.getText().toString()).child("Password").getValue().toString().equals(password.getText().toString())) {
                                    SharedPreferences.Editor edit = getSharedPreferences("SharedPreferences", MODE_PRIVATE).edit();
                                    edit.putString("name", dataSnapshot.child(phoneNumber.getText().toString()).child("Name").getValue().toString());
                                    edit.putString("phone", dataSnapshot.child(phoneNumber.getText().toString()).child("Phone").getValue().toString());
                                    edit.putString("code", dataSnapshot.child(phoneNumber.getText().toString()).child("security_code").getValue().toString());
                                    edit.putString("password", dataSnapshot.child(phoneNumber.getText().toString()).child("Password").getValue().toString());
                                    edit.putString("isStaff", dataSnapshot.child(phoneNumber.getText().toString()).child("isStaff").getValue().toString());
                                    edit.apply();

                                    Intent intent = new Intent(SigninActivity.this, Home.class);
                                    passToVerify = dataSnapshot.child(phoneNumber.getText().toString()).child("Password").getValue().toString();
                                    intent.putExtra("password",passToVerify);
                                    intent.putExtra("phone", phoneNumber.getText().toString());
                                    intent.putExtra("name",dataSnapshot.child(phoneNumber.getText().toString()).child("Name").getValue().toString());
                                    intent.putExtra("code",dataSnapshot.child(phoneNumber.getText().toString()).child("security_code").getValue().toString());
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(SigninActivity.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SigninActivity.this, "Invalid Phone number Or not Admin!", Toast.LENGTH_SHORT).show();
                            }
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
