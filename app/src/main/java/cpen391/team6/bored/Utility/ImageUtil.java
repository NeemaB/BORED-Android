package cpen391.team6.bored.Utility;

import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Items.Point;

/**
 * Created by neema on 2017-03-20.
 */
public class ImageUtil {

    public static Point mapPointToDevice(Point point, int drawSpaceWidth, int drawSpaceHeight){

        int x = point.locX;
        int y = point.locY;

        double widthRatio = (double) x / (double) drawSpaceWidth;
        double heightRatio = (double) y / (double) drawSpaceHeight;

        int bored_locX = (int) Math.floor(widthRatio * BoredApplication.boredScreenWidth);
        int bored_locY = (int) Math.floor(heightRatio * BoredApplication.boredScreenHeight);

        return new Point(bored_locX, bored_locY);
    }
}