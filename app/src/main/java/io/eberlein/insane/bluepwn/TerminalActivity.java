package io.eberlein.insane.bluepwn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static io.eberlein.insane.bluepwn.Static.TYPE_CLASSIC;
import static io.eberlein.insane.bluepwn.Static.TYPE_DUAL;
import static io.eberlein.insane.bluepwn.Static.TYPE_LE;

public class TerminalActivity extends AppCompatActivity {
    @BindView(R.id.log) EditText log;
    @BindView(R.id.cmd) EditText cmd;

    private TerminalSession session;
    private Device device;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter;

    @OnClick(R.id.send)
    public void sendClicked(){
        String data = cmd.getText().toString();
        log.append(data);
        session.cmds.add(data);

    }

    void processData(){

    }

    void doConnectGatt(){
        bluetoothDevice.connectGatt(this, false, new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if (status == BluetoothGatt.GATT_FAILURE || newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING){
                    log.append("[-] disconnected");
                    cmd.setEnabled(false);
                } else if(newState == BluetoothProfile.STATE_CONNECTED){
                    log.append("[+] connected");
                    cmd.setEnabled(true);
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
            }

            @Override
            public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorRead(gatt, descriptor, status);
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                super.onDescriptorWrite(gatt, descriptor, status);
            }

            @Override
            public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                super.onReliableWriteCompleted(gatt, status);
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
            }
        });
    }

    void doRfCommConnect(){
        try{
            bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(((Spinner) findViewById(R.id.serviceRecords)).getSelectedItem().toString()));
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    void doRfCommConnectDialog(){
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_choose_service, null);
        AlertDialog d = new AlertDialog.Builder(this)
                .setView(v)
                .setTitle("select service")
                .setPositiveButton("connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        doRfCommConnect();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

        ButterKnife.bind(v, d);
        d.show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);
        ButterKnife.bind(this);
        cmd.setEnabled(false);
        device = Device.get(getIntent().getStringExtra("address"));
        session = TerminalSession.get(getIntent().getStringExtra("session"));
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) bluetoothAdapter.enable();
        bluetoothDevice = bluetoothAdapter.getRemoteDevice(device.address);
        switch(device.type){
            case TYPE_DUAL:
                new AlertDialog.Builder(this)
                        .setTitle("dual device")
                        .setMessage("connect to..")
                        .setPositiveButton("rfcomm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doRfCommConnectDialog();
                    }
                }).setNeutralButton("gatt", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doConnectGatt();
                        dialog.dismiss();
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            case TYPE_LE:
                doConnectGatt();break;
            case TYPE_CLASSIC:
                Toast.makeText(this, "didnt implement. sry", Toast.LENGTH_SHORT).show();
                break;
            default:

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.save();
    }
}