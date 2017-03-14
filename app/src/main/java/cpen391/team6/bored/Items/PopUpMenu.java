package cpen391.team6.bored.Items;


import processing.core.PApplet;

/**
 * Created by neema on 2017-03-13.
 */
public abstract class PopUpMenu {

    protected int mLocX;
    protected int mLocY;
    protected int mWidth;
    protected int mHeight;
    protected PApplet mDrawer;

    public abstract void drawSelf();

    public abstract void hideSelf();

    public abstract void handlePress(Point loc);
}
