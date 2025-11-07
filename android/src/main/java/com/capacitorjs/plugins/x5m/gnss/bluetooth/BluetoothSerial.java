package com.capacitorjs.plugins.x5m.gnss.bluetooth;



import android.Manifest;
import android.content.pm.PackageManager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDataListener;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDeviceCallback;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDeviceListener;
import com.capacitorjs.plugins.x5m.gnss.contracts.onBluetoothPermissionCallBack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.util.Log;

/**
 * PhoneGap Plugin for Serial Communication over Bluetooth
 */
public class BluetoothSerial  {

    // actions
    private static final String LIST = "list";
    private static final String CONNECT = "connect";
    private static final String CONNECT_INSECURE = "connectInsecure";
    private static final String DISCONNECT = "disconnect";
    private static final String WRITE = "write";
    private static final String AVAILABLE = "available";
    private static final String READ = "read";
    private static final String READ_UNTIL = "readUntil";
    private static final String SUBSCRIBE = "subscribe";
    private static final String UNSUBSCRIBE = "unsubscribe";
    private static final String SUBSCRIBE_RAW = "subscribeRaw";
    private static final String UNSUBSCRIBE_RAW = "unsubscribeRaw";
    private static final String IS_ENABLED = "isEnabled";
    private static final String IS_CONNECTED = "isConnected";
    private static final String CLEAR = "clear";
    private static final String SETTINGS = "showBluetoothSettings";
    private static final String ENABLE = "enable";
    private static final String DISCOVER_UNPAIRED = "discoverUnpaired";
    private static final String SET_DEVICE_DISCOVERED_LISTENER = "setDeviceDiscoveredListener";
    private static final String CLEAR_DEVICE_DISCOVERED_LISTENER = "clearDeviceDiscoveredListener";
    private static final String SET_NAME = "setName";
    private static final String SET_DISCOVERABLE = "setDiscoverable";

    // callbacks

    private final List<OnBluetoothDeviceListener> bluetoothDeviceListeners = new ArrayList<OnBluetoothDeviceListener>();
    private final List<OnBluetoothDataListener> bluetoothDataListeners = new ArrayList<OnBluetoothDataListener>();
    private onBluetoothPermissionCallBack.BluetoothRequestPermissionCallback requestPermissionCallback;


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSerialService bluetoothSerialService;

    // Debugging
    private static final String TAG = "BluetoothSerial";
    private static final boolean D = true;

    // Message types sent from the BluetoothSerialService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int MESSAGE_READ_RAW = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    StringBuffer buffer = new StringBuffer();
    private String delimiter;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    // Android 23 requires user to explicitly grant permission for location to discover unpaired
    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int CHECK_PERMISSIONS_REQ_CODE = 2;


    public void ini(Context context) {
        if (bluetoothSerialService == null) {
            bluetoothSerialService = new BluetoothSerialService(mHandler, context);
        }

        getAdapter();
    }


    public void getAdapter(){
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
    }

    public void addBluetoothDeviceListener(OnBluetoothDeviceListener listener){
        bluetoothDeviceListeners.add(listener);
    }

    public void addBluetoothDataListener(OnBluetoothDataListener listener){
        bluetoothDataListeners.add(listener);
    }

    public void addRequestPermissionCallback(onBluetoothPermissionCallBack.BluetoothRequestPermissionCallback callBack){
        requestPermissionCallback = callBack;

    }

    public void notifyDeviceConnected(boolean v) {

        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothConnected(v);
    }

    public void notifyDeviceEnabled(boolean v) {

        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothEnabled(v);
    }

    public void notifyDeviceDeviceList(JSONArray jsonArray) {

        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothDeviceList(jsonArray);
    }

    public void notifyUnpairedDevicesInfo(JSONArray jsonArray) {

        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothUnpairedDevices(jsonArray);
    }

    public void notifyOneUnpairedDeviceInfo(JSONObject jsonObject) {

        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothOneUnpairedDevice(jsonObject);
    }



    public void notifyUnpairedDevicesSuccess(boolean v) {

        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothUnpairedSuccess(v);
    }

    public void notifyBroadcastReceiverSuccess(final BroadcastReceiver discoverReceiver){
        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothBroadcastReceiverSuccess(discoverReceiver);
    }

    public void notifyBroadcastReceiverInfo(final BroadcastReceiver discoverReceiver){
        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothBroadcastReceiver(discoverReceiver);
    }


    public void notifyDeviceConnectError(String error) {

        for (OnBluetoothDeviceListener listener : bluetoothDeviceListeners)
            listener.onBluetoothConnectError(error);
    }

    public void notifyReadingBytes(byte[] bytes) {

        for (OnBluetoothDataListener listener : bluetoothDataListeners)
            listener.onBluetoothReadingBytes(bytes);
    }

    public void notifyReadingString(String data) {

        for (OnBluetoothDataListener listener : bluetoothDataListeners)
            listener.onBluetoothReadingData(data);
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {

            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "User enabled Bluetooth");
                notifyDeviceEnabled(true);
            } else {
                Log.d(TAG, "User did *NOT* enable Bluetooth");
                notifyDeviceEnabled(false);
            }

        }
    }


    public void onDestroy() {
        if (bluetoothSerialService != null) {
            bluetoothSerialService.stop();
        }
    }

    public JSONArray listBondedDevices() throws JSONException {
        JSONArray deviceList = new JSONArray();
        Context ctx = bluetoothSerialService.getBluethoothContext();
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return deviceList;
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : bondedDevices) {
            deviceList.put(deviceToJSON(device));
        }

        return deviceList;
        //notifyDeviceDeviceList(deviceList);
    }

    private void discoverUnpairedDevices(final onBluetoothPermissionCallBack.BluetoothRequestPermissionCallback callbackContext) throws JSONException {



        final BroadcastReceiver discoverReceiver = new BroadcastReceiver() {

            private JSONArray unpairedDevices = new JSONArray();

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    try {
                        JSONObject o = deviceToJSON(device);
                        unpairedDevices.put(o);
                        notifyOneUnpairedDeviceInfo(o);

                    } catch (JSONException e) {
                        // This shouldn't happen, log and ignore
                        Log.e(TAG, "Problem converting device to JSON", e);
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    notifyUnpairedDevicesSuccess(true);
                    notifyUnpairedDevicesInfo(unpairedDevices);
                    notifyBroadcastReceiverSuccess(this);
                }
            }
        };

        notifyBroadcastReceiverInfo(discoverReceiver);

        Context ctx = bluetoothSerialService.getBluethoothContext();

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bluetoothAdapter.startDiscovery();
    }

    private JSONObject deviceToJSON(BluetoothDevice device) throws JSONException {

        JSONObject json = new JSONObject();

        Context ctx = bluetoothSerialService.getBluethoothContext();

        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.



            return new JSONObject();

        }
        json.put("name", device.getName());
        json.put("address", device.getAddress());
        json.put("id", device.getAddress());
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }

    public void connect(String macAddress, boolean secure, OnBluetoothDeviceCallback.BluetoothDeviceConnectedCallback callbackContext) throws JSONException {

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

        if (device != null) {

            bluetoothSerialService.connect(device, secure);
            buffer.setLength(0);

            callbackContext.success(true);
            //notifyDeviceConnected(true);

        } else {
            callbackContext.error("Could not connect to " + macAddress);
            //notifyDeviceConnected(false);
            //callbackContext.error("Could not connect to " + macAddress);
        }
    }


    public void stop(){
        bluetoothSerialService.stop();
    }

    public void write(byte[] data){
        bluetoothSerialService.write(data);
    }

    // The Handler that gets information back from the BluetoothSerialService
    // Original code used handler for the because it was talking to the UI.
    // Consider replacing with normal callbacks
    private final Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    buffer.append((String)msg.obj);


                        sendDataToSubscriber();


                    break;
                case MESSAGE_READ_RAW:

                        byte[] bytes = (byte[]) msg.obj;
                        sendRawDataToSubscriber(bytes);

                    break;
                case MESSAGE_STATE_CHANGE:

                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothSerialService.STATE_CONNECTED:
                            Log.i(TAG, "BluetoothSerialService.STATE_CONNECTED");
                            notifyConnectionSuccess();
                            break;
                        case BluetoothSerialService.STATE_CONNECTING:
                            Log.i(TAG, "BluetoothSerialService.STATE_CONNECTING");
                            break;
                        case BluetoothSerialService.STATE_LISTEN:
                            Log.i(TAG, "BluetoothSerialService.STATE_LISTEN");
                            break;
                        case BluetoothSerialService.STATE_NONE:
                            Log.i(TAG, "BluetoothSerialService.STATE_NONE");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    //  byte[] writeBuf = (byte[]) msg.obj;
                    //  String writeMessage = new String(writeBuf);
                    //  Log.i(TAG, "Wrote: " + writeMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    Log.i(TAG, msg.getData().getString(DEVICE_NAME));
                    break;
                case MESSAGE_TOAST:
                    String message = msg.getData().getString(TOAST);
                    notifyConnectionLost(message);
                    break;
            }
        }
    };

    private void notifyConnectionLost(String error) {
        notifyDeviceConnectError(error);
    }

    private void notifyConnectionSuccess() {
        notifyDeviceConnected(true);
    }

    private void sendRawDataToSubscriber(byte[] data) {
        if (data != null && data.length > 0) {
            notifyReadingBytes(data);
        }
    }

    private void sendDataToSubscriber() {
        String data = readUntil(delimiter);
        if (data != null && data.length() > 0) {

            notifyReadingString(data);

            sendDataToSubscriber();
        }
    }

    private int available() {
        return buffer.length();
    }

    private String read() {
        int length = buffer.length();
        String data = buffer.substring(0, length);
        buffer.delete(0, length);
        return data;
    }

    private String readUntil(String c) {
        String data = "";
        int index = buffer.indexOf(c, 0);
        if (index > -1) {
            data = buffer.substring(0, index + c.length());
            buffer.delete(0, index + c.length());
        }
        return data;
    }


    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) throws JSONException {

        for(int result:grantResults) {
            if(result == PackageManager.PERMISSION_DENIED) {
                Log.d(TAG, "User *rejected* location permission");
                requestPermissionCallback.error("Location permission is required to discover unpaired devices.");
                return;
            }
        }

        switch(requestCode) {
            case CHECK_PERMISSIONS_REQ_CODE:
                Log.d(TAG, "User granted location permission");
                discoverUnpairedDevices(requestPermissionCallback);
                break;
        }
    }
}

