package de.mmbbs.tictactoetournament.game;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class AnimatedObject extends Object2D {

	private int segments;
	private int ticks;
	private int frame;
	Rect srcRect;
	
	public AnimatedObject(int id, Context context,int segments) {
		super(id, context);
		this.segments=segments;
		rect = new Rect(x,y,x+bitmap.getWidth()/segments,y+bitmap.getHeight());
		frame=0;
		srcRect = new Rect(0,0,bitmap.getWidth()/segments,bitmap.getHeight());
	}

	@Override
	public int getWidth() {
		return super.getWidth()/segments;
	}

	@Override
	public void setPosition(int xPos, int yPos) {
		super.setPosition(xPos, yPos);
		rect.set(x, y, x+bitmap.getWidth()/segments, y+bitmap.getHeight());
	}

	@Override
	public void paint(Canvas c, Paint p) {
		c.drawBitmap(bitmap, srcRect, rect, p);
	}
	
	public void tick() {
		ticks++;
		if (ticks>=6) {
			ticks=0;
			frame++;
			if (frame>=segments) {
				frame=0;
			}
			srcRect =new Rect(frame*this.getWidth(),0,(frame+1)*this.getWidth(),bitmap.getHeight());
		}
	}

}
