package com.capacitorjs.plugins.x5m.gnss;



import android.annotation.SuppressLint;


import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Nmea {

    

    public static String toNmeaGga(double lat, double lon, Date timeUtc) {
        if (timeUtc == null) {
            timeUtc = new Date();
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(timeUtc);

        @SuppressLint("DefaultLocale") String timeStr = String.format("%02d%02d%02d",
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND));

        // Format latitude
        String latHemi = lat < 0 ? "S" : "N";
        double absLat = Math.abs(lat);
        int latDeg = (int) Math.floor(absLat);
        double latMin = (absLat - latDeg) * 60;
        String latField = String.format("%02d%06.3f", latDeg, latMin);

        // Format longitude
        String lonHemi = lon < 0 ? "W" : "E";
        double absLon = Math.abs(lon);
        int lonDeg = (int) Math.floor(absLon);
        double lonMin = (absLon - lonDeg) * 60;
        String lonField = String.format("%03d%06.3f", lonDeg, lonMin);

        // Fixed fields: quality=1, sats=12, hdop=1.0, alt=100.0
        String core = String.format("GNGGA,%s,%s,%s,%s,%s,1,12,1.0,100.0,M,0.0,M,,",
                timeStr, latField, latHemi, lonField, lonHemi);

        // Calculate checksum
        int checksum = 0;
        for (char c : core.toCharArray()) {
            checksum ^= c;
        }

        return String.format("$%s*%02X", core, checksum);
    }

    public static String getAuthRequest(double lat, double lng, String username, String password, String mountpoint) {


        // Create Base64 encoded auth string
        String auth = Base64.encodeToString(
                (username + ":" + password).getBytes(StandardCharsets.UTF_8),
                Base64.NO_WRAP // Opcional: evita saltos de línea
        );

        // Get NMEA GGA string
        String gga = toNmeaGga(lat, lng, null);

        // Build and return the NTRIP request
        return String.format(
                "GET /%s HTTP/1.0\r\n" +
                        "User-Agent: NTRIP JavaApp/1.0\r\n" +
                        "Accept: */*\r\n" +
                        "Authorization: Basic %s\r\n" +
                        "Ntrip-GGA: %s\r\n\r\n",
                mountpoint, auth, gga
        );
    }

}
