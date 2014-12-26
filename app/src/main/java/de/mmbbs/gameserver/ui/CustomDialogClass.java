package de.mmbbs.gameserver.ui;

import de.mmbbs.R;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomDialogClass extends Dialog  implements android.view.View.OnClickListener{
	 public Activity c;
	 // public Dialog d;
	  public Button yes, no;
	  public String msg,pos_txt,neg_txt;
	  private CustomDialogListener listener;
	  private CustomDialogType type;
	private boolean showing;
	  
		
	  public CustomDialogClass(Activity a,CustomDialogType type,String msg,String pos_button_text,String neg_button_text) {
	    super(a);
	    this.c = a;
	    this.msg=msg;
	    this.pos_txt=pos_button_text;
	    this.neg_txt=neg_button_text;
	    this.type=type;
	  }
	  
	  public CustomDialogClass(Activity a) {
		  super(a);
		  this.c=a;
		  requestWindowFeature(Window.FEATURE_NO_TITLE);
	  }

	  public void update() {
		 //this.findViewById(R.id.my_dialog).invalidate();
	  }

	@Override
	  protected void onCreate(Bundle savedInstanceState) {
		Log.d(Main.TAG,"!! Custom Dialog onCreate()");
	    super.onCreate(savedInstanceState);

	    setContentView(R.layout.my_dialog);
	    yes = (Button) findViewById(R.id.button_positive);
	    yes.setOnClickListener(this);
	    no = (Button) findViewById(R.id.button_negative);
	    no.setOnClickListener(this);
	    this.setContent(msg);
	    this.setPositiveMsg(pos_txt);
	    this.setNegativeMsg(neg_txt);
		this.setType(type);
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

	  }

	  
	  
	  @Override
	public void dismiss() {
		super.dismiss();
		showing=false;
		Log.d(Main.TAG,"dismiss Dialog");	
	}

	@Override
	public boolean isShowing() {
		return showing;
	}

	@Override
	public void show() {
		super.show();
		showing=true;
		Log.d(Main.TAG,"showing Dialog");
	}

	@Override
	  public void onClick(View v) {
	    switch (v.getId()) {
	    case R.id.button_positive:
	      if (listener!= null) listener.onPositiveButton();
	      break;
	    case R.id.button_negative:
	    	if (listener!= null)listener.onNegativeButton();
	      break;
	    default:
	      break;
	    }
	    dismiss();
	  }
	  
	  public void setOnCustomDialog(CustomDialogListener listener) {
		  this.listener=listener;
	  }

	public void setType(CustomDialogType type) {
		this.type=type;
		ImageView iv =(ImageView) this.findViewById(R.id.imageView_dialog);
		if (iv!=null) {
			switch (type) {
			case WARNING:
				iv.setImageResource(R.drawable.warning);
				break;
			case ERROR:
				iv.setImageResource(R.drawable.error);
				break;
			case INFO:
				iv.setImageResource(R.drawable.info);
				break;
			case SUCCESS:
				iv.setImageResource(R.drawable.success);
				break;
			}
		}
		
	}

	public void setContent(String string) {
		this.msg=string;
		 TextView tv = (TextView) findViewById(R.id.textView_msg_dialog);
		 if (tv!=null) tv.setText(msg);
		
	}

	public void setPositiveMsg(String string) {
		this.pos_txt=string;
	    yes = (Button) findViewById(R.id.button_positive);
		if (yes!=null) {
		    if (this.pos_txt==null) {
				yes.setVisibility(View.INVISIBLE);
			}
			else {
				yes.setText(pos_txt);
				yes.setVisibility(View.VISIBLE);
			}
		}


		
	}

	public void setNegativeMsg(String string) {
		this.neg_txt=string;
		no = (Button) findViewById(R.id.button_negative);
		if (no!=null) {
			if (neg_txt==null) {
				no.setVisibility(View.INVISIBLE);
			}
			else {
				no.setText(neg_txt);
				no.setVisibility(View.VISIBLE);
			}
		}
	}

	public void setActivity(Activity activity) {
		this.c=activity;
		this.setOwnerActivity(activity);
		
	}

}
