package de.mmbbs.gameserver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.games.Games;

import org.json.JSONObject;

import de.mmbbs.R;
import de.mmbbs.four2win.SoundPlayer;
import de.mmbbs.gameserver.ui.CustomDialogClass;
import de.mmbbs.gameserver.ui.CustomDialogListener;
import de.mmbbs.gameserver.ui.CustomDialogType;
import de.mmbbs.gameserver.ui.FontOverride;
import de.mmbbs.gameserver.ui.Main;

public abstract class GameManagementActivity extends BaseGameActivity implements
		GameServerListener,View.OnClickListener {

	protected Handler handler;
	protected static GameServerApplication gc;
	public static CustomDialogClass cdd;
	public GameManagementActivity instance;
	private int idDialog;
    public static SoundPlayer soundPlayer;

	public CustomDialogClass getCustomDialog() {
		if (cdd == null) {
			cdd = new CustomDialogClass(this);
		}
		return cdd;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FontOverride.setDefaultFont(this, "DEFAULT", "fonts/Isserley-Bold.otf");
		FontOverride.setDefaultFont(this, "MONOSPACE",
				"fonts/Isserley-Bold.otf");
		FontOverride.setDefaultFont(this, "SANS_SERIF",
				"fonts/Isserley-Bold.otf");
		handler = new Handler();
		gc = (GameServerApplication) getApplication();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		instance = this;

	}

	@Override
	protected void onResume() {
		super.onResume();
		gc.setServerCallbacks(this, handler);
		Log.d(Main.TAG,
				"onResume GameManagementActivity connected=" + gc.isConnected()
						+ " state=" + gc.getState());
		if (!gc.isConnected()) {
			gc.connect("http://service.joerg-tuttas.de:8080", this, handler);
		} else {
			if (gc.getState() == GameStates.CONNECTED) {
				SharedPreferences pref = PreferenceManager
						.getDefaultSharedPreferences(this);
				String user = pref.getString("user", null);
				String pw = pref.getString("password", null);
				gc.login(user, pw, Main.GAME);
			}
			if (gc.getState() == GameStates.LOGGED_IN
					|| gc.getState() == GameStates.REQUEST_PENDING) {
                cdd=null;
				onLogin();
			}
		}

	}

	@Override
	protected void onStart() {
		Log.d(Main.TAG, "onStart() GameManagement Activity");
		super.onStart();
        soundPlayer=new SoundPlayer(this);
	}

	@Override
	protected void onStop() {
		Log.d(Main.TAG, "GameManagementActivity onStop()");
		super.onStop();

		if (this.getCustomDialog().isShowing()) {
			// Dieser Client wurde angefragt
			if (gc.getPendingRequestToPlayer().compareTo(gc.getUser()) == 0) {
				gc.request(gc.getPendingRequestFromPlayer(), "request_rejected",Main.GAME);
			} else {
				gc.request(gc.getPendingRequestToPlayer(), "cancelrequest",Main.GAME);
			}
			this.getCustomDialog().dismiss();
			gc.setPendingrequest(null, null, 0);
		}

	}

	public static GameServerApplication getGameServer() {
		return gc;
	}

	/**
	 * GameServer Schnittstelle
	 */

	@Override
	public void updateLogin(JSONObject obj) {
		Log.d(Main.TAG, "update Login Game Management Activity");

		if (obj.optBoolean("success")) {
			String user = obj.optString("user");
			String pw = obj.optString("password");
            int score = obj.optInt("score");
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(this);
			Editor e = pref.edit();
			e.putString("user", user);
			e.putString("password", pw);
            e.putInt("score",score);
			e.commit();
			onLogin();

		}

	}

	@Override
	public void updateResendLogin(JSONObject obj) {

	}

	@Override
	public void updateRegister(JSONObject obj) {

	}

	@Override
	public void updateRequest(final JSONObject obj) {

		Log.d(Main.TAG, "!!update request in GameManagement Activity command="
				+ obj.optString("command"));
		if (obj.optString("command").compareTo("request") == 0) {
            cdd=null;
            soundPlayer.play(SoundPlayer.Sounds.REQUEST);
			this.showRequestDialog(obj.optString("from_player"),obj.optString("game"));

		} else if (obj.optString("command").compareTo("request_acknowledged") == 0) {
			Log.d(Main.TAG,
					"---> request acknowladged in GameManagement Activity");
			this.getCustomDialog().dismiss();
			gc.setPendingrequest(null, null, 0);
			startGame(false, obj.optString("from_player"),obj.optString("game"));

		} else if (obj.optString("command").compareTo("request_finished") == 0) {
			Log.d(Main.TAG,
					"-----> request finished in GameManagement Activity");
			this.getCustomDialog().dismiss();
			gc.setPendingrequest(null, null, 0);
			startGame(true, obj.optString("from_player"),obj.optString("game"));
		} else if (obj.optString("command").compareTo("request_rejected") == 0) {
			this.getCustomDialog().dismiss();
			gc.setPendingrequest(null, null, 0);
			Toast.makeText(
					getApplicationContext(),
					"Request rejected from player '"
							+ obj.optString("from_player") + "'!",
					Toast.LENGTH_LONG).show();
		} else if (obj.optString("command").compareTo("cancelrequest") == 0) {
			this.getCustomDialog().dismiss();
			Log.d(Main.TAG, "!!  Dialog ausgeschaltet!");
			gc.setPendingrequest(null, null, 0);

			Toast.makeText(
					getApplicationContext(),
					"Request canceled from player '"
							+ obj.optString("from_player") + "'",
					Toast.LENGTH_LONG).show();
		}

	}

	public void showRequestDialog(final String from, final String game) {

		this.getCustomDialog().setType(CustomDialogType.INFO);
		this.getCustomDialog().setContent("Request from player '" + from + "' for game "+game);
		this.getCustomDialog().setPositiveMsg(
				this.getResources().getString(R.string.ok));
		this.getCustomDialog().setNegativeMsg(
				this.getResources().getString(R.string.reject));
		this.getCustomDialog().setOnCustomDialog(new CustomDialogListener() {

			@Override
			public void onNegativeButton() {
				gc.request(from, "request_rejected",game);
				Log.d(Main.TAG, "Request rejected!");
				gc.setPendingrequest(null, null, 0);

			}

			@Override
			public void onPositiveButton() {
				getCustomDialog().dismiss();
				getCustomDialog().setType(CustomDialogType.INFO);
				getCustomDialog().setContent("Wait for partner ");
				getCustomDialog().setPositiveMsg(null);
				getCustomDialog().setNegativeMsg(null);
				getCustomDialog().setCancelable(false);
				getCustomDialog().show();
				gc.request(from, "request_acknowledged",game);
			}

		});
		getCustomDialog().setCancelable(false);
		gc.setPendingrequest(from, gc.getUser(),
				GameServerApplication.REQUESTED);
		getCustomDialog().show();
	}

	private void startGame(boolean turn, String gegner,String game) {

        Intent i=null;
        if (game.compareTo("tttmmbbs")==0) {
            i = new Intent(this, de.mmbbs.tictactoetournament.game.Game.class);
        }
        else if (game.compareTo("four2win")==0) {
            i = new Intent(this, de.mmbbs.four2win.Game.class);
        }
        if (i!=null) {
            i.putExtra("start", turn).putExtra("gegner", gegner);
            startActivity(i);
        }

	}

	@Override
	public void updateDisconnect() {
		Toast.makeText(getApplicationContext(), "updateDisconnect()",
				Toast.LENGTH_LONG).show();
		this.finish();// try activityname.finish instead of this
		setResult(100);

	}

	@Override
	public void connected() {
		if (gc.getState() != GameStates.LOGGED_IN) {
			SharedPreferences pref = PreferenceManager
					.getDefaultSharedPreferences(this);
			String user = pref.getString("user", null);
			String pw = pref.getString("password", null);
			gc.login(user, pw, Main.GAME);
		}

	}

	@Override
	public void connectionError() {
		Toast.makeText(getApplicationContext(), "ConnectionError()",
				Toast.LENGTH_LONG).show();
		getCustomDialog().setType(CustomDialogType.ERROR);
		getCustomDialog().setContent(
				this.getResources().getString(R.string.failed_connect));
		getCustomDialog().setPositiveMsg(null);
		getCustomDialog().setNegativeMsg(
				this.getResources().getString(R.string.retry));
		getCustomDialog().setOnCustomDialog(new CustomDialogListener() {

			@Override
			public void onNegativeButton() {
				reconnect();

			}

			@Override
			public void onPositiveButton() {

			}

		});
		getCustomDialog().setCancelable(false);
		getCustomDialog().show();
	}

	public void reconnect() {
		Log.d(Main.TAG, "**** GameManegementActivity reconnect!");
		startActivity(new Intent(this, Main.class));
		this.finish();
	}

    public void onSignInSucceeded() {
        Log.d(Main.TAG,"onSiginSucceeded()");
        //findViewById(R.id.signinbutton).setVisibility(View.GONE);
        //findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onSignInFailed() {
        Log.d(Main.TAG,"onSiginFailed()");
        //findViewById(R.id.signinbutton).setVisibility(View.VISIBLE);
        //findViewById(R.id.sign_out_button).setVisibility(View.GONE);
    }



    @Override
    public void onClick(View view) {

    }

    public abstract void onLogin();

    public abstract void onButtonClick(View v);
}
