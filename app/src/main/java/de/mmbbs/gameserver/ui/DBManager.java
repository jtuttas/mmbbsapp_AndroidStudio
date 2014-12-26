package de.mmbbs.gameserver.ui;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class DBManager extends SQLiteOpenHelper {

	private static final String[] SQL = {
		"CREATE TABLE `friends` (name TEXT PRIMARY KEY)",

	};
	
	public DBManager(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		Log.d("SQlite", "DBM-Manager initdb="+name+ " Version="+version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("SQlite", "DBM-Manager create Database");
		for (int i=0;i<SQL.length;i++) {
			Log.d("SQlite", "Exec SQL Command:"+SQL[i]);
			db.execSQL(SQL[i]);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int versionOld, int versionNew) {
		Log.d("SQlite", "update Database");
		try {
			db.execSQL("DROP TABLE 'friends'");
		}
		catch (SQLiteException e) {

		}
		
		this.onCreate(db);		
	}

	
	public void addFriend(String f) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("INSERT INTO `friends` VALUES ('"+f+"');");
		
	}
	public void removeFriend(String f) {
		SQLiteDatabase db = this.getReadableDatabase();
		db.execSQL("DELETE FROM `friends` WHERE name='"+f+"'");
		
		
	}
	public boolean isFriend(String f) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("select * from friends where name='"+f+"'", null);
		if (c.getCount()!=0) {
			c.close();
			return true;
		}
		c.close();
		return false;
	}
	
	public String execute(String text) throws  Exception {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(text, null);
		String out="";
		String row;
		while (c.moveToNext()) {
			row="";
			for (int i=0;i<c.getColumnCount();i++) {
				row=row+c.getString(i)+";";	
			}
			out=out+row+"\n";
			
			Log.d("SQlite", "DB result:("+row+")");
		}
		c.close();
		return out;
	}


}
