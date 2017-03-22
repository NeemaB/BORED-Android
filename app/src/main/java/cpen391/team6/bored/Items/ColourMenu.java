package cpen391.team6.bored.Items;

import android.content.Context;
import android.widget.FrameLayout;

import java.util.EnumSet;

import cpen391.team6.bored.Fragments.DrawerFragment;
import processing.core.PApplet;

/**
 * Created by neema on 2017-03-13.
 */
public abstract class ColourMenu extends PopUpMenu {


    private EnumSet<Colour> mColourSet;
    private static ColourMenu mColourMenu;


    /***********************************************************************************************
     * Every colour that will be displayed in the colour menu is represented here
     * each entry has 3 int values representing their RGB values
     *
     **********************************************************************************************/
    public enum Colour {

        RED(0xFF, 0x00, 0x00, 0, 0, 0),
        DARKRED(0x8B, 0x00, 0x00, 0, 1, 1),
        ORANGE(0xFF, 0xA5, 0x00, 0, 2, 2),
        PINK(0xFF, 0xC0, 0xCB, 0, 3, 3),
        TEAL(0xFF, 0xFF, 0x00, 1, 0, 4),
        LIME(0x32, 0xCD, 0x32, 1, 1, 5),
        DARK_SEA_GREEN(0x8F, 0xBC, 0x8F, 1, 3, 6),
        GREEN(0x00, 0xFF, 0x00, 1, 2, 7),
        CYAN(0x00, 0xFF, 0xFF, 2, 0, 8),
        NAVY(0x00, 0x00, 0x80, 2, 1, 9),
        PURPLE(0x80, 0x00, 0x80, 2, 2, 10),
        MAGENTA(0xFF, 0x00, 0xFF, 2, 3, 11),
        CHOCOLATE(0xD2, 0x69, 0x1E, 3, 0, 12),
        SLATE_GRAY(0x70, 0x80, 0x90, 3, 1, 13),
        BLACK(0x00, 0x00, 0x00, 3, 2, 14),
        WHITE(0xFF, 0xFF, 0xFF, 3, 3, 15);


        private ColourRGB colour;
        private int row;
        private int coloumn;
        private int index;

        private Colour(int colourR, int colourG, int colourB, int row, int coloumn, int index) {
            this.colour = new ColourRGB(colourR, colourG, colourB);
            this.row = row;
            this.coloumn = coloumn;
            this.index = index;

        }

        public int getIndex() { return index;}

        public int getColourR() {
            return colour.R;
        }

        public int getColourG() {
            return colour.G;
        }

        public int getColourB() {
            return colour.B;
        }

        public static Colour convertRGBToColour (int R, int G, int B){

            EnumSet <Colour> colours = EnumSet.allOf(Colour.class);
            for(Colour colour : colours){
                if(colour.getColourR() == R
                        && colour.getColourG() == G
                        && colour.getColourB() == B){
                    return colour;
                }
            }
            return null;

        }
    }

    /**********************************************************************************************
     * Constructor for a colour menu
     *
     * @Param PApplet drawer, this will be the drawer used for subsequent draw events
     * @Param int locX, the x location of this menu in the draw space
     * @Param int locY, the y location of this menu in the draw space
     * @Param int menuWidth, the width of the menu
     * @Param int menuHeight, the height of the menu
     **********************************************************************************************/

    public ColourMenu(DrawerFragment drawer, int locX, int locY,
                      int menuWidth, int menuHeight) {

        this.mDrawer = drawer;
        this.mColourSet = EnumSet.allOf(Colour.class);
        this.mWidth = menuWidth;
        this.mHeight = menuHeight;
        this.mLocX = locX;
        this.mLocY = locY;


    }

    @Override
    public void drawSelf() {

        mDrawer.stroke(0);
        mDrawer.strokeWeight(1);

        saveScreenState();

        /* Draw a rectangle for each colour in our colour set */
        for (Colour colour : mColourSet) {
            mDrawer.fill(colour);

            mDrawer.rect(this.mLocX + colour.coloumn * mWidth / 4,
                    this.mLocY + colour.row * mHeight / 4,
                    mWidth / 4,
                    mHeight / 4);
        }




    }


    @Override
    public void hideSelf() {

        restoreScreenState();


    }

    /***********************************************************************************************
     * This function can be handled by the draw fragment itself
     * the only part handled in this class is the identification of the colour
     * that was picked from the menu
     *
     * @param colour, the colour that was chosen, null if no colour was chosen
     * @param flag, a flag indicating whether a colour was chosen or not
     *
     **********************************************************************************************/
    public abstract void handlePress(Colour colour, int flag);


    /**********************************************************************************************
     * Find which colour, if any was chosen, relay to the
     * drawer implemented press handler
     *
     * @param loc, location of a press, this must be passed from the drawer fragment
     **********************************************************************************************/

    @Override
    public void handlePress(Point loc) {

        for (Colour colour : mColourSet) {

            /* Check if press was within colour bounds */
            if (loc.locX <= mLocX + (colour.coloumn + 1) * mWidth / 4
                    && loc.locX >= mLocX + (colour.coloumn) * mWidth / 4
                    && loc.locY <= mLocY + (colour.row + 1) * mHeight / 4
                    && loc.locY >= mLocY + (colour.row) * mHeight / 4) {
                handlePress(colour, VALID_PRESS_HANDLE);

            }
        }

        /* User pressed a different part of the screen */
        handlePress(null, INVALID_PRESS_HANDLE);


    }


}
