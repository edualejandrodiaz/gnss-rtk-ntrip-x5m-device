package com.capacitorjs.plugins.x5m.gnss;

import android.content.BroadcastReceiver;
import android.content.Context;

import com.capacitorjs.plugins.x5m.gnss.bluetooth.BluetoothSerial;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDataCallback;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDataListener;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDeviceCallback;
import com.capacitorjs.plugins.x5m.gnss.contracts.OnBluetoothDeviceListener;
import com.capacitorjs.plugins.x5m.gnss.contracts.onBluetoothPermissionCallBack;
import com.capacitorjs.plugins.x5m.gnss.contracts.onTcpDataCallback;

import com.capacitorjs.plugins.x5m.gnss.tcp.ConnTcpOptions;
import com.capacitorjs.plugins.x5m.gnss.tcp.ResultTpc;
import com.capacitorjs.plugins.x5m.gnss.tcp.TcpSocket;
import com.getcapacitor.BridgeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class GnssService {

    private final BluetoothSerial bluetoothSerialManager;
    private TcpSocket tpcSocket;

    private float currentLat;
    private float currentLng;

    private Integer clientIndex;

    public GnssService(){
        bluetoothSerialManager = new BluetoothSerial();
    }

    public void iniBluetoothService(Context context){
        bluetoothSerialManager.ini(context);
    }

    public JSONArray listBondedBluetoothDevices() throws JSONException {
        return bluetoothSerialManager.listBondedDevices();
    }

    public void connectToBluetoothDevice(
            String macAddress,
            boolean secure,
            OnBluetoothDeviceCallback.BluetoothDeviceConnectedCallback callback
    ) throws JSONException {
        bluetoothSerialManager.connect(macAddress, secure, callback);
    }

    public void stopBluetoothDevice(){
        bluetoothSerialManager.stop();
    }

    public void writeToBluetoothDevice(byte[] data){
        bluetoothSerialManager.write(data);
    }

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

    public void startNtrip(
            ConnTcpOptions connTcpOptions,
            double lat,
            double lng,
            onTcpDataCallback.readingDataCallback readingDataCallback,
            onTcpDataCallback.sendingDataCallback sendingDataCallback
    ){

        String host = connTcpOptions.getHost();
        String port = connTcpOptions.getPort();

        tpcSocket = new TcpSocket();

        Integer integer_port = Integer.parseInt(port);

        try {

            clientIndex = tpcSocket.connect(host, integer_port);

            authNtrip(connTcpOptions, lat, lng, sendingDataCallback);

            tpcSocket.read(clientIndex, 4096, "utf8", new onTcpDataCallback.readingDataCallback() {
                @Override
                public void success(ResultTpc resultTpc) {
                    byte[] bytes = resultTpc.toBytes();
                    writeToBluetoothDevice(bytes);
                    readingDataCallback.success(resultTpc);
                }

                @Override
                public void error(String msg) {
                    readingDataCallback.error(msg);
                }

            });


        } catch (Exception e){
            e.printStackTrace();

        }


    }

    public void stopNtrip() throws Exception {

        tpcSocket.disconnect(clientIndex);

    }


    private void authNtrip(
            ConnTcpOptions connTcpOptions,
            double lat,
            double lng,
            onTcpDataCallback.sendingDataCallback sendingDataCallback
    ){

        String username = connTcpOptions.getUsername();
        String password = connTcpOptions.getPassword();
        String mountpoint = connTcpOptions.getMountpoint();

        String request = Nmea.getAuthRequest(lat,lng,username,password,mountpoint);

        tpcSocket.send(clientIndex, request, "utf8", new onTcpDataCallback.sendingDataCallback() {
            @Override
            public void success(boolean v) {
                sendingDataCallback.success(true);
            }

            @Override
            public void error(String msg) {
                sendingDataCallback.error(msg);
            }
        });
    }

    public double getCurrentLat(){
        return currentLat;
    }

    public double getCurrentLng(){
        return currentLng;
    }


    public void handleOnDestroy(){
        if(tpcSocket!=null) {
            tpcSocket.handleOnDestroy();
        }
    }
}