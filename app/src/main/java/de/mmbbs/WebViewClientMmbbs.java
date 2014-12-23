package de.mmbbs;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.opengl.Visibility;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Diese Klasse behandelt die appspezifischen WebView-Probleme.
 * Sie muss bei dem WebView durch setWebViewClient() eingebunden werden.
 * @author Schwanda
 *
 */
public class WebViewClientMmbbs extends WebViewClient {
	boolean enableLinkLoading;
	String alternativeMessage;
	private ProgressDialog progressDialog;
	private Activity activity;
	
	/**
	 * Appspezifischer WebViewClient
	 * @param enableLinkLoading Links, die sich innerhalb einer geladenen Website im WebView befinden, dürfen geladen werden, falls <b><i>true</i></b> �bergeben wird.
	 * @param alternativeMessage Wenn <i>enableLinkLoading</i> <b>false</b> ist, kann hier ein alternativer Text übergeben werden.
	 */
	public WebViewClientMmbbs(final boolean enableLinkLoading, final String alternativeMessage, Activity a) {
		super();
		activity = a;
		this.enableLinkLoading=enableLinkLoading;
		this.alternativeMessage=alternativeMessage;
	}
	
	 @Override
	public void onReceivedError(WebView view, int errorCode,
			String description, String failingUrl) {

			Log.d(TabActivity.TAG, "WebView On Received Error: errorCode="+errorCode);
//		super.onReceivedError(view, errorCode, description, failingUrl);
			String html1 = "<html><body bgcolor=\"#FF0000\"><br><br><div><h1 align=\"center\">Bei der Anfrage ist ein Fehler aufgetreten!</h1><hr>";
			String mime = "text/html";
			String encoding = "utf-8";
			
			view.loadData(html1, mime, encoding);
	}

	public void onLoadResource (WebView view, String url) {
         if (progressDialog == null) {
             progressDialog = new ProgressDialog(activity);
             progressDialog.setMessage("Lade Plan");
             progressDialog.show();
         }
     }
     public void onPageFinished(WebView view, String url) {
         if (progressDialog!=null && progressDialog.isShowing()) {
             progressDialog.dismiss();
             progressDialog = null;
         }
         String title=view.getTitle();
         Log.d (TabActivity.TAG,"Page finished Title="+title);
         if (title!=null && title.contains("Error")) {
        	String html1 = "<html><body bgcolor=\"#FF8C7A\"><br><br><div><h1 align=\"center\">Es liegen keine Daten vor!</h1>";
        	String mime = "text/html";
 			String encoding = "utf-8";
 			//view.loadUrl("file:///android_asset/index.html");
        	 view.loadData(html1, mime, encoding);
         }
     }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	/* Bei einem Redirect wird normalerweise die App verlassen und der Browser angezeigt.
    	 * Dies soll aber verhindert werden. Stattdessen wird je nach Bedarf die Seite im WebView selbst
    	 * angezeigt oder eine alternative Nachricht angezeigt. */
        if (enableLinkLoading) {
        	/* URL wird im WebView selbst angezeigt. */
        	view.loadUrl(url);
        }
        else {
        	/* Alternativer HTML-Text */
        	String html="<html>";
        	html=html+"<head>";
        	html=html+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">";
        	html=html+"</head>";
        	html=html+"<body>";
        	html=html+"<h2>";
        	html=html+alternativeMessage;
        	html=html+"</h2>";
        	html=html+"</body>";
        	html=html+"</html>";
        	
        	view.loadData(html, "text/html", "UTF-8");
        }
        return true;
    }
}

