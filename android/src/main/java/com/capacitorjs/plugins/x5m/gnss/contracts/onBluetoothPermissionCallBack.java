package com.capacitorjs.plugins.x5m.gnss.contracts;

import org.json.JSONArray;

public interface onBluetoothPermissionCallBack {

    public interface BluetoothRequestPermissionCallback {
        public void success(boolean v);
        public void info(JSONArray jsonArray);
        public void error(String error);
    }
}
