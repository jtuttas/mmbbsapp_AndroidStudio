package de.mmbbs.gameserver;

import org.json.JSONObject;

public interface PlayGameListener {

	void updateChat(JSONObject obj);

	void updatePlay(JSONObject obj);

}
