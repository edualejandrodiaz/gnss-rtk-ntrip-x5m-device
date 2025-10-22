import { WebPlugin } from '@capacitor/core';

import type { GnssServicePlugin } from './definitions';

export class GnssServiceWeb extends WebPlugin implements GnssServicePlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
