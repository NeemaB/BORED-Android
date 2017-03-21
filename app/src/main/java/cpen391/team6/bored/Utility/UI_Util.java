package cpen391.team6.bored.Utility;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.Set;

import cpen391.team6.bored.R;

/**
 * Created by neema on 2017-03-13.
 */
public class UI_Util {

    /**********************************************************************************************
     * Set title divider color and text color of a dialog
     *
     * @param dialog The dialog that we want to style
     * @param context A valid context that will allow us to access the devices resources
     **********************************************************************************************/
    public static void setDialogStyle(Dialog dialog, Context context) {
        int titleDividerId = context.getResources().getIdentifier("titleDivider", "id", "android");
        View titleDivider = dialog.findViewById(titleDividerId);
        if (titleDivider != null)
            titleDivider.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));


    }

    /**********************************************************************************************
     *  Set the colour of the overflow menu to the desired color
     *
     *  @param activity An activity that is currently alive
     *  @param color An integer representation of a color
     *
     **********************************************************************************************/
    public static void setOverflowButtonColor(final Activity activity, final int color) {
        final String overflowDescription = activity.getString(R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        final ViewTreeObserver.OnGlobalLayoutListener listener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final ArrayList<View> outViews = new ArrayList<View>();
                decorView.findViewsWithText(outViews, overflowDescription, View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                if (outViews.isEmpty()) {
                    return;
                }
                AppCompatImageView overflow = (AppCompatImageView) outViews.get(0);
                overflow.setColorFilter(color);
                viewTreeObserver.removeOnGlobalLayoutListener(this);
            }
        };
        viewTreeObserver.addOnGlobalLayoutListener(listener);
    }
}
