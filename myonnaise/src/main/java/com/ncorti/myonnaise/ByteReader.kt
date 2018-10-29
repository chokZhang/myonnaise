package com.ncorti.myonnaise

import android.bluetooth.BluetoothGattCharacteristic
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.DecimalFormat

/**
 * This class help you to read the byte line from Myo.
 * Please pay attention that there are no checks for BufferOverFlow or similar problems,
 * you just receive the amount of data you request (1, 2 or 4 bytes).
 *
 * [ByteReader] is useful for handling raw data taken from bluetooth connection with Myo.
 * Use the [ByteReader.getBytes] to get an array of float from a [BluetoothGattCharacteristic].
 */
class ByteReader {

    internal var byteBuffer: ByteBuffer? = null

    var byteData: ByteArray? = null
        set(data) {
            field = data
            this.byteBuffer = ByteBuffer.wrap(field)
            byteBuffer?.order(ByteOrder.nativeOrder())
        }

    val short: Short
        get() = this.byteBuffer!!.short

    val byte: Byte
        get() = this.byteBuffer!!.get()

    val int: Int
        get() = this.byteBuffer!!.int

    fun rewind() = this.byteBuffer?.rewind()

    /**
     * Method for reading n consecutive floats, returned in a new array.
     *
     * @param size Number of bytes to be read (usually 8 or 16)
     * @return A new array with read bytes
     */
    fun getBytes(size: Int): DoubleArray {
        val result = DoubleArray(size)
        for (i in 0 until size)
            result[i] = byteBuffer!!.get().toDouble()
        return result
    }
    fun getShorts(size: Int,item:Float): DoubleArray {
        var result = DoubleArray(size)
        for (i in 0 until size)
            result[i] =byteBuffer!!.short.toDouble()/item
        return result
    }
    fun getmoreShorts(size: Int,item:Float,item2:Float): DoubleArray {
        var result = DoubleArray(size)
        for (i in 0 until size/2)
            result[i] =byteBuffer!!.short.toDouble()/item
        for (i in size/2 until size)
            result[i] =byteBuffer!!.short.toDouble()/item2
        return result
    }
}
