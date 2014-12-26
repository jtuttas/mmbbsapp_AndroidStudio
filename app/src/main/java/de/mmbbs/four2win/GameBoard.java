package de.mmbbs.four2win;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import de.mmbbs.R;

public class GameBoard {
	private StoneColor[][]board=new StoneColor[7][6];
	private Object2D emptyElement,redElement,yellowElement;
	int width,height;
	int elementWidth,elementHeight;
	private int xPos;
	private int yPos;
	private static GameBoard instance;
	
	private GameBoard(Context context) {
		emptyElement=new Object2D(R.drawable.empty, context);
		redElement=new Object2D(R.drawable.emptyred, context);
		yellowElement=new Object2D(R.drawable.emptyyellow, context);
	}
	
	public void init() {
		for (int x=0;x<7;x++) {
			for (int y=0;y<6;y++) {
				board[x][y]=StoneColor.FREE;
			}
		}
	}

	public void setPosition(int xPos,int yPos) {
		this.xPos=xPos;
		this.yPos=yPos;
	}
	
	
	public StoneColor getAboveElement(float x) {
		Log.d(Main.TAG," x="+x+" xPos="+xPos+"  elementWidth="+elementWidth);
		int ix=(int)((x+xPos)/elementWidth);
		return board[ix][0];
	}
	
	public StoneColor won() {
		for (int x=0;x<7;x++) {
			for (int y=0;y<6;y++) {
				if (board[x][y]!=StoneColor.FREE) {
					if (testRow(x,y,board[x][y])!=StoneColor.FREE) return board[x][y];
					if (testColumn(x,y,board[x][y])!=StoneColor.FREE) return board[x][y];
					if (testDiag1(x,y,board[x][y])!=StoneColor.FREE) return board[x][y];
					if (testDiag2(x,y,board[x][y])!=StoneColor.FREE) return board[x][y];
				}
			}
		}
		return StoneColor.FREE;
	}
	
	private StoneColor testDiag1(int x, int y, StoneColor stoneColor) {
		try {
			if (board[x+1][y+1]==stoneColor &&
				board[x+2][y+2]==stoneColor &&
				board[x+3][y+3]==stoneColor
			) return stoneColor;
				return StoneColor.FREE;
		}
		catch (ArrayIndexOutOfBoundsException a) {
			return StoneColor.FREE;
		}
	}

	private StoneColor testDiag2(int x, int y, StoneColor stoneColor) {
		try {
			if (board[x-1][y+1]==stoneColor &&
				board[x-2][y+2]==stoneColor &&
				board[x-3][y+3]==stoneColor
			) return stoneColor;
				return StoneColor.FREE;
		}
		catch (ArrayIndexOutOfBoundsException a) {
			return StoneColor.FREE;
		}
	}

	private StoneColor testColumn(int x, int y, StoneColor stoneColor) {
		try {
			if (board[x][y+1]==stoneColor &&
					board[x][y+2]==stoneColor &&
					board[x][y+3]==stoneColor
						) return stoneColor;
				return StoneColor.FREE;
		}
		catch (ArrayIndexOutOfBoundsException a) {
			return StoneColor.FREE;
		}
	}

	private StoneColor testRow(int x, int y, StoneColor stoneColor) {
		try {
			if (board[x+1][y]==stoneColor &&
				board[x+2][y]==stoneColor &&
				board[x+3][y]==stoneColor
					) return stoneColor;
			return StoneColor.FREE;
		}
		catch (ArrayIndexOutOfBoundsException a) {
			return StoneColor.FREE;
		}
	}

	public void paint(Canvas c,Paint p) {
		for (int x=0;x<7;x++) {
			for (int y=0;y<6;y++) {
				if (board[x][y]==StoneColor.FREE) {
					emptyElement.setPosition(xPos+x*elementWidth, yPos+y*elementHeight);
					emptyElement.paint(c, p);
				}
				else if (board[x][y]==StoneColor.RED) {
					redElement.setPosition(xPos+x*elementWidth, yPos+y*elementHeight);
					redElement.paint(c, p);
				}
				else if (board[x][y]==StoneColor.YELLOW) {
					yellowElement.setPosition(xPos+x*elementWidth, yPos+y*elementHeight);
					yellowElement.paint(c, p);
				}
			}
		}
	}

	

	public void setDimmension(int width, int height) {
		Log.d(Main.TAG," setDimension width="+width);
		this.width=width;
		this.height=height;
		this.elementWidth=width/7;
		this.elementHeight=height/6;
		
		emptyElement.resize(elementWidth, elementHeight);
		redElement.resize(elementWidth, elementHeight);
		yellowElement.resize(elementWidth, elementHeight);
		
	}


	public boolean ground(Player player) {
		int py=(int) (player.getStone().getY()+player.getStone().getHeight());
		int px=(int) player.getStone().getX();
		
		if (elementWidth==0 || elementHeight==0) return false;
		
		try {
			if (board[(px-xPos)/elementWidth][(py-yPos)/elementHeight]!=StoneColor.FREE) {
				board[(px-xPos)/elementWidth][((py-yPos)/elementHeight)-1]=player.getStoneColor();
				return true;
			}
		}
		catch (ArrayIndexOutOfBoundsException e) {
			
		}
		if (py>yPos+height) {
			Log.d(Main.TAG," Stein auf Boden gefallen!");
			board[(px-xPos)/elementWidth][5]=player.getStoneColor();
			return true;
			
		}
		return false;
	}

	public static GameBoard getInstance(Context context) {
		// TODO Auto-generated method stub
		if (instance==null) {
			instance = new GameBoard(context);
			instance.init();
		}
		return instance;
	}


	
	
}
