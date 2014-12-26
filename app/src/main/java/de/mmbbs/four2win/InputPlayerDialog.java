package de.mmbbs.four2win;


import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import de.mmbbs.R;

public class InputPlayerDialog extends Dialog implements android.view.View.OnClickListener {
		private Activity a;
		private int layout;
		private Button positiv;
		private InputPlayerDialogListener listener;
		private LinearLayout mainlayout;
		private String name1,name2;
		private int gameState;
		private Button negative;
		
	  public InputPlayerDialog(Activity a,int layout,String p1,String p2, int gameState) {
		    super(a);
		    this.layout=layout;
		    name1=p1;
		    name2=p2;
		    this.gameState=gameState;
	  }
	  
	  public void setListener(InputPlayerDialogListener l) {
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
	    if (gameState!=Leinwand.PLAY) {
	    	negative.setVisibility(View.INVISIBLE);
	    }
	    mainlayout = (LinearLayout) findViewById(R.id.layout_dialog_main);
	    getWindow().setBackgroundDrawable(new ColorDrawable(0));
	    EditText et= (EditText) findViewById(R.id.editText_player1);
	    et.setText(name1);
	    et= (EditText) findViewById(R.id.editText_player2);
	    et.setText(name2);
	    
	  }
	  
	  public void onClick(View v) {
		  if (listener!=null) listener.onClick(v,mainlayout);
	  }
	
}
