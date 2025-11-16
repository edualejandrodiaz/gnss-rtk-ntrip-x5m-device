package com.capacitorjs.plugins.x5m.gnss.tcp;

public class ConnTcpOptions {

    String host;
    String port;
    String username;
    String password;
    String mountpoint;

    public ConnTcpOptions(String host, String port, String username, String password, String mountpoint) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.mountpoint = mountpoint;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMountpoint() {
        return mountpoint;
    }



}
