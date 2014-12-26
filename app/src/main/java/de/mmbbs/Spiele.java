package de.mmbbs;

import de.mmbbs.gameserver.ui.Main;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

public class Spiele extends Activity {

	private String klasse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.spiele);
		/*
		adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("TEST_DEVICE_ID")
        .build();
        adView.loadAd(adRequest);
        */
	}
	
	public void klick_ttt(View v) {
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		klasse = prefs.getString("klasse", null);
		if (klasse==null) {
			Toast.makeText(this, "Du musst eine Klasse hinterlegen", Toast.LENGTH_LONG).show();
		}
		else {
            Intent i = new Intent(this,Main.class);
            i.putExtra("splashImage",R.drawable.tic_tac_toe_titel);
            i.putExtra("gamename","tttmmbbs");
            startActivity(i);
		}
	}
    public void klick_four2win(View v) {
        SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
        klasse = prefs.getString("klasse", null);
        if (klasse==null) {
            Toast.makeText(this, "Du musst eine Klasse hinterlegen", Toast.LENGTH_LONG).show();
        }
        else {
            Intent i = new Intent(this,Main.class);
            i.putExtra("splashImage",R.drawable.four2win_titel);
            i.putExtra("gamename","four2win");
            startActivity(i);
        }
    }
}
