package de.mmbbs.gameserver.ui;


import java.util.List;

import org.json.JSONObject;

import de.mmbbs.R;
import de.mmbbs.gameserver.GameServerApplication;
import de.mmbbs.gameserver.GameServerListener;
import de.mmbbs.gameserver.GameUserListener;

import de.mmbbs.gameserver.User;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;


@SuppressLint("NewApi")
public class Main extends Activity implements GameServerListener,GameUserListener, LoginDialogListener{
	
	public static final String TAG=Main.GAME;
	public static  String GAME="tttmmbbs";
	private GameServerApplication gs;
	private SharedPreferences pref;
	private Handler handler;
	private ProgressDialog dialog;
	private DBManager dbm;
	private CustomDialogClass cdd;
	private LoginDialog ld;
	
	public void onCreate(Bundle savedInstanceState) {
		Log.d(Main.TAG,"onCReate() Main Activity");
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
	    pref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    handler = new Handler();
	    String user = pref.getString("user", null);
	    String pw = pref.getString("password", null);
		setContentView(R.layout.connecting_game);
		FontOverride.setDefaultFont(this, "DEFAULT", "fonts/Isserley-Bold.otf");
		FontOverride.setDefaultFont(this, "MONOSPACE", "fonts/Isserley-Bold.otf");
		FontOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Isserley-Bold.otf");
		dbm = new DBManager(this, "friends.db", null, 1);
        Bundle extras = getIntent().getExtras();
        ImageView iv = (ImageView) this.findViewById(R.id.imageViewSplashScreen);
        if (extras!=null) {
            GAME = extras.getString("gamename");
            iv.setImageResource(extras.getInt("splashImage"));
            Editor e = pref.edit();
            e.putString("gamename",GAME);
            e.putInt("splashImage",extras.getInt("splashImage"));
            e.commit();
        }
        else {
            GAME = pref.getString("gamename","tttmmbbs");
            iv.setImageResource(pref.getInt("splashImage",R.drawable.tic_tac_toe_titel));
        }
        Log.d(Main.TAG," Main.oncreate() GameName="+GAME);

    }
	
	

	@Override
	protected void onResume() {
		Log.d(Main.TAG,"**Main on resume()");
		gs = (GameServerApplication) getApplication();
		gs.setUserCallbacks(this, handler);
		gs.connect("http://service.joerg-tuttas.de:8080",this,handler);
		dialog= new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(R.string.connecting));
		dialog.setMessage(this.getResources().getString(R.string.connect_to_gameserver));
		dialog.show(); 
		super.onResume();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login_resend_register_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_login:
			ld = new LoginDialog(this, R.layout.login_dialog,LoginDialogType.LOGIN);
			ld.setListener(this);
			ld.show();
			break;
		case R.id.menu_register:
			ld = new LoginDialog(this, R.layout.register_dialog,LoginDialogType.REGISTER);
			ld.setListener(this);
			ld.show();

			break;
		case R.id.menu_resend:
			ld = new LoginDialog(this, R.layout.resend_dialog,LoginDialogType.RESEND);
			ld.setListener(this);
			ld.show();
			break;
		}
		return false;
	}

	
	public void reconnect() {
		dialog= new ProgressDialog(this);
		dialog.setTitle(this.getResources().getString(R.string.connecting));
		dialog.setMessage(this.getResources().getString(R.string.connect_to_gameserver));
		dialog.show(); 
	    gs = (GameServerApplication) getApplication();
		gs.connect("http://service.joerg-tuttas.de:8080", this,handler);
    }
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Main.TAG,"MAIN onActivityResult requestCode="+requestCode);
	    switch (requestCode) {
	    case 100:
	        setResult(requestCode);
	        //gs.disconnect();
	        Log.d(Main.TAG,"onActivityResult requestCode="+requestCode);
	        this.finish();
	        break;

	    default:
	        break;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void updateLogin(JSONObject obj) {
		Log.d(TAG, "update Login Main");
		Toast.makeText(getApplicationContext(),obj.optString("message"), Toast.LENGTH_LONG).show();			
		if (obj.optBoolean("success")) {
			String user = obj.optString("user");
			String pw = obj.optString("password");
			Editor e = pref.edit();
			e.putString("user", user);
			e.putString("password", pw);
			e.commit();
			Log.d(TAG, "update Login Main PUT user="+user);
			
	    	Intent i = new Intent(this, FragmentActivity.class);
	    	startActivityForResult(i, 100);
		}
		else {
			ld = new LoginDialog(this, R.layout.login_dialog,LoginDialogType.LOGIN);
			ld.setListener(this);
			ld.show();
		}
	}



	@Override
	public void updateUsers(List<User> userlist) {
		Log.d(TAG, "update Users in Main Activity");
		for (int i=0;i<userlist.size();i++) {
			User user = userlist.get(i);
			if (dbm.isFriend(user.getName())) {
				user.setFriend(true);
			}
			else {
				user.setFriend(false);
			}
		}
		Log.d(Main.TAG,"updateUser in Main Activity userlist size="+gs.getUserList().size());


	}


	@Override
	public void updateResendLogin(JSONObject obj) {
		Toast.makeText(getApplicationContext(),obj.optString("message"), Toast.LENGTH_LONG).show();			
		if (obj.optBoolean("success")==false) {
			ld = new LoginDialog(this, R.layout.resend_dialog,LoginDialogType.RESEND);
			ld.setListener(this);
			ld.show();			
		}
		else {
			ld = new LoginDialog(this, R.layout.login_dialog,LoginDialogType.LOGIN);
			ld.setListener(this);
			ld.show();			

		}
		
	}



	@Override
	public void updateRegister(JSONObject obj) {
		Log.d(TAG, "update Register");
		if (obj.optBoolean("success")==false) {
			Toast.makeText(getApplicationContext(),obj.optString("message"), Toast.LENGTH_LONG).show();			
			ld = new LoginDialog(this, R.layout.register_dialog,LoginDialogType.REGISTER);
			ld.setListener(this);
			ld.show();			
		}
		else {
			String user =  obj.optString("user");
			String pw = obj.optString("password");
			Log.d(TAG, "update Register OK user="+user);
			
			gs.login(user, pw, Main.GAME);
		}

	}


	

	@Override
	public void updateRequest(JSONObject obj) {
		Log.d(TAG, "update Request");

	}


	

	@Override
	public void updateDisconnect() {
		Log.d(TAG, "update Disconnect");

	}


	@Override
	public void connected() {
		if (dialog!=null && dialog.isShowing()) dialog.dismiss();
	    
		String user = pref.getString("user", null);
	    String pw = pref.getString("password", null);
	    Log.d(TAG,"connected user="+user);
	    if (user==null && pw==null) {
			ld = new LoginDialog(this, R.layout.register_dialog,LoginDialogType.REGISTER);
			ld.setListener(this);
			ld.show();
	    }
	    else {
	    	gs.login(user, pw, Main.GAME);
	    }
	}


	@Override
	protected void onStop() {
		super.onStop();
		
	}



	@Override
	public void connectionError() {
		dialog.dismiss();
		cdd = new CustomDialogClass(this,CustomDialogType.ERROR , this.getResources().getString(R.string.failed_connect),
				null,this.getResources().getString(R.string.retry));
		cdd.setOnCustomDialog(new CustomDialogListener() {


			@Override
			public void onNegativeButton() {
		    	reconnect();
				
			}

			@Override
			public void onPositiveButton() {
				
			}
			
		});
		cdd.setCancelable(false);
		cdd.show();
		
	}

	private boolean register(String user,String email,String pw,String repw,String location) {
		
		if (user.length()==0) {
			Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.error_empty_username), Toast.LENGTH_LONG).show();
			return false;
		}
		else if (pw.toString().length()==0) {
			Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.error_empty_password), Toast.LENGTH_LONG).show();
			return false;
		}
		else if (pw.toString().compareTo(repw.toString())!=0) {
			Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.error_diff_password), Toast.LENGTH_LONG).show();
			return false;
		}
		else if (email.length()==0) {
			Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.error_empty_email), Toast.LENGTH_LONG).show();
			return false;
		}
		else {
			gs.register(Main.GAME, user, pw,email, location);
			return true;
		}
	}

	private boolean login(String user,String pw) {
		if (user.length()==0) {
			Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.error_empty_username), Toast.LENGTH_LONG).show();
			return false;
		}
		else if (pw.length()==0) {
			Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.error_empty_password), Toast.LENGTH_LONG).show();
			return false;
		}
		else {
			gs.login(user, pw, Main.GAME);
			return true;
		}
	}
	
	private boolean resend(String email) {
		if (email.length()==0) {
			Toast.makeText(getApplicationContext(),this.getResources().getString(R.string.error_empty_email), Toast.LENGTH_LONG).show();
			return false;
		}
		else {
			gs.sendUserData(email);
			return true;
		}
	}



	@Override
	public void onClick(View v,LoginDialogType type,LinearLayout l) {
		switch (type) {
		case LOGIN:
			switch (v.getId()) {
			case R.id.button_dialog_negative:
				ld.dismiss();
				break;
			case R.id.button_dialog_positive:
				String username = ((EditText)l.findViewById(R.id.editText_login)).getText().toString();
				String pw = ((EditText)l.findViewById(R.id.editText_password)).getText().toString();
				if (this.login(username, pw)) {					
					ld.dismiss();
				}
				break;
			case R.id.button_next:
				ld.dismiss();
				ld = new LoginDialog(this, R.layout.resend_dialog,LoginDialogType.RESEND);
				ld.setListener(this);
				ld.show();
				break;
			case R.id.button_previous:
				ld.dismiss();
				ld = new LoginDialog(this, R.layout.register_dialog,LoginDialogType.REGISTER);
				ld.setListener(this);
				ld.show();
				break;
		
		}
		break;
		case REGISTER:
			switch (v.getId()) {
			case R.id.button_dialog_negative:
				ld.dismiss();
				break;
			case R.id.button_dialog_positive:
				SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
				String location = prefs.getString("klasse", null);
				//String location = ((Spinner)l.findViewById(R.id.spinner_location)).getSelectedItem().toString();
				String username = ((EditText)l.findViewById(R.id.editText_login)).getText().toString();
				String pw = ((EditText)l.findViewById(R.id.editText_password)).getText().toString();
				String repw = ((EditText)l.findViewById(R.id.editText_repassword)).getText().toString();
				String email = ((EditText)l.findViewById(R.id.editText_email)).getText().toString();
				if (this.register(username, email, pw, repw, location)) {
					ld.dismiss();					
				}
				break;
			case R.id.button_next:
				ld.dismiss();
				ld = new LoginDialog(this, R.layout.login_dialog,LoginDialogType.LOGIN);
				ld.setListener(this);
				ld.show();				
				break;
			case R.id.button_previous:
				ld.dismiss();
				ld = new LoginDialog(this, R.layout.resend_dialog,LoginDialogType.RESEND);
				ld.setListener(this);
				ld.show();
				break;
		
		}
		break;
		case RESEND:
			switch (v.getId()) {
			case R.id.button_dialog_negative:
				ld.dismiss();
				break;
			case R.id.button_dialog_positive:
				String email = ((EditText)l.findViewById(R.id.editText_email)).getText().toString();
				if (this.resend(email)){
					ld.dismiss();
				}
				break;
			case R.id.button_next:
				ld.dismiss();
				ld = new LoginDialog(this, R.layout.register_dialog,LoginDialogType.REGISTER);
				ld.setListener(this);
				ld.show();
				break;
			case R.id.button_previous:
				ld.dismiss();
				ld = new LoginDialog(this, R.layout.login_dialog,LoginDialogType.LOGIN);
				ld.setListener(this);
				ld.show();
				break;
		
		}
		break;
		}
	}
}