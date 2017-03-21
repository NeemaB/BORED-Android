package cpen391.team6.bored.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cpen391.team6.bored.Activities.BluetoothActivity;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Items.ColourMenu;
import cpen391.team6.bored.Items.Command;
import cpen391.team6.bored.Items.PenWidthMenu;
import cpen391.team6.bored.Items.Point;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.ImageUtil;
import cpen391.team6.bored.Utility.UI_Util;
import processing.core.PApplet;

/**
 * Created by neema on 2017-03-12.
 */
public class DrawerFragment extends PApplet {

    public static String LOG_TAG = "Drawer_Fragment";

    private int mColourMenuX;
    private int mColourMenuY;
    private int mColourMenuWidth;
    private int mColourMenuHeight;
    private int mPenWidthMenuX;
    private int mPenWidthMenuY;
    private int mPenWidthMenuWidth;
    private int mPenWidthMenuHeight;

    private int mLastLocX;   //Save the last x location of the user's finger
    private int mLastLocY;   //Save the last y location of the user's finger
    private boolean mValid;  //Variable that determines whether the lastLocation is valid

    private ColourMenu mColourMenu;
    private PenWidthMenu mPenWidthMenu;
    private DrawerState mState;

    private PenWidthMenu.PenWidth mPenWidth;
    private ColourMenu.Colour mPenColour;

    private enum DrawerState {

        DRAWING,
        COLOUR_MENU_ACTIVE,
        WIDTH_MENU_ACTIVE,
        FILL_ACTIVE,
        TEXT_BOX_ACTIVE
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void setup() {

        background(255);
        mValid = false;

        mColourMenuX = width - 500;
        mColourMenuY = 0;
        mColourMenuWidth = 500;
        mColourMenuHeight = 500;

        mPenWidthMenuX = width - 450;
        mPenWidthMenuY = height / 7;
        mPenWidthMenuWidth = 450;
        mPenWidthMenuHeight = 150;

        mPenWidth = PenWidthMenu.PenWidth.SMALL;
        mPenColour = ColourMenu.Colour.BLACK;
        mState = DrawerState.DRAWING;


        /* Create the color menu and implement the press handler */
        mColourMenu = new ColourMenu(this, mColourMenuX, mColourMenuY,
                mColourMenuWidth, mColourMenuHeight) {
            @Override
            public void handlePress(ColourMenu.Colour colour, int flag) {
                if (flag == VALID_PRESS_HANDLE) {
                    mPenColour = colour;

                    /* If we are streaming to the device, send a command string
                     * to change the colour on the BORED
                     */
                    if(BoredApplication.isConnectedToBluetooth){
                        String cmd;
                        cmd = Command.createCommand(
                                Command.CHANGE_COLOUR,
                                mPenColour.getIndex());

                        BluetoothActivity.writeToBTDevice(cmd);
                        Log.d(LOG_TAG, "Sent change colour command to bluetooth:" + cmd);
                    }
                }
                deactivateColourMenu();
            }
        };

        mPenWidthMenu = new PenWidthMenu(this, mPenWidthMenuX, mPenWidthMenuY,
                mPenWidthMenuWidth, mPenWidthMenuHeight) {
            @Override
            public void handlePress(PenWidth penWidth, int flag) {
                if (flag == VALID_PRESS_HANDLE) {
                    mPenWidth = penWidth;

                    /* If we are streaming to the device, send a command string
                     * to change the pen width on the BORED
                     */
                    if (BoredApplication.isConnectedToBluetooth) {
                        String cmd;
                        cmd = Command.createCommand(
                                Command.CHANGE_PEN_WIDTH,
                                mPenWidth.getSize());

                        BluetoothActivity.writeToBTDevice(cmd);
                        Log.d(LOG_TAG, "Sent change pen width command to bluetooth:" + cmd);
                    }
                }
                deactivatePenWidthMenu();

            }
        };
    }

    @Override
    public void draw() {

        /* Drawing is done through user events so this loop is empty */

    }

    @Override
    public void mouseDragged() {

        switch (mState) {

            case DRAWING:

                stroke(mPenColour);
                strokeWeight(mPenWidth);
                
                /* We can draw lines now */
                if (!mValid) {
                    mValid = true;
                } else {
                    line(mLastLocX, mLastLocY, mouseX, mouseY);

                    if(BoredApplication.isConnectedToBluetooth){

                    /* Map the current and last point on the android draw space to a point
                     * on the device
                     */
//                        Point lastLoc = ImageUtil.mapPointToDevice(new Point(mLastLocX, mLastLocY),
//                                this.width, this.height);

                        Point currentLoc = ImageUtil.mapPointToDevice(new Point(mouseX, mouseY),
                                this.width, this.height);

                        //Log.d(LOG_TAG, "lastLoc on the device will be:" + lastLoc.locX + " " + lastLoc.locY);
                        Log.d(LOG_TAG, "currentLoc on the device will be:" + currentLoc.locX + " " + currentLoc.locY);

                        /* Create our parameter list out of the new points */
                        Integer [] params = new Integer [2];
                        params[0] = currentLoc.locX;
                        params[1] = currentLoc.locY;

                        BluetoothActivity.writeToBTDevice(
                                Command.createCommand(
                                        Command.POINT,
                                        params));
                    }
                }

        /* Save the last mouse location */
                mLastLocX = mouseX;
                mLastLocY = mouseY;
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
    public void mousePressed() {

        switch (mState) {


            case DRAWING:

                if(BoredApplication.isConnectedToBluetooth){

                    /* Map the current and last point on the android draw space to a point
                     * on the device
                     */
//                    Point lastLoc = ImageUtil.mapPointToDevice(new Point(mLastLocX, mLastLocY),
//                            this.width, this.height);

                    Point startPoint = ImageUtil.mapPointToDevice(new Point(mouseX, mouseY),
                            this.width, this.height);

                    //Log.d(LOG_TAG, "lastLoc on the device will be:" + lastLoc.locX + " " + lastLoc.locY);
                    Log.d(LOG_TAG, "Start drawing on the device from:" + startPoint.locX + " " + startPoint.locY);

                    /* Create our parameter list out of the new points */
                    Integer [] params = new Integer [2];
                    params[0] = startPoint.locX;
                    params[1] = startPoint.locY;

                    BluetoothActivity.writeToBTDevice(
                            Command.createCommand(
                                    Command.START_DRAWING,
                                    params));
                }

                break;

            case COLOUR_MENU_ACTIVE:
                /* Handle the press and revert back to our default state */
                mColourMenu.handlePress(new Point(mouseX, mouseY));


                break;

            case WIDTH_MENU_ACTIVE:
                mPenWidthMenu.handlePress(new Point(mouseX, mouseY));

                break;

            case TEXT_BOX_ACTIVE:

                break;

            case FILL_ACTIVE:

                break;
        }


    }

    @Override
    public void mouseReleased() {

        /* InValidate last location since we don't want to draw a line
         * as soon as the user presses the screen again
         */

        if(BoredApplication.isConnectedToBluetooth){

            BluetoothActivity.writeToBTDevice(
                    Command.createCommand(
                            Command.STOP_DRAWING));

        }

        mValid = false;

    }

    public void clearScreen() {

        switch (mState) {
            case DRAWING:
                /* Do Nothing */
                break;

            /* Clear Active Menus */
            case COLOUR_MENU_ACTIVE:
                deactivateColourMenu();
                break;
            case WIDTH_MENU_ACTIVE:
                deactivatePenWidthMenu();
                break;

            //TODO: when other menu items have been implemented, make sure to deactivate active menu items
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(getActivity(), R.style.DialogTheme));

        builder.setTitle(getString(R.string.clear_confirmation_title))
                .setMessage(getString(R.string.clear_confirmation_message))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        background(255);
                        if(BoredApplication.isConnectedToBluetooth){
                            String cmd;
                            cmd = Command.createCommand(Command.CLEAR);
                            BluetoothActivity.writeToBTDevice(cmd);
                            Log.d(LOG_TAG, "Sent clear screen command to bluetooth:" + cmd);

                        }
                        dialog.dismiss();
                    }
                }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog clearConfirmationDialog = builder.create();
        clearConfirmationDialog.setCanceledOnTouchOutside(true);
        clearConfirmationDialog.show();

        UI_Util.setDialogStyle(clearConfirmationDialog, getActivity());

    }
    
    public void undo(){

        //TODO: Implement this on the android device, currently it just sends commands to NIOS

        if(BoredApplication.isConnectedToBluetooth){
            String cmd;
            cmd = Command.createCommand(Command.UNDO);
            BluetoothActivity.writeToBTDevice(cmd);
            Log.d(LOG_TAG, "Sent undo command to device:" + cmd);
            
        }
        
    }

    public void redo(){

        //TODO: Implement this on the android device, currently it just sends commands to NIOS

        if(BoredApplication.isConnectedToBluetooth){
            String cmd;
            cmd = Command.createCommand(Command.REDO);
            BluetoothActivity.writeToBTDevice(cmd);
            Log.d(LOG_TAG, "Sent redo command to device:" + cmd);

        }

    }

    public void toggleColourMenu() {

        switch (mState) {
            case DRAWING:
                activateColourMenu();
                break;
            case COLOUR_MENU_ACTIVE:
                deactivateColourMenu();
                break;
            case WIDTH_MENU_ACTIVE:
                deactivatePenWidthMenu();
                break;

            //TODO: when other menu items have been implemented, make sure to deactivate active menu items
        }


    }

    /*
     * Change our current state and draw the colour menu
     */
    private void activateColourMenu() {

        mColourMenu.drawSelf();
        mState = DrawerState.COLOUR_MENU_ACTIVE;

    }

    /*
     * Change our current state and hide the colour menu
     */
    private void deactivateColourMenu() {

        mColourMenu.hideSelf();
        mState = DrawerState.DRAWING;

    }

    public void togglePenWidthMenu() {

        switch (mState) {

            case DRAWING:
                activatePenWidthMenu();
                break;
            case WIDTH_MENU_ACTIVE:
                deactivatePenWidthMenu();
                break;
            case COLOUR_MENU_ACTIVE:
                deactivateColourMenu();
                break;

            //TODO: when other menu items have been implemented, make sure to deactivate active menu items
        }

    }

    private void activatePenWidthMenu() {

        mPenWidthMenu.drawSelf();
        mState = DrawerState.WIDTH_MENU_ACTIVE;
    }

    private void deactivatePenWidthMenu() {

        mPenWidthMenu.hideSelf();
        mState = DrawerState.DRAWING;
    }

    /*
     * Fill function that is compatible with our own
     * enumerated type for colour
     */
    public void fill(ColourMenu.Colour colour) {
        fill(colour.getColourR(),
                colour.getColourG(),
                colour.getColourB());
    }

    /*
     * Stroke function that is compatible with our own
     * enumerated type for colour
     */
    public void stroke(ColourMenu.Colour colour) {
        stroke(colour.getColourR(),
                colour.getColourG(),
                colour.getColourB());
    }

    /*
     * Background function that is compatible with our own
     * enumerated type for colour
     */
    public void background(ColourMenu.Colour colour) {
        background(colour.getColourR(),
                colour.getColourG(),
                colour.getColourB());
    }

    public void strokeWeight(PenWidthMenu.PenWidth penWidth) {
        strokeWeight(penWidth.dp);
    }


    public ColourMenu.Colour getPenColour() {
        return mPenColour;
    }

    public byte[] saveScreen(){

        loadPixels();

        long startTime = System.currentTimeMillis();

        byte[] pixelData = new byte[width * height * 3];
        for(int i = 0; i < 20; i++){
             pixelData[i*3] =  (byte) ((pixels[i] >> 16) & 255);
             pixelData[i*3 + 1] =  (byte) ((pixels[i] >> 8) & 255);
             pixelData[i*3 + 2] =  (byte) (pixels[i] & 255);
            System.out.println("pixel data at index " + i + pixelData[i] + " " + pixelData[i+1] + " " + pixelData[i+2]);
        }

        Log.d(LOG_TAG, "Time to write bitmap as byte array:" + (System.currentTimeMillis() - startTime));

        return pixelData;
    }


    @Override
    public void settings() {

        Bundle arguments = getArguments();

        /* Set the size of the draw space based on the arguments */
        size((int) arguments.getDouble("width"), (int) arguments.getDouble("height"));


    }

    static public void main(String[] passedArgs) {
        String[] appletArgs = new String[]{"DrawSpace"};
        if (passedArgs != null) {
            PApplet.main(concat(appletArgs, passedArgs));
        } else {
            PApplet.main(appletArgs);
        }
    }


}
