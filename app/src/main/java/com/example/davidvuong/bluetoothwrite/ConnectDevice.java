package com.example.davidvuong.bluetoothwrite;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;

import java.util.UUID;

/**
 * Created by David Vuong on 10/15/2016.
 */

public class ConnectDevice extends AsyncTask<Void, Void, Void> {
    private boolean isSuccess = true;
    private String deviceName;
    private String deviceAddress;
    private Context context;
    private MainActivity activity;
    private BluetoothSocket socket;
    private BluetoothAdapter adapter;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConnectDevice(String deviceName, String deviceAddress, Context context, MainActivity activity) {
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.context = context;
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (socket == null || !isSuccess) {
                adapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = adapter.getRemoteDevice(deviceAddress);
                socket = device.createInsecureRfcommSocketToServiceRecord(myUUID);
                adapter.cancelDiscovery();
                socket.connect();
            }
        }
        catch (Exception ex) {
            isSuccess = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        if (isSuccess) {
            activity.processFinish(socket);
        }
    }
}
