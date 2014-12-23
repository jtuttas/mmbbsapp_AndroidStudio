package de.mmbbs;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyAppReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TabActivity.TAG,"**Alarm Manager hat angeschalten");
		Intent i=new Intent(context, Pause.class);
	    
	    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    
	    context.startActivity(i);
	}
}