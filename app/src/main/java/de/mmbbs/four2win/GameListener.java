package de.mmbbs.four2win;

public interface GameListener {

	public void showDialog(String msg);
	
	public void setLeftPlayer(Player p);
	
	public void setRightPlayer(Player p);
	
	public void setScore(int left,int right);
	
	public void setProgessBar(int left,int right);
}
