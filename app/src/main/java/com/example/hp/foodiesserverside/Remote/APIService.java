package com.example.hp.foodiesserverside.Remote;



import com.example.hp.foodiesserverside.model.MyResponse;
import com.example.hp.foodiesserverside.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by hp on 3/7/2018.
 */

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA0_cagbY:APA91bHcmcRzl_PAr7FnqFyrW_F8mvL5yC97LOEjLENOdmP0DyNh_gmZuFC3k92eVDhMykT1p2vGHgOPoEOPKxKhBJ1XMag1dROv3LhBDLpooJkeDa4RtLsiCp7lRY1MK2K7gq7qZFDi"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
