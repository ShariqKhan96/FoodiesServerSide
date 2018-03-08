package com.example.hp.foodiesserverside.Common;

import com.example.hp.foodiesserverside.Remote.APIService;
import com.example.hp.foodiesserverside.Remote.RetrofitClient;
import com.example.hp.foodiesserverside.model.Request;
import com.example.hp.foodiesserverside.model.Requests;

/**
 * Created by hp on 2/24/2018.
 */

public class Common {
    public static Request currentRequest;
    public static double currentLat = 0;
    public static double currentLng= 0;
    public static double destinationLat= 0;
    public static double destinationLng= 0;
    public static String convertcodeToStatus(String status) {
        if (status.equals("0"))
            return "Order Placed";
        else if (status.equals("1"))
            return "On My Way!";
        else
            return "Shipped";

    }
    public static String FCM_BASE_URL = "https://fcm.googleapis.com/";
    public static String topicName = "News";


    public static APIService getFCMClient() {
        return RetrofitClient.getClinet(FCM_BASE_URL).create(APIService.class);
    }

}
