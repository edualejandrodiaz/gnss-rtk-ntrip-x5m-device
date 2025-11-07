package com.capacitorjs.plugins.x5m.gnss.contracts;

import com.capacitorjs.plugins.x5m.gnss.tcp.ResultTpc;

public interface onTcpDataCallback {
    public interface sendingDataCallback {
        public void success(boolean v);
        public void error(String msg);
    }

    public interface readingDataCallback {
        public void success(ResultTpc resultTpc);
        public void error(String msg);
    }
}
