package cpen391.team6.bored.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;


import android.app.Fragment;

import android.app.FragmentTransaction;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.codekrypt.greendao.db.LocalNote;
import com.codekrypt.greendao.db.LocalNoteDao;
import com.joanzapata.iconify.widget.IconTextView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.Observer;

import cpen391.team6.bored.Activities.BluetoothActivity;
import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Items.ColourMenu;
import cpen391.team6.bored.Items.Command;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.UI_Util;
import processing.core.PApplet;



/**
 * Created by neema on 2017-03-12.
 */
public class CreateNoteFragment extends Fragment implements View.OnClickListener {

    private static String LOG_TAG = "CreateNoteFragment";
    private DrawerFragment mDrawer;
    private FrameLayout mDrawFrame;

    private android.os.Handler mHandler;

    private Thread mListener;

    private static int CONNECT_BLUETOOTH = 0;
    private static int DISCONNECT_BLUETOOTH = 1;

    private int mDrawFrameWidth;
    private int mDrawFrameHeight;

    private IconTextView mColourPallette;
    private IconTextView mPenWidth;
    private IconTextView mRedo;
    private IconTextView mUndo;
    private IconTextView mFill;
    private IconTextView mTextBox;
    private IconTextView mClear;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);

        mHandler = new android.os.Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message message) {
                String toastString = message.getData().getString("toast_message");

                Toast.makeText(getActivity(), toastString, Toast.LENGTH_SHORT).show();

            }

        };

        /* We may want to contribute to the action bar menu */
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_note_fragment_layout, container, false);

        //((MainActivity) getActivity()).updateDrawerList();
        /* Find our view in the view hierarchy */
        mDrawFrame = (FrameLayout) view.findViewById(R.id.drawing_space);
        mColourPallette = (IconTextView) view.findViewById(R.id.colour_pallette);
        mPenWidth = (IconTextView) view.findViewById(R.id.pen_width);
        mRedo = (IconTextView) view.findViewById(R.id.redo);
        mUndo = (IconTextView) view.findViewById(R.id.undo);
        mFill = (IconTextView) view.findViewById(R.id.fill);
        mTextBox = (IconTextView) view.findViewById(R.id.text_box);
        mClear = (IconTextView) view.findViewById(R.id.clear_screen);

        /* Set Listeners */
        mColourPallette.setOnClickListener(this);
        mPenWidth.setOnClickListener(this);
        mRedo.setOnClickListener(this);
        mFill.setOnClickListener(this);
        mUndo.setOnClickListener(this);
        mFill.setOnClickListener(this);
        mTextBox.setOnClickListener(this);
        mClear.setOnClickListener(this);

        mDrawer = (DrawerFragment) getActivity().getFragmentManager().findFragmentById(R.id.drawing_space);

        /* We have to wait until the frame layout's dimensions have been determined before we can attach the
         * Processing fragment
         */
        mDrawFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDrawFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mDrawer == null) {

                    /* Get Width and add an additional offset, for some reason getWidth()
                     * doesn't provide the full width of the layout
                     */

                    Bundle arguments = new Bundle();

                    if(savedInstanceState == null) {

                        mDrawFrameWidth = mDrawFrame.getWidth() + 100;
                        mDrawFrameHeight = mDrawFrame.getHeight();

                    }else{
                        if(savedInstanceState.getInt("draw_frame_width") != 0
                                && savedInstanceState.getInt("draw_frame_height") != 0){
                            mDrawFrameWidth = savedInstanceState.getInt("draw_frame_width");
                            mDrawFrameHeight = savedInstanceState.getInt("draw_frame_height");
                        }else{
                            mDrawFrameWidth = mDrawFrame.getWidth() + 100;
                            mDrawFrameHeight = mDrawFrame.getHeight();
                        }
                    }

                    System.out.println("Draw Frame Width:" + mDrawFrameWidth);
                    System.out.println("Draw Frame Height:" + mDrawFrameHeight);

                    //pass width and height of screen as arguments to launch animation
                    arguments.putDouble("width", mDrawFrameWidth);
                    arguments.putDouble("height", mDrawFrameHeight);

                    //TODO: These values are hardcoded so it will be easier to compress the image on the DE1 side,
                    //TODO: Need to find a better work around to accomodate variable screen sizes

                    //arguments.putDouble("width", 1362);
                    //arguments.putDouble("height", 956);

                    mDrawer = new DrawerFragment();
                    mDrawer.setArguments(arguments);

                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

                    /* Replace the current fragment that is being displayed, provide it with a tag so we can
                    * locate it in the future
                    */
                    transaction.replace(R.id.drawing_space, mDrawer, "draw_space");

                    /* This call is necessary so we don't create a new fragment by default, not sure why */
                    transaction.addToBackStack(null);

                    /* allows for smoother transitions between screens */
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                    /* Actually make the transition */
                    transaction.commit();

                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CONNECT_BLUETOOTH){
                mDrawer.initRemoteScreen();
                //BoredApplication.isConnectedToBluetooth = true ;
                ((MainActivity) getActivity()).updateMenu(
                        R.id.stream_to_device,
                        R.mipmap.bluetooth_connected);

                mListener = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //BoredApplication.isConnectedLock.lock();

                        for(;;){

                            if(Thread.interrupted()){
                                return;
                            }

                            String cmdString = BluetoothActivity.readFromBTDevice();
                            //BoredApplication.isConnectedLock.unlock();
                            if(cmdString.equals("A")){
                                sendMessageToUI("Able To Draw On NIOS");
                            }else if(cmdString.equals("B")){
                                sendMessageToUI("Unable To Draw On NIOS");
                            }else{
                                                /*do nothing */
                            }

                            //BoredApplication.isConnectedLock.lock();
                        }

                    }
                });

                mListener.start();


            }else if(requestCode == DISCONNECT_BLUETOOTH){
                //BoredApplication.isConnectedToBluetooth = false ;
                ((MainActivity) getActivity()).updateMenu(
                        R.id.stream_to_device,
                        R.mipmap.bluetooth);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ((MainActivity) getActivity()).closeDrawer();

        switch (item.getItemId()) {

            case R.id.stream_to_device:

                Intent intent = new Intent(getActivity(), BluetoothActivity.class);

                /* Close the bluetooth connection */
                if (BoredApplication.isConnectedToBluetooth) {
                    String cmd = Command.createCommand(Command.TERMINATE);
                    BluetoothActivity.writeToBTDevice(cmd);
                    Log.i(LOG_TAG, "Sent terminate connection command to bluetooth:" + cmd);

                    intent.putExtra("bluetooth_request", BluetoothActivity.CLOSE_CONNECTION);

                    if(mListener != null)
                        mListener.interrupt();
                    startActivityForResult(intent, DISCONNECT_BLUETOOTH);

                    /* Open the connection so we can stream data to the bluetooth chip */
                } else {
                    intent.putExtra("bluetooth_request", BluetoothActivity.OPEN_CONNECTION);
                    startActivityForResult(intent, CONNECT_BLUETOOTH);
                }

                break;

            case R.id.save_draw_space:

                final View titleDialogView = getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.dialog_note_title_selection, null);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(getActivity(), R.style.DialogTheme));

                builder.setTitle(getString(R.string.save_note))
                        .setView(titleDialogView)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //THIS DOES NOTHING, WE PROVIDE THE ACTUAL IMPLEMENTATION AFTERWARDS
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                final AlertDialog titleDialog = builder.create();
                titleDialog.setCanceledOnTouchOutside(true);
                titleDialog.show();

                UI_Util.setDialogStyle(titleDialog, getActivity());

                titleDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText setTitleEditText = (EditText) titleDialogView.findViewById(R.id.set_note_title);
                        Log.d(LOG_TAG, "" + setTitleEditText.getText().length());
                        if(setTitleEditText.getText().length() == 0){
                            titleDialogView.findViewById(R.id.title_error_message)
                                    .setVisibility(View.VISIBLE);
                        }else {

                            EditText setTopicEditText = (EditText) titleDialogView.findViewById(R.id.set_note_topic);

                            String noteTitle = setTitleEditText.getText().toString();
                            String noteTopic = setTopicEditText.getText().toString();

                            saveNote(noteTitle, noteTopic);
                            titleDialog.dismiss();
                        }
                    }
                });

                break;

            case R.id.load_draw_space:

                break;




        }

        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.getItem(0).setVisible(true);
        menu.getItem(0).setEnabled(true);
        menu.getItem(1).setVisible(true);
        menu.getItem(1).setEnabled(true);
        menu.getItem(2).setVisible(true);
        menu.getItem(2).setEnabled(true);

        /* Depending on whether we are connected to the device
         * change the bluetooth icon
         */
        if (BoredApplication.isConnectedToBluetooth) {
            menu.findItem(R.id.stream_to_device).setIcon(R.mipmap.bluetooth_connected);
        } else {
            menu.findItem(R.id.stream_to_device).setIcon(R.mipmap.bluetooth);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        if(mDrawFrameWidth != 0 && mDrawFrameHeight != 0) {
            savedInstanceState.putInt("draw_frame_width", mDrawFrameWidth);
            savedInstanceState.putInt("draw_frame_height", mDrawFrameHeight);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.colour_pallette:
                mDrawer.toggleColourMenu();
                break;

            case R.id.pen_width:
                mDrawer.togglePenWidthMenu();
                break;

            case R.id.clear_screen:
                mDrawer.clearScreen();
                break;

            case R.id.undo:
                mDrawer.undo();

                break;

            case R.id.redo:
                mDrawer.redo();
                break;

            case R.id.fill:
                mDrawer.toggleFillActive();
        }

    }


    private void saveNote(String title, String topic){

        /* Clear any visible popup menus */
        mDrawer.clearMenus();

                            /* Load the RGB values so we can generate our jpeg file */
        mDrawer.loadPixels();
        Bitmap bmp = Bitmap.createBitmap(mDrawer.pixels,
                mDrawer.width,
                mDrawer.height,
                Bitmap.Config.ARGB_8888);


        /* Create a jpeg file using the RGB array */
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream outputStream;
        File file = getActivity().getFilesDir();
        String path = file.getAbsolutePath();

        String filename = (title + ".jpg").replaceAll("\\W+", "_");

        //TODO: Check for duplicates filnames here, inform user that existing note will be overwritten
        try{
            outputStream = new FileOutputStream(path + "/" + filename);
            outputStream.write(byteArray);
            outputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }


        Log.i(LOG_TAG, "path to files:" + path);

        for (int i = 0; i < file.listFiles().length; i++) {
            String s = file.listFiles()[i].toString();
            long size = file.listFiles()[i].getTotalSpace();
            Log.i(LOG_TAG, "file contained in app directory: " + s);
            Log.i(LOG_TAG, "file contained" + size + "bytes");
        }

        /* Get the database manager */
        LocalNoteDao localNoteDao = BoredApplication.getDaoSession().getLocalNoteDao();
        LocalNote localNote = new LocalNote();

        Calendar calendar = Calendar.getInstance();

        Date noteDate = new Date();

        /* Set the date of the note to the current time */
        noteDate.setTime(calendar.getTimeInMillis());

        /* Set the fields in our note, then add it to our database */
        localNote.setDate(noteDate);
        localNote.setTitle(title);
        localNote.setTopic(topic);
        localNote.setFilePath(path + "/" + filename);
        localNoteDao.insert(localNote);

    }
    private void sendMessageToUI(String msg) {

        Message message = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("toast_message", msg);
        message.setData(bundle);
        message.sendToTarget();
    }


    public void updateRedoIcon(int iconColorId, int backgroundColorId){
        mRedo.setTextColor(getResources().getColor(iconColorId));
        mRedo.setBackgroundColor(getResources().getColor(backgroundColorId));
    }

    public void updateUndoIcon(int iconColorId, int backgroundColorId){
        mUndo.setTextColor(getResources().getColor(iconColorId));
        mUndo.setBackgroundColor(getResources().getColor(backgroundColorId));
    }

    public void updateColourIcon(int iconColorId, int backgroundColorId){
        mColourPallette.setTextColor(getResources().getColor(iconColorId));
        mColourPallette.setBackgroundColor(getResources().getColor(backgroundColorId));
    }

    public void updatePenWidthIcon(int iconColorId, int backgroundColorId){
        mPenWidth.setTextColor(getResources().getColor(iconColorId));
        mPenWidth.setBackgroundColor(getResources().getColor(backgroundColorId));
    }

    public void updateFillIcon(int iconColorId, int backgroundColorId){
        mFill.setTextColor(getResources().getColor(iconColorId));
        mFill.setBackgroundColor(getResources().getColor(backgroundColorId));
    }



}
