package de.mmbbs.four2win;

import de.mmbbs.four2win.Game;
import de.mmbbs.gameserver.GameManagementActivity;
import de.mmbbs.gameserver.ui.Main;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

public class Leinwand extends SurfaceView implements OnTouchListener  {

	public static final int PLACE=1;
    public static final int WAIT=3;
	public static final int OVER=2;
	
	
	public float mTouchX=-1;
	public float mTouchY=-1;
	Runner runner;
	int ticks;
	 Player player1,player2,currentPlayer;

	GameBoard gameBoard;
	private GameListener listener;
    private int state;

	private Context context;
    private int score;
    private int storedXi=-1;
    private float lastTouchX=-1;
    private int moveCounter;
    private Vibrator vibrator;


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
        gameBoard = GameBoard.getInstance(context);
        reset();
        runner = new Runner(this);

	}
	

	
	public void exit() {
		runner.stop();

	}
	
	public void update() {
		ticks++;
		currentPlayer.tick();
        if (player1==currentPlayer) currentPlayer.decTicker();
        if (currentPlayer.getTicker()<=0 && state!=Leinwand.OVER) {
            Game.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.timeout(currentPlayer, player1);
                }
            });
            setState(Leinwand.OVER);

        }
        if (currentPlayer==player1) {
            Game.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.setProgessBar(player1.getTicker(),0);
                }
            });
        }
        else {
            Game.getHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null)
                        listener.setProgessBar(0,500);
                }
            });

        }
		if (gameBoard.ground(currentPlayer)) {
            currentPlayer.reset();
            //vibrator.vibrate(150);
            moveCounter=gameBoard.elementWidth/2;
			if (gameBoard.won()==currentPlayer.getStoneColor()) {
				this.setState(OVER);
                GameManagementActivity.soundPlayer.play(SoundPlayer.Sounds.GAMEOVER);
				Game.getHandler().post(new Runnable() {
                    @Override
                    public void run() {

                        if (listener != null) listener.won(currentPlayer, player1);
                    }
                });
			}
            else if (gameBoard.drawn()) {
                GameManagementActivity.soundPlayer.play(SoundPlayer.Sounds.GAMEOVER);
                this.setState(OVER);
                Game.getHandler().post(new Runnable() {
                    @Override
                    public void run() {

                        if (listener!=null) listener.drawn();
                    }
                });
            }
            else {
                if (state == PLACE) setState(WAIT);
                else setState(PLACE);
                currentPlayer.decScore();
                GameManagementActivity.soundPlayer.play(SoundPlayer.Sounds.BOUNCE);
                if (player1 == currentPlayer) {
                    currentPlayer = player2;
                    player2.resetTicker();
                } else {
                    currentPlayer = player1;
                    player1.resetTicker();
                }
                Game.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (listener != null) listener.setScore(player1.getScore(), player2.getScore());
                    }
                });

                if (storedXi != -1) this.placeStone(storedXi);
            }
            Log.d(Main.TAG, " aktueller Spieler ist nun " + currentPlayer.getName());
		}


	}

    public void setState(int s) {

        Log.d(Main.TAG," setzte Leinwand state auf "+s);
        state=s;
    }

    public void render(Canvas g) {
		Paint p = new Paint();
		p.setColor(Color.parseColor("#2F3049"));
		p.setAntiAlias(true);
		p.setTextSize((float) 15.0);
		g.drawRect(0, 0, this.getWidth(), this.getHeight(), p);
		currentPlayer.paint(g, p);
        gameBoard.paint(g, p);
	}

	public boolean onTouch(View v, MotionEvent event) {

		if (state==PLACE) {
            mTouchX = (int)event.getX();
            mTouchY = (int)event.getY();
            int yOffset=0;
            if (lastTouchX!=-1) {
                //Log.d(Main.TAG," moveCounter="+moveCounter+" elementWindth="+gameBoard.elementWidth);
                // Bewegung nach rechts
                if (mTouchX > lastTouchX) {
                    moveCounter+=(mTouchX-lastTouchX);
                }
                // Bewegung nach links
                else if (mTouchX < lastTouchX) {
                    moveCounter-=(lastTouchX-mTouchX);
                }
                if (moveCounter<=0 || moveCounter>=gameBoard.elementWidth) {
                    Log.d(Main.TAG," Hubbel! movecounter="+moveCounter+ " elementwidth="+gameBoard.elementWidth);
                    vibrator.vibrate(75);
                    currentPlayer.getStone().setPositionOffset(0,-10);
                    if (moveCounter<=0) moveCounter+=gameBoard.elementWidth;
                    else if (moveCounter>=gameBoard.elementWidth) moveCounter-=gameBoard.elementWidth;
                }
                else {
                    currentPlayer.getStone().setPositionOffset(0,0);
                }
            }
            lastTouchX=mTouchX;
            //Log.d(Main.TAG," x="+mTouchX+" y="+mTouchY+" this.width="+this.getWidth());
            if (mTouchX>gameBoard.width-currentPlayer.getStone().getWidth()/2) mTouchX=gameBoard.width-currentPlayer.getStone().getWidth()/2;
            if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                //Log.d(Main.TAG," ACTION_DOWN x="+mTouchX+" y="+mTouchY);
                if (currentPlayer.getState() == PlayerState.WAIT) {
                    if (currentPlayer.hit((int) mTouchX, (int) mTouchY))
                        currentPlayer.setTouched(mTouchX, mTouchY);
                }

            } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                //Log.d(Main.TAG," ACTION_UP x="+mTouchX+" y="+mTouchY);
                lastTouchX=-1;
                if (currentPlayer.getState() == PlayerState.MOVE) {
                    if (gameBoard.getAboveElement(currentPlayer.getStone().getX() + currentPlayer.getStone().getWidth() / 2) == StoneColor.FREE) {
                        currentPlayer.setState(PlayerState.FALL);
                        final int xPos = (int) ((currentPlayer.getStone().getX() + currentPlayer.getStone().getWidth() / 2) / currentPlayer.getStone().getWidth()) * currentPlayer.getStone().getWidth();
                        currentPlayer.getStone().setPosition(xPos, (int) currentPlayer.getStone().getY());
                        Game.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) listener.turn(xPos/gameBoard.elementWidth);
                            }
                        });

                    } else {
                        currentPlayer.reset();
                    }
                }
            } else {
                if (currentPlayer.getState() == PlayerState.MOVE) {
                    currentPlayer.movePostition(mTouchX);
                }
            }
        }
        else Log.d(Main.TAG," dieser Spieler ist nicht am zug aktueller Spieler ist"+currentPlayer.getName()+" state="+state);
		return true;
	}

	public void reset() {

        this.reset(this.getWidth(),this.getHeight());
	}

	public void reset(int width, int height) {
		if (width!=0) {
            lastTouchX=-1;
			player1.setWidth(width/7);
			player2.setWidth(width/7);
			player1.initPostition(0,0);
			player2.initPostition(width-player2.getStone().getWidth(),0);
			Log.d(Main.TAG," reset() width="+width+" player1width="+player1.getStone().getWidth());
			gameBoard.setDimmension(player1.getStone().getWidth()*7,player1.getStone().getWidth()*6);
			gameBoard.setPosition(0,player1.getStone().getHeight());
            moveCounter=gameBoard.elementWidth/2;
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




    public void placeStone(int xi) {
        Log.d(Main.TAG,"Gegner setzt Stein an Stell xi="+xi);
        if (state==PLACE) {
            // hier fällt der Stein wohl noch
            storedXi=xi;
        }
        else {
            storedXi=-1;
            currentPlayer.getStone().setPosition(xi * gameBoard.elementWidth, (int) currentPlayer.getStone().getY());
            if (gameBoard.getAboveElement(currentPlayer.getStone().getX() + currentPlayer.getStone().getWidth() / 2) == StoneColor.FREE) {
                currentPlayer.setState(PlayerState.FALL);
                final int xPos = (int) ((currentPlayer.getStone().getX() + currentPlayer.getStone().getWidth() / 2) / currentPlayer.getStone().getWidth()) * currentPlayer.getStone().getWidth();
                currentPlayer.getStone().setPosition(xPos, (int) currentPlayer.getStone().getY());

            } else {
                currentPlayer.reset();
            }
        }
    }


    public void setStart(boolean firstTurn) {
        if (firstTurn) {
            this.setState(PLACE);
            currentPlayer=player1;
            player1.setStoneColor(StoneColor.RED,context);
            player2.setStoneColor(StoneColor.YELLOW,context);
        }
        else {
            this.setState(WAIT);
            currentPlayer=player2;
            player2.setStoneColor(StoneColor.RED, context);
            player1.setStoneColor(StoneColor.YELLOW,context);
        }
        player1.setScore(42);
        player2.setScore(42);
        player1.resetTicker();
        player2.resetTicker();
        Game.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) listener.setScore(player1.getScore(), player2.getScore());
            }
        });

        Log.d(Main.TAG,"setStart() aktuller Spieler ist "+currentPlayer.getName()+" game state="+state);
        gameBoard.init();
        vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        runner.start();
    }

    public int getState() {
        return state;
    }
}
