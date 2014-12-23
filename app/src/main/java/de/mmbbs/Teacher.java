package de.mmbbs;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import android.widget.ImageView;

public class Teacher {

	private String name;
	private String vname;
	private String shortName;
	private String gender;
	private String email;
	private ImageDownloaderTask task;

	
	Bitmap bitmap;
	Context context;
	
	public Teacher(Context c,String name, String vname, String shortName, String gender) {
		super();
		this.name = name;
		this.vname = vname;
		this.shortName = shortName;
		this.gender = gender;
		context = c;

	}
	
	public String getName() {
		return name;
	}
	
	public String getVName() {
		return vname;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getEmail() {
		return name+"@mmbbs.de";
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void back() {
		if (task!=null) {
			task.back();
			task=null;
		}
	}
	
	public boolean isLoading() {
		if (task!=null) {
			return task.isAlive();
		}
		return false;
	}
	
	public void getImage(ImageView imageView, Context context) {
		task = new ImageDownloaderTask(imageView,context);
        task.execute(TabActivity.IMAGE_URL+getShortName().toLowerCase()+".jpg");  
	}	
}
