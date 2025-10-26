package com.capacitorjs.plugins.x5m.gnss.contracts;

public interface OnBluetoothDataListener {
    public void onBluetoothReadingBytes(byte[] bytes);
    public void onBluetoothReadingData(String data);
}
