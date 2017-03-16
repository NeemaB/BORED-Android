package cpen391.team6.bored;

import android.app.Application;

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
