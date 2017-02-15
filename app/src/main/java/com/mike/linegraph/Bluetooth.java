package com.mike.linegraph;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class Bluetooth extends AppCompatActivity {
    TextView myLabel;
    EditText myTextbox;

    //////////////////////////////////////////////////////////////////

    BluetoothAdapter mBluetoothAdapter;

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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();

        Button openButton = (Button)findViewById(R.id.open);
        Button sendButton = (Button)findViewById(R.id.send);
        Button closeButton = (Button)findViewById(R.id.close);

        myLabel = (TextView)findViewById(R.id.label);
        myTextbox = (EditText)findViewById(R.id.entry);

        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        final ListAdapter mArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,s);

        ListView thelist = (ListView)findViewById(R.id.thelist);
        thelist.setAdapter(mArrayAdapter);



        //Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mySocketConnected != null) {
                    String send = "1";
                    byte[] bytesToSend = send.getBytes();
                    mySocketConnected.write(bytesToSend);
                }
            }
        });

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

        //Close button
        closeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mySocketConnected != null) {
                    String send = "2";
                    byte[] bytesToSend = send.getBytes();
                    mySocketConnected.write(bytesToSend);
                }
            }
        });


        if (mBluetoothAdapter == null) {
            //Device does not support Bluetooth
            Toast.makeText(this, "Bluetooth Device Not Available", Toast.LENGTH_SHORT).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            //Disabled
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        if (pairedDevices.size() > 0) {
            //Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                //Add the name and address to an array adapter to show in a ListView
                s.add(device.getName());
            }
        }
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
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);

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
