package com.example.droen;

import android.app.Activity;

import com.example.droen.annotations.SimpleEvent;
import com.example.droen.annotations.SimpleFunction;
import com.example.droen.ble.BluetoothLEint;
import com.example.droen.runtime.AndroidNonvisibleComponent;
import com.example.droen.runtime.Component;
import com.example.droen.runtime.Deleteable;

import java.util.HashSet;
import java.util.Set;

public class BluetoothLE extends AndroidNonvisibleComponent implements Component, Deleteable {
    public static final int ERROR_DEVICE_INDEX_OOB = 9101;
    public static final int ERROR_SERVICE_INDEX_OOB = 9102;
    public static final int ERROR_SERVICE_INVALID_UUID = 9103;
    public static final int ERROR_CHARACTERISTIC_INDEX_OOB = 9104;

    /**
     * Basic Variables
     */
    private static final String LOG_TAG = "BluetoothLE";
    private final Activity activity;
    private BluetoothLEint inner;
    Set<com.example.droen.ble.BluetoothLE.BluetoothConnectionListener> connectionListeners = new HashSet<com.example.droen.ble.BluetoothLE.BluetoothConnectionListener>();

    @Override
    public void onDelete() {
        if (inner != null) {
            inner.Disconnect();
            inner = null;
        }
    }

    @SimpleFunction
    public void WriteBytesWithResponse(String serviceUuid, String characteristicUuid,
                                       boolean signed, Object values) {
        if (inner != null) {
            inner.WriteByteValuesWithResponse(serviceUuid, characteristicUuid, signed,
                    toList(Integer.class, values, 1));
        }
    }
    @SimpleFunction
    public void StartScanning() {
        if (inner != null) {
            if (SDK26Helper.shouldAskForPermission(form)) {
                SDK26Helper.askForPermission(this, new Runnable() {
                    public void run() {
                        inner.StartScanning();
                    }
                });
            } else {
                inner.StartScanning();
            }
        }
    }
    @SimpleEvent
    public void DeviceFound() {
    }
    @SimpleFunction
    public void ConnectWithAddress(String address) {
        if (inner != null) {
            inner.ConnectWithAddress(address);
        }
    }
    @SimpleFunction
    public void Disconnect() {
        if(inner != null) {
            inner.Disconnect();
        }
    }
    @SimpleFunction
    public void StopScanning() {
        if (inner != null) {
            inner.StopScanning();
        }
    }
    @SimpleEvent
    public void Connected() {
    }
    @SimpleEvent
    public void Disconnected() {
    }
}
