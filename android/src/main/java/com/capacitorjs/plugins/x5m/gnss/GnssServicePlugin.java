package com.capacitorjs.plugins.x5m.gnss;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import android.util.Base64;

import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDataCallback;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDeviceCallback;

import com.capacitorjs.plugins.x5m.gnss.contracts.onBluetoothPermissionCallBack;
import com.getcapacitor.JSObject;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import android.Manifest;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.Socket;


@CapacitorPlugin(
        name = "GnssRtkNtripX5mDevice",
        permissions = {
                @Permission(strings = {
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                }, alias = "bluetooth")
        }
)
public class GnssServicePlugin  extends Plugin {

    private final GnssService implementation = new GnssService();

    @PluginMethod
    public void iniGNSSService(PluginCall call){

        String source = call.getString("source");

        if(source==null || source.equals("bluetooth")) {

            Context context = getActivity().getApplicationContext();

            implementation.iniBluetoothService(context);

            readGNSSBluetoothDataListeners();

            call.resolve();
        } else {
            call.reject("Invalid source");
        }
    }


    @PluginMethod
    public void listSourcesDevices(PluginCall call){

        try {

            JSONArray devices = implementation.listBondedBluetoothDevices();

            JSObject ret = new JSObject();

            ret.put("devices", devices);

            call.resolve(ret);

        } catch (JSONException e) {
            call.reject(e.getMessage(), e);
        }
    }


    @PluginMethod
    public void connect(PluginCall call){

        try {

            boolean secure = Boolean.TRUE.equals(call.getBoolean("secure"));
            String macAddress = call.getString("macAddress");

            OnBluetoothDeviceCallback.BluetoothDeviceConnectedCallback callback;

            callback = new OnBluetoothDeviceCallback.BluetoothDeviceConnectedCallback() {
                @Override
                public void success(boolean v) {

                    JSObject ret = new JSObject();

                    ret.put("value", ret);

                    call.resolve(ret);
                }

                @Override
                public void error(String error) {
                    call.reject(error);
                }

            };

            implementation.connectToBluetoothDevice(macAddress, secure, callback);

        } catch (Exception e) {
            call.reject(e.getMessage());

        }



    }

    @PluginMethod
    public void disconnect(PluginCall call){

        implementation.stopBluetoothDevice();
        call.resolve();

    }

    /**
    * host: string,
     * port: string,
     * username: string,
     * password: string,
     * mountpoint: string
    */


    @PluginMethod
    public void startNtrip(PluginCall call){

    }

    @PluginMethod
    public void sendNtripData(PluginCall call){


        String base64Data = call.getString("data");

        if (base64Data == null) {
            call.reject("Missing 'data' parameter");
            return;
        }

        try {
            byte[] ntripBytes = Base64.decode(base64Data, Base64.DEFAULT);
            implementation.writeToBluetoothDevice(ntripBytes);
            call.resolve();
        } catch (Exception e) {
            call.reject("Error decoding NTRIP data: " + e.getMessage());
        }


    }


    public void readGNSSBluetoothDataListeners() {

        implementation.addBluetoothDataListeners(
                new OnBluetoothDataCallback.BluetoothReadingBytesCallback() {
                    @Override
                    public void success(byte[] bytes) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("readData", ret);
                    }
                },
                new OnBluetoothDataCallback.BluetoothReadingDataCallback() {
                    @Override
                    public void success(String data) {

                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("readRawData", ret);
                    }
                }
        );
    }


    @PluginMethod
    public void deviceListeners(PluginCall call) {


        implementation.addBluetoothDeviceListeners(
                new OnBluetoothDeviceCallback.BluetoothDeviceConnectedCallback() {
                    @Override
                    public void success(boolean v) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }

                    @Override
                    public void error(String error) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }

                },
                new OnBluetoothDeviceCallback.BluetoothDeviceEnabledCallback() {

                    @Override
                    public void success(boolean v) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }
                },
                new OnBluetoothDeviceCallback.BluetoothDeviceListCallback() {
                    @Override
                    public void success(JSONArray jsonArray) {

                    }
                },
                new OnBluetoothDeviceCallback.BluetoothUnpairedDevicesCallback() {
                    @Override
                    public void info(JSONArray jsonArray) {

                    }

                    @Override
                    public void success(boolean v) {

                    }

                    @Override
                    public void error(String error) {

                    }
                },
                new OnBluetoothDeviceCallback.BluetoothBroadcastReceiverCallback() {
                    @Override
                    public void success(BroadcastReceiver discoverReceiver) {
                        getActivity().unregisterReceiver(discoverReceiver);
                    }

                    @Override
                    public void info(BroadcastReceiver discoverReceiver) {
                        Activity activity = getActivity();
                        activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
                        activity.registerReceiver(discoverReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

                    }
                },
                new OnBluetoothDeviceCallback.BluetoothDeviceStartedCallback(){

                    @Override
                    public void success(boolean v) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }

                },
                new OnBluetoothDeviceCallback.BluetoothDeviceStoppedCallback(){

                    @Override
                    public void success(boolean v) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }

                }
        );






        implementation.addBluetoothRequestPermissionCallback(
                new onBluetoothPermissionCallBack.BluetoothRequestPermissionCallback(){


                    @Override
                    public void success(boolean v) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }

                    @Override
                    public void info(JSONArray jsonArray) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }

                    @Override
                    public void error(String error) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }
                }
        );
    }

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        // Check if granted
        if (getPermissionState("bluetooth") == PermissionState.GRANTED) {
            JSObject ret = new JSObject();
            ret.put("granted", true);
            call.resolve(ret);
        } else {
            // Request permissions
            requestPermissionForAlias("bluetooth", call, "bluetoothPermsCallback");
        }
    }

    @PermissionCallback
    private void bluetoothPermsCallback(PluginCall call) {
        JSObject ret = new JSObject();
        if (getPermissionState("bluetooth") == PermissionState.GRANTED) {
            ret.put("granted", true);
        } else {
            ret.put("granted", false);
        }
        call.resolve(ret);
    }


    @Override
    protected void handleOnDestroy() {
        implementation.handleOnDestroy();
        super.handleOnDestroy();
    }







}