package es.carlosrolindez.inwall_tester;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.KeyEvent;



public class RemoteControlReceiver extends BroadcastReceiver {
	
    private static MediaPlayer mediaPlayer=null;
    
    public RemoteControlReceiver() {
    }

	@Override
	public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
    		if (mediaPlayer==null) {
        		mediaPlayer = MediaPlayer.create(context,R.raw.inwall_sample);
            	mediaPlayer.setLooping(true);
    		}
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            Log.e("Broadcast",event.toString());
            if (KeyEvent.KEYCODE_MEDIA_PLAY == event.getKeyCode()) {
		        mediaPlayer.start();
            } else if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()) {
		        mediaPlayer.pause();
            } else if (KeyEvent.KEYCODE_MEDIA_STOP == event.getKeyCode()) {
            	mediaPlayer.stop();
            }
        }
	}

}
