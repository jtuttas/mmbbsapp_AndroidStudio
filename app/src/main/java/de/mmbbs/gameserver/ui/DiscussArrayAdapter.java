package de.mmbbs.gameserver.ui;

import java.util.ArrayList;
import java.util.List;

import de.mmbbs.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("InlinedApi")
public class DiscussArrayAdapter extends ArrayAdapter<OneComment> {

	private List<OneComment> chatlines = new ArrayList<OneComment>();

	@Override
	public void add(OneComment object) {
		chatlines.add(object);
		super.add(object);
	}

	public DiscussArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.chatlines.size();
	}

	public OneComment getItem(int index) {
		return this.chatlines.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.chatline, parent, false);
		}

		LinearLayout wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		OneComment coment = getItem(position);

		TextView user_image = (TextView) row.findViewById(R.id.textView_user_image);
		TextView player = (TextView) row.findViewById(R.id.textView_chat_player);
		TextView comment = (TextView) row.findViewById(R.id.comment);

		if (coment.player.toLowerCase().compareTo("server")==0) {
			user_image.setVisibility(View.GONE);
			player.setVisibility(View.GONE);
			comment.setText(coment.comment);
			wrapper.setGravity(Gravity.RIGHT);
			comment.setTextColor(Color.BLACK);
		}
		else if (coment.me.compareTo(coment.player)==0) {
			user_image.setVisibility(View.VISIBLE);
			player.setVisibility(View.VISIBLE);
			player.setText(coment.player);
			comment.setText(coment.comment);
			wrapper.setGravity(Gravity.LEFT);
			comment.setTextColor(Color.rgb(36, 90, 37));
		}
		else {
			user_image.setVisibility(View.VISIBLE);
			player.setVisibility(View.VISIBLE);
			player.setText(coment.player);
			comment.setText(coment.comment);
			wrapper.setGravity(Gravity.LEFT);
			comment.setTextColor(Color.BLACK);
		}

		return row;
	}

	@Override
	public void clear() {
		chatlines.clear();
		super.clear();
	}


}