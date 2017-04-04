package cpen391.team6.bored.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import cpen391.team6.bored.Activities.BluetoothActivity;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Data.Note;
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
import processing.core.PImage;


/**
 * Created by neema on 2017-03-12.
 */
public class DrawerFragment extends PApplet {

    public static String LOG_TAG = "Drawer_Fragment";

    /* Command flags */
    private static int TOAST_CMD = 0;
    private static int UI_CMD = 1;

    /* Set of active threads spawned by this fragment */
    private Set<Thread> mActiveThreads;

    private android.os.Handler mHandler;
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
    public DrawerState mState;

    private UndoList mUndoListHead; // Implements an undo list so that we can undo and redo

    private PenWidthMenu.PenWidth mPenWidth;
    private ColourMenu.Colour mPenColour;

    public enum DrawerState {

        DRAWING,
        COLOUR_MENU_ACTIVE,
        WIDTH_MENU_ACTIVE,
        FILL_ACTIVE,
        TEXT_BOX_ACTIVE,
        SENDING
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mActiveThreads != null) {
            for (Thread thread : mActiveThreads) {
                thread.interrupt();
            }
        }

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

        mActiveThreads = new HashSet<>();

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

        mHandler = new android.os.Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message message) {

                int requestType = (int) message.getData().get("requestType");

                /* command to display a toast in response to a bluetooth event */
                if (requestType == TOAST_CMD) {
                    String msg = (String) message.getData().get("toast_message");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();


                /* Commands to update the UI of the parent fragment */
                } else if (requestType == UI_CMD) {
                    String request = (String) message.getData().get("request");
                    switch (request) {

                        case "activate_colour":
                            ((CreateNoteFragment) getParentFragment())
                                    .updateColourIcon(R.color.colorPrimary,
                                            R.color.colorSecondary);

                            break;
                        case "deactivate_colour":
                            ((CreateNoteFragment) getParentFragment())
                                    .updateColourIcon(R.color.colorSecondary,
                                            R.color.colorPrimary);
                            break;

                        case "activate_penWidth":
                            ((CreateNoteFragment) getParentFragment())
                                    .updatePenWidthIcon(R.color.colorPrimary,
                                            R.color.colorSecondary);
                            break;

                        case "deactivate_penWidth":
                            ((CreateNoteFragment) getParentFragment())
                                    .updatePenWidthIcon(R.color.colorSecondary,
                                            R.color.colorPrimary);
                            break;

                        case "activate_fill":
                            ((CreateNoteFragment) getParentFragment())
                                    .updateFillIcon(R.color.colorPrimary,
                                            R.color.colorSecondary);
                            break;

                        case "deactivate_fill":
                            ((CreateNoteFragment) getParentFragment())
                                    .updateFillIcon(R.color.colorSecondary,
                                            R.color.colorPrimary);

                            break;

                        case "highlight_undo_icon":
                            ((CreateNoteFragment) getParentFragment())
                                    .updateUndoIcon(R.color.colorPrimary,
                                            R.color.white);

                            break;

                        case "restore_undo_icon":
                            ((CreateNoteFragment) getParentFragment())
                                    .updateUndoIcon(R.color.white,
                                            R.color.colorPrimary);

                            break;

                        case "highlight_redo_icon":
                            ((CreateNoteFragment) getParentFragment())
                                    .updateRedoIcon(R.color.colorPrimary,
                                            R.color.white);

                            break;

                        case "restore_redo_icon":
                            ((CreateNoteFragment) getParentFragment())
                                    .updateRedoIcon(R.color.white,
                                            R.color.colorPrimary);

                            break;
                    }
                }
            }
        };


        Bundle arguments = getArguments();
        if(arguments.getString("load_note_path") != null){
            String filePath = arguments.getString("load_note_path");
            PImage img = loadImage(filePath);
            image(img, 0, 0);
            sendMessageToUI("Loaded Note Successfully!", TOAST_CMD);
        }
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

                    Log.d(LOG_TAG, "Draw line in draw space");
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
                break;

            /*case SENDING:
                sendScreenState();
                break;*/
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

        /* Add delay so that NIOS can process subsequent commands */
        try {

            Thread.sleep(80);
        } catch (InterruptedException e) {}


        /* Set the pen size on the NIOS II */
        cmd = Command.createCommand(
                Command.CHANGE_PEN_WIDTH,
                mPenWidth.getSize());

        BluetoothActivity.writeToBTDevice(cmd);
        Log.d(LOG_TAG, "Sent change pen width command to bluetooth:" + cmd);

        /* Add delay so that NIOS can process subsequent commands */
        try {

            Thread.sleep(80);
        } catch (InterruptedException e) {}


        /* Clear the screen on the NIOS II */
        cmd = Command.createCommand(Command.CLEAR);

        BluetoothActivity.writeToBTDevice(cmd);

        try {

            Thread.sleep(400);
        }catch (InterruptedException e) {}
        Log.d(LOG_TAG, "Sent clear command to bluetooth:" + cmd);

    }


    /*public void sendScreenState() {
        int NIOSWIDTH = 681;
        int NIOSHEIGHT = 478;
        int lastidx = -1;
        int thisidx = -1;
        int count = 0;

        Log.d("SCREEN_STATE", "Starting sendScreenState");
        Log.d("SCREEN_STATE", "width: " + width + "\nheight: " + height);

        // send a command that tells the board we are starting
        BluetoothActivity.writeToBTDevice(Command.createCommand(Command.START_TRANSFER));

        // wait to let the DE1 setup
        try {
            Thread.sleep(100);
        }catch(InterruptedException e){}

        // y goes from 1 to 478 on the DE1
        for (int y = 0; y < NIOSHEIGHT; y++) {
            // x goes from 34 to 714 on the DE1
            for (int x = 0; x < NIOSWIDTH; x++) {
                // get the colour of the pixel in the upper-left corner
                int pixelValue = get(x * width / NIOSWIDTH, y * height / NIOSHEIGHT);
                ColourMenu.Colour pixelColour = pixelDataToColour(pixelValue);

                if (pixelColour == null) {
                    // set to white if we don't know what the colour is
                    thisidx = 15;
                } else {
                    thisidx = pixelColour.getIndex();
                }

                if (lastidx == -1) {
                    lastidx = thisidx;
                    count = 1;
                } else if (lastidx == thisidx) {
                    count++;
                } else {
                    String cmd = Command.createCommand(Command.TRANSFER, count, lastidx);
                    Log.d("SCREEN_STATE", "cmd: " + cmd);
                    BluetoothActivity.writeToBTDevice(cmd);

                    try {
                        // sleep for at least 80 ms, but sleep for longer if we are sending a large amount of data
                        Thread.sleep(Math.max(count/100, 80));
                    }catch(InterruptedException e){}

                    lastidx = thisidx;
                    count = 1;
                }
            }
        }

        // send the final sequence of data
        String cmd = Command.createCommand(Command.TRANSFER, count, lastidx);
        Log.d("SCREEN_STATE", "cmd: " + cmd + "\n" + count + " " + lastidx);
        BluetoothActivity.writeToBTDevice(cmd);

        Log.d("SCREEN_STATE", "Ending sendScreenState");
    }*/

    public void fill(final int x, final int y, final ColourMenu.Colour colourToFill, final ColourMenu.Colour fillColour) {

        /* Create a new worker thread to perform the algorithm off of the main UI thread */
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

//                HashMap<Point, Boolean> checkedPoints = new HashMap<>();
                /* Stack of points to examine */
                Stack<Point> pointStack = new Stack<>();
                Point nextPoint;
                pointStack.push(new Point(x, y));
                ColourMenu.Colour pixelColour;

                do {
                    /* The UI thread has requested us to stop so do so */
                    if (Thread.interrupted()) {
                        return;
                    }

                    /* Pixels on the stack should be filled in with the desired color */
                    nextPoint = pointStack.pop();
                    set(nextPoint.locX, nextPoint.locY,
                            color(fillColour.getColourR(),
                                    fillColour.getColourG(),
                                    fillColour.getColourB()));


                    /* Check the point to the right to see if it should be filled in */
                    pixelColour = pixelDataToColour(get(nextPoint.locX + 1, nextPoint.locY));
                    if (pixelColour == colourToFill && pixelColour != fillColour) {
                        Point checkPoint = new Point(nextPoint.locX + 1, nextPoint.locY);
                        pointStack.push(checkPoint);
                    } else {
                        set(nextPoint.locX + 1, nextPoint.locY,
                                color(fillColour.getColourR(),
                                        fillColour.getColourG(),
                                        fillColour.getColourB()));
                    }
                    /* Check the point below to see if it should be filled in */
                    pixelColour = pixelDataToColour(get(nextPoint.locX, nextPoint.locY + 1));
                    if (pixelColour == colourToFill && pixelColour != fillColour) {
                        Point checkPoint = new Point(nextPoint.locX, nextPoint.locY + 1);
                        pointStack.push(checkPoint);

                    } else {
                        set(nextPoint.locX, nextPoint.locY + 1,
                                color(fillColour.getColourR(),
                                        fillColour.getColourG(),
                                        fillColour.getColourB()));
                    }
                    /* Check the point to the left to see if it should be filled in */
                    pixelColour = pixelDataToColour(get(nextPoint.locX - 1, nextPoint.locY));
                    if (pixelColour == colourToFill && pixelColour != fillColour) {
                        Point checkPoint = new Point(nextPoint.locX - 1, nextPoint.locY);
                        pointStack.push(checkPoint);


                    } else {
                        set(nextPoint.locX - 1, nextPoint.locY,
                                color(fillColour.getColourR(),
                                        fillColour.getColourG(),
                                        fillColour.getColourB()));
                    }
                    /* Check the point above to see if it should be filled in */
                    pixelColour = pixelDataToColour(get(nextPoint.locX, nextPoint.locY - 1));
                    if (pixelColour == colourToFill && pixelColour != fillColour) {
                        Point checkPoint = new Point(nextPoint.locX, nextPoint.locY - 1);
                        pointStack.push(checkPoint);

                    } else {
                        set(nextPoint.locX, nextPoint.locY - 1,
                                color(fillColour.getColourR(),
                                        fillColour.getColourG(),
                                        fillColour.getColourB()));
                    }
                /* Break out of loop when we have filled in all the points */
                } while (!pointStack.isEmpty());
            }
        });
        /* Add this thread to our set of active threads so that we can destroy it prematurely if needed */
        mActiveThreads.add(thread);
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
                        /* If we are currently filling something, stop the corresponding thread(s)
                         * so we don't fill the screen with a new colour after clearing it
                         */
                        if (mActiveThreads != null) {
                            for (Thread thread : mActiveThreads) {
                                thread.interrupt();
                            }
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
     * <p/>
     * Note: This function will also undo the sequence of lines drawn on the NIOS II if there is an
     * active bluetooth session
     **********************************************************************************************/
    public void undo() {

        //TODO: Implement this on the android device, currently it just sends commands to NIOS

        switch (mState) {

            case DRAWING:
                sendMessageToUI("highlight_undo_icon", UI_CMD);

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

            /* Poll for about 80 milliseconds then inform the UI thread that
             * it should update the undo icon in the parent fragment
             */
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int start_time = millis();
                        while (millis() - start_time < 80) {
                        }
                        ;
                        sendMessageToUI("restore_undo_icon", UI_CMD);
                    }
                });

                thread.start();
                break;
            case WIDTH_MENU_ACTIVE:
                deactivatePenWidthMenu();
                break;
            case COLOUR_MENU_ACTIVE:
                deactivateColourMenu();
                break;
            case FILL_ACTIVE:
                deactivateFill();
                break;

            //TODO: when other menu items have been implemented, make sure to deactivate active menu items
        }

    }

    /**********************************************************************************************
     * Redraw the last sequence of drawn lines that was cleared with Undo
     * <p/>
     * Note: This function will also clear the drawing space on the NIOS II if there is an active
     * bluetooth session
     **********************************************************************************************/
    public void redo() {

        //TODO: Implement this on the android device, currently it just sends commands to NIOS

        switch (mState) {
            case DRAWING:
                sendMessageToUI("highlight_redo_icon", UI_CMD);

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

            /* Poll for about 80 milliseconds then inform the UI thread that
             * it should update the redo icon in the parent fragment
             */
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int start_time = millis();
                        while (millis() - start_time < 80) {
                        }
                        ;
                        sendMessageToUI("restore_redo_icon", UI_CMD);
                    }
                });

                thread.start();
                break;
            case COLOUR_MENU_ACTIVE:
                deactivateColourMenu();
                break;
            case WIDTH_MENU_ACTIVE:
                deactivatePenWidthMenu();
                break;
            case FILL_ACTIVE:
                deactivateFill();
                break;

            //TODO: when other menu items have been implemented, make sure to deactivate active menu items
        }
    }

    /***********************************************************************************************
     * Public function to restore the draw space if popupmenus are active, this is useful when
     * we want to save the screen to a jpeg file
     *
     **********************************************************************************************/
    public void clearMenus(){

        switch(mState){
            case DRAWING:

                /* Do Nothing */
                break;

            case COLOUR_MENU_ACTIVE:
                deactivateColourMenu();
                break;
            case WIDTH_MENU_ACTIVE:
                deactivatePenWidthMenu();
                break;

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
            case FILL_ACTIVE:
                deactivateFill();
                break;

            //TODO: when other menu items have been implemented, make sure to deactivate active menu items
        }


    }

    /**********************************************************************************************
     * Toggles the fill feature
     **********************************************************************************************/
    public void toggleFillActive() {

        switch (mState) {

            case DRAWING:
                activateFill();
                break;
            case COLOUR_MENU_ACTIVE:
                deactivateColourMenu();
                break;
            case WIDTH_MENU_ACTIVE:
                deactivatePenWidthMenu();
                break;
            case FILL_ACTIVE:
                deactivateFill();
                break;
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
            case FILL_ACTIVE:
                deactivateFill();
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
        sendMessageToUI("activate_colour", UI_CMD);


    }

    /***********************************************************************************************
     * Change our current state and hide the colour menu
     **********************************************************************************************/
    private void deactivateColourMenu() {

        mColourMenu.hideSelf();
        sendMessageToUI("deactivate_colour", UI_CMD);

        mState = DrawerState.DRAWING;


    }


    private void activatePenWidthMenu() {

        mPenWidthMenu.drawSelf();
        mState = DrawerState.WIDTH_MENU_ACTIVE;
        sendMessageToUI("activate_penWidth", UI_CMD);

    }

    private void deactivatePenWidthMenu() {

        mPenWidthMenu.hideSelf();
        sendMessageToUI("deactivate_penWidth", UI_CMD);

        mState = DrawerState.DRAWING;


    }

    private void activateFill() {

        mState = DrawerState.FILL_ACTIVE;
        sendMessageToUI("activate_fill", UI_CMD);
    }

    private void deactivateFill() {

        mState = DrawerState.DRAWING;
        sendMessageToUI("deactivate_fill", UI_CMD);

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

    public PenWidthMenu.PenWidth getPenWidth() {
        return mPenWidth;
    }

    /***********************************************************************************************
     * Function to save the screen state using a byte array
     *
     * @return a byte array with the following format
     * <p/>
     * byte[i + 0] = value of R colour
     * byte[i + 1] = value of G colour
     * byte[i + 2] = value of B colour
     * <p/>
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


    public void loadNote(String filePath){

        PImage img = loadImage(filePath);
        image(img, 0, 0);
        sendMessageToUI("Loaded Note Successfully!", TOAST_CMD);

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

    private void sendMessageToUI(String msg, int requestType) {

        if (requestType == UI_CMD) {
            Message message = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("request", msg);
            bundle.putInt("requestType", requestType);
            message.setData(bundle);
            message.sendToTarget();

        } else if (requestType == TOAST_CMD) {
            Message message = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("toast_message", msg);
            bundle.putInt("requestType", requestType);
            message.setData(bundle);
            message.sendToTarget();
        }
    }


}
