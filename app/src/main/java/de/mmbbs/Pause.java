package de.mmbbs;
import java.util.Date;

import de.mmbbs.R;
import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


public class Pause extends Activity implements Runnable{
	 public MediaPlayer mp;  
	 Thread runner;
		private SharedPreferences  pref;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.pause);
    }

	@Override
	protected void onStart() {

		super.onStart();
		runner = new Thread(this);
		runner.start();
		 mp = MediaPlayer.create(getApplicationContext(), R.raw.pause);  
	     mp.start(); 
	     pref = PreferenceManager.getDefaultSharedPreferences(this);
	     if (pref.getString("email", "").compareTo("tuttas")==0) {
	    	 TabActivity.egg=true;
	            Date pause = TabActivity.getNextPause();
	            TabActivity.setAlarm(this,pause);
	     }
	     else {
	    	 Toast.makeText(this, "Easter egg deactivated", Toast.LENGTH_LONG).show();	    	 
	     }
	}

	
	
	public void run() {
		try {
			Thread.sleep(1000*60);
			this.finish();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}	
}
