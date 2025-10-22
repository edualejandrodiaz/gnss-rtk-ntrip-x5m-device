package com.capacitorjs.plugins.x5m.gnss;

import com.getcapacitor.Logger;

public class GnssService {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
