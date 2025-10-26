package com.capacitorjs.plugins.x5m.gnss.contracts;

public interface OnBluetoothDataCallback {
    public interface BluetoothReadingBytesCallback {
        public void success(byte[] bytes);
    }

    public interface BluetoothReadingDataCallback {
        public void success(String data);
    }
}
