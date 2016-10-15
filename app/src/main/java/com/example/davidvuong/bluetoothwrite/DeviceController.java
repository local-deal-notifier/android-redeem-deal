package com.example.davidvuong.bluetoothwrite;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by David Vuong on 10/15/2016.
 */

public class DeviceController {
    private BluetoothSocket socket;
    private Context context;
    private Thread workerThread;
    private boolean stopWorker;
    private int readBufferPosition = 0;
    byte[] readBuffer;

    public DeviceController(BluetoothSocket socket, Context context) {
        this.socket = socket;
        this.context = context;
    }

    public void send(String input) {
        try {
            socket.getOutputStream().write(input.getBytes());
        }
        catch (Exception ex) {
            Toast.makeText(context, "Failed to send to Arduino", Toast.LENGTH_SHORT);
        }
    }

    public void read() throws IOException {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = socket.getInputStream().available();
                        if(bytesAvailable >= 1)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
//                            Log.d("test", Integer.toString(bytesAvailable));
                            socket.getInputStream().read(packetBytes);

                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                Log.d("test", Integer.toString(b));
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    Log.d("test", data);
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            Log.d("test", "sending back");
                                            send("test");
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        Log.d("test", ex.getMessage());
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    public void close() {
        try {
            stopWorker = true;
            socket.close();
        }
        catch (Exception ex) {
            Toast.makeText(context, "Failed to close communication", Toast.LENGTH_SHORT);
        }
    }
}
