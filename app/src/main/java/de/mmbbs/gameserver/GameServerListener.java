package de.mmbbs.gameserver;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Schnittstelle zum GameServer
 * Kommandos die empfangen werden !
 * @author Jörg
 *
 */
public interface GameServerListener {

	void updateLogin(JSONObject obj);

	void updateResendLogin(JSONObject obj);

	void updateRegister(JSONObject obj);

	void updateRequest(JSONObject obj);

	
	void updateDisconnect();

	void connected();

	void connectionError();


}
