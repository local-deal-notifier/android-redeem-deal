package com.example.davidvuong.bluetoothwrite;

import android.bluetooth.BluetoothSocket;

/**
 * Created by David Vuong on 10/15/2016.
 */

public interface ConnectDeviceCallback {
    void processFinish(BluetoothSocket socket);
}
