package de.mmbbs.four2win;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import de.mmbbs.R;
import de.mmbbs.gameserver.ui.Main;

public class Player {
	private String name;
	private StoneColor stoneColor;
	private Stone stone;
	private float touchX;
	private float touchY;
	private int orgX,orgY;
	PlayerState state;
	Acceleration a;
    private int stoneWidth;

    public Player (String n, StoneColor c, Context context) {
		name=n;
		stoneColor=c;
		if (c==StoneColor.RED) {
			stone = new Stone(R.drawable.red, context);
		}
		else {
			stone = new Stone(R.drawable.yellow, context);
		}
		state=PlayerState.WAIT;
		a = new Acceleration(0, 10, 20);
	}

	public void paint(Canvas c,Paint p) {
		stone.paint(c, p);
	}

	public StoneColor getStoneColor() {
		return stoneColor;
	}
	
	public Stone getStone() {
		return stone;
	}
	
	public void setStoneColor(StoneColor c,Context context) {
        this.stoneColor=c;
        if (c==StoneColor.RED) {
            stone = new Stone(R.drawable.red, context);
        }
        else {
            stone = new Stone(R.drawable.yellow, context);
        }
        stone.resize(stoneWidth,stoneWidth);

    }
	
	public void initPostition(int i, int j) {
		orgX=i;
		orgY=j;
		stone.setPosition(i, j)
;	}

	public void setTouched(float mTouchX, float mTouchY) {
		state=PlayerState.MOVE;
		touchX=mTouchX;
		touchY=mTouchY;
		
	}

	public boolean hit(int mTouchX, int mTouchY) {
		return stone.dotInObject(mTouchX, mTouchY);
	}

	public PlayerState getState() {
		return state;
	}

	public void tick() {
		if (state==PlayerState.FALL) {
			a.tick();
			stone.setPosition((int) stone.getX(), a.getS());
		}
	}
	
	public void movePostition(float mTouchX) {
		Log.d(Main.TAG," touchX="+touchX+" mtocuhX="+mTouchX);
		stone.setPosition((int)(stone.getX()-(touchX-mTouchX)), (int)stone.getY());
		touchX=mTouchX;
	}

	public void setWidth(int w) {
        stoneWidth=w;
		stone.resize(w,w);
		
	}

	public void setState(PlayerState s) {
		state=s;		
	}

	public void reset() {
		a.reset();
		state=PlayerState.WAIT;
		stone.setPosition(orgX, orgY);
		Log.d(Main.TAG,"reset to Position x="+orgX+" y="+orgY);
		
	}

	public String getName() {

		return name;
	}

	public void setName(String string) {
		name=string;
		
	}

	
}
