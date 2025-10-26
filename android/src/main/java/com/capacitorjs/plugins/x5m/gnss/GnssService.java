package com.capacitorjs.plugins.x5m.gnss;

import android.content.BroadcastReceiver;

import com.capacitorjs.plugins.x5m.gnss.bluetooth.BluetoothSerial;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDataCallback;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDataListener;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDeviceCallback;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDeviceListener;
import com.capacitorjs.plugins.x5m.gnss.contracts.onBluetoothPermissionCallBack;
import com.getcapacitor.BridgeActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class GnssService {

    private BluetoothSerial bluetoothSerialManager;

    public void addBluetoothDeviceListeners(
            OnBluetoothDeviceCallback.BluetoothDeviceConnectedCallback connectedCallback,
            OnBluetoothDeviceCallback.BluetoothDeviceEnabledCallback enabledCallback,
            OnBluetoothDeviceCallback.BluetoothDeviceListCallback listCallback,
            OnBluetoothDeviceCallback.BluetoothUnpairedDevicesCallback unpairedCallback,
            OnBluetoothDeviceCallback.BluetoothBroadcastReceiverCallback broadcastReceiverCallback,
            OnBluetoothDeviceCallback.BluetoothDeviceStartedCallback startedCallback,
            OnBluetoothDeviceCallback.BluetoothDeviceStoppedCallback stoppedCallback
    ){

        bluetoothSerialManager.addBluetoothDeviceListener(new OnBluetoothDeviceListener() {
            @Override
            public void onBluetoothConnected(boolean connected) {
                connectedCallback.success(connected);
            }

            @Override
            public void onBluetoothConnectError(String error) {
                connectedCallback.error(error);
            }

            @Override
            public void onBluetoothDeviceList(JSONArray jsonArray) {
                listCallback.success(jsonArray);
            }

            @Override
            public void onBluetoothUnpairedDevices(JSONArray jsonArray) {
                unpairedCallback.info(jsonArray);
            }

            @Override
            public void onBluetoothOneUnpairedDevice(JSONObject jsonObject) {

            }

            @Override
            public void onBluetoothBroadcastReceiverSuccess(BroadcastReceiver discoverReceiver) {
                broadcastReceiverCallback.success(discoverReceiver);
            }

            @Override
            public void onBluetoothBroadcastReceiver(BroadcastReceiver discoverReceiver) {
                broadcastReceiverCallback.info(discoverReceiver);
            }

            @Override
            public void onBluetoothUnpairedSuccess(boolean v) {

            }

            @Override
            public void onBluetoothEnabled(boolean enabled){
                enabledCallback.success(enabled);
            }


            @Override
            public void onReadingBluetoothStarted(boolean started) {
                startedCallback.success(started);
            }

            @Override
            public void onReadingBluetoothStopped(boolean stopped) {
                stoppedCallback.success(stopped);
            }


        });

    }


    public void addBluetoothDataListeners(
        OnBluetoothDataCallback.BluetoothReadingBytesCallback readBytesCallback,
        OnBluetoothDataCallback.BluetoothReadingDataCallback readDataCallback
    ){
        bluetoothSerialManager.addBluetoothDataListener(new OnBluetoothDataListener(){
            @Override
            public void onBluetoothReadingBytes(byte[] bytes) {
                readBytesCallback.success(bytes);
            }

            @Override
            public void onBluetoothReadingData(String data) {
                readDataCallback.success(data);
            }
        });
    }


    public void addBluetoothRequestPermissionCallback(
            onBluetoothPermissionCallBack.BluetoothRequestPermissionCallback callback
    ){
        bluetoothSerialManager.addRequestPermissionCallback(callback);
    }

    public boolean connectGnssDeviceByBluetooth(){
        return true;
    }

    public boolean connectNtripByTcp(){
        return true;
    }

    public void sendDataToNtripHost(){

    }

    public void readDataFromNtripHost(){

    }

    public void readLmeaData(){

    }
}