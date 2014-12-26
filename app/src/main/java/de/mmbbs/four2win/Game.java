package de.mmbbs.four2win;

import de.mmbbs.R;
import de.mmbbs.four2win.CustomDialogClass;
import de.mmbbs.four2win.CustomDialogListener;
import de.mmbbs.four2win.CustomDialogType;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
//kommentar 2
public class Game extends Activity implements GameListener, InputPlayerDialogListener {

	private static Handler handler;
	Leinwand l;
	private de.mmbbs.four2win.CustomDialogClass cd;
	private InputPlayerDialog ld;
	private SharedPreferences pref;
	
	 @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.gamemenu, menu);
		 return super.onCreateOptionsMenu(menu);
	}

	 
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		l.onStop();
		l.exit();
	}
	 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.help:
			Log.d(Main.TAG,"Help!!");
			return true;
		case R.id.back:
			finish();
			return true;
		}
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(Main.TAG," **** Game onCreate()");
		//setContentView(R.layout.main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_four2win);
        handler = new Handler();
        l=(Leinwand) this.findViewById(R.id.gui);
        l.setGameListener(this);
        l.reset(this.getWindowManager().getDefaultDisplay().getWidth(),this.getWindowManager().getDefaultDisplay().getHeight());    
	}


	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		Log.d(Main.TAG," **** Game onStart()");
		String p1=null,p2=null;
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		p1 = pref.getString("player1", "Player1");
		p2 = pref.getString("player2", "Player2");
		ld = new InputPlayerDialog(this, R.layout.set_player_dialog,p1,p2,l.getGameState());
		ld.setListener(this);
		ld.show();

		
	}


	@SuppressLint("NewApi")
	@Override
	public void showDialog(String msg) {
		// TODO Auto-generated method stub
		cd = new CustomDialogClass(this,CustomDialogType.INFO ,msg,
				this.getResources().getString(R.string.ok),null);
		cd.setOnCustomDialog(new CustomDialogListener() {


			@Override
			public void onNegativeButton() {

			}

			@Override
			public void onPositiveButton() {
				onBackPressed();
				
			}
			
		});
		cd.setCancelable(false);
		cd.show();
		

	}


	@Override
	public void setLeftPlayer(Player p) {
		// TODO Auto-generated method stub
	}


	@Override
	public void setRightPlayer(Player p) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setScore(int scoreLeft,int scoreRight) {
		TextView tv = (TextView) this.findViewById(R.id.textView_score1);
		tv.setText(Integer.toString(scoreLeft));
		tv = (TextView) this.findViewById(R.id.textView_score2);
		tv.setText(Integer.toString(scoreRight));
		
	}


	@Override
	public void setProgessBar(int left, int right) {
		// TODO Auto-generated method stub
		
	}


	public static Handler getHandler() {
		// TODO Auto-generated method stub
		return handler;
	}


	@Override
	public void onClick(View v, LinearLayout mainlayout) {
		EditText et = (EditText) mainlayout.findViewById(R.id.editText_player1);
		//Log.d(Main.TAG,"l="+l+" player1="+l.player1.toString()+" et="+et);
		l.player1.setName(et.getText().toString());
		et = (EditText) mainlayout.findViewById(R.id.editText_player2);
		l.player2.setName(et.getText().toString());
		//e.putString("player2", et.getText().toString());
		//e.commit();
		TextView tv = (TextView) this.findViewById(R.id.textViewplayerLeft);
		tv.setText(l.getPlayer(1).getName());
		tv = (TextView) this.findViewById(R.id.textViewplayerRight);
		tv.setText(l.getPlayer(2).getName());
		l.setState(Leinwand.PLAY);
		ld.dismiss();
		
	}
	
	
}
