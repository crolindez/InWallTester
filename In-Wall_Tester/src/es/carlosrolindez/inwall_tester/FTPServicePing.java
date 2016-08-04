package es.carlosrolindez.inwall_tester;

import java.net.InetAddress;

import org.apache.commons.net.ftp.FTPClient;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import es.carlosrolindez.inwall_tester.InWallTesterActivity.ResponseReceiver;

public class FTPServicePing extends IntentService {

	public static final String PARAM_OUT_MSG = "MESSAGE";

	public FTPServicePing() {
		super("FTPServicePing");
        Log.w("FTPServicePing","Costructor");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        
        FTPClient ftpClient = null;
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        Log.e("FTPServicePing","onHandle");

        try{       
	        ftpClient = new FTPClient();
	        ftpClient.connect(InetAddress.getByName("carlosrolindez.es"));
            Log.e("FTPServicePing","Connected");

	        ftpClient.logout();
	        ftpClient.disconnect();
	        

	        broadcastIntent.putExtra(PARAM_OUT_MSG, "OK");
	        sendBroadcast(broadcastIntent);




	    }
	    catch (Exception ex)
	    {
            Log.e("FTPServicePing","Exception");
	        ex.printStackTrace();
	        broadcastIntent.putExtra(PARAM_OUT_MSG, "NOK");
	        sendBroadcast(broadcastIntent);

	    }
       
        
        
        
	}

}
