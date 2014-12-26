package de.mmbbs.four2win;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import de.mmbbs.R;


public class Main extends Activity  {
	
	public static final String TAG = "AGAME";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        
        /*
        l = new Leinwand(this);
        setContentView(l);
        l.requestFocus();
        */   
    }
    
    public void startGame(View v) {
    	Log.d(TAG, "Start Game");
    	this.startActivity(new Intent(this,Game.class));
    }
}