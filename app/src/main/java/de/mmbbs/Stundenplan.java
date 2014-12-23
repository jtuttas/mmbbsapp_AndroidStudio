package de.mmbbs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.google.android.gms.ads.*;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import de.mmbbs.gameserver.GameServerApplication;
import de.mmbbs.gameserver.GameServerApplication.TrackerName;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

/**
 * Klasse zu Stundenplan / Vertretungsplan.
 * @author Fritz, Lammers, Schwanda
 *
 */
public class Stundenplan extends Activity   {
	static final String[] weekdays = new String[] {"Sa.","So.","Mo.","Di.","Mi","Do.","Fr.","Sa."};
	/* Hier wird die aktuell ausgewaehlte Woche abgespeichert. */
	GregorianCalendar gc;
	int week;
	boolean errorOccured=false;
	Stundenplan instance;
	boolean tabelle=true;
	  private InterstitialAd interstitial;
	/* Standardwert fuer den Vertretungsplan ist false */
	private boolean vertretungsplan=false;
	private boolean displayWerbungFirst;
	
	/**
	 * Erstellt ein neues Objekt "Stundenplan".
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (vertretungsplan) {
			setContentView(R.layout.vertretungsplan);
		}
		else {
			setContentView(R.layout.stundenplan);
		}
		
		/* Kalenderwoche abrufen */
		gc = new GregorianCalendar();
		gc.setTime(new Date());
		gc.setFirstDayOfWeek(GregorianCalendar.MONDAY);
		week=gc.get(GregorianCalendar.WEEK_OF_YEAR);
		
		/* MMBBS-WebViewClient laden */
		final WebView webV = (WebView) findViewById(R.id.webviewPage);
		webV.setWebViewClient(new WebViewClientMmbbs(false,"Für diese Woche sind leider keine Daten hinterlegt.",this));

		/* Aktuelle Seite aufrufen */
		openPage();
		instance=this;
		if (vertretungsplan) {
			Spinner spinner = (Spinner) findViewById(R.id.spinner1);
			List<String> list = new ArrayList<String>();
			list.add("Tabelle");
			list.add("Liste");
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(dataAdapter);
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
	
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
					if (parent.getItemIdAtPosition(pos)==0) {
						tabelle=true;
					}
					else {
						tabelle=false;
					}
					openPage();
				  }
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

					
				}
				
			});
		}
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		Calendar c = Calendar.getInstance(); 
		int dayOfYear = c.get(Calendar.DAY_OF_YEAR);
		if (pref.getInt("werbung", -1) != dayOfYear ) {
			displayWerbungFirst=true;
		}
		else {
			displayWerbungFirst=false;
		}
		  // Create the interstitial.
	    interstitial = new InterstitialAd(this);
	    interstitial.setAdUnitId("ca-app-pub-5414170988828485/3752490655");
	 // Set the AdListener.
	    interstitial.setAdListener(new AdListener() {
	      @Override
	      public void onAdLoaded() {
	        Log.d(TabActivity.TAG, "onAdLoaded");
	        if (displayWerbungFirst) {
	        	displayInterstitial();
	        }
    		
	      }

	      @Override
	      public void onAdFailedToLoad(int errorCode) {
	        Log.d(TabActivity.TAG, "Failed to load Ads");
	      }
	    });
	    
	    // Create ad request.
	    
	    AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice("ca-app-pub-5414170988828485/3752490655")
        .build();

	    // Begin loading your interstitial.
	    interstitial.loadAd(adRequest);
   }
	
	 // Invoke displayInterstitial() when you are ready to display an interstitial.
	  public void displayInterstitial() {
		Log.d(TabActivity.TAG," Display Integrials");
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
	  }



	/**
	 * Gibt die in der Datenbank gespeicherte Klassenhinterlegung zu der jeweiligen Klasse aus.
	 * @return
	 */
	private final String getKlassenhinterlegung() {
		/* Sucht den in den Einstellungen hinterlegten Klassennamen. */
		String klasse;
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		klasse = prefs.getString("klasse", "");
		/* wandelt alle Kleinbuchstaben in Grossbuchstaben um. */
		klasse=klasse.toUpperCase();
		
		/* Prüfen ob ein Klassenname hinterlegt ist. */
		if(klasse.length()==0) {
			/* Klasse nicht hinterlegt */
			Toast.makeText(this, "Bitte hinterlege in den Einstellungen zuerst deinen Klassennamen.", Toast.LENGTH_LONG).show();
			errorOccured=true;
			return "";
		}
		else {
			/* Sucht zu dem Klassennamen den zugehoerigen Link. */
			
			if (TabActivity.dbm==null) {
				
				TabActivity.dbm=new DBManager(this);
			}
			DBManager dbm = TabActivity.dbm;
			
			if (vertretungsplan) {
				/* SQL-Output Vertretungsplan ausgeben. */
				final String sqlErgebnis=dbm.getVertretungsplanLink(klasse);
				
				if (sqlErgebnis.length()==0) {
					/* Zu dem Klassennamen wurden keine brauchbaren Informationen gefunden. */
					Toast.makeText(this, "Leider sind zu der Klasse \""+klasse+"\" keine Informationen zu dem Vertretungsplan hinterlegt.", Toast.LENGTH_LONG).show();
					errorOccured=true;
				}
				
				return sqlErgebnis;
				
			}
			else {
				/* SQL-Output Stundenplan ausgeben. */
				final String sqlErgebnis=dbm.getStundenplanLink(klasse);
				
				if (sqlErgebnis.length()==0) {
					/* Zu dem Klassennamen wurden keine brauchbaren Informationen gefunden. */
					Toast.makeText(this, "Leider sind zu der Klasse \""+klasse+"\" keine Informationen zu dem Stundenplan hinterlegt.", Toast.LENGTH_LONG).show();
					errorOccured=true;
				}
				
				return sqlErgebnis;
			}
		}
	}
	
	private final String getLehrerhinterlegung() {
		/* Sucht den in den Einstellungen hinterlegten Klassennamen. */
		String lehrer;
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		lehrer = prefs.getString("klasse", "");
		/* wandelt alle Kleinbuchstaben in Grossbuchstaben um. */
		lehrer=lehrer.toUpperCase();
		
		/* Prüfen ob ein Klassenname hinterlegt ist. */
		if(lehrer.length()==0) {
			/* Klasse nicht hinterlegt */
			Toast.makeText(this, "Bitte hinterlege in den Einstellungen zuerst deinen Klassennamen.", Toast.LENGTH_LONG).show();
			errorOccured=true;
			return "";
		}
		else {
			/* Sucht zu dem Klassennamen den zugehoerigen Link. */
			
			if (TabActivity.dbm==null) {
				
				TabActivity.dbm=new DBManager(this);
			}
			DBManager dbm = TabActivity.dbm;
			
			if (vertretungsplan) {
				/* SQL-Output Vertretungsplan ausgeben. */
				final String sqlErgebnis=dbm.getLehrerVertretungsplanLink(lehrer);
				
				if (sqlErgebnis.length()==0) {
					/* Zu dem Klassennamen wurden keine brauchbaren Informationen gefunden. */
					Toast.makeText(this, "Leider sind zu der Klasse \""+lehrer+"\" keine Informationen zu dem Vertretungsplan hinterlegt.", Toast.LENGTH_LONG).show();
					errorOccured=true;
				}
				
				return sqlErgebnis;
				
			}
			else {
				/* SQL-Output Stundenplan ausgeben. */
				final String sqlErgebnis=dbm.getLehrerStundenplanLink(lehrer);
				
				if (sqlErgebnis.length()==0) {
					/* Zu dem Klassennamen wurden keine brauchbaren Informationen gefunden. */
					Toast.makeText(this, "Leider sind zu der Klasse \""+lehrer+"\" keine Informationen zu dem Stundenplan hinterlegt.", Toast.LENGTH_LONG).show();
					errorOccured=true;
				}
				
				return sqlErgebnis;
			}
		}
	}
	/**
	 * Ändert das Datum auf die in <i>week</i> hinterlegte Kalenderwoche und setzt das Datum des
	 * Montags in der Woche als Titel.
	 */
	private final void changeDate() {
		final SimpleDateFormat dateFormatter = new SimpleDateFormat ("dd.MM.yyyy");

		//gc.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
		//gc.set(GregorianCalendar.WEEK_OF_YEAR, week);
		//gc.set(GregorianCalendar.YEAR, GregorianCalendar.getInstance().get(GregorianCalendar.YEAR));
	    
		final String dateOutput = weekdays[gc.get(Calendar.DAY_OF_WEEK)]+" "+dateFormatter.format(gc.getTime());
		
		final TextView lblDate = (TextView) findViewById(R.id.lblDate);
		lblDate.setText(dateOutput);
	}
	
	/**
	 * Öffnet die geforderte Internetseite im WebView.
	 */
	private final void openPage() {
		errorOccured=false;
		/* Ändert das Datum */
		changeDate();
		
		/* Die Wochenzahl muss zweistellig sein. */
		
		
		String url;
		
		if (vertretungsplan) {
			/* Vertretungsplan-URL */
			
			url=getVetretungsplanURL();
		}
		else {
			
			url=getStundenplanURL();
			
		}
		
		if (!errorOccured) {
			/* Es sind keine Fehler bei der URL-Generierung aufgetreten. Die Seite darf angezeigt werden. */
			final WebView webV = (WebView) findViewById(R.id.webviewPage);
			/*
			 * Anfangszoom,Plus-Minus-Zoom-Anzeige,Touchfunktion "Doppeltipp auf WebView -> Zoom"
			 */
			webV.setInitialScale(1); // 1 = 1% Zoom, 100 = 100% Zoom, 150 = 150% Zoom
			webV.getSettings().setLoadWithOverviewMode(true);
			webV.getSettings().setUseWideViewPort(true);
			webV.getSettings().setBuiltInZoomControls(true);
			
			/* Da ein Redirect nicht erkannt wird, wenn das Ziel des Redirects gerade geladen ist, wird dem WebView
			 * einmal ein leerer Content uebermittelt. */
			//webV.loadUrl("");
			/* URL laden */
			webV.loadUrl(url);
			
		}
		else {
			/* Es sind Fehler aufgetreten, wie bspw. keine Hinterlegung des Klassennamens. Die Activity wird verlassen. */
			this.finish();
		}
	}
    

	private String getStundenplanURL() {
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		String klasse = prefs.getString("klasse", "");
		
		
		String wochenzahl=""+week;
		if (wochenzahl.length()==1) wochenzahl="0"+wochenzahl;
		
		String stundenplanURL;
		if (klasse.length()<=3) {
			stundenplanURL="http://stundenplan.mmbbs.de/plan1011/lehrkraft/plan/";
			stundenplanURL += wochenzahl +"/t/";
			stundenplanURL += getLehrerhinterlegung();
			stundenplanURL += ".htm";
		}
		else {
			stundenplanURL="http://stundenplan.mmbbs.de/plan1011/klassen/";			
			stundenplanURL += wochenzahl +"/c/";
			stundenplanURL += getKlassenhinterlegung();
			stundenplanURL += ".htm";
		}
		//Anhängen der Kalenderwoche
		Log.d(TabActivity.TAG,"rufe URL:"+stundenplanURL);
		return stundenplanURL;
	}

	private String getVetretungsplanURL() {
		SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(this);
		String klasse = prefs.getString("klasse", "");
		
		String wochenzahl=""+week;
		if (wochenzahl.length()==1) wochenzahl="0"+wochenzahl;
		if (tabelle) {
			String vertretungsplanURL;
			if (klasse.length()<=3) {
				vertretungsplanURL ="http://stundenplan.mmbbs.de/plan1011/ver_leh/";
				//Anhängen der Kalenderwoche
				vertretungsplanURL += wochenzahl +"/t/";
				vertretungsplanURL += getLehrerhinterlegung();
				vertretungsplanURL += ".htm";								
			}
			else {
				vertretungsplanURL ="http://stundenplan.mmbbs.de/plan1011/ver_kla/";
				//Anhängen der Kalenderwoche
				vertretungsplanURL += wochenzahl +"/c/";
				vertretungsplanURL += getKlassenhinterlegung();
				vertretungsplanURL += ".htm";				
			}
			Log.d(TabActivity.TAG,"rufe URL:"+vertretungsplanURL);
			return vertretungsplanURL;
		}
		else {
			String vertretungsplanURL;
			if (klasse.length()<=3) {
				// stundenplan.mmbbs.de/plan1011/ver_leh/31/v/v00001.htm
				vertretungsplanURL="http://stundenplan.mmbbs.de/plan1011/ver_leh/";
				vertretungsplanURL += wochenzahl +"/v/";
				String h = getLehrerhinterlegung();
				h=h.replace("t", "v");
				vertretungsplanURL +=h; 
				vertretungsplanURL += ".htm";
			}
			else {
				vertretungsplanURL="http://stundenplan.mmbbs.de/plan1011/ver_kla/";
				vertretungsplanURL += wochenzahl +"/w/";
				String h = getKlassenhinterlegung();
				h=h.replace("c", "w");
				vertretungsplanURL +=h; 
				vertretungsplanURL += ".htm";
			}

			
			Log.d(TabActivity.TAG,"rufe URL:"+vertretungsplanURL);
			return vertretungsplanURL;
			
		}
	}

	/**
	 * Zeigt die vorherige Woche im WebView an.
	 * @param v
	 */
    public void klick_btnWeekBefore(View v) {
    	gc.add(Calendar.WEEK_OF_YEAR, -1);
    	week = gc.get(Calendar.WEEK_OF_YEAR);
    	openPage();
    }

	/**
	 * Zeigt die nachfolgende Woche im WebView an.
	 * @param v
	 */
    public void klick_btnWeekBehind(View v) {
    	gc.add(Calendar.WEEK_OF_YEAR, 1);
    	week = gc.get(Calendar.WEEK_OF_YEAR);
    	openPage();
    }

    public void klick_btnShare(View v) {
    	String uri;
    	if (vertretungsplan) {
    		uri = this.getVetretungsplanURL();
    	}
    	else {
    		uri = this.getStundenplanURL();
    	}
    	Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, uri);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Vertretungsplan");
        startActivity(Intent.createChooser(intent, "Share"));
        //startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));	
    }

    
    /**
     * Setter des Attributs <i>vertretungsplan</i>
     * @param vertretungsplan <br/> --> <b><i>false</i></b>: Die Stundenplanseite wird angezeigt.<br/> --> <b><i>true</i></b>: Die Vertretungsplanseite wird angezeigt.
     */
    protected void setVertretungsplan(boolean vertretungsplan) {
    	this.vertretungsplan=vertretungsplan;
    }

    // test
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Get tracker.
            Tracker t = ( (GameServerApplication) this.getApplication()).getTracker(TrackerName.APP_TRACKER);

            // Set screen name.
            // Where path is a String representing the screen name.
            t.setScreenName("stundenplan");

            // Send a screen view.
            t.send(new HitBuilders.AppViewBuilder().build());
    		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    		Calendar c = Calendar.getInstance(); 
    		int dayOfYear = c.get(Calendar.DAY_OF_YEAR);

            	displayInterstitial();
    			instance.finish();

               		

    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }

	

}
