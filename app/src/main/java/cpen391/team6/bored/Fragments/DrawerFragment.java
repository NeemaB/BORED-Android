package cpen391.team6.bored.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Stack;

import cpen391.team6.bored.Activities.BluetoothActivity;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Items.ColourMenu;
import cpen391.team6.bored.Items.Command;
import cpen391.team6.bored.Items.PenWidthMenu;
import cpen391.team6.bored.Items.Point;
import cpen391.team6.bored.Items.PointList;
import cpen391.team6.bored.Items.UndoList;
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

    private UndoList mUndoListHead; // Implements an undo list so that we can undo and redo

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
    public void onStart() {
        super.onStart();
    }

    /***********************************************************************************************
     * Initialize our drawer parameters, setup the draw space and provide implementations for the
     * press handlers provided by the pop up menu classes
     **********************************************************************************************/
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
                    if (BoredApplication.isConnectedToBluetooth) {
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

        /* Perform a different action depending on what state we are in */
        switch (mState) {

            case DRAWING:

                stroke(mPenColour);
                strokeWeight(mPenWidth);
                
                /* We can draw lines now */
                if (!mValid) {
                    mValid = true;

                    if (mUndoListHead == null) {
                        UndoList tmpUndoList = new UndoList(new Point(mouseX, mouseY), this.mPenColour, this.mPenWidth);
                        mUndoListHead = new UndoList(new Point(mouseX, mouseY), this.mPenColour, this.mPenWidth);
                        tmpUndoList.setNext(mUndoListHead);
                        mUndoListHead.setPrev(tmpUndoList);

                    } else {
                        mUndoListHead.setNext(new UndoList(new Point(mouseX, mouseY), this.mPenColour, this.mPenWidth));
                        mUndoListHead.getNext().setPrev(mUndoListHead);
                        mUndoListHead = mUndoListHead.getNext();
                    }

                } else {

                    mUndoListHead.getPointList().setNext(new PointList(new Point(mouseX, mouseY)));
                    mUndoListHead.setPointList(mUndoListHead.getPointList().getNext());

                    line(mLastLocX, mLastLocY, mouseX, mouseY);

                    if (BoredApplication.isConnectedToBluetooth) {

                       /* Map the current point on the android draw space to a point
                        * on the device
                        */

                        Point currentLoc = ImageUtil.mapPointToDevice(new Point(mouseX, mouseY),
                                this.width, this.height);

                        Log.d(LOG_TAG, "currentLoc on the device will be:" + currentLoc.locX + " " + currentLoc.locY);

                        /* Create our parameter list out of the new points */
                        Integer[] params = new Integer[2];
                        params[0] = currentLoc.locX;
                        params[1] = currentLoc.locY;

                        /* Specify a new point that the NIOS II can draw to */
                        BluetoothActivity.writeToBTDevice(
                                Command.createCommand(
                                        Command.POINT,
                                        params));
                    }
                }

                /* Save the current mouse position for future line drawing */
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

        /* Perform a different action depending on what state we are in */
        switch (mState) {


            case DRAWING:

                if (BoredApplication.isConnectedToBluetooth) {

                    /* Map the current and last point on the android draw space to a point
                     * on the device
                     */
                    Point startPoint = ImageUtil.mapPointToDevice(new Point(mouseX, mouseY),
                            this.width, this.height);

                    Log.d(LOG_TAG, "Start drawing on the device from:" + startPoint.locX + " " + startPoint.locY);

                    /* Create our parameter list out of the new points */
                    Integer[] params = new Integer[2];
                    params[0] = startPoint.locX;
                    params[1] = startPoint.locY;

                    /* Indicate to the NIOS II that we are starting to draw */
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

                //TODO: Implement fill on the android device as well
                loadPixels();
                fill(mouseX, mouseY, pixelDataToColour(get(mouseX, mouseY)), mPenColour);

                if (BoredApplication.isConnectedToBluetooth) {

                    /* Map the current and last point on the android draw space to a point
                     * on the device
                     */
                    Point fillPoint = ImageUtil.mapPointToDevice(new Point(mouseX, mouseY),
                            this.width, this.height);

                    Log.d(LOG_TAG, "Send fill command to bluetooth at:" + fillPoint.locX + " " + fillPoint.locY);

                    /* Create our parameter list out of the new points */
                    Integer[] params = new Integer[2];
                    params[0] = fillPoint.locX;
                    params[1] = fillPoint.locY;

                    /* Tell NIOS II to fill in around the point specified in the parameter list */
                    BluetoothActivity.writeToBTDevice(
                            Command.createCommand(
                                    Command.FILL,
                                    params));

                }

                /* Set the DrawerState back to DRAWING */
                mState = DrawerState.DRAWING;
                break;
        }


    }

    @Override
    public void mouseReleased() {

        if (BoredApplication.isConnectedToBluetooth) {

            /* Tell NIOS II to stop drawing now */
            BluetoothActivity.writeToBTDevice(
                    Command.createCommand(
                            Command.STOP_DRAWING));

        }

        /* InValidate last location since we don't want to draw a line
         * as soon as the user presses the screen again
         */

        mValid = false;

    }

    /*********************************************************************************************
     * Initializes the NIOS II screen so that it has the same parameters as
     * the current drawer, we need to send the pen colour and pen size
     *********************************************************************************************/
    public void initRemoteScreen() {

        String cmd;

        /* Set the pen colour on NIOS II */
        cmd = Command.createCommand(
                Command.CHANGE_COLOUR,
                mPenColour.getIndex());

        BluetoothActivity.writeToBTDevice(cmd);
        Log.d(LOG_TAG, "Sent change colour command to bluetooth:" + cmd);

        /* Set the pen size on the NIOS II */
        cmd = Command.createCommand(
                Command.CHANGE_PEN_WIDTH,
                mPenWidth.getSize());

        BluetoothActivity.writeToBTDevice(cmd);
        Log.d(LOG_TAG, "Sent change pen width command to bluetooth:" + cmd);

    }

    public void fill(final int x, final int y, final ColourMenu.Colour colourToFill, final ColourMenu.Colour fillColour) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Stack<Point> pointStack = new Stack<>();
                Point nextPoint;
                pointStack.push(new Point(x, y));
                ColourMenu.Colour pixelColour;

                do {
                    nextPoint = pointStack.pop();

                    set(nextPoint.locX, nextPoint.locY,
                            color(fillColour.getColourR(),
                                    fillColour.getColourG(),
                                    fillColour.getColourB()));

                    pixels[nextPoint.locY * width + nextPoint.locX] = color(fillColour.getColourR(),
                            fillColour.getColourG(),
                            fillColour.getColourB());

                    //updatePixels();
                    pixelColour = pixelDataToColour(get(nextPoint.locX + 1, nextPoint.locY));
                    if (pixelColour == colourToFill && pixelColour != fillColour) {

                        pointStack.push(new Point(nextPoint.locX + 1, nextPoint.locY));
                    }else{
                        set(nextPoint.locX + 1, nextPoint.locY,
                                color(fillColour.getColourR(),
                                        fillColour.getColourG(),
                                        fillColour.getColourB()));
                    }
                    pixelColour = pixelDataToColour(get(nextPoint.locX, nextPoint.locY + 1));
                    if (pixelColour == colourToFill && pixelColour != fillColour) {

                        pointStack.push(new Point(nextPoint.locX, nextPoint.locY + 1));
                    }else{
                        set(nextPoint.locX, nextPoint.locY + 1,
                                color(fillColour.getColourR(),
                                        fillColour.getColourG(),
                                        fillColour.getColourB()));
                    }
                    pixelColour = pixelDataToColour(get(nextPoint.locX - 1, nextPoint.locY));
                    if (pixelColour == colourToFill && pixelColour != fillColour) {
                        pointStack.push(new Point(nextPoint.locX - 1, nextPoint.locY));
                    }else{
                        set(nextPoint.locX - 1, nextPoint.locY,
                                color(fillColour.getColourR(),
                                        fillColour.getColourG(),
                                        fillColour.getColourB()));
                    }
                    pixelColour = pixelDataToColour(get(nextPoint.locX, nextPoint.locY - 1));
                    if (pixelColour == colourToFill && pixelColour != fillColour) {
                        pointStack.push(new Point(nextPoint.locX, nextPoint.locY - 1));
                    }else{
                        set(nextPoint.locX, nextPoint.locY - 1,
                                color(fillColour.getColourR(),
                                        fillColour.getColourG(),
                                        fillColour.getColourB()));
                    }

                } while (!pointStack.isEmpty());
            }
        });
        thread.start();


    }

    /***********************************************************************************************
     * Notify the user that the drawing space will be cleared, then clear it if the user confirms.
     * This function will also clear the drawing space on the NIOS II if there is an active
     * bluetooth session
     **********************************************************************************************/
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

                        mUndoListHead = null;
                        mValid = false;

                        background(255);
                        if (BoredApplication.isConnectedToBluetooth) {
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

    /**********************************************************************************************
     * Clear the last sequence of drawn lines from the draw space. A sequence is considered to be
     * every line drawn between when the user first touches the screen and when the user lets go
     * of the screen.
     * <p>
     * Note: This function will also undo the sequence of lines drawn on the NIOS II if there is an
     * active bluetooth session
     **********************************************************************************************/
    public void undo() {

        //TODO: Implement this on the android device, currently it just sends commands to NIOS

        if (mUndoListHead != null) {

            strokeWeight(mUndoListHead.getPenWidth().dp + 1);
            stroke(255);

            PointList head = mUndoListHead.getPointListHead();

            while (head.getNext() != null) {
                line(head.getPoint().locX,
                        head.getPoint().locY,
                        head.getNext().getPoint().locX,
                        head.getNext().getPoint().locY);

                head = head.getNext();
            }

            if (mUndoListHead.getPrev() != null) {
                mUndoListHead = mUndoListHead.getPrev();
            }
        }

        if (BoredApplication.isConnectedToBluetooth) {
            String cmd;
            cmd = Command.createCommand(Command.UNDO);
            BluetoothActivity.writeToBTDevice(cmd);
            Log.d(LOG_TAG, "Sent undo command to device:" + cmd);

        }


    }

    /**********************************************************************************************
     * Redraw the last sequence of drawn lines that was cleared with Undo
     * <p>
     * Note: This function will also clear the drawing space on the NIOS II if there is an active
     * bluetooth session
     **********************************************************************************************/
    public void redo() {

        //TODO: Implement this on the android device, currently it just sends commands to NIOS

        if (mUndoListHead != null) {

            if (mUndoListHead.getNext() != null) {
                mUndoListHead = mUndoListHead.getNext();
                PointList head = mUndoListHead.getPointListHead();

                stroke(mUndoListHead.getColour());
                strokeWeight(mUndoListHead.getPenWidth());

                while (head.getNext() != null) {
                    line(head.getPoint().locX,
                            head.getPoint().locY,
                            head.getNext().getPoint().locX,
                            head.getNext().getPoint().locY);

                    head = head.getNext();
                }
            }
        }


        if (BoredApplication.isConnectedToBluetooth) {
            String cmd;
            cmd = Command.createCommand(Command.REDO);
            BluetoothActivity.writeToBTDevice(cmd);
            Log.d(LOG_TAG, "Sent redo command to device:" + cmd);

        }

    }


    /**********************************************************************************************
     * This function will toggle the colour menu state, if it is currently visible it will be hidden
     * and deactivated, otherwise it will be shown and active
     **********************************************************************************************/
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

    /**********************************************************************************************
     * Change our current state and draw the colour menu
     **********************************************************************************************/
    private void activateColourMenu() {

        mColourMenu.drawSelf();
        mState = DrawerState.COLOUR_MENU_ACTIVE;

    }

    /***********************************************************************************************
     * Change our current state and hide the colour menu
     **********************************************************************************************/
    private void deactivateColourMenu() {

        mColourMenu.hideSelf();
        mState = DrawerState.DRAWING;

    }

    /**********************************************************************************************
     * Toggles the fill feature
     **********************************************************************************************/
    public void toggleFillActive() {

        if (mState == DrawerState.FILL_ACTIVE) {
            mState = DrawerState.DRAWING;
        } else {
            mState = DrawerState.FILL_ACTIVE;
        }

    }

    /***********************************************************************************************
     * Toggles the state of the pen width menu
     **********************************************************************************************/
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

    /**********************************************************************************************
     * Fill function that is compatible with our own
     * enumerated type for colour
     **********************************************************************************************/
    public void fill(ColourMenu.Colour colour) {
        fill(colour.getColourR(),
                colour.getColourG(),
                colour.getColourB());
    }

    /**********************************************************************************************
     * Stroke function that is compatible with our own
     * enumerated type for colour
     **********************************************************************************************/
    public void stroke(ColourMenu.Colour colour) {
        stroke(colour.getColourR(),
                colour.getColourG(),
                colour.getColourB());
    }

    /**********************************************************************************************
     * Background function that is compatible with our own
     * enumerated type for colour
     **********************************************************************************************/
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

    /***********************************************************************************************
     * Function to save the screen state using a byte array
     *
     * @return a byte array with the following format
     * <p>
     * byte[i + 0] = value of R colour
     * byte[i + 1] = value of G colour
     * byte[i + 2] = value of B colour
     * <p>
     * The pixel location can be determined by the index
     * y = i / mWidth
     * x = i % mWidth
     **********************************************************************************************/
    public byte[] saveScreen() {

        loadPixels();

        long startTime = System.currentTimeMillis();

        byte[] pixelData = new byte[width * height * 3];
        for (int i = 0; i < 20; i++) {
            pixelData[i * 3] = (byte) ((pixels[i] >> 16) & 255);
            pixelData[i * 3 + 1] = (byte) ((pixels[i] >> 8) & 255);
            pixelData[i * 3 + 2] = (byte) (pixels[i] & 255);
            System.out.println("pixel data at index " + i + pixelData[i] + " " + pixelData[i + 1] + " " + pixelData[i + 2]);
        }

        Log.d(LOG_TAG, "Time to write bitmap as byte array:" + (System.currentTimeMillis() - startTime));

        return pixelData;
    }

    public ColourMenu.Colour pixelDataToColour(int pixel) {
        int R = (pixel >> 16) & 255;
        int G = (pixel >> 8) & 255;
        int B = (pixel & 255);

        return (ColourMenu.Colour.convertRGBToColour(R, G, B));

    }

    public int colourToPixelData(ColourMenu.Colour colour) {
        int returnData = (colour.getColourR() << 16)
                | (colour.getColourG() << 8)
                | colour.getColourB();

        return returnData;
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
