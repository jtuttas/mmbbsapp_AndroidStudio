package de.mmbbs;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import de.mmbbs.tictactoetournament.FontOverride;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Activity zu meine Klasse
 * @author tuttas
 *
 */
public class MeineKlasse extends Activity {
	
	//AdView adView;
	private String klasse;
	private String klassenlehrer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		klasse = prefs.getString("klasse", null);
		if (klasse != null) {
			klassenlehrer = TabActivity.dbm.getLehrer(klasse);
		}
		FontOverride.setDefaultFont(this, "DEFAULT", "fonts/Isserley-Bold.otf");
		FontOverride.setDefaultFont(this, "MONOSPACE", "fonts/Isserley-Bold.otf");
		FontOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Isserley-Bold.otf");
		setContentView(R.layout.meineklasse);
		/*
		adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("TEST_DEVICE_ID")
        .build();
        adView.loadAd(adRequest);
        */
	}
	
	
	@Override
	protected void onStart() {

		super.onStart();
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		String klasse = prefs.getString("klasse", "");
				
		if (klasse.length()==2) {
			((Button) findViewById(R.id.button_emailKlasse)).setVisibility(View.INVISIBLE);
			((Button) findViewById(R.id.button_emailKlassenlehrer)).setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Öffnet den Stundenplan.
	 * @author Fritz, Lammers, Schwanda
	 * @param v
	 */
    public void klick_stundenplan(View v) {
    	startActivity(new Intent(this,Stundenplan.class));

    }
    
	
	/**
	 * Öffnet den Stundenplan.
	 * @author Fritz, Lammers, Schwanda
	 * @param v
	 */
    public void klick_vertretungsplan(View v) {
    	startActivity(new Intent(this,Vertretungsplan.class));
    }
    @Override
    public void onPause() {
      //adView.pause();
      super.onPause();
    }

    @Override
    public void onResume() {
      super.onResume();
      //adView.resume();
    }

    @Override
    public void onDestroy() {
      //adView.destroy();
      super.onDestroy();
      
      }
	/**
     * E-Mail an Klassenlehrer
     * @author Herden, Peguschin
     * @param v
     */
    public void klick_emailKlassenlehrer(View v) {
    	//EmailKlassenlehrer objEmailKlassenlehrer = new EmailKlassenlehrer();
    	//objEmailKlassenlehrer.main(this);
    	if(klasse == null) {
			
			//Fehlermeldung
			Toast.makeText(this, "Du musst eine Klasse hinterlegen", Toast.LENGTH_LONG).show();
			
		} 
    	else if(klassenlehrer == null) {
			
			//Fehlermeldung
			Toast.makeText(this, "Die hinterlegte Klasse konnte nicht zugeordnet werden", Toast.LENGTH_SHORT).show();
			
    	}
    	else {
    		Intent i = new Intent(this,SearchTeacher.class);
    	
    		i.putExtra("klassenlehrer",klassenlehrer );
    		startActivity(i);
    	}
    }
    public void klick_emailKlasse(View v) {
    	String klasse;
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		klasse = prefs.getString("klasse", "");
    	if (klasse!="") {
	    	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			String[] recipients = new String[]{klasse+"@mmbbs.eduplaza.de"}; // hier ist dann die kontaktadresse, sollte ja info@mmbbs sein
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mail von MMBBS App");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			emailIntent.setType("text/plain");
			startActivity(Intent.createChooser(emailIntent, "EMail an Klasse"));    
		}   
	    else {
	    	Toast.makeText(this, "Du musst eine Klasse hinterlegen", Toast.LENGTH_SHORT).show();
	    }
    }

}
