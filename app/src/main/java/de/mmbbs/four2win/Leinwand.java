package de.mmbbs.four2win;

import de.mmbbs.four2win.Game;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class Leinwand extends SurfaceView implements OnTouchListener  {

	public static final int INIT=0;
	public static final int PLAY=1;
	public static final int OVER=2;
	
	
	public float mTouchX=-1;
	public float mTouchY=-1;
	Runner runner;
	int ticks;
	Player player1,player2,currentPlayer;
	int score1,score2;
	GameBoard gameBoard;
	private GameListener listener;
	private int state;
	private Context context;

	
	
	public Leinwand(Context context) {
		super(context);
		this.context=context;
		Log.d(Main.TAG, "Konstruktor Leinwand");
		init(context);
	}
	
	public Leinwand(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		Log.d(Main.TAG, "Konstruktor Leinwand");
		init(context);
	}
	

	
	private void init(Context context) {
		setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        player1 = new Player("",StoneColor.RED,context);
        player2 = new Player("",StoneColor.YELLOW,context);
        int pl = PreferenceManager.getDefaultSharedPreferences(context).getInt("player", 1);
        if (pl==1) {
        	currentPlayer=player1;
        }
        else {
        	currentPlayer=player2;
        }
        gameBoard = GameBoard.getInstance(context);
        score1=PreferenceManager.getDefaultSharedPreferences(context).getInt("score1", 42);;
        score2=PreferenceManager.getDefaultSharedPreferences(context).getInt("score2", 42);
        state=PreferenceManager.getDefaultSharedPreferences(context).getInt("state", INIT);
        Log.d(Main.TAG,"Game State="+state);
        reset();
        runner = new Runner(this);
        runner.start();
	}
	
	public void setState(int s) {
		state=s;
	}
	
	public void newGame() {
		this.setState(PLAY);
		currentPlayer=player1;
        score1=42;
        score2=42;
	}
	
	public void onStop() {
		Editor e=PreferenceManager.getDefaultSharedPreferences(context).edit();
		e.putInt("state", state);
		if (currentPlayer==player1) {
			e.putInt("player", 1);
		}
		else {
			e.putInt("player", 2);
		}
		e.putInt("score1", score1);
		e.putInt("score2", score1);
		e.commit();
		
	}
	
	public int getGameState() {
		return state;
	}
	
	public void exit() {
		runner.stop();
	}
	
	public void update() {
		ticks++;
		currentPlayer.tick();
		if (gameBoard.ground(currentPlayer)) {
			if (gameBoard.won()==currentPlayer.getStoneColor()) {
				this.setState(OVER);
				Game.getHandler().post(new Runnable() {	
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (listener!=null) listener.showDialog("Spieler "+currentPlayer.getName()+" hat gewonnen!");
					}
				}); 
			}
			currentPlayer.reset();
			if (player1==currentPlayer) {
				score1--;
				currentPlayer=player2;
				Game.getHandler().post(new Runnable() {	
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (listener!=null) listener.setScore(score1,score2);
					}
				}); 
			}
			else {
				score2--;
				currentPlayer=player1;
				Game.getHandler().post(new Runnable() {	
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (listener!=null) listener.setScore(score2,score2);
					}
				}); 
			}
		}		
	}
	
	public void render(Canvas g) {
		Paint p = new Paint();
		p.setColor(Color.parseColor("#2F3049"));
		p.setAntiAlias(true);
		p.setTextSize((float) 15.0);
		g.drawRect(0, 0, this.getWidth(), this.getHeight(), p);
		gameBoard.paint(g, p);
		currentPlayer.paint(g, p);
	}

	public boolean onTouch(View v, MotionEvent event) {
		mTouchX = (int)event.getX();
		mTouchY = (int)event.getY();
		//Log.d(Main.TAG," x="+mTouchX+" y="+mTouchY+" this.width="+this.getWidth());

		if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
			//Log.d(Main.TAG," ACTION_DOWN x="+mTouchX+" y="+mTouchY);
			if (currentPlayer.getState()==PlayerState.WAIT){
				if (currentPlayer.hit((int)mTouchX,(int)mTouchY)) currentPlayer.setTouched(mTouchX,mTouchY);
			}
		
		} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
			//Log.d(Main.TAG," ACTION_UP x="+mTouchX+" y="+mTouchY);
			if (currentPlayer.getState()==PlayerState.MOVE) {
				if (gameBoard.getAboveElement(currentPlayer.getStone().getX()+currentPlayer.getStone().getWidth()/2)==StoneColor.FREE){
					currentPlayer.setState(PlayerState.FALL);
					int xPos=(int)((currentPlayer.getStone().getX()+currentPlayer.getStone().getWidth()/2)/currentPlayer.getStone().getWidth())*currentPlayer.getStone().getWidth();
					currentPlayer.getStone().setPosition(xPos, (int) currentPlayer.getStone().getY());
				}
				else {
					currentPlayer.reset();
				}
			}
		}
		else {
			if (currentPlayer.getState()==PlayerState.MOVE) {
				currentPlayer.movePostition(mTouchX);
			}
		}
		return true;
	}

	public void reset() {
		this.reset(this.getWidth(),this.getHeight());
	}

	public void reset(int width, int height) {
		if (width!=0) {
			player1.setWidth(width/7);
			player2.setWidth(width/7);
			player1.initPostition(0,0);
			player2.initPostition(width-player2.getStone().getWidth(),0);
			Log.d(Main.TAG," reset() width="+width+" player1width="+player1.getStone().getWidth());
			gameBoard.setDimmension(player1.getStone().getWidth()*7,player1.getStone().getWidth()*6);
			gameBoard.setPosition(0,player1.getStone().getHeight());
		}
	}

	public void setGameListener(GameListener l) {
		listener=l;
		
	}

	public Player getPlayer(int num) {
		if (num==1)	return player1;
		else return player2;
	}

	public Object getGameBoard() {
		return gameBoard;
	}
}
