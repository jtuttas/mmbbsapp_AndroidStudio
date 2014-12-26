package de.mmbbs.gameserver.ui;

	import java.util.ArrayList;

    import de.mmbbs.R;
import de.mmbbs.gameserver.User;

    import android.annotation.SuppressLint;
	import android.content.Context;
    import android.util.Log;
    import android.view.LayoutInflater;
	import android.view.View;
	import android.view.ViewGroup;
	import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
    import android.widget.RelativeLayout;
import android.widget.TextView;


	@SuppressLint("InlinedApi")
public class UserListArrayAdapter extends ArrayAdapter<User>  implements Filterable {

	private ArrayList<User> userlines = new ArrayList<User>();
	private ArrayList<User> filteredData;

	private boolean friendsonly=false;
	private UserFilter mFilter = new UserFilter();
	private LayoutInflater mInflater;
	private Context context;

	
	public UserListArrayAdapter(Context context, int textViewResourceId,ArrayList<User> list) {
		super(context, textViewResourceId);
		this.context=context;
		userlines=(ArrayList<User>) list.clone();
		filteredData=(ArrayList<User>) list.clone();
		mInflater = LayoutInflater.from(context);
	}

	

	public int getCount() {
		return this.filteredData.size();
	}

	public User getItem(int index) {
		return this.filteredData.get(index);
	}


	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent) {
		 ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_line, null);
			holder = new ViewHolder();
			holder.userlayout = (RelativeLayout) convertView.findViewById(R.id.wrapper_userlist);
			holder.player= (TextView) convertView.findViewById(R.id.textView_user_name);
			holder.playerimage= (TextView) convertView.findViewById(R.id.textView_user_image);
			holder.playerfriend= (TextView) convertView.findViewById(R.id.textView_friend_image);
			convertView.setTag(holder);
		}
		else {
            holder = (ViewHolder) convertView.getTag();
        }
		User user = getItem(position);
		Log.d(Main.TAG,"getView() in UserListArrayAdapter() user="+user.getName());
		holder.player.setText(user.getName());

		
		switch (user.getState()) {
			case FREE:
				holder.playerimage.setBackgroundResource(R.drawable.player);
				holder.userlayout.setBackground(context.getResources().getDrawable(R.drawable.free_player));
				break;
			case PENDING:
				holder.playerimage.setBackgroundResource(R.drawable.playerpending);
				holder.userlayout.setBackground(context.getResources().getDrawable(R.drawable.player_pending));
				
				break;
			case IN_GAME:
				holder.playerimage.setBackgroundResource(R.drawable.playerplay);
				holder.userlayout.setBackground(context.getResources().getDrawable(R.drawable.player_play));

				break;
		}
		if (user.isFriend()) {
			holder.playerfriend.setBackgroundResource(R.drawable.heard_minus);
		}
		else {
			holder.playerfriend.setBackgroundResource(R.drawable.heard_plus);			
		}
		return convertView;
	}


	@Override
	public void clear() {
		userlines.clear();
		filteredData.clear();
		super.clear();
	}



	public void setUserList(ArrayList<User> userList) {
		userlines=(ArrayList<User>) userList.clone();
		filteredData=(ArrayList<User>) userList.clone();
	}



	public void setFriendsOnly(boolean b) {
		friendsonly=b;
	}


	public Filter getFilter() {
		Log.d(Main.TAG,"getFilter()");
		return mFilter;
	}
 
	static class ViewHolder {
		RelativeLayout userlayout;
        TextView player;
        TextView playerimage;
        TextView playerfriend;
    }
	private class UserFilter extends Filter {
		

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			FilterResults results = new FilterResults();
			Log.d(Main.TAG,"perfomeFiltering");

			int count = userlines.size();
			final ArrayList<User> nlist = new ArrayList<User>(count);

			
			for (int i = 0; i < count; i++) {
				User u = (User) userlines.get(i);
				if (constraint.length()==0 ||  u.getName().startsWith((String) constraint)) {
					if (u.isFriend() && friendsonly || !friendsonly) {
						nlist.add(u);
					}
				}
			}
			results.values = nlist;
			results.count = nlist.size();

			return results;
		}

		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			Log.d(Main.TAG,"publishResults");
			filteredData = (ArrayList<User>) results.values;
			notifyDataSetChanged();
		}
	}
}

