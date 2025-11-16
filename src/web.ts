import { PluginListenerHandle, WebPlugin } from '@capacitor/core';

import type { ConnNtripOptions, GnssServicePlugin, ReadResult } from './definitions';

export class GnssServiceWeb extends WebPlugin implements GnssServicePlugin {


  async iniGnssService(): Promise<void> {
    console.warn('GNSS NOT SUPPORTED ON WEB');
  }

  async listSourcesDevices(): Promise<{ devices: any[] }> {
    console.warn('NOT SUPPORTED ON WEB');
    return { devices: [] };
  }

  async connect(macAddress: string, secure: boolean): Promise<{ value: boolean }> {

    console.warn('macAddress ' + macAddress + ' ');
    console.warn('secure ' + secure + ' ');
    console.warn('NOT SUPPORTED ON WEB');

    return { value: false };
  }


  async disconnect(): Promise<void> {
    console.warn('NOT SUPPORTED ON WEB');
  }

  async removeAllListeners(): Promise<void> {
    console.warn('NOT SUPPORTED ON WEB');
  }

  async startNtrip(conn: ConnNtripOptions): Promise<void> {
    console.warn('host:', conn.host);
    console.warn('port:', conn.port);
    console.warn('username:', conn.username);
    console.warn('password:', conn.password);
    console.warn('mountpoint:', conn.mountpoint);
    console.warn('NOT SUPPORTED ON WEB');
  }

  async stopNtrip(): Promise<void> {
    console.warn('UHF NOT SUPPORTED ON WEB');
  }

  async addListener(eventName: 'readData' | 'readRawData', listenerFunc: (data: ReadResult) => void): Promise<PluginListenerHandle> {
    console.log('addListener', eventName, listenerFunc);
    console.warn('NOT SUPPORTED ON WEB');
    // Return a noop PluginListenerHandle compatible with @capacitor/core
    return {
      remove: async () => {
        // no-op on web
      },
    };
  }

  async checkPermissions(): Promise<{ granted: boolean }> {
    console.warn('NOT SUPPORTED ON WEB');
    return { granted: false };
  }
}
