package de.mmbbs;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class GcmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        //ComponentName comp = new ComponentName(context.getPackageName(),
          //      GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        
        Log.d(TabActivity.TAG,"GCM BroadcastReceiver ---> onReceive()");
        //context.startService((intent.setComponent(comp)));
        //setResultCode(Activity.RESULT_OK);
    	 Bundle extras = intent.getExtras();
    	 GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
         // The getMessageType() intent parameter must be the intent you received
         // in your BroadcastReceiver.
         String messageType = gcm.getMessageType(intent);

         SharedPreferences prefs =  PreferenceManager.getDefaultSharedPreferences(context);
 		 Boolean gcmCheck = prefs.getBoolean("PushMessage", false);
         
         if (!extras.isEmpty() && gcmCheck) {  // has effect of unparcelling Bundle
             /*
              * Filter messages based on message type. Since it is likely that GCM
              * will be extended in the future with new message types, just ignore
              * any message types you're not interested in, or that you don't
              * recognize.
              */
             if (GoogleCloudMessaging.
                     MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                 notification(context,"Send error: " + extras.toString());
             } else if (GoogleCloudMessaging.
                     MESSAGE_TYPE_DELETED.equals(messageType)) {
                 notification(context,"Deleted messages on server: " +
                         extras.toString());
             // If it's a regular GCM message, do some work.
             } else if (GoogleCloudMessaging.
                     MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                 // Post notification of received message.
                 notification(context,extras.getString("message"));
                 //Log.i(Main.TAG, "Received: " + extras.toString());
                 Log.i(TabActivity.TAG," message:"+extras.getString("message"));
             }
         }
     }
    
 public void notification(Context context,String name) {
    	
        final int id = 2;
        String ns = context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(ns);
        int icon = R.drawable.icon;
        CharSequence tickerText = name;
        long when = System.currentTimeMillis();

        Notification checkin_notification = new Notification(icon, tickerText,
                when);
       
        CharSequence contentText = name;

            Intent notificationIntent = new Intent(context, TabActivity.class);
            notificationIntent.setFlags(  Intent.FLAG_ACTIVITY_SINGLE_TOP);
            
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, 0);
            checkin_notification.setLatestEventInfo(context, "MMBBSapp",
                    contentText, contentIntent);
            checkin_notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
            notificationManager.notify(id, checkin_notification);
			
    }
}