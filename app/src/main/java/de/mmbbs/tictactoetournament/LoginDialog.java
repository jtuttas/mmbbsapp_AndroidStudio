package de.mmbbs.tictactoetournament;

import de.mmbbs.R;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class LoginDialog extends Dialog implements android.view.View.OnClickListener {
		private Activity a;
		private int layout;
		private Button positiv,negative,next,previous;
		private LoginDialogListener listener;
		private LoginDialogType type;
		private LinearLayout mainlayout;
		
	  public LoginDialog(Activity a,int layout,LoginDialogType type) {
		    super(a);
		    this.layout=layout;
		    this.type=type;

		    
	  }
	  
	  public void setListener(LoginDialogListener l) {
		  listener=l;
	  }
	  
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(layout);
	    
	    positiv = (Button) findViewById(R.id.button_dialog_positive);
	    positiv.setOnClickListener(this);
	    negative = (Button) findViewById(R.id.button_dialog_negative);
	    negative.setOnClickListener(this);
	    next = (Button) findViewById(R.id.button_next);
	    next.setOnClickListener(this);
	    previous = (Button) findViewById(R.id.button_previous);
	    previous.setOnClickListener(this);
	    mainlayout = (LinearLayout) findViewById(R.id.layout_dialog_main);
	    getWindow().setBackgroundDrawable(new ColorDrawable(0));
	  }
	  
	  public void onClick(View v) {
		  if (listener!=null) listener.onClick(v,type,mainlayout);
	  }
	
}
