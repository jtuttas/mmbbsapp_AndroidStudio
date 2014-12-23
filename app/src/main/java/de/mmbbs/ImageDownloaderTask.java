package de.mmbbs;

import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
    private String url;
    private final WeakReference<ImageView> imageViewReference;
    private Context context;
    private ProgressDialog dialog;
    private boolean alive=false;
    
    public ImageDownloaderTask(ImageView imageView,Context context) {
    	this.context=context;
        imageViewReference = new WeakReference<ImageView>(imageView);
        dialog= new ProgressDialog(context);
		dialog.setTitle("Loading...");
		dialog.setMessage("Loading Image. Please wait...");
		dialog.show();
        Log.d(TabActivity.TAG, "Image Downloader initialisiert dialog="+dialog);
        alive=true;
        
    }
    
    public void back() {
    	if (dialog!=null) {
    		dialog.cancel();
    		alive=false;
    	}
    	this.cancel(true);
    	Log.d (TabActivity.TAG,"Laden abgebrochen!");
    }

    public boolean isAlive() {
    	return alive;
    }
    
    @Override
    // Actual download method, run in the task thread
    protected Bitmap doInBackground(String... params) {
         // params comes from the execute() call: params[0] is the url.
         return downloadBitmap(params[0]);
    }

    @Override
    // Once the image is downloaded, associates it to the imageView
    protected void onPostExecute(Bitmap bitmap) {
    	dialog.dismiss();
    	alive=false;
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null) {
            ImageView imageView = imageViewReference.get();
            if (imageView != null) {
            	if (bitmap==null) {
            		Log.i(TabActivity.TAG, "Kein Bild gefunden, lade aus Ressouces ");
            		imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.anonym));            	}
            	else {
            		imageView.setImageBitmap(bitmap);
            	}
            }
        }
    }
    
    static Bitmap downloadBitmap(String url) {
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) { 
                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url); 
                return null;
            }
            
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                	
                    inputStream = entity.getContent(); 
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();  
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or IllegalStateException
            getRequest.abort();
            Log.w("ImageDownloader", "Error while retrieving bitmap from " + url+":"+e.toString());
        } finally {
            if (client != null) {
                client.close();
            }
        }
        return null;
    }

}