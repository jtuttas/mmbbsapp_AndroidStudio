package de.mmbbs.four2win;

public interface GameListener {

	public void setScore(int me,int opposit);
	
	public void setProgessBar(int me,int opposit);

    void won(Player currentPlayer,Player me);

    void timeout(Player currentPlayer,Player me);

    void drawn();

    void turn(int xi);
}
