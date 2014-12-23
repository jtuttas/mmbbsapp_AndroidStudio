package de.mmbbs;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


// Hier werden �nderungen gemacht!
public class Kontakt extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/**
		 Start der Activity
		 */
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kontakt);
	}
	

	/**
	 * Auf Kontakt Button geklickt
	 * @param v View des Kontaktbuttons
	 */
	public void buttonClick(View v) {
		switch (v.getId()) {

		case R.id.buttonAnruf:
			startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode("051164619811"))));
			break;
		case R.id.buttonorganisation:
			startActivity(new Intent(this,Organisation.class));
            break;
		case R.id.buttonNavigation:
	        String uri = "geo:52.320783,9.814975?q=mmbbs+expo+plaza+3+hannover&hl=de&hq=mmbbs&hnear=Expo+Plaza+3,+30539+Hannover,+Niedersachsen&t=h&z=16";
	        startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));	        
	        break;
		case R.id.buttonlehrersuche:
			if (TabActivity.dbm==null) {
				TabActivity.dbm=new DBManager(this);
			}
			if (TabActivity.dbm.getShortNames()!=null) {
				startActivity(new Intent(this,SearchTeacher.class));
			}
			else {
				Toast.makeText(this, "Keine Datenbank gefunden", Toast.LENGTH_SHORT).show();
			}
            break;
		case R.id.buttonEmail:
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			String[] recipients = new String[]{"info@mmbbs.de"}; // hier ist dann die kontaktadresse, sollte ja info@mmbbs sein
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Mail von MMBBS App");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			emailIntent.setType("text/plain");
			startActivity(Intent.createChooser(emailIntent, "Kontakt MMBBS"));
			break;
		/**
		ENDE DER ACTIVITY
		*/
		}
	}}
