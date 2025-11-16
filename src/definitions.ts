import { PluginListenerHandle } from "@capacitor/core";

export interface GnssServicePlugin {


  /**
   * Initialization reading of GNSS device by bluetooth or usb.
   *
   * @since 1.0.0
   */
  iniGnssService(source?: 'bluetooth' | 'usb' | null): Promise<void>;

  listSourcesDevices(): Promise<{ devices: any[] }>;

  connect(macAddress: string, secure?: boolean): Promise<{ value: boolean }>;

  disconnect(): Promise<void>;


  /**
   * Listens for the custom data event from the native plugin.
   * The data payload will contain the Base64 encoded string.
   *
   * @param eventName The name of the event to listen to ('myDataEvent').
   * @param listenerFunc The function to be called when the event is fired.
   * @returns A promise that resolves with a handle to the listener, which can be used to remove it.
  */

  addListener(
    eventName: 'readData',
    listenerFunc: (data: ReadResult) => void
  ): Promise<PluginListenerHandle>;

  addListener(
    eventName: 'readRawData',
    listenerFunc: (data: ReadResult) => void,
  ): Promise<PluginListenerHandle>;

  removeAllListeners(): Promise<void>;

  startNtrip(conn: ConnNtripOptions): Promise<void>

  stopNtrip(): Promise<void>;

  //sendNtripData(data: string): Promise<void>;

  checkPermissions(): Promise<{ granted: boolean }>;

}

/**
 * Result of a read operation
 */
export interface ReadResult {
  /**
   * Data read from the bluetooth device
   * Can be UTF-8 string, Base64 encoded string, or Hex string depending on the encoding option
   */
  result?: string;
  /**
   * The encoding of the returned result
   */
  encode?: 'base64' | 'utf8' | 'hex';
}

export interface ConnNtripOptions {
  host: string;
  port: string;
  username: string;
  password: string;
  mountpoint: string;
}
