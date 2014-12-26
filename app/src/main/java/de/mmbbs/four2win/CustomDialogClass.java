package de.mmbbs.four2win;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.mmbbs.R;

public class CustomDialogClass extends Dialog  implements android.view.View.OnClickListener{
	 public Activity c;
	  public Dialog d;
	  public Button yes, no;
	  public String msg,pos_txt,neg_txt;
	  private CustomDialogListener listener;
	  private CustomDialogType type;
	  
	  public CustomDialogClass(Activity a,CustomDialogType type,String msg,String pos_button_text,String neg_button_text) {
	    super(a);
	    // TODO Auto-generated constructor stub
	    this.c = a;
	    this.msg=msg;
	    this.pos_txt=pos_button_text;
	    this.neg_txt=neg_button_text;
	    this.type=type;
	  }

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.my_dialog);
	    yes = (Button) findViewById(R.id.button_positive);
	    no = (Button) findViewById(R.id.button_negative);
	    TextView tv = (TextView) findViewById(R.id.textView_msg_dialog);
	    tv.setText(msg);
	    yes.setOnClickListener(this);
	    no.setOnClickListener(this);
		if (this.pos_txt==null) {
			yes.setVisibility(View.INVISIBLE);
		}
		else {
			yes.setText(pos_txt);
		}
		
		if (neg_txt==null) {
			no.setVisibility(View.INVISIBLE);
		}
		else {
			no.setText(neg_txt);
		}
		
		ImageView iv =(ImageView) this.findViewById(R.id.imageView_dialog);
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
		getWindow().setBackgroundDrawable(new ColorDrawable(0));

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

}
