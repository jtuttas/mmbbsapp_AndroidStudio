package de.mmbbs.gameserver.ui;

import java.util.List;

import de.mmbbs.R;
import de.mmbbs.four2win.SoundPlayer;
import de.mmbbs.gameserver.DBManager;
import de.mmbbs.gameserver.GameManagementActivity;
import de.mmbbs.gameserver.GameServerApplication;
import de.mmbbs.gameserver.GameUserListener;
import de.mmbbs.gameserver.User;
import de.mmbbs.gameserver.UserState;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UserListFragment extends Fragment implements OnItemClickListener, TextWatcher,GameUserListener{

	private GameServerApplication gc;
	public UserListArrayAdapter adapter;
	private DBManager dbm;
	private Handler handler;
	private View rootView;
	private CustomDialogClass customDialog;

	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_list_layout, container, false);
		ListView lv = (ListView) rootView.findViewById(R.id.listView_users);
		gc = (GameServerApplication) getActivity().getApplication();
		adapter = new UserListArrayAdapter(getActivity().getApplicationContext(), R.layout.user_line,gc.getUserList());
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(this);
		lv.setTextFilterEnabled(true);

		this.registerForContextMenu(lv);
		dbm = new DBManager(getActivity(), "friends.db", null);
		Log.d(Main.TAG," onCreate() userlist size="+gc.getUserList().size());
		TextView tv = (TextView) rootView.findViewById(R.id.textView_number_of_users);
		tv.setText(Integer.toString(gc.getUserList().size()));
		EditText et = (EditText) rootView.findViewById(R.id.editText_user_filter);
		et.addTextChangedListener(this);
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		handler = new Handler();
		gc.setUserCallbacks(this, handler);
		this.rootView=rootView;
        return rootView;
    }


    @Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    getActivity().getMenuInflater().inflate(R.menu.player_context, menu);
		AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
		ListView lv = (ListView) getActivity().findViewById(R.id.listView_users);
		
			User user = (User) adapter.getItem(acmi.position);
		
			Log.d(Main.TAG,"Create Context Menu for user "+user.getName()+ "user list size="+gc.getUserList().size());
			if (user.isFriend()) {
				menu.findItem(R.id.item_add_friend).setVisible(false);
			}
			else {
				menu.findItem(R.id.item_remove_friend).setVisible(false);
			}
		
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		Log.d(Main.TAG," Context Selected userlist size="+gc.getUserList().size());
		ListView lv = (ListView) rootView.findViewById(R.id.listView_users);
		User user = (User) adapter.getItem((int) info.id);
		TextView playerfriend; 
		 switch (item.getItemId()) {
	        case R.id.item_add_friend:
	        	dbm.addFriend(user.getName());
	        	user.setFriend(true);
	        	gc.getUserList().set((int) info.id, user);
	        	adapter.notifyDataSetChanged();
	        	return true;
	        case R.id.item_remove_friend:
	        	dbm.removeFriend(user.getName());
	        	user.setFriend(false);
	        	gc.getUserList().set((int) info.id, user);
				adapter.getFilter().filter(((EditText) rootView.findViewById(R.id.editText_user_filter)).getText());
				adapter.notifyDataSetChanged();
				break;
	        case R.id.item_play_with:
	        	requestPlayer(info.id);
	        return true;
	    }
		return super.onContextItemSelected(item);
	}

	
	public void requestPlayer(long index) {
		Log.d(Main.TAG," Request Position="+index);
		requestPlayer(adapter.getItem((int) index));
		
	}
	private void requestPlayer(final User u) {
		if (u.getState()==UserState.FREE || u.getState()==UserState.OFFLINE) {
			
			customDialog = ((GameManagementActivity)this.getActivity()).getCustomDialog();
			customDialog.setType(CustomDialogType.INFO);
			customDialog.setContent(getResources().getString(R.string.request_to_player)+"'"+u.getName()+"'");
			customDialog.setPositiveMsg(null);
			customDialog.setNegativeMsg(this.getResources().getString(R.string.cancel));
			customDialog.setOnCustomDialog(new CustomDialogListener() {


				@Override
				public void onNegativeButton() {
			    	gc.request(u.getName(), "cancelrequest",Main.GAME);
					gc.setPendingrequest(null, null,0);

				}

				@Override
				public void onPositiveButton() {
					
				}
				
			});
			customDialog.setCancelable(false);
			customDialog.update();
			customDialog.show();
			gc.setPendingrequest(gc.getUser(), u.getName(),GameServerApplication.REQUEST);
	    	gc.request(u.getName(), "request",Main.GAME);
		}
		else {
			Toast.makeText(getActivity(),getResources().getString(R.string.not_a_free_player), Toast.LENGTH_LONG).show();				
		}

	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		final User u = adapter.getItem(pos);
		Log.d(Main.TAG,"Geklick auf "+u.getName());
        GameManagementActivity.cdd=null;
        GameManagementActivity.soundPlayer.play(SoundPlayer.Sounds.REQUEST);
		requestPlayer(u);
		
	}

	@Override
	public void afterTextChanged(Editable arg0) {
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		Log.d(Main.TAG," Text Changed cras="+arg0);
		adapter.getFilter().filter(arg0);		

		
	}


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.user_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(Main.TAG," onOPtionImtemSelected in UserListGFragment ");
        switch (item.getItemId()) {
            case R.id.item_friends_only:
                if (item.isChecked()) {
                    Log.d(Main.TAG," Friends olny is checked ");
                    item.setChecked(false);
                    adapter.setFriendsOnly(false);
                    adapter.getFilter().filter(((EditText)this.getView().findViewById(R.id.editText_user_filter)).getText());
                }
                else {
                    Log.d(Main.TAG," Friends olny is unchecked ");
                    item.setChecked(true);
                    adapter.setFriendsOnly(true);
                    adapter.getFilter().filter(((EditText) this.getView().findViewById(R.id.editText_user_filter)).getText());
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
	public void updateUsers(List<User> userlist) {
		Log.d(Main.TAG,"updateUser in UserList Fragment");



		for (int i=0;i<userlist.size();i++) {
			User user = userlist.get(i);
			if (dbm.isFriend(user.getName())) {
				user.setFriend(true);
			}
			else {
				user.setFriend(false);
			}
		}
		adapter.clear();
		adapter.setUserList(gc.getUserList());
		adapter.getFilter().filter(((EditText) rootView.findViewById(R.id.editText_user_filter)).getText());
		TextView tv = (TextView) rootView.findViewById(R.id.textView_number_of_users);
		tv.setText(Integer.toString(gc.getUserList().size()));
		Log.d(Main.TAG,"updateUser in UserList Activity userlist size="+gc.getUserList().size());
	}

	
}
