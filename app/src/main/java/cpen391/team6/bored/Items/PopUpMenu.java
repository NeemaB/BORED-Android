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

    protected int mLocX;        //X start location of the menu, this determines where to start saving screen state
    protected int mLocY;        //Y start location of the menu, this determines where to start saving screen state
    protected int mWidth;       //Width of the menu, this determines how many pixels we need to save
    protected int mHeight;      //Height of the menu, this determines how many pixels we need to save
    protected DrawerFragment mDrawer;

    /**********************************************************************************************
     * Function to save the state of the screen below where the pop up menu will be displayed
     * (so we can restore it later)
     *
     **********************************************************************************************/
    protected void saveScreenState(){

        /* Save the screen state for the menu as well as a small buffer region around the menu
         * in both the x and y direction, this is in case we modify the stroke width
         */
        mScreenState = new int [(mWidth + 1) * (mHeight + 3)];
        mDrawer.loadPixels();
        int [] currState = mDrawer.pixels;
        int cursor;
        int row;

        if(mLocY > 0){
            cursor = (mLocX - 1) + mDrawer.width * (mLocY - 1);
            row = mLocY;
        }else {
            cursor = mLocX - 1 + mDrawer.width * mLocY;
            row = mLocY + 1;
        }

        for(int i = 0; i < mScreenState.length ; i++){
            mScreenState[i] = currState[cursor++];
            if(cursor > row * mDrawer.width){
                cursor = mDrawer.width * row + mLocX - 1;
                row++;
            }
        }
    }

    /***********************************************************************************************
     * Function to restore the state of the screen so that what was visible before the pop up menu
     * was drawn will be shown again. Should be invoked whenever the pop up menu is deactivated
     *
     **********************************************************************************************/
    protected void restoreScreenState(){

        mDrawer.loadPixels();

        int cursor;
        int row;

        if(mLocY > 0){
            cursor = (mLocX - 1) + mDrawer.width * (mLocY - 1);
            row = mLocY;
        }else {
            cursor = mLocX - 1 + mDrawer.width * mLocY;
            row = mLocY + 1;
        }

        for(int i = 0; i < mScreenState.length; i++){
            mDrawer.pixels[cursor++] = mScreenState[i];
            if(cursor > row * mDrawer.width){
                cursor = mDrawer.width * row + mLocX - 1;
                row++;
            }
        }
        mDrawer.updatePixels();

//        mDrawer.strokeWeight(5);
//        mDrawer.stroke(255);
//        mDrawer.line(mLocX - 1, mLocY, mLocX - 1, mLocY + mHeight);
//        mDrawer.line(mLocX, mLocY + mHeight, mLocX + mWidth, mLocY + mHeight);

    }

    public abstract void drawSelf();

    public abstract void hideSelf();

    public abstract void handlePress(Point loc);
}
