package com.example.smarthomeproject.Repositories

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * https://stackoverflow.com/questions/22899475/android-sample-bluetooth-code-to-send-a-simple-string-via-bluetooth
 */
object PairedBluetoothDevice {

    private var blueAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    private lateinit var outStream: OutputStream
    private lateinit var inStream: InputStream
    private lateinit var socket: BluetoothSocket

    init {
        if (blueAdapter.isEnabled) {
            val bondedDevices = blueAdapter.bondedDevices

            if (bondedDevices.size == 1) {
                val devices = bondedDevices.toTypedArray() as Array<*>
                val device = devices[0] as BluetoothDevice
                val uuids = device.uuids
                socket = device.createRfcommSocketToServiceRecord(uuids[0].uuid)
                try {
                    socket.connect()
                    outStream = socket.outputStream
                    inStream = socket.inputStream
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
            else if(bondedDevices.size > 1)
                Log.e("error", "There is more than one Bluetooth device connected. We can't handle that yet.")
            else
                Log.e("error", "No appropriate paired devices.")
        } else {
            Log.e("error", "Bluetooth is disabled.")
        }
    }

    private fun read() {
        val BUFFER_SIZE = 1024
        val buffer = ByteArray(BUFFER_SIZE)
        var bytes = 0
        val b = BUFFER_SIZE

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun write(s: String) {
        outStream.write(s.toByteArray())
    }

    fun write(key: String, value: Boolean): Boolean{
        return if(isConnected()) {
            write("{'$key':'$value'}")
            true
        } else {
            Log.e("error", "No Bluetooth connection.")
            false
        }
    }

    fun isConnected(): Boolean{
        return socket.isConnected
    }
}