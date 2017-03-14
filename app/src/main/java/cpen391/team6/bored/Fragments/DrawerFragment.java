package cpen391.team6.bored.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import cpen391.team6.bored.Items.ColourMenu;
import cpen391.team6.bored.Items.Point;
import processing.core.PApplet;

/**
 * Created by neema on 2017-03-12.
 */
public class DrawerFragment extends PApplet{

    private int lastLocx;   //Save the last x location of the user's finger
    private int lastLocy;   //Save the last y location of the user's finger
    private boolean valid;  //Variable that determines whether the lastLocation is valid

    private ColourMenu.Colour mPenColour;

    private ColourMenu mColourMenu;
    private DrawerState mState;

    private enum DrawerState {

        DRAWING,
        COLOUR_MENU_ACTIVE,
        WIDTH_MENU_ACTIVE,
        FILL_ACTIVE,
        TEXT_BOX_ACTIVE
    }


    @Override
    public void setup(){

        background(255);
        valid = false;
        mState = DrawerState.DRAWING;


        /* Create the color menu and implement the press handler */
        mColourMenu = new ColourMenu(this, width - 500, 0,
                                     500, 500) {
            @Override
            public void handlePress(ColourMenu.Colour colour, int flag){
                if (flag == VALID_PRESS_HANDLE){
                    mPenColour = colour;
                }else{
                    deactivateColourMenu();
                }
            }
        };
    }

    @Override
    public void draw(){

        /* Drawing is done through user events so this loop is empty */

    }

    @Override
    public void mouseDragged(){

        switch(mState){

            case DRAWING:

                if(mPenColour != null)
                    stroke(mPenColour.getColourR(), mPenColour.getColourG(), mPenColour.getColourB());
                else
                    stroke(0);
                /* We can draw lines now */
                if(!valid){
                    valid = true;
                }else{
                    line(lastLocx, lastLocy, mouseX, mouseY);
                }

        /* Save the last mouse location */
                lastLocx = mouseX;
                lastLocy = mouseY;
                break;

            case COLOUR_MENU_ACTIVE:

                break;

            case WIDTH_MENU_ACTIVE:

                break;

            case TEXT_BOX_ACTIVE:

                break;

            case FILL_ACTIVE:

                break;
        }


    }
    
    @Override
    public void mousePressed(){

        switch(mState){

            case DRAWING:
                
                break;

            case COLOUR_MENU_ACTIVE:
                /* Handle the press and revert back to our default state */
                mColourMenu.handlePress(new Point(mouseX, mouseY));
                mState = DrawerState.DRAWING;
                mColourMenu.hideSelf();
                
                break;

            case WIDTH_MENU_ACTIVE:

                break;

            case TEXT_BOX_ACTIVE:

                break;

            case FILL_ACTIVE:

                break;
        }
        
        
    }

    @Override
    public void mouseReleased(){

        /* Invalidate last location since we don't want to draw a line
         * as soon as the user presses the screen again
         */
        valid = false;
    }

    public void toggleColourMenu(){

        if(mState == DrawerState.DRAWING){
            activateColourMenu();
        }else{
            deactivateColourMenu();
        }

    }

    private void activateColourMenu(){

        mColourMenu.drawSelf();
        mState = DrawerState.COLOUR_MENU_ACTIVE;

    }

    private void deactivateColourMenu(){

        mColourMenu.hideSelf();
        mState = DrawerState.DRAWING;

    }


    @Override
    public void settings() {

        Bundle arguments = getArguments();

        /* Set the size of the draw space based on the arguments */
        size((int) arguments.getDouble("width"), (int) arguments.getDouble("height"));


    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[] { "DrawSpace" };
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }



}
