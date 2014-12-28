package de.mmbbs.four2win;

public interface GameListener {

	public void setScore(int left,int right);
	
	public void setProgessBar(int left,int right);

    void won(Player currentPlayer,Player me);

    void turn(int xi);
}
