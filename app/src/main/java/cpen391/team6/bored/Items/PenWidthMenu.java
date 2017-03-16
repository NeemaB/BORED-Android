package cpen391.team6.bored.Items;

import java.util.EnumSet;

import cpen391.team6.bored.Fragments.DrawerFragment;
import processing.core.PApplet;

/**
 * Created by neema on 2017-03-13.
 */
public abstract class PenWidthMenu extends PopUpMenu {

    private EnumSet<PenWidth> mPenWidthSet;

    public enum PenWidth {
        SMALL(5, 2),
        MEDIUM(15, 1),
        LARGE(30, 0);

        public int dp;
        private int index;

        private PenWidth(int dp, int index) {
            this.dp = dp;
            this.index = index;
        }
    }


    public PenWidthMenu(DrawerFragment drawer, int locX, int locY,
                        int menuWidth, int menuHeight) {

        this.mDrawer = drawer;
        this.mLocX = locX;
        this.mLocY = locY;
        this.mWidth = menuWidth;
        this.mHeight = menuHeight;

        this.mPenWidthSet = EnumSet.allOf(PenWidth.class);
    }


    @Override
    public void drawSelf() {

        mDrawer.stroke(0);
        mDrawer.strokeWeight(1);

        saveScreenState();

        for (PenWidth penWidth : mPenWidthSet) {

            mDrawer.fill(ColourMenu.Colour.SLATE_GRAY);
            mDrawer.rect(this.mLocX + penWidth.index * mWidth / 3,
                    this.mLocY,
                    mWidth / 3,
                    mHeight);

            mDrawer.fill(mDrawer.getPenColour());

            mDrawer.ellipse(this.mLocX + penWidth.index * mWidth/3 + mWidth/6,
                    this.mLocY + mHeight/2,
                    2*penWidth.dp,
                    2*penWidth.dp);

        }
    }

    @Override
    public void hideSelf() {

        restoreScreenState();

    }

    public abstract void handlePress(PenWidth penWidth, int flag);

    public void handlePress(Point loc) {

        for(PenWidth penWidth : mPenWidthSet){

        /* Check if press was within colour bounds */
            if (loc.locX <= mLocX + (penWidth.index + 1) * mWidth / 3
                    && loc.locX >= mLocX + (penWidth.index) * mWidth / 3
                    && loc.locY <= mLocY + mHeight
                    && loc.locY >= mLocY) {

                handlePress(penWidth, VALID_PRESS_HANDLE);

            }
        }

        /* User pressed a different part of the screen */
        handlePress(null, INVALID_PRESS_HANDLE);

    }


}
