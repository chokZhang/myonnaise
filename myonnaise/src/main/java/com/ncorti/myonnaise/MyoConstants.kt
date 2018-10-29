package com.ncorti.myonnaise

import java.util.*

/** Service ID - MYO CONTROL  */
val SERVICE_CONTROL_ID: UUID = UUID.fromString("d5060001-a904-deb9-4748-2c7f4a124842")
/** Service ID - MYO DATA  */
val SERVICE_EMG_DATA_ID: UUID = UUID.fromString("d5060005-a904-deb9-4748-2c7f4a124842")
var SERVICE_IMU_DATA_ID: UUID = UUID.fromString("d5060002-a904-deb9-4748-2c7f4a124842")

/** Characteristics ID - Myo Information  */
val CHAR_INFO_ID: UUID = UUID.fromString("d5060101-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - Myo Firmware */
val CHAR_FIRMWARE_ID: UUID = UUID.fromString("d5060201-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - Command ID  */
val CHAR_COMMAND_ID: UUID = UUID.fromString("d5060401-a904-deb9-4748-2c7f4a124842")

/** Characteristics ID - EMG Sample 0  */
val CHAR_EMG_0_ID: UUID = UUID.fromString("d5060105-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - EMG Sample 1  */
val CHAR_EMG_1_ID: UUID = UUID.fromString("d5060205-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - EMG Sample 2  */
val CHAR_EMG_2_ID: UUID = UUID.fromString("d5060305-a904-deb9-4748-2c7f4a124842")
/** Characteristics ID - EMG Sample 3  */
val CHAR_EMG_3_ID: UUID = UUID.fromString("d5060405-a904-deb9-4748-2c7f4a124842")

val CHAR_IMU_ID:   UUID = UUID.fromString("d5060402-a904-deb9-4748-2c7f4a124842")

/** Postfix for all the EMG Characteristic.*/
const val CHAR_EMG_POSTFIX = "05-a904-deb9-4748-2c7f4a124842"
const val CHAR_IMU_POSTFIX = "02-a904-deb9-4748-2c7f4a124842"
/**
 * Android Characteristic ID
 * (from Android Samples/BluetoothLeGatt/SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG)
 */
val CHAR_CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

/** Myo Array Size. The device is sending arrays with 16 bytes in it every time. */
const val EMG_ARRAY_SIZE = 16
const val IMU_ARRAY_SIZE = 3
/** Max Myo Frequency (200Hz) */
const val MYO_MAX_FREQUENCY = 200

/** Number of Myo EMG Channels */
const val MYO_CHANNELS = 8
const val IMU_CHANNELS = 3
/** Max Myo Value. This is used mostly for graphical purposes */
const val MYO_MAX_VALUE = 150.0f

/** Min Myo Value. This is used mostly for graphical purposes */
const val MYO_MIN_VALUE = -150.0f
const val MYOHW_ORIENTATION_SCALE   =16384.0f
const val MYOHW_ACCELEROMETER_SCALE  = 2048.0f
const val MYOHW_GYROSCOPE_SCALE      = 16.0f
/** Keep Alive in MS. We will send an [CommandList.unSleep] command every [KEEP_ALIVE_INTERVAL_MS] */
const val KEEP_ALIVE_INTERVAL_MS = 10000

internal const val TAG = "MYO"