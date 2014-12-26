package de.mmbbs.gameserver.ui;

public class OneComment {
	public String comment;
	public String player;
	public String me;

	public OneComment(String me,String from_player, String comment) {
		this.comment = comment;
		this.player = from_player;
		this.me=me;
	}

}