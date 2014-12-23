package de.mmbbs.tictactoetournament;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import de.mmbbs.R;
import de.mmbbs.gameserver.GameChatListener;
import de.mmbbs.gameserver.GameManagementActivity;
import de.mmbbs.gameserver.GameServerApplication;
import de.mmbbs.gameserver.GameServerListener;
import de.mmbbs.gameserver.GameStates;
import de.mmbbs.gameserver.User;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class ChatFragment extends Fragment implements OnEditorActionListener,
		GameChatListener {

	private DiscussArrayAdapter adapter;
	private Handler handler;
	private GameServerApplication gc;
	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.chat_layout, container, false);
		handler = new Handler();
		gc = (GameServerApplication) getActivity().getApplication();
		EditText et = (EditText) rootView.findViewById(R.id.editText_chat);
		et.setOnEditorActionListener(this);
		ListView lv = (ListView) rootView.findViewById(R.id.listView1);
		adapter = new DiscussArrayAdapter(
				getActivity().getApplicationContext(), R.layout.chatline);
		lv.setAdapter(adapter);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(Main.TAG, "onResume() Chat Activity");
		gc.setChatCallbacks(this, handler);
	}

	@Override
	public void updateGameChat(JSONObject obj) {
		Log.d(Main.TAG, "updateGameChat in ChatActivity");
		adapter.add(new OneComment(gc.getUser(), obj.optString("from_player"),
				obj.optString("content")));
		ListView lv = (ListView) rootView.findViewById(R.id.listView1);
		lv.setSelection(adapter.getCount() - 1);
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		Log.d(Main.TAG, "on Editor Action:" + arg0);
		if (arg2.getAction() == KeyEvent.ACTION_DOWN) {
			this.send(arg0);
			InputMethodManager imm = (InputMethodManager) getActivity()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
			return true;
		}
		return false;
	}

	public void send(View v) {
		EditText et = (EditText) rootView.findViewById(R.id.editText_chat);
		gc.sendGameChat(et.getText().toString());
		et.setText("");
	}
}
