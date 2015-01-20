package de.mmbbs;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.mmbbs.four2win.SoundPlayer;
import de.mmbbs.gameserver.ui.FragmentActivity;
import de.mmbbs.gameserver.ui.Main;


public class GcmBroadcastReceiver extends BroadcastReceiver {

    SoundPlayer sound;
    @Override
    public void onReceive(Context context, Intent intent) {
        sound = new SoundPlayer(context);
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
                 displayNotification(context,"Send error: " + extras.toString(),null);
             } else if (GoogleCloudMessaging.
                     MESSAGE_TYPE_DELETED.equals(messageType)) {
                 displayNotification(context,"Deleted messages on server: " +
                         extras.toString(),null);
             // If it's a regular GCM message, do some work.
             } else if (GoogleCloudMessaging.
                     MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                 // Post notification of received message.
                 String msg = extras.getString("message");
                 String msgText=msg;
                 try {
                     JSONObject jo = new JSONObject(msg);
                     String from_player=jo.getString("from_player");
                     String game=jo.getString("game");
                     String command=jo.getString("command");
                     if (command.compareTo("request")==0) {
                         Log.d(Main.TAG," command=request");
                         msgText="Spieler '"+from_player+"' will mit Dir\ndas Spiel '"+game+"' spielen!";
                         sound.play(SoundPlayer.Sounds.REQUEST);
                         displayNotification(context, msgText,jo);
                     }
                     else if (command.compareTo("cancelrequest")==0) {
                         Log.d(Main.TAG," command=cancelrequest");
                         NotificationManager notificationManager = (NotificationManager) context.getSystemService(Application.NOTIFICATION_SERVICE);
                         notificationManager.cancel(2);

                     }
                     else displayNotification(context, msgText,jo);
                 } catch (JSONException e) {
                     Log.d(Main.TAG," KEIN JSON");
                     e.printStackTrace();
                     displayNotification(context, msgText,null);
                 }


                 Log.i(TabActivity.TAG," GCMBroadcastReceiver message:"+extras.getString("message"));
             }
         }
     }

    private void displayNotification(Context context, String msgText, JSONObject obj) {
        Intent intent = new Intent(context,FragmentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (obj!=null) {
            intent.putExtra("command", obj.optString("command", "unknown"));
            intent.putExtra("from_player", obj.optString("from_player", "unknown"));
            intent.putExtra("game", obj.optString("game"));
        }
        PendingIntent pIntent = PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification n = new Notification.Builder(context)
                .setContentTitle("Games@MMBBS")
                //.setVibrate(new long[] { 1000, 1000 })
                .setContentText(msgText)
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pIntent).setAutoCancel(true)
                .build();


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Application.NOTIFICATION_SERVICE);
        notificationManager.notify(2, n);
    }

    /*
    public void notification(Context context,String name) {
    	
    final int id = 2;
    String ns = context.NOTIFICATION_SERVICE;
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(ns);
    int icon = R.drawable.icon;
    CharSequence tickerText = name;
    long when = System.currentTimeMillis();


    CharSequence contentText="nothing";

     Notification checkin_notification = new Notification(icon, tickerText,
             when);
     Intent notificationIntent = new Intent(context, TabActivity.class);
        notificationIntent.setFlags(  Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        checkin_notification.setLatestEventInfo(context, "MMBBSapp",
                contentText, contentIntent);
        checkin_notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
        notificationManager.notify(id, checkin_notification);
			
    }
    */
}