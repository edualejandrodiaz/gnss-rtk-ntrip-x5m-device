package com.capacitorjs.plugins.x5m.gnss.contracts;

import android.content.BroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONObject;

public interface OnBluetoothDeviceListener {

    public void onBluetoothEnabled(boolean enabled);
    public void onBluetoothConnected(boolean connected);
    public void onBluetoothConnectError(String error);
    public void onBluetoothDeviceList(JSONArray jsonArray);
    public void onBluetoothUnpairedDevices(JSONArray jsonArray);
    public void onBluetoothOneUnpairedDevice(JSONObject jsonObject);
    public void onBluetoothBroadcastReceiverSuccess(BroadcastReceiver discoverReceiver);
    public void onBluetoothBroadcastReceiver(BroadcastReceiver discoverReceiver);
    public void onBluetoothUnpairedSuccess(boolean v);
    public void onReadingBluetoothStarted(boolean started);
    public void onReadingBluetoothStopped(boolean stopped);
}
