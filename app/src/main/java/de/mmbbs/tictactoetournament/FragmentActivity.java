package de.mmbbs.tictactoetournament;

import java.util.List;

import org.json.JSONObject;

import de.mmbbs.R;
import de.mmbbs.gameserver.GameManagementActivity;
import de.mmbbs.gameserver.GameServerApplication;
import de.mmbbs.gameserver.GameServerListener;
import de.mmbbs.gameserver.GameStates;
import de.mmbbs.gameserver.GameUserListener;
import de.mmbbs.gameserver.User;
import de.mmbbs.gameserver.UserState;
import de.mmbbs.tictactoetournament.game.Game;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
public class FragmentActivity extends GameManagementActivity {
	// Declaring our tabs and the corresponding fragments.
		ActionBar.Tab userTab, chatTab,highscoreTab;
		Fragment userListFragment = new UserListFragment();
		Fragment chatFragment = new ChatFragment();
		Fragment highscoreFragment = new HighscoreFragment();
		private GameServerApplication gc;

		//protected CustomDialogClass cdd;
		private FragmentActivity instance;
		//private Handler handler;
		private DBManager dbm;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			
			
			super.onCreate(savedInstanceState);
			Log.d(Main.TAG,"FragmentActivity onCreate()");
			requestWindowFeature(Window.FEATURE_ACTION_BAR);
			setContentView(R.layout.activity_main);
			
			ActionBar actionBar = this.getActionBar();
			Log.d(Main.TAG,"FragmentActivity onCreate() actionBar="+actionBar);
	        actionBar.setDisplayShowHomeEnabled(false);
	        actionBar.setDisplayShowTitleEnabled(false);
	        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	        userTab = actionBar.newTab().setIcon(R.drawable.add_friend);
	        chatTab = actionBar.newTab().setIcon(R.drawable.chat);
	        highscoreTab = actionBar.newTab().setIcon(R.drawable.highscorelist);
	        userTab.setTabListener(new TabListener(userListFragment));
	        chatTab.setTabListener(new TabListener(chatFragment));
	        highscoreTab.setTabListener(new TabListener(highscoreFragment));
	        actionBar.addTab(userTab);
	        actionBar.addTab(chatTab);
	        actionBar.addTab(highscoreTab);
	        
			gc=(GameServerApplication) this.getApplication();
			handler = new Handler();
			
			dbm = new DBManager(this, "friends.db", null, 1);
		}

		@Override
		protected void onStart() {
			super.onStart();
			gc.setActivityVisible(true);
			Log.d(Main.TAG,"onStart() FragmentActivity conected="+gc.isConnected());
		}
		
		
		
		@Override
		protected void onResume() {
			super.onResume();
		}

		@Override
		protected void onNewIntent(Intent intent) {
			super.onNewIntent(intent);
			Bundle extras = intent.getExtras();
			if(extras == null) {
			        Log.d(Main.TAG,":-( onNewIntent kein Extra "+intent.getStringExtra("command"));
			        
			} else {
				if (extras.getString("command").compareTo("request")==0) {
					showRequestDialog(extras.getString("from_player"),extras.getString("game"));
					getIntent().removeExtra("command");
					getIntent().removeExtra("from_player");
			    }
				else {
					Log.d(Main.TAG,"keine richtigen extras ->"+extras.getString("command"));
				}
			}
		}

		@Override
		protected void onStop() {
			
			super.onStop();
			gc.setActivityVisible(false);
		}

		
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
			gc.setServerCallbacks(null, null);
			
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.user_menu, menu);
			return super.onCreateOptionsMenu(menu);
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.item_friends_only:
				if (item.isChecked()) {
					Log.d(Main.TAG," Friends olny is checked ");
					item.setChecked(false);
					
					((UserListFragment) userListFragment).adapter.setFriendsOnly(false);
					
					((UserListFragment) userListFragment).adapter.getFilter().filter(((EditText) userListFragment.getView().findViewById(R.id.editText_user_filter)).getText());
				}
				else {
					Log.d(Main.TAG," Friends olny is unchecked ");
					item.setChecked(true);
					((UserListFragment) userListFragment).adapter.setFriendsOnly(true);
					((UserListFragment) userListFragment).adapter.getFilter().filter(((EditText) userListFragment.getView().findViewById(R.id.editText_user_filter)).getText());
					
				}
				break;
			}
			return super.onOptionsItemSelected(item);
		}
		
		
		@Override
		public void onLogin() {
			Log.d(Main.TAG," --------> Fragment Activity onLogin()");
			Bundle extras = getIntent().getExtras();
			if(extras == null) {
			        Log.d(Main.TAG,":-( kein Extra ");
			        
			} else {
				if (extras.getString("command").compareTo("request")==0) {
					
					showRequestDialog(extras.getString("from_player"),extras.getString("game"));
					getIntent().removeExtra("command");
					getIntent().removeExtra("from_player");
			    }
				else {
					Log.d(Main.TAG,"keine richtigen extras ->"+extras.getString("command"));
				}
			}
					

			
		}
		


		

		
}
