package de.mmbbs;

import de.mmbbs.tictactoetournament.Main;
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
			startActivity(new Intent(this,Main.class));
		}
	}
}
