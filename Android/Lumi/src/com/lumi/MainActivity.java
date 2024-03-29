/**
 *  Lumi - Illuminated Notification System
 *  @version 1.0
 * 
 *  Created by:
 * 
 *  @author Ben Shuyi Chen
 *  @author Aman Ali

 *  Utilizes:
 *  
 *  @version 1.1 (28.01.2013)
 *  http://english.cxem.net/arduino/arduino5.php
 *  @author Koltykov A.V.
 *  
 *  Android Color Picker aka. AmbilWarna Library
 *  https://code.google.com/p/android-color-picker/
 */

package com.lumi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "bluetooth2";

    private Button btnClearScreen, btnConnect, btnColorPicker, btnEraserToggle, btnSMS;
    private TextView txtArduino;
    private GridView gridView;
    
    private Handler h;
    private DataUpdateReceiver dataUpdateReceiver;

    static int currentColor = 0xff6699cc; // Currently selected color

    static boolean eraseMode = false;

    final int RECIEVE_MESSAGE = 1; // Status for Handler
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder sb = new StringBuilder();

    private ConnectedThread mConnectedThread;
 
    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "00:12:08:30:01:62";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnClearScreen = (Button) findViewById(R.id.btnClear);
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnColorPicker = (Button) findViewById(R.id.btnColorPicker);
        btnEraserToggle = (Button) findViewById(R.id.btnErase);
        btnSMS = (Button) findViewById(R.id.btnSMS);
        txtArduino = (TextView) findViewById(R.id.txtArduino); // displays received data from the Arduino
        gridView = (GridView) findViewById(R.id.gvLEDs); // displays interactive LED grid

        // Handles incoming messages via Bluetooth
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                case RECIEVE_MESSAGE: // if receive massage
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1); // create string from bytes array
                    sb.append(strIncom); // append string
                    int endOfLineIndex = sb.indexOf("\r\n"); // determine the end-of-line
                    if (endOfLineIndex > 0) { // if end-of-line,
                        String sbprint = sb.substring(0, endOfLineIndex); // extract string
                        Log.i("MSG", "sbprint: " + sbprint);
                        sb.delete(0, sb.length()); // and clear
                        txtArduino.setText("Data from Arduino: " + sbprint); // update TextView
                    }
                    // Log.d(TAG, "...String:"+ sb.toString() + "Byte:" + msg.arg1 + "...");
                    break;
                }
            };
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        btnClearScreen.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write(Comm.clearScreen());
                int count = gridView.getChildCount();
                ImageView tv;
                    for (int i = 0; i < count; i++){
                        tv = (ImageView) gridView.getChildAt(i);
                        tv.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
                    }
            }
        });

        btnConnect.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                closeSocket();
                connectToBluetooth();
            }
        });

        btnEraserToggle.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!eraseMode) {
                    eraseMode = true;
                    btnEraserToggle.setText("Draw");
                } else {
                    eraseMode = false;
                    btnEraserToggle.setText("Eraser");
                }
            }
        });

        btnSMS.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Currently there's no easy to to fake SMS to the phone, so I'm creating 
                // a new notification with a 1 second delay instead.
                Handler myHandler = new Handler();
                myHandler.postDelayed(delayedMsgNotification, 1000);
            }
        });

        btnColorPicker.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(MainActivity.this, currentColor,
                        new OnAmbilWarnaListener() {
                            // Returned when user selects a color
                            public void onOk(AmbilWarnaDialog dialog, int color) {
                                currentColor = color;
                            }
                            // User cancels color selection
                            public void onCancel(AmbilWarnaDialog dialog) {
                            }
                        });
                dialog.show();
            }
        });

        // Configure interactive LED grid
        gridView.setAdapter(new LEDImageAdapter(this));
        gridView.setBackgroundColor(Color.BLACK);
        gridView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent me) {

                //int action = me.getActionMasked();  // MotionEvent types such as ACTION_UP, ACTION_DOWN
                float currentXPosition = me.getX();
                float currentYPosition = me.getY();
                int position = gridView.pointToPosition((int) currentXPosition, (int) currentYPosition);
                //Log.d("GridView", "Position: " + position + " X: " + currentXPosition + " Y: " + currentYPosition);

                if (position >= 0) {
                    // Access text in the cell, or the object itself
                    ImageView tv = (ImageView) gridView.getChildAt(position);

                    byte[] cmd = null;
                    if (eraseMode) {
                        // Setting the color filter to WHITE is equivalent to removing it
                        tv.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

                        // Sending the color BLACK is equivalent to turning the LED off
                        cmd = Comm.displayLEDBytes(position / 8, position % 8, Color.BLACK);
                    } else {
                        tv.setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);
                        cmd = Comm.displayLEDBytes(position / 8, position % 8, currentColor);
                    }
                    if (cmd != null) {
                        mConnectedThread.write(cmd);
                        return true;
                    } else
                        Log.e("Lumi", "Error generating cmd to send to Lumi via BT.");
                }
                return false;
            }
        });

        // Check if Accessibility is Enabled (Required to catch notifications)
        if (Comm.isLumiAccessibilityEnabled(this)) {
            // Initialize NotificationCatcherService
            Context context = this;
            Intent service = new Intent(context, NotificationCatcherService.class);
            context.startService(service);
        }
    }
    
    

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "...onResume - try connect...");
        connectToBluetooth();
        if (dataUpdateReceiver == null) 
            dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilterEmail = new IntentFilter(Comm.EMAIL);
        registerReceiver(dataUpdateReceiver, intentFilterEmail);
        IntentFilter intentFilterMsg = new IntentFilter(Comm.MSG);
        registerReceiver(dataUpdateReceiver, intentFilterMsg);
    }
    
    protected void sendNotification(String title, String message) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.msg;
        CharSequence tickerText = message;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        Context context = getApplicationContext();
        CharSequence contentTitle = title;
        CharSequence contentText = message;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        mNotificationManager.notify(1, notification);
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord",
                        new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    private void connectToBluetooth() {
        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        // A MAC address, which we got above.
        // A Service ID or UUID. In this case we are using the UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        /*try {
            btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }*/

        // Discovery is resource intensive. Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection. This will block until it connects.
        Log.d(TAG, "...Connecting...");
        txtArduino.setText("Looking for Lumi...");
        try {
            btSocket.connect();
            Log.d(TAG, "....Connection ok...");
            txtArduino.setText("Lumi Ready");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error",
                        "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "...In onPause()...");
        if (dataUpdateReceiver != null) 
            unregisterReceiver(dataUpdateReceiver);
        closeSocket();
    }

    private void closeSocket() {
        try {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private boolean checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if (btAdapter == null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
                return true; // Bluetooth is on
            } else {
                // Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
        return false; // Bluetooth is off
    }

    private void errorExit(String title, String message) {
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256]; // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer); // Get number of bytes and message in "buffer"
                    h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget(); // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
                Log.d(TAG, "...Sending Bytestream: " + msgBuffer + "...");
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
                txtArduino.setText("Error: " + e.getMessage());
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            try {
                mmOutStream.write(message);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
                txtArduino.setText("Error: " + e.getMessage());
            }
        }
    }
    
    // Listens for NotificationCatcherService to catch a notification
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BroadcastReceiver", intent.getAction().toString());
            if (intent.getAction().equals(Comm.EMAIL)) {
                mConnectedThread.write(Comm.displayGmail());
            }
            else if (intent.getAction().equals(Comm.EMAIL_URGENT)) {
                mConnectedThread.write(Comm.displayGmailUrgent());
            }
            else if (intent.getAction().equals(Comm.MSG)) {
                mConnectedThread.write(Comm.displayMsg());
            }
        }
    }
    
    // Demo purposes only
    private Runnable delayedMsgNotification = new Runnable()
    {
        public void run()
        {
            sendNotification("Message", "Hiya Lumi!");
        }
     };
}