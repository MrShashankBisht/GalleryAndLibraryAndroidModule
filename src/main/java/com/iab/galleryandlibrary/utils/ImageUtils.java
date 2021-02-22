package com.iab.galleryandlibrary.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class ImageUtils {

    public static int dpToPx(Context context, int dp) {
        if (context != null) {
            // Get the screen's density scale
            final float density = context.getResources().getDisplayMetrics().density;
            // Convert the dps to pixels, based on density scale
            int pixels = (int) (dp * density + 0.5f);
            return pixels;
        } else {
            return -1;
        }
    }

    public static float pxToDp(Context context, float px){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
