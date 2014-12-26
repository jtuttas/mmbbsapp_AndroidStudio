package de.mmbbs.tictactoetournament.game;

import org.json.JSONObject;

import com.google.android.gms.ads.*;


import de.mmbbs.gameserver.GameManagementActivity;
import de.mmbbs.gameserver.GameStates;
import de.mmbbs.gameserver.PlayGameListener;
import de.mmbbs.gameserver.ui.CustomDialogClass;
import de.mmbbs.gameserver.ui.CustomDialogListener;
import de.mmbbs.gameserver.ui.CustomDialogType;
import de.mmbbs.gameserver.ui.Main;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
//kommentar 2
public class Game extends GameManagementActivity implements PlayGameListener,GameListener {
	private InterstitialAd interstitial;
	Leinwand l;
	private CustomDialogClass cd;
	private static Handler ghandler; 
	private boolean firstTurn;
	private String gegner;
	private boolean abnormalStop=true;
	
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		 Log.d(Main.TAG,"GAME on create()");
		super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(de.mmbbs.R.layout.game);
        ghandler=new Handler();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            gegner= extras.getString("gegner");
            firstTurn=extras.getBoolean("start");
        } else {
            gegner= (String) savedInstanceState.getSerializable("gegner");
            firstTurn= Boolean.parseBoolean((String) savedInstanceState.getSerializable("start"));
        }
        Log.d(Main.TAG,"game onCreate() start="+firstTurn+" gegner="+gegner);
        interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId("ca-app-pub-5414170988828485/9840893458");
	    interstitial.setAdListener(new AdListener() {
	      @Override
	      public void onAdLoaded() {
    		
	      }

	      @Override
	      public void onAdFailedToLoad(int errorCode) {
	      }
	    });
	    
	    // Create ad request.
	    
	    AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice("ca-app-pub-5414170988828485/9840893458")
        .build();

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
	}
	
	 






	@Override
	protected void onResume() {
        Log.d(Main.TAG,"game onResume()");
		gc.setGameCallbacks(this, ghandler);
		super.onResume();
        l=(Leinwand) this.findViewById(de.mmbbs.R.id.gui);
        Display display = getWindowManager().getDefaultDisplay(); 
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();  // deprecated
        l.setListener(this,ghandler);
        l.init(firstTurn,gegner);
        l.reset(width,height);
	}


	@Override
	protected void onStart() {
        Log.d(Main.TAG,"game onStart()");
		super.onStart();
		if (gegner==null) {
			abnormalStop=false;
			this.finish();
		}
		

		
	}


	@Override
	public void updateLogin(JSONObject obj) {
		super.updateLogin(obj);
	}




	@Override
	protected void onStop() {
		Log.d(Main.TAG,"GAME onStop()");
		super.onStop();
		l.exit();
		gc.quitPaaring();
		gegner=null;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	 @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    		abnormalStop=false;
	    		gc.setState(GameStates.LOGGED_IN);
	    	}
	    	return super.onKeyDown(keyCode, event);
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 return super.onCreateOptionsMenu(menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	 
	
	// Invoke displayInterstitial() when you are ready to display an interstitial.
	  public void displayInterstitial() {
		gc.quitPaaring();				
		//onBackPressed();
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
	  }

	@Override
	public void showDialog(String msg) {
		cd = new CustomDialogClass(this,CustomDialogType.INFO ,msg,
				this.getResources().getString(de.mmbbs.R.string.ok),null);
		cd.setOnCustomDialog(new CustomDialogListener() {


			@Override
			public void onNegativeButton() {

			}

			@Override
			public void onPositiveButton() {
				abnormalStop=false;
				gc.setState(GameStates.LOGGED_IN);
				displayInterstitial();
				onBackPressed();	
			}
			
		});
		cd.setCancelable(false);
		cd.show();
		

	}


	
	@Override
	public void updateChat(JSONObject obj) {
		
	}


	@Override
	public void updateDisconnect() {
		Log.d(Main.TAG,"updateDiscopnnect() Game state="+gc.getState());
		this.showDialog(getResources().getString(de.mmbbs.R.string.player_disconnected));
		gc.stats(1, 1, 0);
		gc.addScore(l.getScore());

	}




	@Override
	public void updatePlay(JSONObject obj) {
		Log.d(Main.TAG,"updatePlay GameActivity");
		if (obj.optString("command").compareTo("timeout")==0) {
			this.showDialog(getResources().getString(de.mmbbs.R.string.player_timedout));			
			gc.stats(1, 1, 0);
			gc.addScore(l.getScore());
			l.setPlayerState(PlayerState.WON,PlayerState.LOST);
		}
		else if (obj.optString("command").compareTo("close")==0) {
			this.showDialog(getResources().getString(de.mmbbs.R.string.player_closed));
			gc.stats(1, 1, 0);
			l.setPlayerState(PlayerState.WON,PlayerState.LOST);
			gc.addScore(l.getScore());
		}
		else if (obj.optString("command").compareTo("play")==0 ||
				obj.optString("command").compareTo("won")==0 ||
				obj.optString("command").compareTo("penalty")==0
				) {
			int turnx=obj.optInt("current_turnx");
			int turny=obj.optInt("current_turny");
			l.moveTo(turnx,turny);			
		}
	}




	@Override
	public void setLeftPlayer(Player p) {
		TextView tv = (TextView) this.findViewById(de.mmbbs.R.id.textViewplayerLeft);
		ImageView iv = (ImageView) this.findViewById(de.mmbbs.R.id.imageViewplayerLeft);
		tv.setText(p.getName());
		iv.setImageResource(p.getIcon());
	}




	@Override
	public void setRightPlayer(Player p) {
		TextView tv = (TextView) this.findViewById(de.mmbbs.R.id.textViewplayerRight);
		ImageView iv = (ImageView) this.findViewById(de.mmbbs.R.id.imageViewplayerRight);
		tv.setText(p.getName());
		iv.setImageResource(p.getIcon());
	}




	@Override
	public void setScore(int score) {
		((TextView) this.findViewById(de.mmbbs.R.id.textView_score)).setText(Integer.toString(score));
		
	}


	@Override
	public void setProgessBar(int left, int right) {
		ProgressBar pbl = (ProgressBar) this.findViewById(de.mmbbs.R.id.progressBarleft);
		ProgressBar pbr = (ProgressBar) this.findViewById(de.mmbbs.R.id.ProgressBarright);
		pbl.setProgress(left);
		pbr.setProgress(right);
	}

	public static Handler getHandler() {
		return ghandler;
				
	}
	@Override
	public void onLogin() {
		
	}
}
