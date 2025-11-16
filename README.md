# @capacitor/gnss-rtk-ntrip-x5m-device

GNSS by X5M device with NTRIP

## Install

```bash
npm install @capacitor/gnss-rtk-ntrip-x5m-device
npx cap sync
```

## API

<docgen-index>

* [`iniGnssService(...)`](#inignssservice)
* [`listSourcesDevices()`](#listsourcesdevices)
* [`connect(...)`](#connect)
* [`disconnect()`](#disconnect)
* [`addListener('readData', ...)`](#addlistenerreaddata-)
* [`addListener('readRawData', ...)`](#addlistenerreadrawdata-)
* [`removeAllListeners()`](#removealllisteners)
* [`startNtrip(...)`](#startntrip)
* [`stopNtrip()`](#stopntrip)
* [`checkPermissions()`](#checkpermissions)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### iniGnssService(...)

```typescript
iniGnssService(source?: "bluetooth" | "usb" | null | undefined) => Promise<void>
```

Initialization reading of GNSS device by bluetooth or usb.

| Param        | Type                                      |
| ------------ | ----------------------------------------- |
| **`source`** | <code>'bluetooth' \| 'usb' \| null</code> |

**Since:** 1.0.0

--------------------


### listSourcesDevices()

```typescript
listSourcesDevices() => Promise<{ devices: any[]; }>
```

**Returns:** <code>Promise&lt;{ devices: any[]; }&gt;</code>

--------------------


### connect(...)

```typescript
connect(macAddress: string, secure?: boolean | undefined) => Promise<{ value: boolean; }>
```

| Param            | Type                 |
| ---------------- | -------------------- |
| **`macAddress`** | <code>string</code>  |
| **`secure`**     | <code>boolean</code> |

**Returns:** <code>Promise&lt;{ value: boolean; }&gt;</code>

--------------------


### disconnect()

```typescript
disconnect() => Promise<void>
```

--------------------


### addListener('readData', ...)

```typescript
addListener(eventName: 'readData', listenerFunc: (data: ReadResult) => void) => Promise<PluginListenerHandle>
```

Listens for the custom data event from the native plugin.
The data payload will contain the Base64 encoded string.

| Param              | Type                                                                 | Description                                         |
| ------------------ | -------------------------------------------------------------------- | --------------------------------------------------- |
| **`eventName`**    | <code>'readData'</code>                                              | The name of the event to listen to ('myDataEvent'). |
| **`listenerFunc`** | <code>(data: <a href="#readresult">ReadResult</a>) =&gt; void</code> | The function to be called when the event is fired.  |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('readRawData', ...)

```typescript
addListener(eventName: 'readRawData', listenerFunc: (data: ReadResult) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                 |
| ------------------ | -------------------------------------------------------------------- |
| **`eventName`**    | <code>'readRawData'</code>                                           |
| **`listenerFunc`** | <code>(data: <a href="#readresult">ReadResult</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### removeAllListeners()

```typescript
removeAllListeners() => Promise<void>
```

--------------------


### startNtrip(...)

```typescript
startNtrip(conn: ConnNtripOptions) => Promise<void>
```

| Param      | Type                                                          |
| ---------- | ------------------------------------------------------------- |
| **`conn`** | <code><a href="#connntripoptions">ConnNtripOptions</a></code> |

--------------------


### stopNtrip()

```typescript
stopNtrip() => Promise<void>
```

--------------------


### checkPermissions()

```typescript
checkPermissions() => Promise<{ granted: boolean; }>
```

**Returns:** <code>Promise&lt;{ granted: boolean; }&gt;</code>

--------------------


### Interfaces


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### ReadResult

Result of a read operation

| Prop         | Type                                     | Description                                                                                                                    |
| ------------ | ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------ |
| **`result`** | <code>string</code>                      | Data read from the bluetooth device Can be UTF-8 string, Base64 encoded string, or Hex string depending on the encoding option |
| **`encode`** | <code>'base64' \| 'utf8' \| 'hex'</code> | The encoding of the returned result                                                                                            |


#### ConnNtripOptions

| Prop             | Type                |
| ---------------- | ------------------- |
| **`host`**       | <code>string</code> |
| **`port`**       | <code>string</code> |
| **`username`**   | <code>string</code> |
| **`password`**   | <code>string</code> |
| **`mountpoint`** | <code>string</code> |

</docgen-api>
