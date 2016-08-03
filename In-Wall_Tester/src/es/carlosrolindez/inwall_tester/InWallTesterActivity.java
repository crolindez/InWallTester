package es.carlosrolindez.inwall_tester;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InWallTesterActivity extends Activity  {
	private static String TAG = "InWall Tester";

    private BluetoothAdapter mBluetoothAdapter = null;
    
	private final InWallHandler  handler = new InWallHandler();
	
	private static TextView message;
	private static TextView messageAux;
	
	private static ArrayAdapter<String> deviceListAdapter = null;
	private static ArrayList<String> deviceList;
	
	private Context mContext;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);
		setContentView(R.layout.activity_inwall_tester);
		mContext = this;
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
		    Toast.makeText(this, getString(R.string.bt_not_availabe), Toast.LENGTH_LONG).show();
		    finish();
		}	
		
		ListView listView = (ListView) findViewById(R.id.list);
		deviceList = new ArrayList<String>();
		deviceListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1,deviceList);
		listView.setAdapter(deviceListAdapter); 

        message =(TextView) findViewById(R.id.DeviceName); 
        messageAux =(TextView) findViewById(R.id.DeviceFound); 
        	
    }
	
    @Override
    public void onStart() {
        super.onStart();

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, Constants.REQUEST_ENABLE_BT);
        } else 
        	new A2dpService(this,handler);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                	new A2dpService(this,handler);
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled,Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;           
        }
    }


    
    @Override
    public void onResume() {
        super.onResume();
    }

    
 		
	@Override
	protected void onDestroy() {
		A2dpService.closeService();
		super.onDestroy();
	}
		


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId())
		{
	        case R.id.bt_scan: 
	            // Launch the DeviceListActivity to see devices and do scan
//	        	doDiscovery();
	            return true;

			case R.id.action_settings:
                return true;
		}

		return super.onOptionsItemSelected(item);
	}
	

	
	public class InWallHandler extends Handler {

	    public static final int MESSAGE_CONNECTED = 1; 
	    public static final int MESSAGE_DISCONNECTED = 2; 
	    public static final int MESSAGE_FOUND = 3; 
	    private String deviceMessage;
	    private String deviceMAC;
	    private String deviceName;
	    
	    @Override
	    public void handleMessage(Message msg) {
	        switch (msg.what) {
	            case MESSAGE_CONNECTED:
	            	deviceMessage = (String) msg.obj;
		            Log.e("TAG","Message: " + deviceMessage);	 
	            	if (deviceMessage.length()>13) {
	            		deviceName = deviceMessage.substring(17);
			            Log.e("TAG","Name: " + deviceName);
	            		deviceMAC = deviceMessage.substring(0, 17);
			            Log.e("TAG","MAC: " + deviceMAC);
	            	}	
	            	message.setText(deviceName);
	            	messageAux.setText(deviceMAC);
	            	if ( (deviceName.length()!=11) || (!deviceName.substring(0,7).equals("KINGBT-")) ) {
	            		message.setTextColor(Color.parseColor("#FF0000"));	            		
	            	} else {
	            		message.setTextColor(Color.parseColor("#00FF00"));	 
	            		Intent msgIntent = new Intent(mContext, FTPService.class);
	            	    msgIntent.putExtra(Constants.DEVICE_NAME, deviceName);
	            	    msgIntent.putExtra(Constants.DEVICE_MAC, deviceMAC);
	            	    startService(msgIntent);
						if (!deviceList.contains(message.getText()))
						{
							deviceList.add(0,message.getText().toString());
							deviceListAdapter.notifyDataSetChanged(); 
						}
	            	}


	                break;
	            case MESSAGE_DISCONNECTED:
	            	message.setText(mContext.getResources().getString(R.string.searching));
            		messageAux.setText("");
            		message.setTextColor(Color.parseColor("#dddddd"));	

	                break;
	            case MESSAGE_FOUND:
	            	deviceName = (String) msg.obj;
            		messageAux.setText(mContext.getResources().getString(R.string.found) + " " + deviceName);

	                break;
   	

	        }
	    }
	}

    

}
