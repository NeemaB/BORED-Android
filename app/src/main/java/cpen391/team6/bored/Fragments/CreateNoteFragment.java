package cpen391.team6.bored.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.os.Bundle;


import android.app.Fragment;

import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconTextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import cpen391.team6.bored.Activities.BluetoothActivity;
import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.Items.ColourMenu;
import cpen391.team6.bored.R;
import processing.core.PApplet;

/**
 * Created by neema on 2017-03-12.
 */
public class CreateNoteFragment extends Fragment implements View.OnClickListener {

    private static String LOG_TAG = "CreateNoteFragment";
    private DrawerFragment mDrawer;
    private FrameLayout mDrawFrame;

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

        /* We may want to contribute to the action bar menu */
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_note_fragment_layout, container, false);

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

                    mDrawFrameWidth = mDrawFrame.getWidth() + 100;
                    mDrawFrameHeight = mDrawFrame.getHeight();

                    System.out.println("Draw Frame Width:" + mDrawFrame.getWidth());
                    System.out.println("Draw Frame Height:" + mDrawFrame.getHeight());

                    //pass width and height of screen as arguments to launch animation
                    Bundle arguments = new Bundle();

                    arguments.putDouble("width", mDrawFrameWidth);
                    arguments.putDouble("height", mDrawFrameHeight);

                    //TODO: These values are hardcoded so it will be easier to compress the image on the DE1 side,
                    //TODO: Need to find a better work around to accomodate variable screen sizes

                    //arguments.putDouble("width", 1362);
                    //arguments.putDouble("height", 956);

                    mDrawer = new DrawerFragment();
                    mDrawer.setArguments(arguments);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    /* Replace the current fragment that is being displayed, provide it with a tag so we can
                    * locate it in the future
                    */
                    transaction.add(R.id.drawing_space, mDrawer, "draw_space");

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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.stream_to_device:

                Intent intent = new Intent(getActivity(), BluetoothActivity.class);

                /* Close the bluetooth connection */
                if (BoredApplication.isConnectedToBluetooth) {
                    intent.putExtra("bluetooth_request", BluetoothActivity.CLOSE_CONNECTION);

                    /* Open the connection so we can stream data to the bluetooth chip */
                } else {
                    intent.putExtra("bluetooth_request", BluetoothActivity.OPEN_CONNECTION);
                }

                startActivity(intent);

        }

        return true;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.getItem(0).setVisible(true);
        menu.getItem(0).setEnabled(true);

        /* Depending on whether we are connected to the device
         * change the title of this option
         */
        if (BoredApplication.isConnectedToBluetooth) {
            menu.getItem(0).setTitle(R.string.close_bluetooth_stream);
        } else {
            menu.getItem(0).setTitle(R.string.open_bluetooth_stream);
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
                final byte[] pixelData = mDrawer.saveScreen();

                int sendSize = 20;
                final byte [] sendData = new byte[sendSize];
                for (int i = 0; i < sendSize; i++){
                    sendData[i] = pixelData[i];
                }
//                for(int i = 0; i < 30; i++) {
//                    int data = pixelData[i] & 255;
//                    System.out.println(data);
//                }

                if(BoredApplication.isConnectedToBluetooth) {
                    Toast.makeText(getActivity(), "Sending frame to bluetooth...", Toast.LENGTH_SHORT).show();
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            BluetoothActivity.writeToBTDevice(sendData);
                        }
                    });
                    thread.start();
                }else{
                    Toast.makeText(getActivity(), "Failed to send frame, not connected", Toast.LENGTH_SHORT).show();
                }


//                Bitmap bitmap = BitmapFactory.decodeByteArray(pixelData, 0, pixelData.length, new BitmapFactory.Options());
//                if(bitmap == null){
//                    Log.e(LOG_TAG, "Failed to Decode Byte Array");
//                }else{
//                    Log.i(LOG_TAG, "Success, Bitmap Decoded From Byte Array");
//                }

                break;

//                File file = getActivity().getFilesDir();
//
//                String path = file.getAbsolutePath();
//
//                Log.i(LOG_TAG, "path to files:" + path);
//
//                mDrawer.saveFrame(path + "/frame.jpg");
//
//                File frame = new File(path + "/frame.jpg");
//                for (int i = 0; i < file.listFiles().length; i++) {
//                    String s = file.listFiles()[i].toString();
//                    long size = file.listFiles()[i].getTotalSpace();
//                    Log.i(LOG_TAG, "file contained in app directory: " + s);
//                    Log.i(LOG_TAG, "file contained" + size + "bytes");
//                }
//                FileInputStream inputStream = null;
//                try {
//                    inputStream = new FileInputStream(frame);
//                } catch (FileNotFoundException e) {
//                }
//
//                //final BitmapFactory.Options options = new BitmapFactory.Options();
//                //options.inJustDecodeBounds = true;
//                BitmapFactory.Options o = new BitmapFactory.Options();
//                o.inPreferredConfig = Bitmap.Config.ARGB_8888;
//
//                Rect rect = new Rect(50, 50, 50, 50);
//                Bitmap frameBitmap = null;
//                try {
//                    frameBitmap = BitmapRegionDecoder.newInstance(inputStream, true).decodeRegion(rect, o);
//                } catch (IOException e) {
//                }
//

//                int size = frameBitmap.getRowBytes() * frameBitmap.getHeight();
//                ByteBuffer b = ByteBuffer.allocate(size);
//
//                frameBitmap.copyPixelsToBuffer(b);

//                byte[] bytes = new byte[size];
//
//                try {
//                    b.get(bytes, 0, bytes.length);
//                } catch (BufferUnderflowException e) {
//                    // always happens
//                }
//
//                //BluetoothActivity.writeToBTDevice(bytes.toString());
//                System.out.println("bitmap file:" + bytes.toString());

                // do something with byte[]
        }

    }

}
