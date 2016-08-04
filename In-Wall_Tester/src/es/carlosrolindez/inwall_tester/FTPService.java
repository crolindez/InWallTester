package es.carlosrolindez.inwall_tester;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import es.carlosrolindez.inwall_tester.InWallTesterActivity.ResponseReceiver;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class FTPService extends IntentService {
	
	public static final String PARAM_OUT_MSG = "MESSAGE";

	public FTPService() {
		super("FTPService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String mode = intent.getStringExtra(Constants.FTP_MODE);
        String name;
        String mac;
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date());
      
        FTPClient ftpClient = null;
        InputStream input = null;
        
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
 
        try{       
	        ftpClient = new FTPClient();
            Log.e("FTPService","Created");
	        ftpClient.connect(InetAddress.getByName("carlosrolindez.es"));
            Log.e("FTPService","Connected");
	        if(!ftpClient.login("carlosrolindez.es"	, "Julia2009"))
	        {
	            Log.e("FTPService","Login failed");	        	
	        	ftpClient.logout();
		        if (mode.equals("PING")) {
		        	broadcastIntent.putExtra(PARAM_OUT_MSG, "NOK");
			        sendBroadcast(broadcastIntent);
			    }
	            return;
	        }

	        ftpClient.changeWorkingDirectory("data/InWall Tester");
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);	   
	        if (mode.equals("PING")) {
	            name = "PING " + date;
	            mac = " ";
        	
	        } else {
	            name = intent.getStringExtra(Constants.DEVICE_NAME) + " " + date;
	            mac = intent.getStringExtra(Constants.DEVICE_MAC);
	        }
	        input = new ByteArrayInputStream( mac.getBytes( Charset.defaultCharset() ) );
	        ftpClient.enterLocalPassiveMode();	
	        ftpClient.storeFile(name, input);	
	        input.close();
	        ftpClient.logout();
	        ftpClient.disconnect();
	        if (mode.equals("PING")) {
	        	broadcastIntent.putExtra(PARAM_OUT_MSG, "OK");
		        sendBroadcast(broadcastIntent);
	        }


	    }
	    catch (Exception ex)
	    {
            Log.e("FTPService","Exception");
	        ex.printStackTrace();
	        if (mode.equals("PING")) {
	        	broadcastIntent.putExtra(PARAM_OUT_MSG, "NOK");
		        sendBroadcast(broadcastIntent);
	        }
	        return;
	    }
       
        
        
        
	}
/*public class FTPServicePing extends IntentService {

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
*/
}
