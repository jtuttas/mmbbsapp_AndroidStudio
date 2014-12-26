package de.mmbbs.tictactoetournament.game;





import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import de.mmbbs.gameserver.ui.Main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class GameBoard {

	public static final String markx="mark_x.png";
	public static final String marko="mark_o.png";
	public static final String markfree="empty.png";
	//public static final String markselected="selected.png";
			
	
	private String[][] data = new String[3][3];
	
	protected Bitmap freebitmap;
	protected Bitmap selectedbitmap;
	protected Bitmap xbitmap;
	protected Bitmap obitmap;
	private int current_turnx;
	private int current_turny;
	private int width,height;
	
	public GameBoard(Context context) {
		freebitmap = BitmapFactory.decodeResource(context.getResources(),de.mmbbs.R.drawable.markfree);
		selectedbitmap = BitmapFactory.decodeResource(context.getResources(),de.mmbbs.R.drawable.mark_selected);
		xbitmap = BitmapFactory.decodeResource(context.getResources(),de.mmbbs.R.drawable.stone_mark_x);
		obitmap = BitmapFactory.decodeResource(context.getResources(),de.mmbbs.R.drawable.stone_mark_o);
		init();
	}
	
	public void setDimmension(int w,int h) {
		selectedbitmap = Bitmap.createScaledBitmap(selectedbitmap, w/3, h/3, false);
		freebitmap = Bitmap.createScaledBitmap(freebitmap, w/3, h/3, false);
		xbitmap = Bitmap.createScaledBitmap(xbitmap, w/3, h/3, false);
		obitmap = Bitmap.createScaledBitmap(obitmap, w/3, h/3, false);
		width=w;
		height=h;	
		Log.d(Main.TAG,"free bitmap height="+freebitmap.getHeight()+"width="+freebitmap.getWidth());
	}
	
	public String[][] getData() {
		return data;
	}

	public void paint(Canvas c,Paint p) {
		int xp=0;
		int yp=0;
		for (int x=0;x<3;x++) {
			for (int y=0;y<3;y++) {
				if (data[y][x].compareTo(markx)==0) {
						c.drawBitmap(xbitmap,xp,yp,p);
				}
				if (data[y][x].compareTo(marko)==0) {
					c.drawBitmap(obitmap,xp,yp,p);
				}
				if (data[y][x].compareTo(markfree)==0) {
					c.drawBitmap(freebitmap,xp,yp,p);
				}
				yp+=height/3;
			}
			yp=0;
			xp+=width/3;
		}
	}
	
	
	
	public boolean won(Player p) {
		String s = p.getSymbol();
		for (int x=0;x<3;x++) {
			if (data[x][0].compareTo(s)==0 && data[x][1].compareTo(s)==0 && data[x][2].compareTo(s)==0) return true;
		}
		for (int y=0;y<3;y++) {
			if (data[0][y].compareTo(s)==0 && data[1][y].compareTo(s)==0 && data[2][y].compareTo(s)==0) return true;
		}
		if (data[0][0].compareTo(s)==0 && data[1][1].compareTo(s)==0 && data[2][2].compareTo(s)==0) return true;
		if (data[2][0].compareTo(s)==0 && data[1][1].compareTo(s)==0 && data[0][2].compareTo(s)==0) return true;
		return false;
	}
	
	public boolean penalty() {
		for (int x=0;x<3;x++) {
			for (int y=0;y<3;y++) {
				if (data[x][y].compareTo(markfree)==0) return false;
			}
		}
		return true;
	}
	
	public String getStone(float x,float y) {
		if (x<0 || x>width) {
			return "-";
		}
		if (y<0 || y>height) {
			return "-";
		}
		int xi=(int) ((x)/(width/3));
		int yi=(int) ((y)/(height/3));
		if (xi==3) xi=2;
		if (yi==3) yi=2;
		return data[yi][xi];
	}

	public void setStone(float x,float y,String s) {
		current_turnx=(int) ((x)/(width/3));
		current_turny=(int) ((y)/(height/3));
		data[current_turny][current_turnx]=s;
	}
	public int getStoneX(float mTouchX) {
		float x = mTouchX;
		int xi=(int) ((mTouchX)/(width/3));
		if (xi==3) xi=2;
		return (xi*(width/3));
	} 

	public int getStoneY(float mTouchY) {
		float y = mTouchY;
		int yi=(int) ((mTouchY)/(height/3));
		if (yi==3) yi=2;
		return (yi*(height/3));
	}

	public JSONObject getBoard() {
		JSONObject jo = new JSONObject();
		JSONArray ja = new JSONArray();
		for (int y=0;y<3;y++) {
			JSONArray row = new JSONArray();
			for (int x=0;x<3;x++) {
				row.put(data[y][x]);
			}
			ja.put(row);
		}
		Log.d(Main.TAG,"board in Json ="+ja.toString());
		try {
			jo.put("board", ja);
			jo.put("current_turnx",current_turnx);
			jo.put("current_turny",current_turny);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jo;
	}

	public int getStoneXbyIndex(int turnx) {
		return turnx*width/3;
	}

	public int getStoneYbyIndex(int turny) {
		return turny*height/3;
	}

	public void setStoneByIndex(int turnx, int turny, String mark) {
		data[turny][turnx]=mark;
		
	}

	public void init() {
		for (int x=0;x<3;x++) {
			for (int y=0;y<3;y++) {
				data[x][y]=markfree;
			}
		}
	}
}
