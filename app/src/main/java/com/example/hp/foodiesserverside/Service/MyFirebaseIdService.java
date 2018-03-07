package com.example.hp.foodiesserverside.Service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.hp.foodiesserverside.Home;
import com.example.hp.foodiesserverside.model.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static android.content.ContentValues.TAG;

/**
 * Created by hp on 3/7/2018.
 */

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        if (Home.PHONE != null)
            updateUserToken(token);
    }

    private void updateUserToken(String updatedToken) {

        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(updatedToken, true);

        tokenRef.child(Home.PHONE).setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e("TokenUpdated", task.getResult().toString());

                } else
                    Log.e(TAG, "onComplete: " + " Something went wrong");
            }
        });

    }
}
