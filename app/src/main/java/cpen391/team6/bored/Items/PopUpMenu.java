package cpen391.team6.bored.Items;


import java.util.EnumSet;

import cpen391.team6.bored.Fragments.DrawerFragment;
import processing.core.PApplet;

/**
 * Created by neema on 2017-03-13.
 */
public abstract class PopUpMenu {

    public static int VALID_PRESS_HANDLE = 1; // Set if a press handler can successfully find the corresponding item
    public static int INVALID_PRESS_HANDLE = 0; // Set if a press handler can't successfully find the corresponding item


    protected int mLocX;
    protected int mLocY;
    protected int mWidth;
    protected int mHeight;
    protected DrawerFragment mDrawer;

    public abstract void drawSelf();

    public abstract void hideSelf();

    public abstract void handlePress(Point loc);
}
