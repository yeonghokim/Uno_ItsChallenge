package com.example.kim_yeongho.uno_application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {



    BluetoothSocket mSocket;
    BluetoothDevice mRemoteDevice;

    OutputStream mOutputStream;
    InputStream mInputStream;

    public static final int REQUEST_ENABLE_BT = 1;
    public static boolean IsConnectedBluetooth = false;

    Set<BluetoothDevice> mDevices;

    Thread mWorkerThread;

    String mDelimiter = "U";

    int readBufferPosition;
    byte[] readBuffer;

    ImageView[] mCloud = new ImageView[3];

    private TimerTask mCloudTask;

    private Timer mTimer;

    BluetoothAdapter mBluetoothAdapter;

    TextView DtextView;
    TextView VtextView;
    TextView OntextView;
    LinearLayout ONOFFLayout;

    TextView BackgroundTextView;
    RelativeLayout Background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bicycledata_layout);

        BluetoothConnect();

        MakeCloud();


        Button button = (Button)findViewById(R.id.onoffButton);
        button.setBackgroundColor(Color.rgb(38,232,167));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button)view;
                if(button.getText().equals("OFF")) {
                    button.setText("ON");
                    button.setBackgroundColor(Color.rgb(38,232,167));
                    sendData("1");
                    BackgroundTextView.setText("주행중");
                    Background.setBackgroundColor(getResources().getColor(R.color.Backgroundon));
                }
                else {
                    button.setText("OFF");
                    button.setBackgroundColor(Color.rgb(170,170,170));
                    sendData("0");
                    BackgroundTextView.setText("작동 안함");
                    Background.setBackgroundColor(getResources().getColor(R.color.Backgroundoff));
                }

            }
        });


        VtextView= (TextView)findViewById(R.id.block1_number);
        DtextView= (TextView)findViewById(R.id.block2_number);
        OntextView= (TextView)findViewById(R.id.block3_OnOFF);


        ONOFFLayout= (LinearLayout)findViewById(R.id.block3_block);

        BackgroundTextView = (TextView)findViewById(R.id.BackgroundTextID);
        Background = (RelativeLayout)findViewById(R.id.BackgroundID);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT) {
            switch (resultCode) {
                case RESULT_OK:

                mBluetoothAdapter.enable();

                    break;
                case RESULT_CANCELED:

                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mWorkerThread.interrupt();
            mInputStream.close();
            mOutputStream.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    void MakeCloud(){

        mCloud[0] = (ImageView)findViewById(R.id.cloud1);
        mCloud[1] = (ImageView)findViewById(R.id.cloud2);
        mCloud[2] = (ImageView)findViewById(R.id.cloud3);

        mCloudTask = new TimerTask() {
            @Override
            public void run(){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for(int i=0;i<3;i++) {
                            mCloud[i].setPadding(mCloud[i].getPaddingLeft()>1300?0:mCloud[i].getPaddingLeft() + 10*(3-i), mCloud[i].getPaddingTop(), mCloud[i].getPaddingRight(), mCloud[i].getPaddingBottom());

                        }
                    }
                });

            }
        };

        mTimer = new Timer();

        mTimer.schedule(mCloudTask, 0,100);
    }

    private void BluetoothConnect(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){

            //블루투스 지원을 하지 않음
            finish();

        }else if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void SelectDevice(View v){

        if(IsConnectedBluetooth){

            return;
        }

        mDevices = mBluetoothAdapter.getBondedDevices();

        if(mDevices.size()==0)
            return ;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 설정");
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            listItems.add(device.getName());
        }

        listItems.add("취소");

        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == mDevices.size()) {

                } else {
                    // 연결할 장치를 선택한 경우
                    // 선택한 장치와 연결을 시도함

                    connectToSelectedDevices(items[item].toString());
                    beginListenForData();

                }
            }
        });
        builder.show();


    }

    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for(BluetoothDevice device : mDevices) {
            if(name.equals(device.getName())) {
                selectedDevice = device;
                break;
            }
        }
        return selectedDevice;
    }

    void connectToSelectedDevices(String selectedDeviceName){
        mRemoteDevice = getDeviceFromBondedList(selectedDeviceName);

        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            mSocket = mRemoteDevice.createRfcommSocketToServiceRecord(uuid);

            mSocket.connect();

            mOutputStream  =mSocket.getOutputStream();
            mInputStream = mSocket.getInputStream();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            try {
                System.out.println("trying fallback...");

                mSocket =(BluetoothSocket) mRemoteDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(mRemoteDevice,1);
                mSocket.connect();
                mOutputStream  =mSocket.getOutputStream();
                mInputStream = mSocket.getInputStream();

                System.out.println("Connected");
            }
            catch (Exception e2) {
                System.out.println( "Couldn't establish Bluetooth connection!");
            }
        }
    }

    void sendData(String msg) {
        msg += mDelimiter;
        try {

            mOutputStream.write(msg.getBytes());    // 문자열 전송
            System.out.println(msg);

        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "데이터 전송중 오류가 발생", Toast.LENGTH_LONG).show();
        }
    }

    void beginListenForData(){

        final Handler handler = new Handler();

        readBuffer = new byte[1024] ;  //  수신 버퍼
        readBufferPosition = 0;        //   버퍼 내 수신 문자 저장 위치

        // 문자열 수신 쓰레드
        mWorkerThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted()){
                    try {
                        int bytesAvailable = mInputStream.available();    // 수신 데이터 확인
                        if(bytesAvailable > 0) {                     // 데이터가 수신된 경우
                            byte[] packetBytes = new byte[bytesAvailable];
                            mInputStream.read(packetBytes);
                            for(int i=0 ; i<bytesAvailable; i++) {
                                byte b = packetBytes[i];
                                if(b == 'U' ) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            try {
//data가 아두이노에서 준 문자열
                                                System.out.println(data);
                                                String[] GetValue = data.split("a");

                                                VtextView.setText(GetValue[0]);

                                                DtextView.setText(GetValue[1]);

                                                if (GetValue[2].equals("t")) {
                                                    OntextView.setText("ON");
                                                    ONOFFLayout.setBackgroundColor(getResources().getColor(R.color.ONON));
                                                } else {
                                                    OntextView.setText("OFF");
                                                    ONOFFLayout.setBackgroundColor(getResources().getColor(R.color.OFFOFF));
                                                }


                                            }catch(Exception e){
                                                e.printStackTrace();
                                            }





                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        // 데이터 수신 중 오류 발생.
                        finish();
                    }
                }
            }
        });

        mWorkerThread.start();
    }



}
