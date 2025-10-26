package com.capacitorjs.plugins.x5m.gnss.contracts;

import android.content.BroadcastReceiver;

import org.json.JSONArray;

public interface OnBluetoothDeviceCallback {

    public interface BluetoothDeviceEnabledCallback {
        public void success(boolean v);
    }

    public interface BluetoothDeviceConnectedCallback {
        public void success(boolean v);
        public void error(String error);
    }

    public interface BluetoothDeviceListCallback {
        public void success(JSONArray jsonArray);
    }

    public interface BluetoothUnpairedDevicesCallback {
        public void info(JSONArray jsonArray);
        public void success(boolean v);
        public void error(String error);

    }

    public interface BluetoothBroadcastReceiverCallback {

        public void success(final BroadcastReceiver discoverReceiver);
        public void info(final BroadcastReceiver discoverReceiver);

    }



    public interface BluetoothDeviceStartedCallback {
        public void success(boolean v);
    }

    public interface BluetoothDeviceStoppedCallback {
        public void success(boolean v);
    }


}
