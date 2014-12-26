package de.mmbbs.four2win;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Object2D {

	protected int x=0;
	protected int y=0;
	protected Bitmap bitmap;
	protected Rect rect;
	public float getX;
	public float getY;
	
	public Object2D (int id,Context context) {
		bitmap = BitmapFactory.decodeResource(context.getResources(),id);
		rect = new Rect(x,y,x+bitmap.getWidth(),y+bitmap.getHeight());
	}
	
	public void resize(int width,int height) {
		bitmap = bitmap.createScaledBitmap(bitmap, width, height, false);
	}
	
	public void setPosition(int xPos, int yPos) {
		x=xPos;
		y=yPos;
		rect.set(x, y, x+bitmap.getWidth(), y+bitmap.getHeight());
	}
	
	public void paint(Canvas c,Paint p) {
		c.drawBitmap(bitmap, x, y, p);
	}
	
	
	public boolean dotInObject(int x,int y) {
		return rect.intersects(x, y, x, y);
	}
	
	public boolean collide(Object2D o) {
		return rect.intersects(rect, o.getRect());
	}

	public Rect getRect() {
		// TODO Auto-generated method stub
		return rect;
	}

	public int getWidth() {
		return bitmap.getWidth();
	}
	
	public int getHeight() {
		return bitmap.getHeight();
	}
	
	public void left(int i) {
		// TODO Auto-generated method stub
		this.setPosition(x-i, y);
	}

	public void right(int i) {
		// TODO Auto-generated method stub
		this.setPosition(x+i, y);
	}

	public void up(int i) {
		// TODO Auto-generated method stub
		this.setPosition(x, y-i);
	}
	public void down(int i) {
		// TODO Auto-generated method stub
		this.setPosition(x, y+i);
	}

	public float getX() {
		// TODO Auto-generated method stub
		return x;
	}

	public float getY() {
		// TODO Auto-generated method stub
		return y;
	}
}
