package com.example.hp.foodiesserverside;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.hp.foodiesserverside.Common.Common;
import com.example.hp.foodiesserverside.Remote.APIService;
import com.example.hp.foodiesserverside.model.MyResponse;
import com.example.hp.foodiesserverside.model.Notification;
import com.example.hp.foodiesserverside.model.Sender;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendMessage extends AppCompatActivity {

    MaterialEditText title;
    MaterialEditText message;
    FButton button;

    APIService apiService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        button = findViewById(R.id.send);

        apiService = Common.getFCMClient();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(TextUtils.isEmpty(title.getText().toString())) && !(TextUtils.isEmpty(message.getText().toString())))
                {

                    Notification notification = new Notification(message.getText().toString(),title.getText().toString());
                    Sender sender = new Sender();
                    sender.to = new StringBuilder("/topics/").append(Common.topicName).toString();
                    sender.notification = notification;

                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
                       // Log.e("Retrofit Response!", "Hello inside");
                        @Override
                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                            Log.e("Retrofit Response!", "Hello inside");
                            Log.e("ResponseCode", String.valueOf(response.code()));
                            if(response.isSuccessful())
                            {
                                Toast.makeText(SendMessage.this, "Message Sent!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<MyResponse> call, Throwable t) {
                            Log.e("Retrofit Exception", t.getMessage());

                        }
                    });
                }
            }
        });
    }
}
