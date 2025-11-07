package com.capacitorjs.plugins.x5m.gnss.tcp;

import android.Manifest;
import android.util.Log;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import android.util.Base64;
import com.capacitorjs.plugins.x5m.gnss.contracts.onTcpDataCallback;

/**
 * TcpSocketPlugin - Capacitor plugin that provides TCP Socket communication functionality
 * Supports connection, data sending, data receiving and disconnection operations
 */

public class TcpSocket {
    private static final String TAG = "TcpSocketPlugin";
    private List<Socket> clients = new ArrayList<>();
    
    // Supported data encoding types
    private enum DataEncoding {
        UTF8("utf8"),
        BASE64("base64"),
        HEX("hex");
        
        private final String value;
        
        DataEncoding(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static DataEncoding fromString(String text) {
            for (DataEncoding encoding : DataEncoding.values()) {
                if (encoding.value.equalsIgnoreCase(text)) {
                    return encoding;
                }
            }
            return UTF8; // Default to UTF8 if not found
        }
    }

    /**
     * Connect to TCP server
     * Parameters:
     * - ipAddress: Server IP address (required)
     * - port: Server port, default 9100
     * Returns:
     * - client: Client index, used for subsequent operations
     */

    public Integer connect(String ipAddress, Integer port) throws IOException{


        try {
            Socket socket = new Socket(ipAddress, port);
            clients.add(socket);
            
            JSObject ret = new JSObject();
            return clients.size() - 1;

        } catch (IOException e) {

            Log.e(TAG, "Connection failed: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Send data to server with specified encoding
     * Parameters:
     * - client: Client index
     * - data: Data to send
     * - encoding: Data encoding (utf8, base64, hex), default is utf8
     */

    public void send(
            final Integer clientIndex,
            final String data,
            final String encodingString,
            onTcpDataCallback.sendingDataCallback sendingDataCallback) throws RuntimeException {


        if (clientIndex == -1) {
            throw new RuntimeException("Client not specified or invalid index");

        }
        
        if (clientIndex >= clients.size() || clientIndex < 0) {
            throw new RuntimeException("Client index out of range");

        }
        

        if (data == null || data.isEmpty()) {
            throw new RuntimeException("No data provided");

        }
        

        final DataEncoding encoding = DataEncoding.fromString(encodingString);

        Socket socket = clients.get(clientIndex);
        if (!socket.isConnected()) {
            closeSocketSafely(socket);
            throw new RuntimeException("Socket not connected");

        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    final Socket socket = clients.get(clientIndex);
                    DataOutputStream bufferOut = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    
                    // Convert and send data according to the specified encoding
                    byte[] bytes;
                    switch (encoding) {
                        case BASE64:
                            bytes = Base64.decode(data, Base64.DEFAULT);
                            break;
                        case HEX:
                            bytes = hexStringToBytes(data);
                            break;
                        case UTF8:
                        default:
                            bytes = data.getBytes(StandardCharsets.UTF_8);
                            break;
                    }
                    
                    if (bytes != null) {
                        bufferOut.write(bytes);
                        bufferOut.flush();
                        sendingDataCallback.success(true);
                    } else {
                        sendingDataCallback.error("Failed to decode data with encoding: " + encodingString);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Send failed: " + e.getMessage());
                    sendingDataCallback.error("Send failed: " + e.getMessage());
                }
            }
        };
        
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Read data from server
     * Parameters:
     * - client: Client index
     * - expectLen: Expected number of bytes to read, default 1024
     * - timeout: Timeout in seconds (Note: Android implementation doesn't support timeout yet)
     * - encoding: Preferred encoding for returned data (utf8, base64, hex), default is utf8
     * Returns:
     * - result: Data read
     * - encoding: The encoding used for the result
     */

    public void read(
            final Integer clientIndex,
            final Integer length,
            final String encodingString,
            onTcpDataCallback.readingDataCallback readingDataCallback) {


        if (clientIndex == -1) {

            throw new RuntimeException("Client not specified or invalid index");

        }
        
        if (clientIndex >= clients.size() || clientIndex < 0) {

            throw new RuntimeException("Client index out of range");

        }

        Socket socket = clients.get(clientIndex);

        if (!socket.isConnected()) {
            closeSocketSafely(socket);
            throw new RuntimeException("Socket not connected");

        }
        

        final DataEncoding encoding = DataEncoding.fromString(encodingString);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    final Socket socket = clients.get(clientIndex);
                    DataInputStream bufferIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    byte[] bytes = new byte[length];
                    int read = bufferIn.read(bytes, 0, length);

                    JSObject ret = new JSObject();
                    String result = "";
                    String actualEncoding = encoding.getValue();
                    
                    if (read > 0) {
                        byte[] actualData = new byte[read];
                        System.arraycopy(bytes, 0, actualData, 0, read);
                        
                        switch (encoding) {
                            case UTF8:
                                try {
                                    result = new String(actualData, StandardCharsets.UTF_8);
                                } catch (Exception e) {
                                    // If UTF-8 conversion fails, fall back to base64
                                    result = Base64.encodeToString(actualData, Base64.NO_WRAP);
                                    actualEncoding = DataEncoding.BASE64.getValue();
                                }
                                break;
                            case BASE64:
                                result = Base64.encodeToString(actualData, Base64.NO_WRAP);
                                break;
                            case HEX:
                                result = bytesToHexString(actualData);
                                break;
                        }
                    }
                    


                    ResultTpc resultTpc = new ResultTpc(result, actualEncoding);

                    // Devuelve la instancia creada.
                    readingDataCallback.success(resultTpc);

                } catch (IOException e) {
                    Log.e(TAG, "Read failed: " + e.getMessage());
                    readingDataCallback.error("Read failed: " + e.getMessage());

                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Disconnect from server
     * Parameters:
     * - client: Client index
     * Returns:
     * - client: Disconnected client index
     */

    public Integer disconnect(final Integer clientIndex) throws Exception {

        if (clientIndex == -1) {
            throw new Exception("Client not specified or invalid index");

        }
        
        if (clients.isEmpty()) {
            throw new Exception("No active connections");

        }
        
        if (clientIndex >= clients.size() || clientIndex < 0) {
            throw new Exception("Client index out of range");
        }
        
        final Socket socket = clients.get(clientIndex);
        try {
            if (!socket.isConnected()) {
                socket.close();
                throw new Exception("Socket not connected");

            }
            socket.close();
        } catch (IOException e) {
            Log.e(TAG, "Disconnect failed: " + e.getMessage());
            throw new Exception("Disconnect failed: " + e.getMessage());

        }


        return clientIndex;
    }
    
    /**
     * Helper method to safely close a socket and handle any exceptions
     */
    private void closeSocketSafely(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket: " + e.getMessage());
            }
        }
    }
    
    /**
     * Clean up resources when plugin is destroyed
     */

    public void handleOnDestroy() {
        for (Socket socket : clients) {
            closeSocketSafely(socket);
        }
        clients.clear();

    }
    
    /**
     * Convert hex string to bytes
     * @param hex Hex string to convert
     * @return Byte array or null if invalid hex string
     */
    private byte[] hexStringToBytes(String hex) {
        // Remove 0x prefix, spaces, newlines, etc.
        String cleanHex = hex.replace("0x", "")
                             .replace(" ", "")
                             .replace("\n", "");
                             
        if (cleanHex.length() % 2 != 0) {
            // Invalid hex string
            return null;
        }
        
        byte[] data = new byte[cleanHex.length() / 2];
        
        try {
            for (int i = 0; i < cleanHex.length(); i += 2) {
                data[i / 2] = (byte) Integer.parseInt(cleanHex.substring(i, i + 2), 16);
            }
            return data;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid hex string: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Convert bytes to hex string
     * @param bytes Byte array to convert
     * @return Hex string representation
     */
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
