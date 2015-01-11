package de.mmbbs.gameserver.ui;

import org.json.JSONArray;
import org.json.JSONObject;

import de.mmbbs.R;
import de.mmbbs.gameserver.GameHighscoreListener;
import de.mmbbs.gameserver.GameServerApplication;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HighscoreFragment extends Fragment implements GameHighscoreListener {

	ProgressDialog pd;
	private View rootView;
	private Handler handler;
	private GameServerApplication gc;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.highscore_layout, container, false);
		handler = new Handler();
		gc = (GameServerApplication) getActivity().getApplication();
		return rootView;
	}

	@Override
	public void onStart() {
		Log.d(Main.TAG,"onStart() Highscore Activity");
		gc.setHighscoreCallbacks(this, handler);
		super.onStart();
		if (pd!=null && pd.isShowing()) pd.dismiss();
		pd = new ProgressDialog(getActivity(),de.mmbbs.R.style.MyTheme);
		pd.setCancelable(false);
		pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
		pd.show();
		gc.highscores(20);
	}

	@Override
	public void onStop() {
		super.onStop();
		if (pd!=null && pd.isShowing()) pd.dismiss();
		Log.d(Main.TAG,"onStop() Highscore Activity");
	}
	

	@Override
	public void updateHighscores(JSONObject obj) {
		Log.d(Main.TAG,"=========== update Highscores");
		if (pd!=null && pd.isShowing()) pd.dismiss();
		JSONArray rows = obj.optJSONArray("rows");
		TableLayout layout = (TableLayout) rootView.findViewById(R.id.tablellayout_highscore);
		layout.removeViews(1, layout.getChildCount()-1);
		boolean foundme=false;
		for (int i=0;i<rows.length();i++) {
			Log.d(Main.TAG,"update Highscores:"+i);
			TableRow tableRow = new TableRow(getActivity());
			tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			//Log.d(Main.TAG," Vergeleicht ("+rows.optJSONObject(i).optString("Name")+") mit ("+gc.getUser()+") = "+rows.optJSONObject(i).optString("Name").equalsIgnoreCase(gc.getUser()));
            if (rows.optJSONObject(i).optString("Name").equalsIgnoreCase(gc.getUser())) {
				tableRow.setBackgroundColor(getResources().getColor(R.color.lineme));
				foundme=true;
			}
			else {
				if (i%2==0)  tableRow.setBackgroundColor(getResources().getColor(R.color.line0));
				else  tableRow.setBackgroundColor(getResources().getColor(R.color.line1));
			}
			this.addRow(tableRow, 
					Integer.toString(i+1),
					Integer.toString(rows.optJSONObject(i).optInt("score")),
					rows.optJSONObject(i).optString("Name"),
					Integer.toString(rows.optJSONObject(i).optInt("games")),
					Integer.toString(rows.optJSONObject(i).optInt("won")),
					Integer.toString(rows.optJSONObject(i).optInt("lost")),
					rows.optJSONObject(i).optString("location"));
					
             
             layout.addView(tableRow);
		}
		if (!foundme) {
			TableRow tableRow = new TableRow(getActivity());
			tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			this.addRow(tableRow, ":", ":", ":", ":", ":", ":", ":");
            layout.addView(tableRow);
			tableRow = new TableRow(getActivity());
			tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
			this.addRow(tableRow, Integer.toString(obj.optInt("ranking")), 
					Integer.toString(obj.optInt("score")),
					gc.getUser(),
					Integer.toString(obj.optInt("games")),
					Integer.toString(obj.optInt("won")),
					Integer.toString(obj.optInt("lost")),
					obj.optString("location"));
            layout.addView(tableRow);
		}
		
	}

	private void addRow(TableRow tableRow,String rank,String score, String name,String games,String won,String lost, String location) {
        // Rank
		 TextView tv = new TextView(getActivity());
        tv.setText(rank);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setPadding(10, 0, 0, 0);
        tv.setTextSize(20);	         
        tableRow.addView(tv);

        // Score
		 tv = new TextView(getActivity());
        tv.setText(score);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setPadding(5, 0, 0, 0);
        tv.setTextSize(20);
        tableRow.addView(tv);

        // Name
		 tv = new TextView(getActivity());
        tv.setText(name);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setPadding(5, 0, 0, 0);
        tv.setTextSize(20);
        tableRow.addView(tv);

        // Games
		 tv = new TextView(getActivity());
        tv.setText(games);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setPadding(5, 0, 0, 0);
        tv.setTextSize(20);
        tableRow.addView(tv);
        
        // Games won
		 tv = new TextView(getActivity());
        tv.setText(won);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setPadding(5, 0, 0, 0);
        tv.setTextSize(20);
        tableRow.addView(tv);
        
        // Games lost
		 tv = new TextView(getActivity());
        tv.setText(lost);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setPadding(5, 0, 0, 0);
        tv.setTextSize(20);
        tableRow.addView(tv);
        
        // Name
		 tv = new TextView(getActivity());
        tv.setText(location);
        tv.setTextColor(Color.BLACK);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        tv.setPadding(5, 0, 0, 0);
        tv.setTextSize(20);
        tableRow.addView(tv);

	}

}
