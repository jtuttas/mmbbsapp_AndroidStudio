package de.mmbbs.four2win;

import android.graphics.Canvas;
import android.util.Log;

public class Runner implements Runnable {

	Thread runner;
	Leinwand leinwand;
	boolean running = false;
	long start,stop,diff;
	
	public Runner(Leinwand l) {
		leinwand =l;
	}
	
	public void start() {
		running = true;
		runner = new Thread(this);
		runner.start();
	}

	public void stop() {
		running=false;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Canvas c = null;
		while (running) {
			c=null;
			try {
				c=leinwand.getHolder().lockCanvas();
				synchronized(leinwand.getHolder()) {
					start = System.currentTimeMillis();
					leinwand.update();
					if (c!=null) {
						leinwand.render(c);
					
					}
					stop = System.currentTimeMillis();
					diff=stop-start;
					//Log.d(Main.TAG, "diff="+diff);
				}
			}
			finally {
				if (c != null) {
					leinwand.getHolder().unlockCanvasAndPost(c);
				}				
			}
			try {
				if ((15-diff)>0)
				Thread.sleep(15-diff);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
