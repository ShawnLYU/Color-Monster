package com.cityuytic.colormonster;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class BluetoothActivity extends AppCompatActivity {

    private TextView view_time_remaining;
    private ImageView view_btn_start;
    private TextView view_my_score;
    private TextView view_rival_score;
    private TextView view_task_type;



    private TextView view_task_content;

    /////////////////////////////////////////////////bluetooth////////////////////////////////////////
    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private ColorToothService colorToothService = null;


    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_VISIBLE_BT = 2;

    private static final int IDENTITY_SERVER = 0;
    private static final int IDENTITY_CLIENT = 1;

    private static final int SEND_SRC = 0;
    private static final int SEND_DEST = 1;

    private int identity=-1;
    private int send_identity=0;
    private boolean isDialogDismissed = false;

    private boolean isConnected=false;

    private BluetoothAdapter myBluetoothAdapter;
    private ArrayAdapter<String> adapter;
    private String selectedDevice;
    private HashMap<String,BluetoothDevice> devices = new HashMap<>();
    private BluetoothDevice device = null;
    private AlertDialog  alertDialog;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case ColorToothService.STATE_CONNECTED:
//                            mTitle.setText(R.string.title_connected_to);
//                            mTitle.append(mConnectedDeviceName);
//                            mConversationArrayAdapter.clear();


                            break;
                        case ColorToothService.STATE_CONNECTING:
//                            mTitle.setText(R.string.title_connecting);
                            break;
                        case ColorToothService.STATE_LISTEN:
                        case ColorToothService.STATE_NONE:
//                            mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
//                    byte[] writeBuf = (byte[]) msg.obj;
//                    // construct a string from the buffer
//                    String writeMessage = new String(writeBuf);
//                    textMsg.setText(writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    switch (readMessage){
                        case "Start":
                            mainActivityStart();
                            view_btn_start.setTag("pause");
                            view_btn_start.setImageResource(R.drawable.btn_pause);
                            break;
                        case "Pause":
                            pause();
                            view_btn_start.setTag("resume");
                            view_btn_start.setImageResource(R.drawable.btn_start);
                            break;
                        case "Resume":
                            isPaused = false;
                            resume();
                            view_btn_start.setTag("pause");
                            view_btn_start.setImageResource(R.drawable.btn_pause);
                            break;
                        case "Restart":
                            mainActivityStart();
                            view_btn_start.setTag("pause");
                            view_btn_start.setImageResource(R.drawable.btn_pause);
                            break;
                        case "stop":
                            if(round!=null)
                                round.stop();
                        case "OnRestart":
                            if(round!=null)
                                round.resume();

                            break;
                        case "Home":
                            finish();
                            break;

                    }
                    switch (readMessage.split(":")[0]){

                        case "timeRemaining":
                            view_time_remaining.setText("" + Integer.parseInt(readMessage.split(":")[1] ));
                            //Put count down timer remaining time in a variable
                            timeRemaining = Integer.parseInt(readMessage.split(":")[1] );
                            break;
                    }
                    if(isInteger(readMessage))
                        view_rival_score.setText(readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    isConnected = true;
                    if(!isDialogDismissed)
                        alertDialog.dismiss();
                    view_connect.setClickable(false);
                    String mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    if(mConnectedDeviceName.split("as")[1].equals(" Server"))
                    {
                        identity = IDENTITY_SERVER;
                        view_btn_start.setClickable(true);
                        view_btn_start.setVisibility(View.VISIBLE);
                    }else{
                        identity = IDENTITY_CLIENT;
                    }
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                devices.put(device.getName(), device);
                adapter.add(device.getName() + "address" + "\n" + device.getAddress());
                adapter.notifyDataSetChanged();
            }
        }
    };



    ////////////////////////////////////////////////////////////////////////////////////




    private ImageView view_connect;
    private CountDownTimer timer;


    private ArrayList<ImageButton> buttons = new ArrayList<>();
    private HashMap<String,Integer> images = new HashMap<>();
    BluetoothModeRound round;

    public static String[] colors = {"紅","黃","藍","綠","黑"};




    //Declare a variable to hold count down timer's paused status
    private boolean isPaused = false;


    //Declare a variable to hold CountDownTimer remaining time
    private long timeRemaining = 0;
    private View.OnTouchListener mBottonTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction()==MotionEvent.ACTION_DOWN) {
                v.animate().translationXBy(3).translationYBy(3).setInterpolator(null).setDuration(50);
            } else if (event.getAction()==MotionEvent.ACTION_UP) {
                v.animate().translationXBy(-3).translationYBy(-3).setInterpolator(null).setDuration(50);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        //binding the views with java
        processViews();
        //inject the ids of images into ArrayList
        register();

        view_btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view_btn_start.getTag().equals("start")) {
                    sendMessage("Start");
                    mainActivityStart();
                    view_btn_start.setTag("pause");
                    view_btn_start.setImageResource(R.drawable.btn_pause);
                } else if (view_btn_start.getTag().equals("pause")) {
                    createAndShowDialog();
                    isPaused = true;
                    sendMessage("Pause");
                    pause();
                    view_btn_start.setTag("resume");
                    view_btn_start.setImageResource(R.drawable.btn_start);
                } else if (view_btn_start.getTag().equals("resume")) {
                    isPaused = false;
                    sendMessage("Resume");
                    resume();
                    view_btn_start.setTag("pause");
                    view_btn_start.setImageResource(R.drawable.btn_pause);
                } else if (view_btn_start.getTag().equals("restart")) {
                    sendMessage("Restart");
                    mainActivityStart();
                    view_btn_start.setTag("pause");
                    view_btn_start.setImageResource(R.drawable.btn_pause);
                }


            }
        });
        connectColorTooth();






      /*view_monster.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
          @Override
          public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
              Log.e("MainActivity", "monster top=" + top);

              float curPositionX = view_xiaozhi.getY();
              Log.e("MainActivity", "xiaozhi Y=" + curPositionX);
              float curPositionM = view_monster.getY();
              Log.e("MainActivity", "monster Y=" + curPositionM);

              if(curPositionM - curPositionX < 20){
                  round.stop();
                  view_time_remaining.setText(0 + "");
                  view_btn_start.setTag("restart");
                  view_btn_start.setImageResource(R.drawable.unit_start);
                  monster_animator.cancel();
                  Log.e("MainActivity", "<20 should stop");
              }

          }
      });*/

    }


    public void processViews(){
        view_connect = (ImageView)findViewById(R.id.bluetooth_connect);


        view_time_remaining = (TextView)findViewById(R.id.bluetooth_mode_time_remaining);


        view_btn_start = (ImageView)findViewById(R.id.bluetooth_mode_btn_start);
        view_btn_start.setImageResource(R.drawable.btn_start);
        view_btn_start.setTag("start");
        view_btn_start.setClickable(false);
        view_btn_start.setVisibility(View.INVISIBLE);
        view_rival_score = (TextView)findViewById(R.id.bluetooth_view_rivalscore);
        view_my_score = (TextView)findViewById(R.id.bluetooth_view_myscore);
        view_task_type = (TextView)findViewById(R.id.bluetooth_mode_task_type);
        view_task_content = (TextView)findViewById(R.id.single_mode_task_content);


        view_my_score.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sendMessage(view_my_score.getText().toString());
            }
        });

        view_time_remaining.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(identity == IDENTITY_CLIENT && Integer.parseInt(view_time_remaining.getText().toString())==1){
                    view_time_remaining.setText(0+"");
                    // Generate custom pause dialog
                    android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(BluetoothActivity.this,R.style.CustomDialog); // Create new alert dialog
                    View dialogView = getLayoutInflater().inflate(R.layout.single_dialog_scoreboard, null); // Inflate the custom layout in to a View object
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    final android.support.v7.app.AlertDialog scoreboardDialog = dialog.create(); // Create the dialog in a final context
                    // Find the Button object within the inflated view
                    ImageButton btnDialogScoreboardReplay=(ImageButton)dialogView.findViewById(R.id.btnSingleDialogScoreboardReplay);
                    ImageButton btnDialogScoreboardHome=(ImageButton)dialogView.findViewById(R.id.btnSingleDialogScoreboardHome);
                    TextView txtDialogScoreboardScore=(TextView)dialogView.findViewById(R.id.txtSingleDialogScoreboardScore);
                    ImageView dialogScoreboardBg=(ImageView)dialogView.findViewById(R.id.singleDialogScoreboardBg);
                    txtDialogScoreboardScore.setText(view_my_score.getText());
                    btnDialogScoreboardReplay.setVisibility(View.GONE);
                    btnDialogScoreboardHome.setVisibility(View.GONE);
                    scoreboardDialog.show(); // Show the dialog
                    if (Integer.parseInt(view_rival_score.getText().toString()) - (Integer.parseInt(view_my_score.getText().toString()))>0) {
                        dialogScoreboardBg.setImageResource(R.drawable.dialog_scoreboard_lose_bg);
                    }
                    else
                        dialogScoreboardBg.setImageResource(R.drawable.dialog_scoreboard_win_bg);
                }

                else if(Integer.parseInt(view_time_remaining.getText().toString())==0){
                    // Generate custom pause dialog
                    android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(BluetoothActivity.this,R.style.CustomDialog); // Create new alert dialog
                    View dialogView = getLayoutInflater().inflate(R.layout.single_dialog_scoreboard, null); // Inflate the custom layout in to a View object
                    dialog.setView(dialogView);
                    dialog.setCancelable(false);
                    final android.support.v7.app.AlertDialog scoreboardDialog = dialog.create(); // Create the dialog in a final context
                    // Find the Button object within the inflated view
                    ImageButton btnDialogScoreboardReplay=(ImageButton)dialogView.findViewById(R.id.btnSingleDialogScoreboardReplay);
                    ImageButton btnDialogScoreboardHome=(ImageButton)dialogView.findViewById(R.id.btnSingleDialogScoreboardHome);
                    TextView txtDialogScoreboardScore=(TextView)dialogView.findViewById(R.id.txtSingleDialogScoreboardScore);
                    ImageView dialogScoreboardBg=(ImageView)dialogView.findViewById(R.id.singleDialogScoreboardBg);
                    txtDialogScoreboardScore.setText(view_my_score.getText());
                    // Set the onClickListener
                    btnDialogScoreboardReplay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { // Restart activity
                            scoreboardDialog.dismiss();
                            sendMessage("Restart");
                            mainActivityStart();
                            view_btn_start.setTag("pause");
                            view_btn_start.setImageResource(R.drawable.btn_pause);
                        }
                    });
                    btnDialogScoreboardHome.setOnClickListener(new View.OnClickListener() { // Finish activity
                        @Override
                        public void onClick(View v) {
                            sendMessage("Home");
                            finish();
                        }
                    });
                    btnDialogScoreboardReplay.setVisibility(View.GONE);
                    scoreboardDialog.show(); // Show the dialog
                    if (Integer.parseInt(view_rival_score.getText().toString()) - (Integer.parseInt(view_my_score.getText().toString()))>0) {
                        dialogScoreboardBg.setImageResource(R.drawable.dialog_scoreboard_lose_bg);
                    }
                    else
                        dialogScoreboardBg.setImageResource(R.drawable.dialog_scoreboard_win_bg);
                }
            }
        });


        ///////////////////////dialog start





        /////////////////////////dialog over
/////////////////////////////////////////bluetooth///////////////////////////


        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //prepare for dialog
        alertDialog = new AlertDialog.Builder(BluetoothActivity.this).create();
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.list, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Choose a device as listed below");
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                isDialogDismissed = false;
            }
        });
        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        adapter= new ArrayAdapter<String>(BluetoothActivity.this,android.R.layout.simple_list_item_1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = ((TextView) view).getText().toString().split("address")[0];
                device = devices.get(selectedDevice);
                colorToothService.connect(device);
                alertDialog.dismiss();
            }
        });



        view_connect.setClickable(true);

        view_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Home");
                finish();
            }
        });

        view_connect.setClickable(false);
        //initiate the bluetooth service
        colorToothService = new ColorToothService(this,mHandler);

    }

    private void createAndShowDialog(){
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(BluetoothActivity.this,R.style.CustomDialog); // Create new alert dialog
        View dialogView = getLayoutInflater().inflate(R.layout.bluetooth_dialog_pause, null); // Inflate the custom layout in to a View object
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final android.support.v7.app.AlertDialog pauseDialog = dialog.create(); // Create the dialog in a final context
        // Find the Button object within the inflated view
        ImageButton btnDialogPauseResume=(ImageButton)dialogView.findViewById(R.id.btnBluetoothDialogPauseResume);
        ImageButton btnDialogPauseHome=(ImageButton)dialogView.findViewById(R.id.btnBluetoothDialogPauseHome);
        // Set the onClickListener
        btnDialogPauseResume.setOnClickListener(new View.OnClickListener() { // Dismiss and resume game
            @Override
            public void onClick(View v) {
                isPaused = false;
                sendMessage("Resume");
                resume();
                view_btn_start.setTag("pause");
                view_btn_start.setImageResource(R.drawable.btn_pause);
                pauseDialog.dismiss();
            }
        });
        btnDialogPauseHome.setOnClickListener(new View.OnClickListener() { // Finish activity
            @Override
            public void onClick(View v) {
                sendMessage("Home");
                finish();
            }
        });
        // Set onTouchListener
        btnDialogPauseResume.setOnTouchListener(mBottonTouchListener);
        btnDialogPauseHome.setOnTouchListener(mBottonTouchListener);
        pauseDialog.show();
    }


    public void mainActivityStart(){


        round = new BluetoothModeRound(buttons, images, view_task_type,view_task_content,view_my_score, null,this);
        round.start();

        if(identity == IDENTITY_SERVER) {
            timer = new CountDownTimer(70000, 1000) {
                public void onTick(long millisUntilFinished) {
                    //Do something in every tick
                    if (isPaused) {
                        //If user requested to unit_pause or cancel the count down timer
                        cancel();
                        Log.e("timer.toString()", timer.toString());
                        Log.e("timer.hashCode()", timer.hashCode()+"");
                        Log.e("MainActivity",millisUntilFinished / 1000+"");
                        sendMessage("Pause");
                    } else {
                        view_time_remaining.setText("" + millisUntilFinished / 1000);
                        //Put count down timer remaining time in a variable
                        timeRemaining = millisUntilFinished;
                        sendMessage("timeRemaining" + ":" + millisUntilFinished / 1000);
                    }
                }

                public void onFinish() {
                    round.stop();
                    sendMessage("RoundOver");
                    view_time_remaining.setText(0 + "");
                    view_btn_start.setTag("restart");
                    view_btn_start.setImageResource(R.drawable.btn_start);
                }
            }.start();
        }
    }

    public void pause(){
        isPaused = true;
        if(round!=null)
            round.pause();
    }

    private void pause_onStop(){
        if(timer!=null)
            timer.cancel();
        if(round!=null)
            round.pause();
    }

    public void resume(){


        if(round!=null)
            round.resume();

        if(identity == IDENTITY_SERVER){
            long millisInFuture = timeRemaining;
            long countDownInterval = 1000;
            timer = new CountDownTimer(millisInFuture, countDownInterval){
                public void onTick(long millisUntilFinished){
                    //Do something in every tick
                    if(isPaused )
                    {
                        cancel();
                        sendMessage("Pause");
                    }
                    else {
                        view_time_remaining.setText("" + millisUntilFinished / 1000);
                        timeRemaining = millisUntilFinished;
                        sendMessage("timeRemaining" + ":" + millisUntilFinished / 1000);

                    }
                }
                public void onFinish(){
                    round.stop();
                    view_time_remaining.setText(0+"");
                    view_btn_start.setTag("restart");
                    view_btn_start.setImageResource(R.drawable.btn_start);
                }
            }.start();
        }
    }


    public void register(){
        buttons.add((ImageButton) findViewById(R.id.imgBtn00));
        buttons.add((ImageButton) findViewById(R.id.imgBtn01));
        buttons.add((ImageButton) findViewById(R.id.imgBtn02));
        buttons.add((ImageButton) findViewById(R.id.imgBtn03));
        buttons.add((ImageButton) findViewById(R.id.imgBtn04));

        buttons.add((ImageButton) findViewById(R.id.imgBtn10));
        buttons.add((ImageButton) findViewById(R.id.imgBtn11));
        buttons.add((ImageButton) findViewById(R.id.imgBtn12));
        buttons.add((ImageButton) findViewById(R.id.imgBtn13));
        buttons.add((ImageButton) findViewById(R.id.imgBtn14));

        buttons.add((ImageButton) findViewById(R.id.imgBtn20));
        buttons.add((ImageButton) findViewById(R.id.imgBtn21));
        buttons.add((ImageButton) findViewById(R.id.imgBtn22));
        buttons.add((ImageButton) findViewById(R.id.imgBtn23));
        buttons.add((ImageButton) findViewById(R.id.imgBtn24));

        buttons.add((ImageButton) findViewById(R.id.imgBtn30));
        buttons.add((ImageButton) findViewById(R.id.imgBtn31));
        buttons.add((ImageButton) findViewById(R.id.imgBtn32));
        buttons.add((ImageButton) findViewById(R.id.imgBtn33));
        buttons.add((ImageButton) findViewById(R.id.imgBtn34));

        buttons.add((ImageButton) findViewById(R.id.imgBtn40));
        buttons.add((ImageButton) findViewById(R.id.imgBtn41));
        buttons.add((ImageButton) findViewById(R.id.imgBtn42));
        buttons.add((ImageButton) findViewById(R.id.imgBtn43));
        buttons.add((ImageButton) findViewById(R.id.imgBtn44));

        images.put("00", R.drawable.unit00);
        images.put("01", R.drawable.unit01);
        images.put("02", R.drawable.unit02);
        images.put("03", R.drawable.unit03);
        images.put("04", R.drawable.unit04);
        images.put("10", R.drawable.unit10);
        images.put("11", R.drawable.unit11);
        images.put("12", R.drawable.unit12);
        images.put("13", R.drawable.unit13);
        images.put("14", R.drawable.unit14);
        images.put("20", R.drawable.unit20);
        images.put("21", R.drawable.unit21);
        images.put("22", R.drawable.unit22);
        images.put("23", R.drawable.unit23);
        images.put("24", R.drawable.unit24);
        images.put("30", R.drawable.unit30);
        images.put("31", R.drawable.unit31);
        images.put("32", R.drawable.unit32);
        images.put("33", R.drawable.unit33);
        images.put("34", R.drawable.unit34);
        images.put("40", R.drawable.unit40);
        images.put("41", R.drawable.unit41);
        images.put("42", R.drawable.unit42);
        images.put("43", R.drawable.unit43);
        images.put("44", R.drawable.unit44);
        images.put("55", R.drawable.unit55);
    }

    /////////////////////////////////////////bluetooth///////////////////////////
    private void connectColorTooth(){
        if(!isConnected){
            devices.clear();
            if (!myBluetoothAdapter.isEnabled()) {
                Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

                Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                        Toast.LENGTH_LONG).show();
            }
            else{
                connectBluetoothAfterOn();
            }

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is connected",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void connectBluetoothAfterOn(){
        Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                Toast.LENGTH_LONG).show();

        //getVisible
        colorToothService.start();


        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        getVisible.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(getVisible, REQUEST_VISIBLE_BT);




    }

    private void startDiscovery() {
        adapter.clear();
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        } else {


            myBluetoothAdapter.startDiscovery();

            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            new CountDownTimer(6000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (devices.size() != 0) {
                        alertDialog.show();
                    } else {
                        colorToothService.stop();
                        colorToothService.start();
                        view_connect.setClickable(true);
                        Toast.makeText(getApplicationContext(), "There is no device connectable and you need to connect on your own", Toast.LENGTH_LONG).show();
                    }
                }
            }.start();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode){
            case REQUEST_ENABLE_BT:
                if(myBluetoothAdapter.isEnabled()){
                    connectBluetoothAfterOn();
                } else {
                    Toast.makeText(getApplicationContext(), "You must make turn on the bluetooth to play this mode", Toast.LENGTH_LONG).show();
                    view_connect.setClickable(true);
                }
                break;
            case REQUEST_VISIBLE_BT:
                if(resultCode==RESULT_CANCELED){
                    view_connect.setClickable(true);
                    Toast.makeText(getApplicationContext(), "You must make your device visible to play this mode", Toast.LENGTH_LONG).show();

                }else{
                    startDiscovery();
                }
        }
    }

    private void connect(){
        connectColorTooth();
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (colorToothService.getState() != colorToothService.STATE_CONNECTED) {
            Toast.makeText(this, "Device is not connected!", Toast.LENGTH_SHORT).show();
            if(round!=null)
                round.stop();
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            colorToothService.write(send);

            // Reset out string buffer to zero and clear the edit text field
//            mOutStringBuffer.setLength(0);
//            mOutEditText.setText(mOutStringBuffer);
        }
    }

    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }

    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }



    @Override
    protected void onRestart() {
        super.onRestart();
        if(send_identity == SEND_SRC)
            sendMessage("onRestart");
        resume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        view_btn_start.setTag("pause");
        view_btn_start.setImageResource(R.drawable.btn_pause);
        if(send_identity == SEND_SRC)
            sendMessage("Pause");
        pause_onStop();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        try{
            unregisterReceiver(bReceiver);
        } catch (IllegalArgumentException e){
            finish();
        }

        if (colorToothService != null) colorToothService.stop();
    }

}
