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
import com.capacitorjs.plugins.x5m.gnss.contracts.onTcpDataCallback;
import com.capacitorjs.plugins.x5m.gnss.tcp.ConnTcpOptions;
import com.capacitorjs.plugins.x5m.gnss.tcp.ResultTpc;
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




@CapacitorPlugin(
        name = "GnssNtripDevice",
        permissions = {
                @Permission(strings = {
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN
                }, alias = "bluetooth")
        }
)
public class GnssServicePlugin  extends Plugin {

    private final GnssService implementation = new GnssService();

    /**
     * Initialization GNSS Bluetooth Service
     *
     * @param call PluginCall
     */
    @PluginMethod
    public void iniGnssService(PluginCall call){

        String source = call.getString("source");

        if(source==null || source.equals("bluetooth")) {

            Context context = getActivity().getApplicationContext();

            implementation.iniBluetoothService(context);

            call.resolve();
        } else {
            call.reject("Invalid source");
        }
    }

    /**
     * Lists bonded devices
     *
     * @param call PluginCall
     */
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

    /**
     * Connect to a Bluetooth device.
     *
     * @param call PluginCall
     */
    @PluginMethod
    public void connect(PluginCall call){

        try {

            boolean secure = Boolean.TRUE.equals(call.getBoolean("secure"));
            String macAddress = call.getString("macAddress");
            OnBluetoothDeviceCallback.BluetoothDeviceConnectedCallback callback;

            callback = new OnBluetoothDeviceCallback.BluetoothDeviceConnectedCallback() {
                @Override
                public void success(boolean v) {

                    readGNSSBluetoothDataListeners();

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

    /**
     * Disconnect from GNSS device
     *
     * @param call PluginCall
     */
    @PluginMethod
    public void disconnect(PluginCall call){

        implementation.stopBluetoothDevice();
        call.resolve();

    }


    /**
     * Initiates a connection to an NTRIP Caster to receive GNSS correction data.
     *
     * This function establishes a network connection to an NTRIP server (Caster), authenticates
     * (if required), and requests a data stream from a specific mountpoint. Once connected, it
     * begins receiving correction data (typically in RTCM format) and forwards it to a listener
     * for processing by the device's GNSS receiver.
     *
     * The entire operation is executed asynchronously on a background thread to avoid blocking
     * the main application thread.
     *
     * @param host The IP address or domain name of the NTRIP Caster. For example, "caster.example.com".
     * @param port The port on which the Caster is listening. This is commonly 2101.
     * @param mountpoint The specific mountpoint on the Caster that provides the desired correction data stream. For example, "RTCM3_IMAX".
     * @param username The username for authentication with the Caster. If the mountpoint is public, this parameter can be null or an empty string.
     * @param password The password corresponding to the username. Can be null or an empty string if no authentication is required.
     *
     * @throws IllegalArgumentException If any of the essential parameters (host, port, mountpoint)
     *                                  are null or invalid.
     */
    @PluginMethod
    public void startNtrip(PluginCall call){

        String host = call.getString("host");
        String post = call.getString("port");
        String username = call.getString("username");
        String password = call.getString("password");
        String mountpoint = call.getString("mountpoint");

        ConnTcpOptions connTcpOptions = new ConnTcpOptions(host, post, username, password, mountpoint);

        double lat = implementation.getCurrentLat();
        double lng = implementation.getCurrentLng();


        implementation.startNtrip(
                connTcpOptions,
                lat,
                lng,
                //receiving ntrip data
                new onTcpDataCallback.readingDataCallback() {
                    @Override
                    public void success(ResultTpc resultTpc) {
                        JSObject ret = new JSObject();
                        ret.put("result", resultTpc.getResult());
                        ret.put("encoding", resultTpc.getEncoding());
                        call.resolve(ret);
                    }

                    @Override
                    public void error(String msg) {
                        call.reject(msg);
                    }
                },
                //sending auth data result
                new onTcpDataCallback.sendingDataCallback() {
                    @Override
                    public void success(boolean v) {
                        JSObject ret = new JSObject();
                        ret.put("value", v);
                        call.resolve(ret);
                    }

                    @Override
                    public void error(String msg) {
                        call.reject(msg);
                    }
                }
        );

    }

    /**
     * Stop NTRIP
     *
     * @param call PluginCall
     */
    @PluginMethod
    public void stopNtrip(PluginCall call){
        try {
            implementation.stopNtrip();
            call.resolve();
        } catch (Exception e){
            call.reject(e.getMessage());
        }
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




    public void readGNSSBluetoothDataListeners() {

        implementation.addBluetoothDataListeners(
                new OnBluetoothDataCallback.BluetoothReadingBytesCallback() {
                    @Override
                    public void success(byte[] bytes) {

                        String base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP);

                        JSObject ret = new JSObject();

                        ret.put("result", base64Data);

                        ret.put("encode", "base64");

                        notifyListeners("readData", ret);
                    }
                },
                new OnBluetoothDataCallback.BluetoothReadingDataCallback() {
                    @Override
                    public void success(String data) {

                        JSObject ret = new JSObject();

                        ret.put("result", data);

                        ret.put("encode", "base64");

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


    @Override
    protected void handleOnDestroy() {
        implementation.handleOnDestroy();
        super.handleOnDestroy();
    }







}