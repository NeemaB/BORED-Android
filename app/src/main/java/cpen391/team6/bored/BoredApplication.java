package cpen391.team6.bored;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.EntypoModule;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.MaterialModule;

/**
 * Created by neema on 2017-03-12.
 */
public class BoredApplication extends Application {

    /* Global variable that the application can set to see if we are connected to the bluetooth chip */
    public static boolean isConnectedToBluetooth = false;

    /* Global variables for the width and height of the actual device in pixels */
    public static int boredScreenWidth = 681;
    public static int boredScreenHeight = 478;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    public void onCreate() {
        super.onCreate();

        /* Make use of the following icon bitmap libraries */
        Iconify.
                with(new FontAwesomeModule())
                .with(new EntypoModule())
                .with(new MaterialModule());


    }
}
