package de.mmbbs;
/**
 * Klasse zum Verwalten der Datenbank test
 */
import java.util.Vector;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class DBManager extends SQLiteOpenHelper {

    public static final String DBNAME = "lehrer";
    private static int VERSION = 1;
    public static String CREATE = "CREATE TABLE lehrer(ID INTEGER PRIMARY KEY NOT NULL, SHORT TEXT, NNAME TEXT, VNAME TEXT, GENDER TEXT, EMAIL TEXT,STUNDENPLAN TEXT, VERTRETUNGSPLAN TEXT);";
    public static String CREATE_KLASSEN = "CREATE TABLE klassen(ID INTEGER PRIMARY KEY NOT NULL, KLASSE TEXT, ID_LEHRER TEXT, STUNDENPLAN TEXT, VERTRETUNGSPLAN TEXT);";
    Context context;


    public static String[] ADD = {
    };

    public static String[] ADD_KLASSEN = {
    };


    public DBManager(Context context) {
        super(context,DBNAME,null,DBManager.getVersion(context));
        this.context=context;
        Log.d(TabActivity.TAG, "DBM-Manager initialisiert Version="+VERSION);
        this.getReadableDatabase();
    }

    public static void setVersion(int v,Context c) {
        VERSION=v;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        Editor e = pref.edit();
        e.putInt("dbvers", v);
        e.commit();
    }

    public static int getVersion(Context c) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(c);
        return pref.getInt("dbvers", 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TabActivity.TAG, "DBM-Manager create Database");
        db.execSQL(CREATE);
        for (int i=0;i<ADD.length;i++) {
            db.execSQL(ADD[i]);
        }

        //Creating DB
        db.execSQL(CREATE_KLASSEN);
        for (int i=0;i<ADD_KLASSEN.length;i++) {
            db.execSQL(ADD_KLASSEN[i]);
        }

        //Create table
        //db.execSQL(CREATE_KLASSENLEHRER);
        //for (int i=0; i < ADD_KLASSENLEHRER.length; i++) {
        //db.execSQL(ADD_KLASSENLEHRER[i]);
        //}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
        Log.d(TabActivity.TAG, "DBM-Manager update Database from "+oldversion+" to "+newversion);
        try {
            db.execSQL("DROP TABLE 'lehrer'");
        }
        catch (SQLiteException e) {

        }
        try {
            db.execSQL("DROP TABLE 'klassen'");
        }
        catch (SQLiteException e) {

        }
        this.onCreate(db);
    }


    /**
     * get Teacher from shortName
     * @param s ShotName(ex. TU for Tuttas)
     * @return the Teacher
     */
    public Teacher getTeacher(String s) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM lehrer WHERE SHORT='"+s+"'" , null);
        Teacher teacher = null;
        while (c.moveToNext()) {
            String name = c.getString(2);
            String vname = c.getString(3);
            String shortName = c.getString(1);
            String gender = c.getString(4);
            Log.d(TabActivity.TAG, "Gender aus DB:("+c.getString(4)+")");
            teacher = new Teacher(context,name,vname,shortName,gender);
        }
        c.close();
        return teacher;

    }


    public String[] getShortNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM lehrer ORDER BY SHORT ASC" , null);
        Log.d(TabActivity.TAG, "Datenbank Lehrer erntält "+c.getCount()+" einträge");
        if (c.getCount()==0) {
            c.close();
            db.close();
            return null;
        }
        String[] s = new String[c.getCount()];
        Log.d(TabActivity.TAG, "Cursor size="+c.getCount());
        int i=0;
        while (c.moveToNext()) {
            s[i++]=c.getString(1);
        }
        c.close();
        return s;

    }

    public String getVertretungsplanLink(final String klasse) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT VERTRETUNGSPLAN FROM klassen WHERE KLASSE='"+klasse+"' ORDER BY KLASSE ASC LIMIT 1" , null);
        String[] s = new String[c.getCount()];
        Log.d(TabActivity.TAG, "Cursor size="+c.getCount());
        int i=0;
        while (c.moveToNext()) {
            s[i++]=c.getString(0);
        }
        c.close();
        if (s.length>0) {
            Log.d(TabActivity.TAG," getVertretungsplanLink von "+klasse+" return:"+s[0]);
            return s[0];
        }
        else {
            Log.d(TabActivity.TAG," getVertretungsplanLink von "+klasse+" return: nix");
            return "";
        }
    }
    public String getLehrerVertretungsplanLink(final String lehrer) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT VERTRETUNGSPLAN FROM lehrer WHERE SHORT='"+lehrer+"'" , null);
        String[] s = new String[c.getCount()];
        Log.d(TabActivity.TAG, "Cursor size="+c.getCount());
        int i=0;
        while (c.moveToNext()) {
            s[i++]=c.getString(0);
        }
        c.close();
        if (s.length>0) {
            Log.d(TabActivity.TAG," getVertretungsplanLink von "+lehrer+" return:"+s[0]);
            return s[0];
        }
        else {
            Log.d(TabActivity.TAG," getVertretungsplanLink von "+lehrer+" return: nix");
            return "";
        }
    }

    public String getStundenplanLink(final String klasse) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT STUNDENPLAN FROM klassen WHERE KLASSE='"+klasse+"' ORDER BY KLASSE ASC LIMIT 1" , null);
        String[] s = new String[c.getCount()];
        Log.d(TabActivity.TAG, "Cursor size="+c.getCount());
        int i=0;
        while (c.moveToNext()) {
            s[i++]=c.getString(0);
        }
        c.close();
        if (s.length>0) {
            return s[0];
        }
        else return "";
    }
    public String getLehrerStundenplanLink(final String lehrer) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT STUNDENPLAN FROM lehrer WHERE SHORT='"+lehrer+"'" , null);
        String[] s = new String[c.getCount()];
        Log.d(TabActivity.TAG, "Cursor size="+c.getCount());
        int i=0;
        while (c.moveToNext()) {
            s[i++]=c.getString(0);
        }
        c.close();
        if (s.length>0) {
            return s[0];
        }
        else return "";
    }
    /**
     * herausfinden der Klassenlehrer Email Addresse
     * @param klasse klasse
     * @return string (EMail Adresse)
     * @author Tuttas
     */

    public String getLehrer(String klasse) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT ID_LEHRER FROM klassen WHERE KLASSE='" + klasse + "' LIMIT 1" , null);
        if (c.getCount()==0) {
            c.close();
            return null;
        }
        String[] s = new String[c.getCount()];
        int i=0;
        while (c.moveToNext()) {
            s[i++]=c.getString(0);
        }

        Log.d(TabActivity.TAG, "Klassenlehrer ist :"+s[0]);
        return s[0];


    }

}
