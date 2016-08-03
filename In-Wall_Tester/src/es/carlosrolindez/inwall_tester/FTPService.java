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

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class FTPService extends IntentService {

	public FTPService() {
		super("FTPService");
	}
	public FTPService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
        String name = intent.getStringExtra(Constants.DEVICE_NAME);
        String mac = intent.getStringExtra(Constants.DEVICE_MAC);
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.US).format(new Date());

        name = name + " " + date;
        
        FTPClient ftpClient = null;

        InputStream input = null;

        try{       
	        ftpClient = new FTPClient();
            Log.e("FTPService","Created");
	        ftpClient.connect(InetAddress.getByName("carlosrolindez.es"));
            Log.e("FTPService","Connected");
	        if(!ftpClient.login("carlosrolindez.es"	, "Julia2009"))
	        {
	            Log.e("FTPService","Login failed");	        	
	        	ftpClient.logout();
	            return;
	        }

	        ftpClient.changeWorkingDirectory("data/InWall Tester");
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);	          
	        input = new ByteArrayInputStream( mac.getBytes( Charset.defaultCharset() ) );
	        ftpClient.enterLocalPassiveMode();	
	        ftpClient.storeFile(name, input);	
	        input.close();
	        ftpClient.logout();
	        ftpClient.disconnect();


	    }
	    catch (Exception ex)
	    {
            Log.e("FTPService","Exception");
	        ex.printStackTrace();
	        return;
	    }
       
        
        
        
	}

}
