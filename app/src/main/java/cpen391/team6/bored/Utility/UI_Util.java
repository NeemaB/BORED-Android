package cpen391.team6.bored.Utility;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import cpen391.team6.bored.R;

/**
 * Created by neema on 2017-03-13.
 */
public class UI_Util {

    // Set title divider color and text color
    public static void setDialogStyle(Dialog dialog, Context context) {
        int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));


    }
}
