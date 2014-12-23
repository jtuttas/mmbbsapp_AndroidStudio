package de.mmbbs.tictactoetournament.game;

import de.mmbbs.tictactoetournament.Main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;

public class Player {

	private String name;
	private Object2D object2d;
	private PlayerState state;
	private int startX;
	private int startY;
	private String symbol;
	private int ressourceSymbol;
	
	public Player(Context context,String name,int ressource_stone,int ressource_symbol,PlayerState state,String symbol) {
		this.name=name;
		object2d= new Object2D(ressource_stone, context);
		this.ressourceSymbol=ressource_symbol;
		this.state=state;
		this.symbol=symbol;
		
	}
	
	public void setState(PlayerState state) {
		this.state=state;
	}

	public String getName() {
		return name;
	}

	public Object2D getObject2d() {
		return object2d;
	}

	public PlayerState getState() {
		return state;
	}
	
	public void paint(Canvas c,Paint p) {
		object2d.paint(c, p);
	}

	public void setPosition(int i, int j) {
		object2d.setPosition(i, j);
		startX=i;
		startY=j;
	}
	
	public void resetPosition() {
		object2d.setPosition(startX, startY);
		
	}

	public String getSymbol() {
		return this.symbol;
	}


	public int getIcon() {
		return ressourceSymbol;
	}
	
	
}
