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

    int [] mScreenState;

    protected int mLocX;
    protected int mLocY;
    protected int mWidth;
    protected int mHeight;
    protected DrawerFragment mDrawer;

    protected void saveScreenState(){

        mScreenState = new int [mWidth * mHeight];
        mDrawer.loadPixels();
        int [] currState = mDrawer.pixels;

        int cursor = mLocX;
        int row = 1;

        for(int i = 0; i < mWidth * mHeight ; i++){
            mScreenState[i] = currState[cursor++];
            if(cursor > row * mDrawer.width){
                cursor = mDrawer.width * row + mLocX;
                row++;
            }
        }
    }

    protected void restoreScreenState(){

        int cursor = mLocX;
        int row = 1;

        for(int i = 0; i < mWidth * mHeight; i++){
            mDrawer.pixels[cursor++] = mScreenState[i];
            if(cursor > row * mDrawer.width){
                cursor = mDrawer.width * row + mLocX;
                row++;
            }
        }

        mDrawer.updatePixels();

    }

    public abstract void drawSelf();

    public abstract void hideSelf();

    public abstract void handlePress(Point loc);
}
