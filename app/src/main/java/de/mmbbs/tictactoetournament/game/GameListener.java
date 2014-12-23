package de.mmbbs.tictactoetournament.game;

public interface GameListener {

	public void showDialog(String msg);
	
	public void setLeftPlayer(Player p);
	
	public void setRightPlayer(Player p);
	
	public void setScore(int score);
	
	public void setProgessBar(int left,int right);
}
