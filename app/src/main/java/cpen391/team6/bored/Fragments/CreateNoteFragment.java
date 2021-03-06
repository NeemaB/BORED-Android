package cpen391.team6.bored.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codekrypt.greendao.db.LocalNote;
import com.codekrypt.greendao.db.LocalNoteDao;
import com.codekrypt.greendao.db.ScreenInfo;
import com.joanzapata.iconify.widget.IconTextView;

import org.greenrobot.greendao.query.QueryBuilder;

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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.Observer;

import cpen391.team6.bored.Activities.BluetoothActivity;
import cpen391.team6.bored.Activities.MainActivity;
import cpen391.team6.bored.Adapters.DialogLocalNoteAdapter;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Items.ColourMenu;
import cpen391.team6.bored.Items.Command;
import cpen391.team6.bored.Items.Point;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.ImageUtil;
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

    private Integer mStatusBarHeight;
    private Integer mStatusBarWidth;

    private boolean mDrawFrameInit;
    private boolean mStatusBarInit;

    /* Command flags */
    private static int TOAST_CMD = 0;
    private static int BLUETOOTH_STATUS_CMD = 1;

    private int mDrawFrameWidth;
    private int mDrawFrameHeight;

    private IconTextView mColourPallette;
    private IconTextView mPenWidth;
    private IconTextView mRedo;
    private IconTextView mUndo;
    private IconTextView mFill;
//    private IconTextView mTextBox;
    private IconTextView mClear;

    private LinearLayout mContentView;
    private LinearLayout mStatusBar;
    private TextView mBluetoothStatus;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);

        mStatusBarInit = false;
        mDrawFrameInit = false;

        mHandler = new android.os.Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message message) {

                int requestType = (int) message.getData().get("requestType");

                /* command to display a toast in response to a bluetooth event */
                if (requestType == TOAST_CMD) {
                    String msg = (String) message.getData().get("toast_message");
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

                } else if (requestType == BLUETOOTH_STATUS_CMD) {

                    int request = (int) message.getData().get("request");

                    switch (request) {


                        case R.string.connected_awaiting_permission:

                            updateBluetoothStatus(
                                    getString(R.string.connected_awaiting_permission),
                                    R.color.yellow);
                            break;


                        case R.string.connected_initializing_remote_screen:

                            updateBluetoothStatus(
                                    getString(R.string.connected_initializing_remote_screen),
                                    R.color.orange
                            );
                            break;

                        case R.string.connected_can_draw_on_NIOS:

                            updateBluetoothStatus(
                                    getString(R.string.connected_can_draw_on_NIOS),
                                    R.color.green
                            );
                            break;

                    }
                }

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
        mUndo = (IconTextView) view.findViewById(R.id.undo);
        mRedo = (IconTextView) view.findViewById(R.id.redo);
        mFill = (IconTextView) view.findViewById(R.id.fill);
//        mTextBox = (IconTextView) view.findViewById(R.id.text_box);
        mClear = (IconTextView) view.findViewById(R.id.clear_screen);
        mBluetoothStatus = (TextView) view.findViewById(R.id.bluetooth_status);
        mStatusBar = (LinearLayout) view.findViewById(R.id.bluetooth_status_bar);

        /* Set Listeners */
        mColourPallette.setOnClickListener(this);
        mPenWidth.setOnClickListener(this);
        mUndo.setOnClickListener(this);
        mRedo.setOnClickListener(this);
        mFill.setOnClickListener(this);
//        mTextBox.setOnClickListener(this);
        mClear.setOnClickListener(this);

        mDrawer = (DrawerFragment) getActivity().getFragmentManager().findFragmentById(R.id.drawing_space);

        /* We have to wait until the frame layout's dimensions have been determined before we can attach the
         * Processing fragment
         */

        mStatusBar.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mStatusBar.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mStatusBarHeight = mStatusBar.getHeight();
                mStatusBarWidth = mStatusBar.getWidth();

                if (mDrawFrameInit) {

                    Bundle myArguments = getArguments();
                    Bundle arguments = new Bundle();

                    if (myArguments != null) {
                        arguments.putString("load_note_path", myArguments.getString("load_note_path"));
                        arguments.putString("command_list", myArguments.getString("command_list"));
                        if(myArguments.getBoolean("external_note")){
                            arguments.putBoolean("external_note", true);
                        }else{
                            arguments.putBoolean("external_note", false);
                        }
                    }

                    //pass width and height of screen as arguments to launch animation
                    arguments.putInt("width", mDrawFrameWidth);
                    arguments.putInt("height", mDrawFrameHeight - mStatusBarHeight);

                    Log.i(LOG_TAG, "Draw Frame Height With Status Bar Offset:" + (mDrawFrameHeight - mStatusBarHeight));

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

                mStatusBarInit = true;

            }
        });
        mDrawFrame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mDrawFrame.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if (mDrawer == null) {

                    /* To fix bug where screen size is determined incorrectly upon re-creating this fragment,
                     * persist the screen dimensions the first time we create the fragment so we can retrieve it later
                     * (screen size) isn't going to change dynamically of course
                     */

                    if (ScreenInfo.getInfo() == null) {

                        mDrawFrameWidth = mDrawFrame.getWidth() + 100;
                        mDrawFrameHeight = mDrawFrame.getHeight();

                        ScreenInfo.saveInfo(mDrawFrameWidth, mDrawFrameHeight);

                    } else {

                        ScreenInfo screenInfo = ScreenInfo.getInfo();

                        mDrawFrameWidth = screenInfo.getWidth();
                        mDrawFrameHeight = screenInfo.getHeight();
                    }


                    Log.i(LOG_TAG, "Draw Frame Width:" + mDrawFrameWidth);
                    Log.i(LOG_TAG, "Draw Frame Height:" + mDrawFrameHeight);


                    if (mStatusBarInit) {
                        Bundle myArguments = getArguments();
                        Bundle arguments = new Bundle();

                        if (myArguments != null) {
                            arguments.putString("load_note_path", myArguments.getString("load_note_path"));
                            arguments.putString("command_list", myArguments.getString("command_list"));
                            if(myArguments.getBoolean("external_note")){
                                arguments.putBoolean("external_note", true);
                            }else{
                                arguments.putBoolean("external_note", false);
                            }
                        }


                    /* Get Width and add an additional offset, for some reason getWidth()
                     * doesn't provide the full width of the layout
                     */

                        Log.i(LOG_TAG, "Draw Frame Height With Status Bar Offset:" + (mDrawFrameHeight - mStatusBarHeight));


                        //pass width and height of screen as arguments to launch animation
                        arguments.putInt("width", mDrawFrameWidth);
                        arguments.putInt("height", mDrawFrameHeight - mStatusBarHeight);

//                        if (myArguments != null) {
//                            arguments.putString("load_note_path", myArguments.getString("load_note_path"));
//                        }

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
                    mDrawFrameInit = true;
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CONNECT_BLUETOOTH) {
                //mDrawer.initRemoteScreen();
                //BoredApplication.isConnectedToBluetooth = true ;
                ((MainActivity) getActivity()).updateMenu(
                        R.id.stream_to_device,
                        R.mipmap.bluetooth_connected);

                updateBluetoothStatus(getString(R.string.connected_awaiting_permission), R.color.yellow);

                mListener = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //BoredApplication.isConnectedLock.lock();

                        for (; ; ) {

                            if (Thread.interrupted()) {
                                return;
                            }

                            String cmdString = BluetoothActivity.readFromBTDevice();
                            //BoredApplication.isConnectedLock.unlock();
                            if (cmdString.equals("A")) {
                                /*sendMessageToUI("Sending Screen State To NIOS");
                                DrawerFragment.DrawerState temp = mDrawer.mState;
                                mDrawer.mState = DrawerFragment.DrawerState.SENDING;
                                mDrawer.mousePressed();
                                mDrawer.mState = temp;*/
                                mDrawer.permissionToDraw = true;
                                sendMessageToUI("Streaming Permission Acquired ", TOAST_CMD);
                                sendMessageToUI(R.string.connected_initializing_remote_screen, BLUETOOTH_STATUS_CMD);
                                mDrawer.initRemoteScreen();
                                sendMessageToUI(R.string.connected_can_draw_on_NIOS, BLUETOOTH_STATUS_CMD);
                            } else if (cmdString.equals("B")) {
                                mDrawer.permissionToDraw = false;
                                sendMessageToUI("Streaming Permission Revoked", TOAST_CMD);
                                sendMessageToUI(R.string.connected_awaiting_permission, BLUETOOTH_STATUS_CMD);
                            } else {
                                                /*do nothing */
                            }

                            Log.d("CommandList", cmdString);
                            //BoredApplication.isConnectedLock.lock();
                        }

                    }
                });

                mListener.start();


            } else if (requestCode == DISCONNECT_BLUETOOTH) {
                //BoredApplication.isConnectedToBluetooth = false ;
                ((MainActivity) getActivity()).updateMenu(
                        R.id.stream_to_device,
                        R.mipmap.bluetooth);

                updateBluetoothStatus(getString(R.string.not_connected), R.color.red);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ((MainActivity) getActivity()).closeDrawer();
        mDrawer.clearMenus();

        switch (item.getItemId()) {

            case R.id.stream_to_device:

                Intent intent = new Intent(getActivity(), BluetoothActivity.class);

                /* Close the bluetooth connection */
                if (BoredApplication.isConnectedToBluetooth) {
                    String cmd = Command.createCommand(Command.TERMINATE);
                    BluetoothActivity.writeToBTDevice(cmd);
                    Log.i(LOG_TAG, "Sent terminate connection command to bluetooth:" + cmd);

                    intent.putExtra("bluetooth_request", BluetoothActivity.CLOSE_CONNECTION);

                    if (mListener != null)
                        mListener.interrupt();
                    startActivityForResult(intent, DISCONNECT_BLUETOOTH);

                    /* Open the connection so we can stream data to the bluetooth chip */
                } else {
                    intent.putExtra("bluetooth_request", BluetoothActivity.OPEN_CONNECTION);
                    startActivityForResult(intent, CONNECT_BLUETOOTH);
                }

                break;

            case R.id.save_draw_space:

                /* Create view to place inside of the dialog window */
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

                        boolean err;

                        /* If the user has not entered a title, display error message */
                        EditText setTitleEditText = (EditText) titleDialogView.findViewById(R.id.set_note_title);
                        Log.d(LOG_TAG, "" + setTitleEditText.getText().length());
                        if (setTitleEditText.getText().length() == 0) {
                            titleDialogView.findViewById(R.id.title_error_message)
                                    .setVisibility(View.VISIBLE);
                        } else {

                            EditText setTopicEditText = (EditText) titleDialogView.findViewById(R.id.set_note_topic);

                            String noteTitle = setTitleEditText.getText().toString();
                            String noteTopic = setTopicEditText.getText().toString();

                            /* Persist note to local storage */
                            err = saveNote(noteTitle, noteTopic);

                            /* Inform the user that note was saved successfully */
                            if (err) {
                                Toast.makeText(getActivity(), "Failed to save note!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Saved note successfully!", Toast.LENGTH_SHORT).show();
                            }
                            titleDialog.dismiss();
                        }
                    }
                });

                break;

            case R.id.load_draw_space:


                /* Retrieve all the local notes */
                LocalNoteDao localNoteDao = BoredApplication.getDaoSession().getLocalNoteDao();
                QueryBuilder<LocalNote> qb = localNoteDao.queryBuilder();
                final List<LocalNote> mNotesList = qb.list();


                /* Create view to place inside of the dialog window */
                final View loadNoteDialogView = getActivity()
                        .getLayoutInflater()
                        .inflate(R.layout.dialog_load_note, null);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(
                        new ContextThemeWrapper(getActivity(), R.style.DialogTheme));

                builder2.setTitle(getString(R.string.load_saved_note))
                        .setView(loadNoteDialogView);

                final AlertDialog loadNoteDialog = builder2.create();
                loadNoteDialog.setCanceledOnTouchOutside(true);
                loadNoteDialog.show();

                final RelativeLayout noNotes = (RelativeLayout) loadNoteDialog.findViewById(R.id.no_saved_notes);
                final ListView dialogNoteListView = (ListView) loadNoteDialog.findViewById(R.id.simple_note_list);

                if(!(mNotesList.size() == 0)) {
                /* Load the local notes into the listView within the dialog */

                    noNotes.setVisibility(View.GONE);
                    DialogLocalNoteAdapter adapter = new DialogLocalNoteAdapter(
                            getActivity(),
                            R.layout.dialog_note_list_item,
                            mNotesList);

                    dialogNoteListView.setAdapter(adapter);
                }else{
                    dialogNoteListView.setVisibility(View.GONE);
                    noNotes.setVisibility(View.VISIBLE);
                }


                dialogNoteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        mDrawer.loadNote(mNotesList.get(position).getFilePath(), mNotesList.get(position).getCommandList());
                        loadNoteDialog.dismiss();
                    }
                });

                UI_Util.setDialogStyle(loadNoteDialog, getActivity());


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
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (BoredApplication.isConnectedToBluetooth) {
            Intent intent = new Intent(getActivity(), BluetoothActivity.class);
            String cmd = Command.createCommand(Command.TERMINATE);
            BluetoothActivity.writeToBTDevice(cmd);
            Log.i(LOG_TAG, "Sent terminate connection command to bluetooth:" + cmd);

            intent.putExtra("bluetooth_request", BluetoothActivity.CLOSE_CONNECTION);

            if (mListener != null)
                mListener.interrupt();
            startActivityForResult(intent, DISCONNECT_BLUETOOTH);
        }


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


    private boolean saveNote(String title, String topic) {

        /* Clear any visible popup menus */
        mDrawer.clearMenus();

        /* Get the database manager */
        LocalNoteDao localNoteDao = BoredApplication.getDaoSession().getLocalNoteDao();
        LocalNote localNote = new LocalNote();

        Calendar calendar = Calendar.getInstance();

        /* Set the date of the note to the current time */
        Date noteDate = calendar.getTime();

        /* Set the fields in our note, then add it to our database */
        localNote.setDate(noteDate);
        localNote.setTitle(title);
        localNote.setTopic(topic);
        localNote.setCommandList(mDrawer.getCommandList());

        /* Load the RGB values so we can generate our jpeg file */
        mDrawer.loadPixels();
        Bitmap bmp = Bitmap.createBitmap(mDrawer.pixels,
                mDrawer.width,
                mDrawer.height,
                Bitmap.Config.ARGB_8888);

        /* Indicate that operation failed */
        if (bmp == null) {
            return true;
        }

        /* Create a jpeg file using the RGB array */
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        FileOutputStream outputStream;
        File file = getActivity().getFilesDir();
        String path = file.getAbsolutePath();

        /* Create a unique filename using the title and the date at which the note was created */
        String filename = (title + noteDate.toString() + ".jpg").replaceAll("\\W+", "_");

        //TODO: Check for duplicates filnames here, inform user that existing note will be overwritten
        try {
            outputStream = new FileOutputStream(path + "/" + filename);
            outputStream.write(byteArray);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.i(LOG_TAG, "path to files:" + path);

        for (int i = 0; i < file.listFiles().length; i++) {
            String s = file.listFiles()[i].toString();
            long size = file.listFiles()[i].getTotalSpace();
            Log.i(LOG_TAG, "file contained in app directory: " + s);
            Log.i(LOG_TAG, "file contained" + size + "bytes");
        }

        localNote.setFilePath(path + "/" + filename);

        /* If rowId = -1, indicate that save operation failed */
        if (localNoteDao.insert(localNote) == -1)
            return true;

        return false;

    }

    private void sendMessageToUI(Object msg, int requestType) {

        if (requestType == BLUETOOTH_STATUS_CMD) {
            Message message = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("request", (Integer) msg);
            bundle.putInt("requestType", requestType);
            message.setData(bundle);
            message.sendToTarget();

        } else if (requestType == TOAST_CMD) {
            Message message = mHandler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("toast_message", (String) msg);
            bundle.putInt("requestType", requestType);
            message.setData(bundle);
            message.sendToTarget();
        }

    }

    private void updateBluetoothStatus(String status, int textColorId) {
        if (this.mBluetoothStatus != null) {
            this.mBluetoothStatus.setText(status);
            this.mBluetoothStatus.setTextColor(getResources().getColor(textColorId));
        }
//        this.mBluetoothStatus.setTextColor(textColorId);

    }

    public void updateRedoIcon(int iconColorId, int backgroundColorId) {
        mRedo.setTextColor(getResources().getColor(iconColorId));
        mRedo.setBackgroundColor(getResources().getColor(backgroundColorId));
    }

    public void updateUndoIcon(int iconColorId, int backgroundColorId) {
        mUndo.setTextColor(getResources().getColor(iconColorId));
        mUndo.setBackgroundColor(getResources().getColor(backgroundColorId));
    }

    public void updateColourIcon(int iconColorId, int backgroundColorId) {
        mColourPallette.setTextColor(getResources().getColor(iconColorId));
        mColourPallette.setBackgroundColor(getResources().getColor(backgroundColorId));
    }

    public void updatePenWidthIcon(int iconColorId, int backgroundColorId) {
        mPenWidth.setTextColor(getResources().getColor(iconColorId));
        mPenWidth.setBackgroundColor(getResources().getColor(backgroundColorId));
    }

    public void updateFillIcon(int iconColorId, int backgroundColorId) {
        mFill.setTextColor(getResources().getColor(iconColorId));
        mFill.setBackgroundColor(getResources().getColor(backgroundColorId));
    }


}
