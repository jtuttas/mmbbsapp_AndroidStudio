package de.mmbbs;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.TabHost.TabSpec;

import de.mmbbs.gameserver.GCMHelper;

public class TabActivity extends android.app.TabActivity implements Loadfinished  {

    private String klasse;
	// Pausen 9:30 , 11:20, 13:10 , 15:00
	public static final int[] PAUSE_MIN = {30,20,10,0};
	public static final int[] PAUSE_STD = {9,11,13,15};
	public static boolean egg=false;
	
	public static final int NEW_VERSION=0;
	public static final int UPDATE_TEACHER_DB=1;
	public static final int UPDATE_CLASSES_DB=2;
	public static final int UPDATE_CLASSTEACHER_DB=3;
	public static final int UPDATE_FINISHED=4;
	public static int UPDATE_STATE=NEW_VERSION;
	
	private int dbVers;
	
	public static final String TAG = "mmbbsapp";
	public static String IMAGE_URL = "http://www.seminar-mediendidaktik.de/mmbbsapp/";
	public static String DB_URL = "http://service.joerg-tuttas.de/mmbbsapp/";
	private DBDownloaderTask dbtask;
	private ProgressDialog dialog;
	public static DBManager dbm;
	private SharedPreferences  pref;
	
	public static GCMHelper gcmHelper;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        
        
        pref = PreferenceManager.getDefaultSharedPreferences(this);
    	dbm = new DBManager(this);
    	klasse = pref.getString("klasse", null);
        gcmHelper = new GCMHelper(this,DB_URL+"gcm-neu.php",klasse);


    	String v = getString(R.string.version);
    	if (pref.getString("newversion2", "0.0").compareTo(v)!=0) {
    		Editor e = pref.edit();
    		e.putString("newversion2", v);
    		e.commit();
    		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    		alertDialog.setTitle("Neue Funktionen");
    		String s = getString(R.string.new_version_txt);
    		alertDialog.setMessage(s);
    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int which) {
    			alertDialog.cancel();
    			//finish();
    		}
    		});
    		//alertDialog.setIcon(R.drawable.joerg);
    		alertDialog.show();
               
    	}
        TabHost tabHost = getTabHost();
         
        // Tab for Klasse
        TabSpec mklasse = tabHost.newTabSpec("Meine Klasse");
        // setting Title and Icon for the Tab
        mklasse.setIndicator("Meine Klasse", null);
        Intent klasseIntent = new Intent(this, MeineKlasse.class);
        mklasse.setContent(klasseIntent);
         
        // Tab for Schule
        TabSpec mschule = tabHost.newTabSpec("Meine Schule");
        // setting Title and Icon for the Tab
        mschule.setIndicator("Meine Schule",null);
        Intent schuleIntent = new Intent(this, Kontakt.class);
        mschule.setContent(schuleIntent);
         
        // Tab for Spiele
        TabSpec mspiele = tabHost.newTabSpec("Spiele");
        // setting Title and Icon for the Tab
        mspiele.setIndicator(null,getResources().getDrawable(R.drawable.games_icon));


        Intent spieleIntent = new Intent(this, Spiele.class);
        mspiele.setContent(spieleIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(mklasse);
        tabHost.addTab(mschule); 
        tabHost.addTab(mspiele); 
        
      //if (UPDATE_STATE!=UPDATE_FINISHED) {
    	Log.d(TabActivity.TAG, "Aus Pref Version="+DBManager.getVersion(this));
    	dbtask = new DBDownloaderTask(this,this);
    	dbtask.execute(TabActivity.DB_URL+"index.php");
    //}
    }
	
	@Override
	protected void onStart() {
		super.onStart();

        Log.d(TabActivity.TAG,"onStart()");
    
        
        if (pref.getString("email", "").compareTo("tuttas")==0 && !egg) {
        	egg=true;
        	Toast.makeText(this, "Easter Egg aktiv", Toast.LENGTH_LONG).show();
            Date pause = getNextPause();
            setAlarm(this,pause);
        }
        
        Log.d(TabActivity.TAG,"onStart() beendet");
	}

	@Override
	protected void onResume() {

		super.onResume();
	      //adView.resume();
		gcmHelper.checkPlayServices();
	}
	@Override
    public void onPause() {
      //adView.pause();
      super.onPause();
    }

	

    @Override
    public void onDestroy() {
      //adView.destroy();
      super.onDestroy();
    }
    
    
    @Override
   	public boolean onCreateOptionsMenu(Menu menu) {
   		getMenuInflater().inflate(R.menu.mainmenu, menu);
   		return super.onCreateOptionsMenu(menu);
   	}

   	@Override
   	public boolean onOptionsItemSelected(MenuItem item) {
   		switch (item.getItemId()) {
   		case R.id.item_sync:
   			DBManager.setVersion(1, this);
   			this.deleteDatabase(DBManager.DBNAME);
   			UPDATE_STATE=NEW_VERSION;  
   			dbtask = new DBDownloaderTask(this,this);
           	dbtask.execute(TabActivity.DB_URL+"index.php");
   			break;
   			/*
   		case R.id.item_umsatz:
   			startActivity(new Intent(this,UmsatzActivity.class));
   			break;
   			*/
   		case R.id.item_einstellungen:
   			startActivity(new Intent(this,Pref.class));
   			break;
   		case R.id.item_info:
   			
   			//SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
   			//String s = prefs.getString("kennwort", "null");
   			AlertDialog.Builder aboutView = new AlertDialog.Builder(this);
   	        aboutView.setTitle("Info");
   	           //aboutView.setMessage(R.string.about_text);
   	        ScrollView sv = new ScrollView(this);
   	             
   	        LinearLayout credits = new LinearLayout(this);
   	        credits.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
   	        credits.setOrientation(LinearLayout.VERTICAL);
   	        credits.setBackgroundColor(0xffffff);
               ImageView img = new ImageView(this);
               img.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 150));
               img.setScaleType(ScaleType.CENTER_INSIDE);
               img.setImageResource(R.drawable.joerg);
               img.setBackgroundColor(0xffffff);
               credits.addView(img);
              
               TextView tv = new TextView(this);
               tv.setText(R.string.about_txt);
               tv.setGravity(Gravity.CENTER);
               credits.addView(tv);
               sv.addView(credits);
               aboutView.setView(sv);
               aboutView.setPositiveButton(R.string.btn_ok, null);
              //aboutView.setIcon(R.drawable.mine);
           
               aboutView.show();
              
               return true;
   			//Toast.makeText(this, "(c) 2011/12 by WPK \n'Softwareentwicklung f. mobile Endgeräte'", Toast.LENGTH_LONG).show();
   			//return true;

   		}
   		return super.onOptionsItemSelected(item);
   	}

    



    

    
    public void loadFinished(String s) {

		Log.d(TAG,"loadFinished() UPDATE_STATE="+UPDATE_STATE+" s="+s);
		switch (UPDATE_STATE) {
			case NEW_VERSION:
			case UPDATE_FINISHED:
				if (s!=null) {
					s=s.substring(0, s.indexOf("\r"));
					dbVers = Integer.parseInt(s);
					if (dbVers!=DBManager.getVersion(this)) {
						Log.d(TabActivity.TAG, "Versionen unterscheide sich");
						//DBManager.VERSION=vers;
						//Toast.makeText(this, "Neue Datenbank verfügbar", Toast.LENGTH_LONG).show();
						dialog= new ProgressDialog(this);
						dialog.setTitle("Loading...");
						dialog.setMessage("Updating Databases..");
						dialog.show();  
						Log.d(TAG,"Dialog anzeigen"+dialog);
						UPDATE_STATE=UPDATE_TEACHER_DB;
						dbtask = new DBDownloaderTask(this,this);
						dbtask.execute(TabActivity.DB_URL+"index.php?cmd=lehrer");
					}
					else {
						Log.d(TabActivity.TAG, "Installiere Datenbank und remote Datenbank sind identisch");
						UPDATE_STATE=UPDATE_FINISHED;
					}
				}
				else {
					Toast.makeText(this, "Kann Updateserver nicht erreichen!", Toast.LENGTH_LONG).show();
				}
				break;
			case UPDATE_TEACHER_DB:
				//dialog.dismiss();
				UPDATE_STATE=UPDATE_CLASSES_DB;
				DBManager.ADD = s.split("\\n");
				//Log.d(Main.TAG, "DB Lehrer[0]="+DBManager.ADD[0]);
				//Log.d(Main.TAG, "DB Lehrer[1]="+DBManager.ADD[1]);
				//dialog.show(this, "", "Updating Klassen. Please wait...");
				
				dbtask = new DBDownloaderTask(this,this);
				dbtask.execute(TabActivity.DB_URL+"index.php?cmd=klassen");
				
				break;
			/*
			case UPDATE_CLASSES_DB:
				//dialog.dismiss();
				DBManager.ADD_KLASSEN = s.split("\\r?\\n");
				//Log.d(Main.TAG, "DB Klassen[0]="+DBManager.ADD_KLASSEN[0]);
				//Log.d(Main.TAG, "DB Klassen[1]="+DBManager.ADD_KLASSEN[1]);
				//dialog.show(this, "", "Updating Klassenlehrer. Please wait...");
				UPDATE_STATE=UPDATE_CLASSTEACHER_DB;
				dbtask = new DBDownloaderTask(this,this);
				dbtask.execute(Main.IMAGE_URL+"klassenlehrer.db");
				break;
			*/
			case UPDATE_CLASSES_DB:
				DBManager.ADD_KLASSEN = s.split("\\n");
				//Log.d(Main.TAG, "DB Klassenlehrer[0]="+DBManager.ADD_KLASSENLEHRER[0]);
				pref = PreferenceManager.getDefaultSharedPreferences(this);
				
				
				UPDATE_STATE=UPDATE_FINISHED;
				DBManager.setVersion(dbVers,this);
				dbm=new DBManager(this);
				
				dialog.cancel();
				Log.d(TabActivity.TAG, "Dialog schließen "+dialog);

				break;
		}
	}

    public static  void setAlarm(Context context,Date pause) {

    	AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        Log.d(TabActivity.TAG, "Alarm gestellt auf "+pause.toString());
        Intent intent = new Intent(context, MyAppReciever.class);           
        PendingIntent sender = PendingIntent.getBroadcast(context, 192837, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        am.set(AlarmManager.RTC_WAKEUP, pause.getTime(), sender);
        Log.d(TabActivity.TAG, "Alarm Manager initalisiert");

	}

	public static Date getNextPause() {

		Date d = new Date();
		Log.d(TabActivity.TAG,"Heute ist "+d);
		Date pause = (Date)d.clone();
		
		int i=0;
		for (;i<PAUSE_STD.length;i++) {
			pause.setHours(PAUSE_STD[i]);
			pause.setMinutes(PAUSE_MIN[i]);
			pause.setSeconds(0);
			
			if (pause.after(d)) break;
		}
		if (i==PAUSE_STD.length) {
			Log.d(TabActivity.TAG,"Pause erst wieder nächste Tag");
			d.setHours(PAUSE_STD[0]);
			d.setMinutes(PAUSE_MIN[0]);
			d.setSeconds(0);
			d = new Date(d.getTime()+(1000*60*60*24));
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			if (c.get(Calendar.DAY_OF_WEEK)==7) {
				d = new Date(d.getTime()+(1000*60*60*24*2));				
				Log.d(TabActivity.TAG,"Morgen ist Sa. nächste Pause also erst wieder um "+d);
			}
		}
		else {
			//i--;
			d.setHours(PAUSE_STD[i]);
			d.setMinutes(PAUSE_MIN[i]);
			d.setSeconds(0);
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			Log.d(TabActivity.TAG,"DAY OF WEEK ist "+c.get(Calendar.DAY_OF_WEEK));			
			
			if (c.get(Calendar.DAY_OF_WEEK)==7) {
				d = new Date(d.getTime()+(1000*60*60*24*2));				
				d.setHours(PAUSE_STD[0]);
				d.setMinutes(PAUSE_MIN[0]);
				d.setSeconds(0);
				Log.d(TabActivity.TAG,"Heute ist Sa. nächste Pause also erst wieder um "+d);
			}
			if (c.get(Calendar.DAY_OF_WEEK)==1) {
				d = new Date(d.getTime()+(1000*60*60*24*1));				
				d.setHours(PAUSE_STD[0]);
				d.setMinutes(PAUSE_MIN[0]);
				d.setSeconds(0);
				Log.d(TabActivity.TAG,"Heute ist So. nächste Pause also erst wieder um "+d);
			}
		}
		Log.d(TabActivity.TAG, "nächste Pause ist um "+d.toString());
		return d;
	}

    
}
