export interface GnssServicePlugin {

  echo(options: { value: string }): Promise<{ value: string }>;

  /**
   * Initialization reading of GNSS device by bluetooth or usb.
   *
   * @since 1.0.0
   */
  iniGnssService(source?: 'bluetooth' | 'usb'): Promise<void>;

  listSourcesDevices(): Promise<{ devices: any }>;

  connect(macAddress: string, secure: boolean): Promise<{ value: boolean }>;

  disconnect(): Promise<void>;

  checkPermissions(): Promise<{ granted: boolean }>;

  sendNtripData(data: string): Promise<void>;

  addListener(eventName: 'readData' | 'readRawData', listenerFunc: (event: any) => void): Promise<any>;

  removeAllListeners(): Promise<void>;

  startNtrip(conn: {
    host: string,
    port: string,
    username: string,
    password: string,
    mountpoint: string
  }): Promise<void>

  stopNtrip(): Promise<void>;

}
