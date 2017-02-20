package com.mike.linegraph;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class Bluetooth extends AppCompatActivity {
    TextView myLabel;
    EditText myTextbox;
    Handler mHandler;
    int i = 0;
    DataPoint values;
    GraphView graph;
    private LineGraphSeries<DataPoint> mSeries1,mSeries2;

    //////////////////////////////////////////////////////////////////
    final int RECIEVE_MESSAGE = 1;
    BluetoothAdapter blueAdapter = null;
    BluetoothSocket blueSocket = null;
    private StringBuilder sb = new StringBuilder();
    Set<BluetoothDevice> pairedDevices;

    final ArrayList<String> s = new ArrayList<String>();

    private final static int REQUEST_ENABLE_BT = 1;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";

    ConnectThread myDeviceConnected;
    ConnectedThread mySocketConnected;


    //////////////////////////////////////////////////////////////////


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        graph = (GraphView) findViewById(R.id.graph);
       /* mSeries1= new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });*/

        //graph.addSeries(mSeries1);
        values = new DataPoint(0,0);
        mSeries1 = new LineGraphSeries<>();
        myLabel = (TextView)findViewById(R.id.label);
        myTextbox = (EditText)findViewById(R.id.entry);

        blueAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = blueAdapter.getBondedDevices();

        Button sendButton = (Button)findViewById(R.id.send);

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        final ListAdapter mArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,s);

        ListView thelist = (ListView)findViewById(R.id.thelist);
        thelist.setAdapter(mArrayAdapter);

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:													// if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        int myNum = 0;
                        String strIncom = new String(readBuf, 0, msg.arg1);					// create string from bytes array
                        sb.append(strIncom);												// append string
                        int endOfLineIndex = sb.indexOf("\r\n");							// determine the end-of-line
                        if (endOfLineIndex > 0) { 											// if end-of-line,
                            String sbprint = sb.substring(0, endOfLineIndex);				// extract string
                            sb.delete(0, sb.length());										// and clear
                            myLabel.setText("Data from Arduino: " + sbprint);               // update TextView

                            try {
                                myNum = Integer.parseInt(sbprint);
                            } catch(NumberFormatException nfe) {
                                myNum = 0;
                            }

                            values = new DataPoint(i, myNum);
                            mSeries1.appendData(values,true, 60);
                            graph.addSeries(mSeries1);
                            i++;
                        }
                        break;
                }
            }
        };


        //Send Button
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mySocketConnected != null) {
                    String send = myTextbox.getText().toString();
                    byte[] bytesToSend = send.getBytes();
                    mySocketConnected.write(bytesToSend);
                }
            }
        });


    //Check Bluetooth
        if (blueAdapter == null) {
            //Device does not support Bluetooth
            Toast.makeText(this, "Bluetooth Device Not Available", Toast.LENGTH_SHORT).show();
        }
        if (!blueAdapter.isEnabled()) {
            //Disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    //Create List of Paired Devices
        if (pairedDevices.size() > 0) {
            //Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                //Add the name and address to an array adapter to show in a ListView
                s.add(device.getName());
            }
        }
        //Click on device to connect
        thelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String deviceName = String.valueOf(adapterView.getItemAtPosition(position));
                String Picked = "You selected " + String.valueOf(adapterView.getItemAtPosition(position));
                Toast.makeText(Bluetooth.this, Picked, Toast.LENGTH_SHORT).show();
                ;

                for (BluetoothDevice device : pairedDevices) {
                    if (deviceName.equals(device.getName())) {

                        myDeviceConnected = new ConnectThread(device);
                        myDeviceConnected.start();
                    }
                }
            }
        });


    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            mySocketConnected = new ConnectedThread(mmSocket);
            mySocketConnected.start();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);		// Get number of bytes and message in "buffer"
                    mHandler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();		// Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
