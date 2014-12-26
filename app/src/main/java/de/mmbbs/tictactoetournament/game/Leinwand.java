package de.mmbbs.tictactoetournament.game;
import de.mmbbs.gameserver.ui.Main;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class Leinwand extends SurfaceView implements OnTouchListener  {

	private float mTouchX=-1;
	private float mTouchY=-1;
	private Runner runner;
	private int ticks;
	private GameBoard board;
	private int destX;
	private int destY;
	private Context context;
	private GameListener listener;
	private Handler handler;
	private Player me;
	private Player oposite;
	private int score;
	private float scale;
	private int timeout;
	private boolean first;
	
	public Leinwand(Context context) {
		super(context);
		this.context=context;
		init(context);
	}
	
	
	public Leinwand(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init(context);
	}
	
	public void setListener(GameListener l, Handler handler) {
		listener=l;
		this.handler=handler;
	}


	@Override
	public void draw(Canvas canvas) {
		Log.d(Main.TAG,"Leinwand.draw()");
		super.draw(canvas);
//		this.render(canvas);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Log.d(Main.TAG,"Leinwand.onDraw()");
		super.onDraw(canvas);
//		this.render(canvas);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	    this.setMeasuredDimension(parentWidth, parentHeight);
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	private void init(Context context) {
		setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        board = new GameBoard(context);
    	runner = new Runner(this);
    	score=100;
    	timeout=500;
	}
	
	public void exit() {
		runner.stop();
		board.init();
		Game.getGameServer().play("close", board.getBoard());
		
	}
	
	
	
	public void update() {
		ticks++;
		me.getObject2d().tick();
		oposite.getObject2d().tick();
		if (me.getState()==PlayerState.TURN) {
			timeout--;
			if (first) {
				listener.setProgessBar(timeout, 0);
			}
			else {
				listener.setProgessBar(0,timeout);
				
			}
			if (timeout<=0 ) {
				Game.getGameServer().play("timeout", board.getBoard());
				Game.getGameServer().stats(1, 0, 1);
				me.setState(PlayerState.LOST);
				oposite.setState(PlayerState.WON);
				this.showDialog("Timeout! "+oposite.getName()+" hat gewonnen!");
			}	
			
			if (ticks%5==0) { // alle 0.1 Sekunden
				Game.getHandler().post(new Runnable() {	
					@Override
					public void run() {
						listener.setScore(score);
					}
				}); 
				if (score>0) score--;
			}
		}
		if (me.getState()==PlayerState.MOVE && me.getObject2d().getX()==destX && me.getObject2d().getY()==destY) {
			Log.d(Main.TAG,"** angekommen Player_x");
			me.getObject2d().setAcceleration(null,null);
			board.setStone(destX, destY, me.getSymbol());
			me.resetPosition();
			if (board.won(me)) {
				me.setState(PlayerState.WON);
				oposite.setState(PlayerState.LOST);
				showDialog(me.getName()+" "+context.getResources().getString(de.mmbbs.R.string.has_won));
				Game.getGameServer().play("won", board.getBoard());
				//Game.getGameServer().play("play", board.getBoard());
				Game.getGameServer().stats(1, 1, 0);
				Game.getGameServer().addScore(score);

			}
			else if (board.penalty()) {
				me.setState(PlayerState.PENALTY);
				oposite.setState(PlayerState.PENALTY);
				showDialog(context.getResources().getString(de.mmbbs.R.string.penalty));
				Game.getGameServer().play("penalty", board.getBoard());
				//Game.getGameServer().play("play", board.getBoard());
				Game.getGameServer().stats(1, 0, 0);
				Game.getGameServer().addScore(score);

			}
			else {
				me.setState(PlayerState.WAIT);
				oposite.setState(PlayerState.TURN);
				timeout=500;
				if (first)	listener.setProgessBar(0, 500);
				else listener.setProgessBar(500, 0);
				Game.getGameServer().play("play", board.getBoard());
			}
		}
		if (oposite.getState()==PlayerState.MOVE && oposite.getObject2d().getX()==destX && oposite.getObject2d().getY()==destY) {
			Log.d(Main.TAG,"** angekommen Player_o");
			oposite.getObject2d().setAcceleration(null,null);
			board.setStone(destX, destY, oposite.getSymbol());
			oposite.resetPosition();
			if (board.won(oposite)) {
				oposite.setState(PlayerState.WON);
				me.setState(PlayerState.LOST);
				showDialog(oposite.getName()+" "+context.getResources().getString(de.mmbbs.R.string.has_won));
				//Game.getGameServer().play("won", board.getBoard());
				Game.getGameServer().stats(1, 0, 1);
			}
			else if (board.penalty()) {
				me.setState(PlayerState.PENALTY);
				oposite.setState(PlayerState.PENALTY);
				showDialog(context.getResources().getString(de.mmbbs.R.string.penalty));
				//Game.getGameServer().play("penalty", board.getBoard());
				Game.getGameServer().stats(1, 0, 0);
				Game.getGameServer().addScore(score);
			}
			else {
				oposite.setState(PlayerState.WAIT);
				me.setState(PlayerState.TURN);
				timeout=500;
				listener.setProgessBar(500, 500);
			}
		}
		
		
	}
	
	


	private void showDialog(final String string) {
		handler.post(new Runnable() {	
			@Override
			public void run() {
				listener.showDialog(string);					}
		}); 
		
	}

	public void render(Canvas g) {
		Paint p = new Paint();
		p.setColor(Color.rgb(164,0,0));
		g.drawRect(0,0,g.getWidth(),g.getHeight(),p);
		if (!isInEditMode()) {
			board.paint(g, p);
			me.paint(g, p);
			oposite.paint(g, p);
		}
	}

	public boolean onTouch(View v, MotionEvent event) {
		if (me.getState()==PlayerState.TURN ) {
			mTouchX = (int)event.getX();
			mTouchY = (int)event.getY();
			Log.d(Main.TAG,"Stone ist "+board.getStone(mTouchX, mTouchY));
			if (board.getStone(mTouchX, mTouchY).compareTo(GameBoard.markfree)==0) {
				destX=board.getStoneX(mTouchX);
				destY=board.getStoneY(mTouchY);
				//board.setStone(destX,destY, GameBoard.markselected);
				Log.d(Main.TAG,"Destination= "+destX+"/"+destY);
		
				int vx=5*(int)(-me.getObject2d().getX()+destX);
			    int vy=5*(int)(-me.getObject2d().getY()+destY);
				me.getObject2d().setAcceleration(
						new Acceleration(me.getObject2d().getX(), vx, 0),
						new Acceleration(me.getObject2d().getY(), vy, 0));
				me.setState(PlayerState.MOVE);
			}
		}
		else if (me.getState()==PlayerState.WAIT){
						
		}
		return true;
	}

	public void reset() {
		this.reset(this.getWidth(),this.getHeight());
	}

	public void reset(int width, int height) {
		if (!isInEditMode()) {
			Log.d(Main.TAG,"Leinwand reset width="+width+" height="+height);
			this.getHolder().setFixedSize(width, width);

			if (width!=0) {
				scale = getResources().getDisplayMetrics().density;
				me.getObject2d().resize((width-10)/3, (width-10)/3);
				oposite.getObject2d().resize((width-10)/3, (width-10)/3);
				board.setDimmension(width, width);
				if (first) {
					me.setPosition(-me.getObject2d().getWidth(), height);
					oposite.setPosition(width, height);
					
				}
				else {
					oposite.setPosition(-oposite.getObject2d().getWidth(), height);
					me.setPosition(width, height);
					
				}
			}
		}
	}


	public void init(boolean firstTurn, String gegner) {
		if (!isInEditMode()) {
			Log.d(Main.TAG,"Init players");
			if (firstTurn) {
				me = new Player(context, Game.getGameServer().getUser(), de.mmbbs.R.drawable.stone_mark_o, de.mmbbs.R.drawable.mark_o,PlayerState.TURN,GameBoard.marko);
				oposite = new Player(context, gegner, de.mmbbs.R.drawable.stone_mark_x, de.mmbbs.R.drawable.mark_x, PlayerState.WAIT,GameBoard.markx);
				listener.setLeftPlayer(me);
				listener.setRightPlayer(oposite);
				first=true;
				listener.setProgessBar(0, 500);
			}
			else {
				oposite = new Player(context, gegner, de.mmbbs.R.drawable.stone_mark_o,de.mmbbs.R.drawable.mark_o,PlayerState.TURN,GameBoard.marko);
				me = new Player(context, Game.getGameServer().getUser(), de.mmbbs.R.drawable.stone_mark_x,de.mmbbs.R.drawable.mark_x,PlayerState.WAIT,GameBoard.markx);
				listener.setLeftPlayer(oposite);
				listener.setRightPlayer(me);
				first=false;
				listener.setProgessBar(500,0);
			}
			board.init();
			listener.setScore(score);
			
	    	runner.start();
		}
	}

	public void moveTo(int turnx, int turny) {
		destX=board.getStoneXbyIndex(turnx);
		destY=board.getStoneYbyIndex(turny);
		Log.d(Main.TAG," moreto() "+turnx+"/"+turny+ " DestXY="+destX+"/"+destY);
		Log.d(Main.TAG,"Destination= "+destX+"/"+destY);

		if (oposite.getState()==PlayerState.TURN) {
			int vx=5*(int)(-oposite.getObject2d().getX()+destX);
		    int vy=5*(int)(-oposite.getObject2d().getY()+destY);
			oposite.getObject2d().setAcceleration(
					new Acceleration(oposite.getObject2d().getX(), vx, 0),
					new Acceleration(oposite.getObject2d().getY(), vy, 0));
			oposite.setState(PlayerState.MOVE);
		}		
	}

	public Player getMe() {
		return me;
	}

	public int getScore() {
		return score;
	}


	public void setPlayerState(PlayerState meState, PlayerState opositeState) {
		me.setState(meState);
		oposite.setState(opositeState);
	}
}
