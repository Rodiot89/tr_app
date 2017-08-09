package t_friends.trichterscoreboard;



import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;


import static t_friends.trichterscoreboard.Scoreboard_Activity.GlobalData;
import static t_friends.trichterscoreboard.Scoreboard_Activity.SortList;


public class newEntry_Activity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "NewEntryActivity";

    // Variables
    Toolbar tbNewEntry;
    FloatingActionButton btnSwitchBT;
    Button btnScanBT;
    ListView lvBTDevices;
    ProgressBar pbDiscoverBT;
    ImageView ivGreenLED;
    //ImageView ivRedLED;
    EditText etTimeToBT;
    AutoCompleteTextView etPersonName;
    EditText etTime;
    Button btnGetTimeViaBT;
    AutoCompleteTextView etEventName;
    Button btnAdd;
    Button btnClear;
    Boolean GlobalDataChanged;
    Boolean PersonAlreadyExits;
    Boolean BT_status_On;
    Boolean BTDiscoverable_status_On;
    Boolean RequestTime = Boolean.FALSE;

    //StringBuilder messages;

    HashSet<String> autofill_pn = new HashSet<>();      //AutoFillListe mit PersonNamen (ohne Duplikate)
    HashSet<String> autofill_en = new HashSet<>();           //AutoFillListe mit EventNamen (ohne Duplikate)

    // Bluetooth Stuff
    // ********************************************
    // ********************************************

    public final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothConnectionService mBluetoothConnection;
    BluetoothDevice mBTDevice;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");    // 00001101-0000-1000-8000-00805F9B34FB   // 8ce255c0-200a-11e0-ac64-0800200c9a66
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public ArrayList<String> mBTDevicesStrings = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;



    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        BT_status_On = Boolean.FALSE;
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BT_status_On = Boolean.TRUE;
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Receiver for changes made to bluetooth states such as:
     * 1) Discoverability mode on/off or expire.
     */
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        BTDiscoverable_status_On = Boolean.TRUE;
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        BTDiscoverable_status_On =  Boolean.FALSE;
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnScanBT() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());

                ArrayList  tempDevices = new ArrayList<String>();

                for (BluetoothDevice b : mBTDevices) {
                    String paired = "Paired";
                    if (b.getBondState() != 12) {
                        paired = "Not Paired";
                    }
                    tempDevices.add(b.getName() + " - [ " + paired + " ] ");
                }

                mBTDevicesStrings.clear();

                mBTDevicesStrings.addAll(tempDevices);
                mDeviceListAdapter.notifyDataSetChanged();


                //mDeviceListAdapter = new DeviceListAdapter(context, R.layout.second_listview_bt_devices_layout, mBTDevices);
                //lvBTDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3b = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION Discovery finished.");

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                pbDiscoverBT.setVisibility(View.INVISIBLE);
                btnScanBT.setVisibility(View.VISIBLE);
                }
        }
    };


    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    //inside BroadcastReceiver4
                    mBTDevice = mDevice;
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    // Handhabt Intent mit Request, später obsolet mit Arduino
    private final BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String request = intent.getStringExtra("Request");
            if (request.equals("requestTime")) {
                //RequestTime = Boolean.TRUE;
                Log.d(TAG, "BroadcastReceiver5: RequestTime received." );

                String TimeToSend = etTimeToBT.getText().toString();        // hole Zeit aus editFeld und sende
                byte[] bytes = TimeToSend.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes);
            }
        }
    };

    // handhabt Intent mit TimeFromArduino
    private final BroadcastReceiver mBroadcastReceiver6 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String time = intent.getStringExtra("TimeFromArduino");

            Log.d(TAG,"BroadcastReceiver6: TimeFromArduino = "+ time);

            etTime.setText(time);
        }
    };


    // OnCreate OnDestroy
    //*******************************************************
    //*******************************************************

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        try {
            this.unregisterReceiver(mBroadcastReceiver1);
            this.unregisterReceiver(mBroadcastReceiver2);
            this.unregisterReceiver(mBroadcastReceiver3);
            this.unregisterReceiver(mBroadcastReceiver4);
        } catch (Exception e) {
            Log.i(TAG, "mBroadcastReceiver1 is already unregistered");
        }
        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.disable();
        //disableBT();
    }

    @Override
    public void onResume() {
        super.onResume();


        btnSwitchBT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if (BT_status_On){     // wenn BT on, dann schalte BT aus
                    btnSwitchBT.setBackgroundTintList(ColorStateList.valueOf(0xaaa));
                    btnScanBT.setVisibility(View.INVISIBLE);
                    mBluetoothAdapter.cancelDiscovery();
                    mBluetoothAdapter.disable();
                    BT_status_On = Boolean.FALSE;

                }
                else{
                    btnSwitchBT.setBackgroundTintList(ColorStateList.valueOf(0xff669900));
                    btnScanBT.setVisibility(View.VISIBLE);
                    enableBT();                 //Activate Bluetooth automatically when activity started
                    if (BT_status_On){
                        enableDiscoverable();   // set Discoverable for 300s
                    }
                    if (BTDiscoverable_status_On){
                        discoverBTDevices();    // discover  for paired and unpaired bluetooth devices and fill listview
                    }
                }

            }
        });
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newentry_layout);

        // init variabels
        GlobalDataChanged = Boolean.FALSE;
        PersonAlreadyExits = Boolean.FALSE;
        BT_status_On = Boolean.FALSE;
        BTDiscoverable_status_On = Boolean.FALSE;

        // init views
        tbNewEntry = (Toolbar) findViewById(R.id.toolbar_newEntry);
        setSupportActionBar(tbNewEntry);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etPersonName = (AutoCompleteTextView) findViewById(R.id.editText_InsertName);
        etTime = (EditText) findViewById(R.id.editText_InsertTime);
        etEventName = (AutoCompleteTextView) findViewById(R.id.editText_InsertEvent);
        btnAdd = (Button) findViewById(R.id.button_add);
        btnClear = (Button) findViewById(R.id.button_clear);

        btnSwitchBT = (FloatingActionButton) findViewById(R.id.switch_BT_OnOff);
        btnScanBT = (Button) findViewById(R.id.button_ScanBT);
        lvBTDevices = (ListView) findViewById(R.id.listview_BTDevice);
        pbDiscoverBT =(ProgressBar) findViewById(R.id.progressBar_DiscoverBT);
        ivGreenLED = (ImageView) findViewById(R.id.imageView_greenLED);
        //ivRedLED = (ImageView) findViewById(R.id.imageView_redLED);

        /*RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, );
        dr.setCornerRadius(3);
        ivRedLED.setImageDrawable(dr);*/

        mBTDevices = new ArrayList<>();
        btnGetTimeViaBT = (Button) findViewById(R.id.button_getTimeByBT);
        etTimeToBT = (EditText) findViewById(R.id.editText_NumberToSend);

        // Bluetooth Init
        /*IntentFilter filter_discoverBTDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);    // run Code in mBroadcastReceiver3: update List of found BT devices if BT device(s) found
        registerReceiver(mBroadcastReceiver3, filter_discoverBTDevices);

        IntentFilter filter_finishDiscoverBTDevices = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);    // run Code in mBroadcastReceiver3b: update progressbar and btnScanBT if Discovery is over
        registerReceiver(mBroadcastReceiver3b, filter_finishDiscoverBTDevices);*/

        IntentFilter filter_bondState = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);  //Broadcasts when bond state changes (ie:pairing)
        registerReceiver(mBroadcastReceiver4, filter_bondState);
        //final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mDeviceListAdapter = new DeviceListAdapter(this, R.layout.second_listview_bt_devices_layout, mBTDevicesStrings);       // TODO mBTDevices ersetzen durch Arraylist<String of all BT devices>
        lvBTDevices.setAdapter(mDeviceListAdapter);
        lvBTDevices.setOnItemClickListener(newEntry_Activity.this);

        // Bluetooth Chat
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver5, new IntentFilter("incomingRequest"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver6, new IntentFilter("incomingMessage"));



        // Set Layout Listener
        // ***********************************************
        // ***********************************************

        // **1a** discover BT Devices
        btnScanBT.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                discoverBTDevices();
            }
        });

        // **1b** GetTime via BT
        btnGetTimeViaBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sende Aufforderung an BT-Device (Arduino): Sende Time
                String requestTime ="requestTime";
                byte[] bytes = requestTime.getBytes(Charset.defaultCharset());
                mBluetoothConnection.write(bytes);
            }
        });


        // **2** Auto Complete PersonName
        autofill_pn=getUniquePersonNamesOfGlobalData(); // holt unique PersonName aus der Globaldata
        final ArrayAdapter<String> adapter_pn = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,autofill_pn.toArray(new String[autofill_pn.size()])); // HashSet --> ArrayList<String> : autofill_pn.toArray(new String[autofill_pn.size()])
        etPersonName.setAdapter(adapter_pn);

        etPersonName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(final View view, boolean b) {  // Dropdown menu mit auto suggestion bereits beim ersten Klick auf Feld
                etPersonName.showDropDown();
            }
        });
        etPersonName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {    // Dropdown menu mit auto suggestion immer bei klicken auf Feld
                etPersonName.showDropDown();
            }
        });

        etPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                //ColorDrawable drawable = (ColorDrawable) etPersonName.getBackground();
                for (String pn : autofill_pn){      // prüft ob eingegebener text als Name im GlobalData existiert
                    if (pn.equals(text.toString())) {
                        etPersonName.setBackgroundColor(Color.GREEN);  // setzt Farbe neu wenn True
                        break;
                    }
                    else{
                        etPersonName.setBackgroundColor(Color.TRANSPARENT);  // andernfalls return to transparent
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // **3** Auto Complete EventName
        autofill_en=getUniqueEventNamesOfSortList();
        final ArrayAdapter<String> adapter_en = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,autofill_en.toArray(new String[autofill_en.size()])); // HashSet --> ArrayList<String> : autofill_pn.toArray(new String[autofill_pn.size()])
        etEventName.setAdapter(adapter_en);

        etEventName.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(final View view, boolean b) {
                etEventName.showDropDown();
            }
        });
        etEventName.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                etEventName.showDropDown();
            }
        });

        etEventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                //ColorDrawable drawable = (ColorDrawable) etPersonName.getBackground();
                for (String en : autofill_en){      // prüft ob eingegebener text als Name im GlobalData existiert
                    if (en.equals(text.toString())) {
                        etEventName.setBackgroundColor(Color.GREEN);  // setzt Farbe neu wenn True
                        break;
                    }
                    else{
                        etEventName.setBackgroundColor(Color.TRANSPARENT);  // andernfalls return to transparent
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // **4** Add Entry
        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                String pn = etPersonName.getText().toString().trim();
                String time = etTime.getText().toString().trim();
                long d = Calendar.getInstance().getTimeInMillis();
                String en = etEventName.getText().toString().trim();
                //System.out.println(String.valueOf(t));
                if (!(pn.isEmpty() || time.isEmpty())) {  // Name und Zeit darf nicht leer sein --> Toast
                    double t = Double.parseDouble(time); // wenn time nicht leer dann convert in double
                    //Prüfen ob Person bereits vorhanden
                    for (TrichterPerson tp : GlobalData){
                        if (tp.getPersonName().equals(pn)){     // wenn PersonenName bereits vorhanden
                            tp.create_event(t,d,en);            // erstelle für diese Person neues Event
                            PersonAlreadyExits = Boolean.TRUE;
                            Toast.makeText(getApplicationContext(),"Notice: New Event added to an EXISTING Person!",Toast.LENGTH_SHORT).show();
                        }break;
                    }
                    if (PersonAlreadyExits == Boolean.FALSE) {      // wenn Person noch nicht existiert
                        TrichterPerson tp = new TrichterPerson(pn); // erstelle neue Person mit Event
                        tp.create_event(t, d, en);
                        GlobalData.add(tp);
                        Toast.makeText(getApplicationContext(),"Notice: New Event added to a NEW Person!",Toast.LENGTH_SHORT).show();
                    }
                    GlobalDataChanged = Boolean.TRUE;

                    Intent backToMain =new Intent(newEntry_Activity.this, Scoreboard_Activity.class);
                    backToMain.putExtra("GlobalDataChanged",GlobalDataChanged);
                    startActivity(backToMain);


                    // empty the edit text time
                    etTime.setText("");
                }else{
                    Toast.makeText(getApplicationContext(),"Name of Person and Trichter Time must be given!",Toast.LENGTH_LONG).show();
                }
            }
        });

        // **5** Clear Entry
        btnClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                etPersonName.setText("");
                etTime.setText("");
                etEventName.setText("");
            }
        });

    }

    // Bluetooth-Methoden
    //*****************************************
    //*****************************************

    // **1a** Enable  BT
    public void enableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableBT");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);

            BT_status_On = Boolean.TRUE;
        }else if (mBluetoothAdapter.isEnabled()){
            BT_status_On = Boolean.TRUE;
        }
    }

    // **1b** Disable BT
    public void disableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "disableBT: Does not have BT capabilities.");
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "DisableBT");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);

            BT_status_On = Boolean.FALSE;
        }
    }

    // **2** Enable Discoverable
    public void enableDiscoverable() {
        Log.d(TAG, "enableDiscoverable: Making device discoverable for 300 seconds.");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,intentFilter);
        BTDiscoverable_status_On = Boolean.TRUE;
    }

    // **3a** get and show already paired devices
    private void getPairedDevices() {

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice curDevice : pairedDevices) {
                mBTDevices.add(curDevice);
            }
            Log.i(TAG, "getPairedDevices: Paired Number of Devices : " + pairedDevices.size());


            ArrayList  tempDevices = new ArrayList<String>();

            for (BluetoothDevice b : mBTDevices) {
                String paired = "Paired";
                if (b.getBondState() != 12) {
                    paired = "Not Paired";
                }
                tempDevices.add(b.getName() + " - [ " + paired + " ] ");
            }
            mBTDevicesStrings.clear();

            mBTDevicesStrings.addAll(tempDevices);
            mDeviceListAdapter.notifyDataSetChanged();
        }
    }


    // **3b** Discover BT devices
    public void discoverBTDevices() {
        Log.d(TAG, "DiscoverBTDevices: Looking for BT devices.");
        btnScanBT.setVisibility(View.INVISIBLE);
        pbDiscoverBT.setVisibility(View.VISIBLE);
        mBTDevices.clear(); // alte gefunden devices löschen
        mBTDevicesStrings.clear(); //Strings (Name) der alten Devices löschen
        mDeviceListAdapter.notifyDataSetChanged();

        getPairedDevices();     // schaue erst nach paired devices

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }
        //check BT permissions in manifest
        checkBTPermissions();

        mBluetoothAdapter.startDiscovery();
        IntentFilter filter_discoverBTDevices = new IntentFilter(BluetoothDevice.ACTION_FOUND);    // run Code in mBroadcastReceiver3: update List of found BT devices if BT device(s) found
        registerReceiver(mBroadcastReceiver3, filter_discoverBTDevices);

        IntentFilter filter_finishDiscoverBTDevices = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);    // run Code in mBroadcastReceiver3b: update progressbar and btnScanBT if Discovery is over
        registerReceiver(mBroadcastReceiver3b, filter_finishDiscoverBTDevices);
    }



    // **4* Select Found devices in Listview
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();
        pbDiscoverBT.setVisibility(View.INVISIBLE);

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TAG, "Trying to pair with " + deviceName);
            mBTDevices.get(i).createBond();

            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(newEntry_Activity.this);

            startBTConnection(mBTDevice,MY_UUID_INSECURE);      //Start Bluetooth Chat Service
        }
    }


    // **5**  starting chat service method
    // ** remember the connection will fail and app will crash if you haven't paired first
    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }

    // **6** Check BT Permissions
    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT >= 23){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }




    // Hilfsmethoden
    // **************************************
    // **************************************

    // **1**
    private  HashSet<String> getUniquePersonNamesOfGlobalData(){
        Collections.sort(SortList, new BeanComparator("date"));
        Collections.reverse(SortList);
        for (TrichterPerson.TrichterEvent te :SortList) {
            autofill_pn.add(te.getParentPersonName());
        }
        return autofill_pn;
    }

    // **2**
    private HashSet<String> getUniqueEventNamesOfSortList(){
        Collections.sort(SortList, new BeanComparator("date"));
        Collections.reverse(SortList);
        for (TrichterPerson.TrichterEvent te : SortList){
            autofill_en.add(te.getEventName());
        }
        return autofill_en;
    }






}
//  Toast.makeText(getApplicationContext(),"No Name found!",Toast.LENGTH_SHORT).show();