package cpen391.team6.bored.Activities;

/**
 * Created by neema on 2017-03-14.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import cpen391.team6.bored.BoredApplication;
import cpen391.team6.bored.R;
import cpen391.team6.bored.Utility.UI_Util;


/**
 * Created by neema on 2017-03-14.
 */
public class BluetoothActivity extends Activity {

    // A constant that we use to determine if our request to turn on bluetooth worked
    public final static int REQUEST_ENABLE_BT = 1;
    // A handle to the tablet’s bluetooth adapter

    // Flags for whether the activity should open the bluetooth connection or close it
    public final static int OPEN_CONNECTION = 1;
    public final static int CLOSE_CONNECTION = 0;

    // input/output “streams” with which we can read and write to device
    // Use of “static” important, it means variables can be accessed
    // without an object, this is useful as other activities can use
    // these streams to communicate after they have been opened.
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;

    // a bluetooth “socket” to a bluetooth device
    private BluetoothSocket mmSocket = null;

    private BluetoothAdapter mBluetoothAdapter;
    private int create_or_close;
    private AlertDialog mConnectionDialog;
    private Handler mHandler;

    // get the context for the application. We use this with things like "toast" popups

    public synchronized static void writeToBTDevice(String message) {
        String s = new String("\r\n");
        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();

        System.out.println("Sending:" + msgBuffer.toString());
        try {
            mmOutStream.write(msgBuffer);
            mmOutStream.write(newline);
        } catch (IOException e) {

        }

//        try {
//            Thread.sleep(60);
//        }catch(InterruptedException e){}
    }

    public synchronized static void writeToBTDevice(byte[] bytes) {
        String s = new String("\r\n");
        byte[] newline = s.getBytes();

        // System.out.println("Sending:" + msgBuffer.toString());
        try {
            mmOutStream.write(bytes);
            mmOutStream.write(newline);
        } catch (IOException e) {

        }


    }

    public static String readFromBTDevice() {

        byte c;
        String s = new String("");

        try { // Read from the InputStream using polling and timeout
            for (int i = 0; i < 4; i++) { // try to read for 2 seconds max
                SystemClock.sleep(10);
                if (mmInStream != null && mmInStream.available() > 0) {
                    if ((c = (byte) mmInStream.read()) != '\r') // '\r' terminator
                        s += (char) c; // build up string 1 byte by byte
                    else
                        return s;
                }
            }
        } catch (IOException e) {
            return new String("-- No Response --");
        }
        return s;
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.dialog_progress_bar);

        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(this, R.style.DialogTheme));

        builder.setTitle(getString(R.string.dialog_connecting_bluetooth))
                .setView(R.layout.dialog_progress_bar);

        mConnectionDialog = builder.create();
        mConnectionDialog.setCanceledOnTouchOutside(false);
        mConnectionDialog.show();

        UI_Util.setDialogStyle(mConnectionDialog, this);

        create_or_close = CLOSE_CONNECTION;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            create_or_close = extras.getInt("bluetooth_request");
        }
        // This call returns a handle to the onex    bluetooth device within your Android device
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // check to see if your android device even has a bluetooth device !!!!,
        if (mBluetoothAdapter == null) {
            Toast toast = Toast.makeText(this, "No Bluetooth Available!!", Toast.LENGTH_LONG);
            toast.show();
            mConnectionDialog.dismiss();
            finish(); // if no bluetooth device on this tablet don’t go any further.
        }

        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message message) {
                String msg = message.getData().getString("toast_message");
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                // This is where you do your work in the UI thread.
                // Your worker tells you in the message what to do.
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {


                // If the bluetooth device is not enabled, let’s turn it on
                if (!mBluetoothAdapter.isEnabled()) {

                    // create a new intent that will ask the bluetooth adaptor to “enable” itself.
                    // A dialog box will appear asking if you want turn on the bluetooth device

                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

                    // REQUEST_ENABLE_BT below is a constant (defined as '1 - but could be anything)
                    // When the “activity” is run and finishes, Android will run your onActivityResult()
                    // function (see next page) where you can determine if it was successful or not

                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }

                Set<BluetoothDevice> thePairedDevices = mBluetoothAdapter.getBondedDevices();
                // If there are devices that have already been paired
                // get an iterator for the set of devices and iterate 1 device at a time

                boolean deviceFound = false;

                try {
                    if (thePairedDevices.size() > 0) {
                        Iterator<BluetoothDevice> iter = thePairedDevices.iterator();
                        BluetoothDevice aNewdevice;
                        while (iter.hasNext()) { // while at least one more device
                            aNewdevice = iter.next(); // get next element in set
                            if (aNewdevice.getName().equals(getString(R.string.device_name))) {

                                deviceFound = true;
                                CreateSerialBluetoothDeviceSocket(aNewdevice);

                                if (create_or_close == OPEN_CONNECTION) {
                                    connect();

                                    //writeToBTDevice("something");
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                    }

                                    System.out.println("Bluetooth output: " + readFromBTDevice());
                                    mConnectionDialog.dismiss();
                                    //sendMessageToUI("Sent Sample Message To Bluetooth Chip");

                                    //Toast toast = Toast.makeText(getApplicationContext(), "Connected to bluetooth device and sent sample message!", Toast.LENGTH_SHORT);
                                    //toast.show();

                                    setResult(RESULT_OK);
                                    killActivity();

                                } else if (create_or_close == CLOSE_CONNECTION) {
                                    closeConnection();
                                    setResult(RESULT_OK);
                                    sendMessageToUI("Closed Bluetooth Connection");
                                    //Toast.makeText(getApplicationContext(), "Closed Bluetooth Connection", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                    }
                    if (!deviceFound) {
                        sendMessageToUI("Could Not Find Bluetooth Device In Paired Devices!");
                        //Toast toast = Toast.makeText(getApplicationContext(), "Could not find bluetooth device in paired devices", Toast.LENGTH_SHORT);
                        //toast.show();
                    }
                } catch (IOException e) {
                    sendMessageToUI("Connection Failed!");
                    //Toast.makeText(getApplicationContext(), "Connection Failed", Toast.LENGTH_SHORT).show();
                }

                mConnectionDialog.dismiss();
                finish();
            }
        });

        thread.start();

    }


    private boolean connect() throws IOException {

        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        // Attempt connection to the device through the socket.
        mmSocket.connect();
        sendMessageToUI("Connection Made");
        //Toast.makeText(getApplicationContext(), "Connection Made", Toast.LENGTH_LONG).show();


        //create the input/output stream and record fact we have made a connection
        GetInputOutputStreamsForSocket(); // see page 26
        BoredApplication.isConnectedToBluetooth = true;

        return true;
    }

    // gets the input/output stream associated with the current socket
    private void GetInputOutputStreamsForSocket() {
        try {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        } catch (IOException e) {
        }
    }


    private void CreateSerialBluetoothDeviceSocket(BluetoothDevice device) {
        mmSocket = null;

        // universal UUID for a serial profile RFCOMM blue tooth device
        // this is just one of those “things” that you have to do and just works
        UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        // Get a Bluetooth Socket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            mmSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            sendMessageToUI("Socket Creation Failed!");
            //Toast.makeText(getApplicationContext(), "Socket Creation Failed", Toast.LENGTH_LONG).show();
        }
    }

    private void sendMessageToUI(String msg) {

        Message message = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("toast_message", msg);
        message.setData(bundle);
        message.sendToTarget();
    }


    private void closeConnection() {

        try {
            mmInStream.close();
            mmInStream = null;
        } catch (IOException e) {
        }
        try {
            mmOutStream.close();
            mmOutStream = null;
        } catch (IOException e) {
        }
        try {
            mmSocket.close();
            mmSocket = null;
        } catch (IOException e) {
        }

        BoredApplication.isConnectedToBluetooth = false;

    }

    private void discover() {

        // Before starting discovery make sure discovery is cancelled
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        // now start scanning for new devices. The broadcast receiver
        // we wrote earlier will be called each time we discover a new device
        // don't make this call if you only want to show paired devices
        mBluetoothAdapter.startDiscovery();
    }

    /* this call back function is run when an activity that returns a result ends.
    * Check the requestCode (given when we start the activity) to identify which
    * activity is returning a result, and then resultCode is the value returned
    * by the activity. In most cases this is RESULT_OK. If not end the activity
    */

    private void killActivity() {
        finish();
    }

    public void onDestroy() {
        super.onDestroy();
        //unregisterReceiver ( mReceiver ); // make sure we unregister
        // our broadcast receiver at end
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) // was it the “enable bluetooth” activity?
            if (resultCode != RESULT_OK) { // if so did it work OK?
                sendMessageToUI("Bluetooth Failed To Start!");
                //Toast toast = Toast.makeText(getApplicationContext(), "BlueTooth Failed to Start ",
                //        Toast.LENGTH_LONG);
                //toast.show();
                finish();
                return;
            }
    }

}