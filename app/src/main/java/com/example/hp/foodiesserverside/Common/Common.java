package com.example.hp.foodiesserverside.Common;

/**
 * Created by hp on 2/24/2018.
 */

public class Common {
    public static String convertcodeToStatus(String status) {
        if (status.equals("0"))
            return "Order Placed";
        else if (status.equals("1"))
            return "On My Way!";
        else
            return "Shipped";

    }
}
