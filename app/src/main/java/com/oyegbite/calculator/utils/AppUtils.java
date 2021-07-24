package com.oyegbite.calculator.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class AppUtils {
    private AppUtils() {}

    public static Toast showToastLong(Context context, String message, Toast prevToast, int toastLength) {
        // cancel the previous prevToast message before setting a new one
        if (prevToast != null) {
            prevToast.cancel();
        }
        Toast newToast = Toast.makeText(
                context,
                message,
                toastLength
        );
        // Center align text in prevToast.
        TextView view = (TextView) newToast.getView().findViewById(android.R.id.message);
        if(view != null) view.setGravity(Gravity.BOTTOM);
        // Set the prevToast view at the center of the device.
        newToast.setGravity(Gravity.CENTER, 0, 0);
        newToast.show();
        return newToast;
    }
}
