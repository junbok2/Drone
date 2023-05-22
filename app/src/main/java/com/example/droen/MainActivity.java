package com.example.droen;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.droen.ble.BluetoothLE;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private BluetoothAdapter bluetoothAdapter;
    private static int REQUESt_ENABLE_BT = 0;
    private static final UUID UUID_Service = UUID.fromString("00000226-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_Characteristic = UUID.fromString("00000227-0000-1000-8000-00805f9b34fb");

    private boolean mScanning = true;
    private Handler handler;
    private static final long SCAN_PERIOD = 10000;
    private ScanCallback leScanCallback;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothLE bluetoothLE;
    private String[] connectionList;

    static int up_down = 0;
    static int startBit1 = 38;
    static int startBit2 = 168;
    static int startBit3 = 20;
    static int startBit4 = 177;
    static int length = 20;
    static int roll1 = 0;
    static int roll2 = 0;
    static int pitch1 = 0;
    static int pitch2 = 0;
    static int yaw1 = 0;
    static int yaw2 = 0;
    static int throttle1 = 0;
    static int throttle2 = 0;
    static int option1 = 15;
    static int option2 = 0;
    static int posvel1 = 100;
    static int posvel2 = 0;
    static int yawvel1 = 100;
    static int yawvel2 = 0;
    static int checkSum = 0;
    static int result = 0;

    Button searchbutton;
    Button connectbutton;
    Button upButton;
    Button downButton;
    Button stopButton;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchbutton = findViewById(R.id.searchbutton);
        connectbutton = findViewById(R.id.connectbutton);
        upButton = findViewById(R.id.upbutton);
        downButton = findViewById(R.id.downbutton);
        stopButton = findViewById(R.id.stopbutton);

        //블루투스 어댑터 가져오기
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        //블루투스 활성화
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUESt_ENABLE_BT);
        }

        Timer timer = new Timer();
        TimerTask TT = new TimerTask() {
            @Override
            public void run() {
                checkThrottle();
                checkCRC();
            }
        };
        timer.schedule(TT, 0, 1000);

        List list = {startBit1, startBit2, startBit3, startBit4, length, checkSum, roll1, roll2, pitch1, pitch2,
                        yaw1, yaw2, throttle1, throttle2, option1, option2, posvel1, posvel2, yawvel1, yawvel2};
        bluetoothLE.WriteBytesWithResponse("00000226-0000-1000-8000-00805f9b34fb", "00000227-0000-1000-8000-00805f9b34fb", false, list);

        //기기검색
        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothLE.StartScanning();
            }
        });
        bluetoothLE.DeviceFound();{
            connectionList = bluetoothLE.DeviceList();
        };
        //장치연결
        connectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothLE.ConnectWithAddress();
            }
        });
        //상승
        upButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //눌렀을 때 동작
                    up_down = 1;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) { //뗐을 때 동작
                    up_down = 0;
                }
                return false;
            }
        });
        //하강
        downButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //눌렀을 때 동작
                    up_down = 2;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) { //뗐을 때 동작
                    up_down = 0;
                }
                return false;
            }
        });
        //정지
        stopButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { //눌렀을 때 동작
                    throttle1 = 0;
                    option1 = 14;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) { //뗐을 때 동작
                    option1 = 15;
                }
                return false;
            }
        });



    }
    private void checkThrottle() {
        if(up_down == 1){
            if(throttle1 < 141){
                throttle1 = throttle1 + 20;
            }
        }else if(up_down == 2){
            if(throttle1 > 9){
                throttle1 = throttle1 - 10;
            }
        }
    }
    private void checkCRC() {
        result = roll1 + roll2 + pitch1 + pitch2 + yaw1 + yaw2;
        result = result + throttle1 + throttle2 + option1 + option2;
        result = result + posvel1 + posvel2 + yawvel1 + yawvel2;
        checkSum = result % 256;
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    private String showDialog() {
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("연결장치");
        builder.setItems(connectionList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                return connectionList[i];
            }
        });
    }

}