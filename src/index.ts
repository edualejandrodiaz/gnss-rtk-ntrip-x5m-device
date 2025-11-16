import { registerPlugin } from '@capacitor/core';

import type { GnssServicePlugin } from './definitions';

const GnssServicePlugin = registerPlugin<GnssServicePlugin>('GnssService', {
  web: () => import('./web').then((m) => new m.GnssServiceWeb()),
});

export * from './definitions';
export { GnssServicePlugin };
