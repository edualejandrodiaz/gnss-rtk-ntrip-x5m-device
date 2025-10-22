export interface GnssServicePlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
