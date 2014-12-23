package de.mmbbs.tictactoetournament.game;

import android.util.Log;

public class Acceleration {

	/**
	 * Beschleunigte Bewergung
	 * @param v0 Anfangsgeschwindigkeit
	 * @param a Beschleunigung
	 */
	
	private int ticks;
	private float distance;
	private int v0;
	private int a;
	private float s0;
	
	public Acceleration (float s0,int v0,int a) {
		this.v0=v0;
		this.a=a;
		this.setS0(s0);
		reset();
	}
	
	public void setS0(float s0) {
		this.s0=s0*100;
		//Log.d ("Ttest","Set Pos to s0="+this.s0);
	}
	
	public void setV0(int v) {
		v0=v;
	}
	
	public void seta(int acc) {
		a=acc;
	}
	
	public void tick() {
		ticks++;
		distance=s0+a*ticks*ticks/2+a*ticks+a/2+v0*ticks;
		//Log.d ("Ttest","Distance="+distance+ " v0="+v0);
	}
	
	public int getS() {
		return (int) (distance/100);
	}
	
	public void reset() {
		ticks=0;
		distance=0;
	}

	public void bounce() {
		ticks=ticks-2;
		this.tick();
		v0=(-a*ticks)-v0-2*a;
		s0=distance-a/2;
		Log.d ("Ttest","** BOUNCE  ** v0="+v0+" s0="+s0+"ticks="+ticks);
		ticks=0;
		
	}
}
