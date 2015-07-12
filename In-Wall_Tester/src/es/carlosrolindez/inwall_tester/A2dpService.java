package es.carlosrolindez.inwall_tester;

import java.util.List;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothA2dp;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import es.carlosrolindez.inwall_tester.InWallTesterActivity.InWallHandler;


public class A2dpService {
	private static String TAG = "A2DP Service";

    private static final String inWallFootprint = "00:0D:18";
    
    private static BluetoothAdapter mBluetoothAdapter ;

	
	private static Context mContextBt;

	private static boolean mBtA2dpIsBound = false;
	private static IBluetoothA2dp iBtA2dp = null;	
	private static boolean a2dpReceiverRegistered = false;
	
	private static BluetoothDevice connectingDevice;
	
	private static boolean connectedA2dp; 
	
    private static InWallHandler mHandler;
	

	public A2dpService(Context context,InWallHandler handler) {
		
		mContextBt = context;
		connectedA2dp = false;
		mHandler = handler;
		
		mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
		mBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.A2DP);

		
		IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_FOUND);			
		IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);			
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);	
 //       IntentFilter filter6 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);	    

        context.registerReceiver(mBtReceiver, filter2);
        context.registerReceiver(mBtReceiver, filter3);	  
        context.registerReceiver(mBtReceiver, filter4);	
        context.registerReceiver(mBtReceiver, filter5);	
 //       context.registerReceiver(mBtReceiver, filter6);	
        	

	}
		
	private static final BroadcastReceiver mBtReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.e(TAG,"Found "+device.getName());
        		if (device.getAddress().substring(0,8).equals(inWallFootprint)) {
        			Log.e(TAG,"Connecting");
        			switchA2dp(device);
        		}
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	((InWallTesterActivity)context).setProgressBarIndeterminateVisibility(false);
                Log.e(TAG,"Discovery Finished");
            	if (!connectedA2dp) {
            		doDiscovery();	
            	}
            
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            	((InWallTesterActivity)context).setProgressBarIndeterminateVisibility(false);
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                Log.e(TAG,"Connected "+device.getName());
                Toast.makeText(context, device.getName() + " Connected", Toast.LENGTH_SHORT).show();
                playBt();     
                mHandler.obtainMessage(InWallHandler.MESSAGE_CONNECTED, -1, -1, device.getName()).sendToTarget();
                
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                Toast.makeText(context, device.getName() + " Disconnected", Toast.LENGTH_SHORT).show();
                Log.e(TAG,"Disconnected "+device.getName());
                connectedA2dp = false;
                mHandler.obtainMessage(InWallHandler.MESSAGE_DISCONNECTED, -1, -1, device.getName()).sendToTarget();
                doDiscovery();
                
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState()==BluetoothDevice.BOND_BONDED) {
                	switchBluetoothA2dp(device);  
                }
            }
		}

	};

	
	public static void playBt() 
	{
  		new Handler().postDelayed(new Runnable() {
		    @Override
		    public void run() {
				AudioManager am = (AudioManager)mContextBt.getSystemService(Context.AUDIO_SERVICE);

				long eventtime = SystemClock.uptimeMillis() - 1;
				KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
				am.dispatchMediaKeyEvent(downEvent);

				eventtime++;
				KeyEvent upEvent = new KeyEvent(eventtime,eventtime,KeyEvent.ACTION_UP,KeyEvent.KEYCODE_MEDIA_PLAY, 0);         
				am.dispatchMediaKeyEvent(upEvent);

		  	}
		}, 2500);


		
/*		Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
		Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null); 
		
		KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0); 
		KeyEvent upEvent = 	 new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);


		downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent); 
		mContextBt.sendBroadcast(downIntent, null); 

		upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent); 
		mContextBt.sendBroadcast(upIntent, null); 		*/
		
		
		
	}

	
	
	public static void switchBluetoothA2dp(BluetoothDevice device) {
		connectingDevice = device;
		if (!a2dpReceiverRegistered) {
			IntentFilter filter1 = new IntentFilter(Constants.a2dpFilter);
			mContextBt.registerReceiver(mA2dpReceiver, filter1);
			a2dpReceiverRegistered = true;
			Log.e(TAG,"a2dp receiver registered ");
		}
		Intent i = new Intent(IBluetoothA2dp.class.getName());
		mContextBt.bindService(i, mBtA2dpServiceConnection, Context.BIND_AUTO_CREATE);
		Log.e(TAG,"a2dp receiver binded ");
	}	


	public static ServiceConnection mBtA2dpServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e(TAG,"on Service Connected ");
			mBtA2dpIsBound = true;
			iBtA2dp = IBluetoothA2dp.Stub.asInterface(service);

			Intent intent = new Intent();
			intent.setAction(Constants.a2dpFilter);
			mContextBt.sendBroadcast(intent);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBtA2dpIsBound = false;

		}

	};

	private static final BroadcastReceiver mA2dpReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			Log.e(TAG,"on BroadCastReceiver ");
			new connectA2dpTask().execute();
		}

	};
	

	private static class connectA2dpTask extends AsyncTask<String, Void, Boolean> {


		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			a2dpDone();
		}

		protected void onPreExecute() {
			Log.e(TAG,"on PreExecute  ");
		}

		@Override
		protected Boolean doInBackground(String... arg0) {
			
			BluetoothDevice device = connectingDevice;
			Log.e(TAG,"doing in background ");

			BluetoothAdapter mBTA = BluetoothAdapter.getDefaultAdapter();
			if (mBTA == null || !mBTA.isEnabled())
				return false;


			try {
				if ( (A2dpService.iBtA2dp != null) && (A2dpService.iBtA2dp.getConnectionState(device) == 0) ) {
					A2dpService.iBtA2dp.connect(device);
					Log.e(TAG,"connect "+device.getName());
				} else {
					A2dpService.iBtA2dp.disconnect(device);
					Log.e(TAG,"disconnect "+device.getName());
				}

			} catch (Exception e) {
			}
			return true;
		}

	}	



	
	public static void closeService( ){
		Log.e(TAG,"Close service ");
		a2dpDone();	
		mContextBt.unregisterReceiver(mBtReceiver);
	}
	
	
	private static void a2dpDone() {
		Log.e(TAG,"a2dpDone");
		if (a2dpReceiverRegistered) {
			mContextBt.unregisterReceiver(mA2dpReceiver);
			a2dpReceiverRegistered = false;
			doUnbindServiceBtA2dp();
		}


	}
	
	public static void doUnbindServiceBtA2dp() {
		Log.e(TAG,"Unbind");
		if (mBtA2dpIsBound) {
			try {
				mContextBt.unbindService(mBtA2dpServiceConnection);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	


	private static BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {

        public void onServiceConnected(int profile, BluetoothProfile proxy) {        	
            if (profile == BluetoothProfile.A2DP) {
                BluetoothA2dp btA2dp = (BluetoothA2dp) proxy;
                List<BluetoothDevice> a2dpConnectedDevices = btA2dp.getConnectedDevices();
/*                if (a2dpConnectedDevices.size() != 0) {
                	connectedA2dp = true;
                    for (BluetoothDevice a2dpDevice : a2dpConnectedDevices) {
                    	switchBluetoothA2dp(a2dpDevice);
                    }
                }*/
                if (a2dpConnectedDevices.size() == 0) {
                    doDiscovery();              	
                }
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(BluetoothProfile.A2DP, btA2dp);
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP) {
            }
        }
    };


	
	public static void switchA2dp (BluetoothDevice device) {
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		
		
		if   (device != null) {
			if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
				device.createBond();
			} else {
				connectedA2dp = true;
				switchBluetoothA2dp(device);
			}

		}
	}

	/**
     * Start device discover with the BluetoothAdapter
     */
    private static void doDiscovery() {
        Log.e(TAG,"Start Discovery");
        // Indicate scanning in the title
    	((InWallTesterActivity)mContextBt).setProgressBarIndeterminateVisibility(true);

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }
    


	

}

