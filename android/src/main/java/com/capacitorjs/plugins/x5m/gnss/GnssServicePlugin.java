package com.capacitorjs.plugins.x5m.gnss;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDataCallback;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDeviceCallback;

import com.capacitorjs.plugins.x5m.gnss.contracts.onBluetoothPermissionCallBack;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import org.json.JSONArray;

@CapacitorPlugin(name = "GnssRtkNtripX5mDevice")
public class GnssServicePlugin  extends Plugin {

    private final GnssService implementation = new GnssService();

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


        implementation.addBluetoothDataListeners(
                new OnBluetoothDataCallback.BluetoothReadingBytesCallback() {
                    @Override
                    public void success(byte[] bytes) {
                        JSObject ret = new JSObject();

                        ret.put("value", ret);

                        notifyListeners("initResult", ret);
                    }
                },
                new OnBluetoothDataCallback.BluetoothReadingDataCallback() {
                    @Override
                    public void success(String data) {
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


}