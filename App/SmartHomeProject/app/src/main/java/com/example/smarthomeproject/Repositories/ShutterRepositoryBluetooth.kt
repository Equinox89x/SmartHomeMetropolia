package com.example.smarthomeproject.Repositories

class ShutterRepositoryBluetooth {

    fun shutter1Open(){
        RepositoryStates.isShutter1Open = true
        PairedBluetoothDevice.write("{\"shutter1\": \"open\"}",true)
    }

    fun shutter1Close(){
        RepositoryStates.isShutter1Open = false
        PairedBluetoothDevice.write("{\"shutter1\": \"close\"}", false)
    }

    fun shutter2Open(){
        //RepositoryStates.shutter2Open = true
        PairedBluetoothDevice.write("shutter2", true)
    }

    fun shutter2Close(){
        //RepositoryStates.shutter2Open = false
        PairedBluetoothDevice.write("shutter2", false)
    }
}


