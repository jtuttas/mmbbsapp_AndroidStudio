package de.mmbbs.gameserver;

public class User {

	private boolean friend=false;
	private String name;
	private UserState state;
	
	public User(String name) {
		this.name=name;
		state=UserState.FREE;
	}
	
	public boolean isFriend() {
		return friend;
	}
	
	public void setFriend(boolean b) {
		friend=b;
	}
	
	public String getName() {
		return name;
	}
	
	public UserState getState() {
		return state;
	}
	
	public void setState(UserState s) {
		
		state=s;
	}
	
	
}
