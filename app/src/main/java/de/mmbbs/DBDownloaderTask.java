package de.mmbbs;
//
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class DBDownloaderTask extends AsyncTask<String, Integer, String> {

	private Activity a;
	private Loadfinished lf;
	
	public DBDownloaderTask(Activity a, Loadfinished lf) {
    	this.a=a;
    	this.lf = lf;
        //dialog = ProgressDialog.show(context, "", "Updating Database. Please wait...", true);  
        
    }
	@Override
	protected String doInBackground(String... urls) {
		Log.d(TabActivity.TAG, "Lade DBInfo:"+urls[0]);
		try {
		    // Create a URL for the desired page
		    URL url = new URL(urls[0]);

		    // Read all the text returned by the server
		    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		    String str;
		    String s="";
		    while ((str = in.readLine()) != null) {
		        // str is one line of text; readLine() strips the newline character(s)
		    	s=s+str+"\r\n";
		    }
		    in.close();
		    return s;
		} catch (MalformedURLException e) {
			Log.d(TabActivity.TAG, "Malformed URL Exception bei Lade DBInfo:"+urls[0]);
			return null;
		} catch (IOException e) {
			Log.d(TabActivity.TAG, "IO-Exception bei Lade DBInfo:"+urls[0]);
			return null;
		}
		
	}
	
	 protected void onPostExecute(String s) {
		//Log.d(Main.TAG, "laden beendet:"+s);
		lf.loadFinished(s);
     }

}
