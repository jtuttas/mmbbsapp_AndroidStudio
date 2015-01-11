package de.mmbbs.gameserver.ui;

import de.mmbbs.R;
import de.mmbbs.gameserver.GameManagementActivity;
import de.mmbbs.gameserver.GameServerApplication;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;


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
			//requestWindowFeature(Window.FEATURE_ACTION_BAR);
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
        if (userListFragment.isVisible()) userListFragment.onCreateOptionsMenu(menu,this.getMenuInflater());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (userListFragment.isVisible())  userListFragment.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }

    @Override
		public void onLogin() {
			Log.d(Main.TAG," --------> Fragment Activity onLogin()");
			Bundle extras = getIntent().getExtras();
			if(extras == null) {
			        Log.d(Main.TAG,":-( kein Extra ");
			        
			} else {
				if (extras.getString("command")!=null && extras.getString("command").compareTo("request")==0) {
					
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
