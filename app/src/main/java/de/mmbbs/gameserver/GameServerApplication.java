package de.mmbbs.gameserver;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import de.mmbbs.R;
import de.mmbbs.io.socket.IOAcknowledge;
import de.mmbbs.io.socket.IOCallback;
import de.mmbbs.io.socket.SocketIO;
import de.mmbbs.io.socket.SocketIOException;
import de.mmbbs.gameserver.ui.FragmentActivity;
import de.mmbbs.gameserver.ui.Main;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class GameServerApplication extends Application implements IOCallback {

	public static final int REQUESTED = 1;
	public static final int REQUEST = 2;
	private String url;
	private GameServerListener listener;
	private Handler handler;
	private SocketIO socket;
	private String user;
	public static String game;
    private int score;
	private GameStates state = GameStates.DISCONNECTED;
	private GameServerApplication instance;
	private ArrayList<User> userlist = new ArrayList<User>();
	private GameChatListener chatlistener;
	private Handler gamechathandler;
	private GameUserListener userlistener;
	private Handler gameuserhandler;
	private GameHighscoreListener highscorelistener;
	private Handler gamehighscorehandler;
	private PlayGameListener gamelistener;
	private Handler gamehandler;
	private ArrayList<JSONObject> missedChatLines = new ArrayList<JSONObject>();
	private JSONObject missedUserUpdate;
	private String from_player, to_player;
	private boolean pendingRequest = false;

	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 * 
	 * A single tracker is usually enough for most purposes. In case you do need
	 * multiple trackers, storing them all in Application object helps ensure
	 * that they are created only once per application instance.
	 */
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
						// roll-up tracking.
	}

	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	private boolean activityVisible;
	private int requestType;

	public GameServerApplication() {
		super();
		instance = this;
	}

	@Override
	public void onCreate() {

		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();

	}

	public synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
					.newTracker("UA-53969267-1") : analytics
					.newTracker(R.xml.global_tracker);
			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}

	public void setPendingrequest(String from, String to, int requestType) {
		if (from == null) {
			pendingRequest = false;
		} else {
			pendingRequest = true;
		}
		from_player = from;
		to_player = to;
		this.requestType = requestType;
	}

	public String getPendingRequestFromPlayer() {
		return from_player;
	}

	public String getPendingRequestToPlayer() {
		return to_player;
	}

	public void connect(String url, GameServerListener listener, Handler h) {
		this.handler = h;
		this.url = url;
		this.listener = listener;
		try {
			socket = new SocketIO(url, this);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

	}

	public void add(User object) {
		Log.d(Main.TAG, "Add User " + object.getName());
		userlist.add(object);
	}

	public void clearUserlist() {
		userlist.clear();
	}

	public void setServerCallbacks(GameServerListener listener, Handler handler) {
		this.listener = listener;
		this.handler = handler;
	}

	public void setChatCallbacks(GameChatListener chatlistner,
			Handler chathandler) {
		this.chatlistener = chatlistner;
		this.gamechathandler = chathandler;
		for (int i = 0; i < missedChatLines.size(); i++) {
			final JSONObject obj = missedChatLines.get(i);
			gamechathandler.post(new Runnable() {
				@Override
				public void run() {
					chatlistener.updateGameChat(obj);
				}
			});

		}
		missedChatLines.clear();
	}

	public void setGameCallbacks(PlayGameListener gamelistner,
			Handler gamehandler) {
		this.gamelistener = gamelistner;
		this.gamehandler = gamehandler;
	}

	public void setUserCallbacks(GameUserListener userlistner,
			Handler userhandler) {
		this.userlistener = userlistner;
		this.gameuserhandler = userhandler;
		gameuserhandler.post(new Runnable() {

			@Override
			public void run() {
				userlistener.updateUsers(userlist);

			}
		});
	}

	public void setHighscoreCallbacks(GameHighscoreListener highscorelistner,
			Handler highscorehandler) {
		this.highscorelistener = highscorelistner;
		this.gamehighscorehandler = highscorehandler;
	}

	@Override
	public void onDisconnect() {
		Log.d(Main.TAG, "onDisconnect() GameServer");
	}

	@Override
	public void onConnect() {
		Log.d(Main.TAG, "onConnect() GameServer");
		handler.post(new Runnable() {
			@Override
			public void run() {
				listener.connected();
			}
		});
	}

	@Override
	public void onMessage(String data, IOAcknowledge ack) {
		Log.d(Main.TAG, "onMessage()");

	}

	@Override
	public void onMessage(JSONObject json, IOAcknowledge ack) {

	}

	@Override
	public void on(String event, IOAcknowledge ack, Object... args) {
		if (event.compareTo("updatelogin") == 0) {
			String json = args[0].toString();
			try {
				final JSONObject obj = new JSONObject(json);
				if (obj.getBoolean("success")) {
                    score = obj.getInt("score");
                    Log.d(Main.TAG," Die Score ist "+score);
					JSONObject data = new JSONObject();
					try {
						data.put("game", game);
						data.put("player", user);
						data.put("score", 1234);

					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (socket != null)
						socket.emit("adduser", data);
					state = GameStates.LOGGED_IN;
				}
				obj.put("user", obj.optString("user"));
				if (handler != null)
					handler.post(new Runnable() {
						@Override
						public void run() {
							listener.updateLogin(obj);
						}
					});
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} else if (event.compareTo("updateresendlogin") == 0) {
			String json = args[0].toString();
			try {
				final JSONObject obj = new JSONObject(json);

				if (handler != null)
					handler.post(new Runnable() {
						@Override
						public void run() {
							listener.updateResendLogin(obj);
						}
					});
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} else if (event.compareTo("updatehighscores") == 0) {
			String json = args[0].toString();
			try {
				final JSONObject obj = new JSONObject(json);
				JSONArray rows = obj.optJSONArray("rows");
				for (int i = 0; i < rows.length(); i++) {

					rows.optJSONObject(i).put("Name",
							rows.optJSONObject(i).optString("Name"));

				}
				if (gamehighscorehandler != null)
					gamehighscorehandler.post(new Runnable() {
						@Override
						public void run() {
							highscorelistener.updateHighscores(obj);
						}
					});
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} else if (event.compareTo("updateregister") == 0) {
			String json = args[0].toString();
			try {
				final JSONObject obj = new JSONObject(json);
				obj.put("user", obj.optString("user"));
				obj.put("password", obj.optString("password"));
				if (handler != null)
					handler.post(new Runnable() {
						@Override
						public void run() {
							listener.updateRegister(obj);
						}
					});
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} else if (event.compareTo("updategamechat") == 0) {
			Log.d(Main.TAG, "updategamechat");
			String json = args[0].toString();
			try {
				final JSONObject obj = new JSONObject(json);
				obj.put("content", obj.optString("content"));
				obj.put("from_player", obj.optString("from_player"));

				if (gamechathandler != null) {
					gamechathandler.post(new Runnable() {
						@Override
						public void run() {
							chatlistener.updateGameChat(obj);
						}
					});
				} else {
					missedChatLines.add(obj);
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} else if (event.compareTo("updatechat") == 0) {
			String json = args[0].toString();
			try {

				final JSONObject obj = new JSONObject(json);
				obj.put("content", obj.optString("content"));

				if (gamehandler != null)
					gamehandler.post(new Runnable() {
						@Override
						public void run() {
							gamelistener.updateChat(obj);
						}
					});
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} else if (event.compareTo("updaterequest") == 0) {
			Log.d(Main.TAG, "on() updaterequest activityVisible="
					+ activityVisible);
			String json = args[0].toString();
			try {
				final JSONObject obj = new JSONObject(json);
				Log.d(Main.TAG,
						"on() updaterequest command="
								+ obj.optString("command"));
				if (obj.optString("command").compareTo("request") == 0) {
					state = GameStates.REQUEST_PENDING;
					if (!activityVisible) {

						// prepare intent which is triggered if the
						// notification is selected
						Intent intent = new Intent(getApplicationContext(),
								FragmentActivity.class);

						intent.setAction("foo2");
						intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
								| Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("command", "request");
						intent.putExtra("from_player",
								obj.optString("from_player", "unknown"));
                        intent.putExtra("game", obj.optString("game"));

						PendingIntent pIntent = PendingIntent.getActivity(this,
								0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

						Notification n = new Notification.Builder(this)
								.setContentTitle("Games@MMBBS")
								.setVibrate(new long[] { 1000, 1000 })
								.setContentText(
										"Request from "
												+ obj.optString("from_player",
														"unknown")
                                                +" for game "+obj.optString("game","unknown")
                                )
								.setSmallIcon(R.drawable.icon)
								.setContentIntent(pIntent).setAutoCancel(true)
								.build();

						NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

						notificationManager.notify(0, n);
					}
				} else if (obj.optString("command").compareTo("cancelrequest") == 0) {
					state = GameStates.CONNECTED;
					NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					notificationManager.cancel(0);
				} else if (obj.optString("command").compareTo(
						"request_rejected") == 0) {
					state = GameStates.CONNECTED;
				} else if (obj.optString("command").compareTo(
						"request_random_failed") == 0) {
					state = GameStates.CONNECTED;
				} else if (obj.optString("command").compareTo(
						"request_random_failed") == 0) {
					state = GameStates.CONNECTED;
				} else if (obj.optString("command").compareTo(
						"player_not_found") == 0) {
					state = GameStates.CONNECTED;
				} else if (obj.optString("command").compareTo(
						"request_acknowledged") == 0) {
					state = GameStates.PLAY;
				} else if (obj.optString("command").compareTo(
						"request_finished") == 0) {
					state = GameStates.PLAY;
				}

				if (handler != null)
					handler.post(new Runnable() {
						@Override
						public void run() {
							Log.d(Main.TAG, "fire update request!");
							listener.updateRequest(obj);
						}
					});

			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} else if (event.compareTo("updateplay") == 0) {
			String json = args[0].toString();
			try {
				final JSONObject obj = new JSONObject(json);
				if (obj.optString("command").compareTo("timeout") == 0) {
					state = GameStates.LOGGED_IN;
				} else if (obj.optString("command").compareTo("won") == 0) {
					state = GameStates.LOGGED_IN;
				} else if (obj.optString("command").compareTo("penalty") == 0) {
					state = GameStates.LOGGED_IN;
				} else if (obj.optString("command").compareTo("close") == 0) {
					state = GameStates.LOGGED_IN;
				}
				if (gamehandler != null)
					gamehandler.post(new Runnable() {
						@Override
						public void run() {
							gamelistener.updatePlay(obj);
						}
					});
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		} else if (event.compareTo("updatedisconnected") == 0) {
			if (handler != null)
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (state == GameStates.PLAY) {
							listener.updateDisconnect();
							state = GameStates.LOGGED_IN;
						}
					}
				});

		} else if (event.compareTo("updateusers") == 0) {
			Log.d("GameServer", "on() updateusers");
			try {
				String json = args[0].toString();
				final JSONObject obj = new JSONObject(json);
                if (obj.optString("game").compareTo(Main.GAME)==0) {
                    userlist.clear();
                    Iterator<?> keys = obj.keys();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        try {
                            if (obj.get(key) instanceof JSONObject) {
                                JSONObject o = (JSONObject) obj.get(key);
                                User u = new User(o.optString("name"));
                                Log.d(Main.TAG,
                                        "GameServer updateusers name="
                                                + u.getName());
                                if (o.optString("ingame").compareTo("freeplayer") == 0) {
                                    u.setState(UserState.FREE);
                                } else if (o.optString("ingame").compareTo(
                                        "playerpending") == 0) {
                                    u.setState(UserState.PENDING);
                                } else if (o.optString("ingame").compareTo(
                                        "playerplay") == 0) {
                                    u.setState(UserState.IN_GAME);
                                }
                                if (user.compareTo(u.getName()) != 0)
                                    this.add(u);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(Main.TAG, " userlist size=" + userlist.size());

                    if (gameuserhandler != null) {
                        gameuserhandler.post(new Runnable() {

                            @Override
                            public void run() {
                                Log.d(Main.TAG, "updateUser() 2");
                                userlistener.updateUsers(userlist);

                            }
                        });
                    }
                }
                else {
                    Log.d(Main.TAG," kein Handlungsbedarf, war nicht das angezeigte Spiel");
                }
			} catch (JSONException e1) {
				e1.printStackTrace();
			}

		}
	}

	@Override
	public void onError(SocketIOException socketIOException) {
		Log.d(Main.TAG, "onError()");
		if (handler != null)
			handler.post(new Runnable() {
				@Override
				public void run() {
					listener.connectionError();
				}
			});
	}

	public void login(String user, String pw, String game) {
		if (socket != null && socket.isConnected()) {
			this.user = user;
			this.game = game;
			this.pendingRequest = false;
			JSONObject data = new JSONObject();
			try {
				data.put("user", user);
				data.put("password", pw);
				data.put("game", game);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("login", data);
		}
	}

	public void sendChat(String msg) {
		if (socket != null && socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("from_player", user);
				data.put("content_class", "usrmsg");
				data.put("content", msg);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("sendchat", data);
		}

	}

	public void sendGameChat(String msg) {
		if (socket != null && socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("from_player", user);
				data.put("content_class", "usrmsg");
				data.put("content", msg);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("sendgamechat", data);
		}
	}

	public void highscores(int max) {
		if (socket != null && socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("player", user);
				data.put("max", max);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("highscores", data);
		}
	}

	public void sendUserData(String email) {
		if (socket != null && socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("email", email);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("resendlogin", data);
		}
	}

	public void stats(int games_total, int games_won, int games_lost) {
		if (socket != null && socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("user", user);
				data.put("games_total", games_total);
				data.put("games_won", games_won);
				data.put("games_lost", games_lost);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("stats", data);
		}
	}

	public void register(String game, String user, String password,
			String email, String location) {
		if (socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("user", user);
				data.put("password", password);
				data.put("email", email);
				data.put("location", location);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			socket.emit("register", data);
		}

	}

	public void play(String command, JSONObject jo) {
		if (socket != null && socket.isConnected()) {
			try {
				jo.put("game", game);
				jo.put("from_player", user);
				jo.put("command", command);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("play", jo);
		}
	}

	public void quitPaaring() {
		if (socket != null && socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("from_player", user);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("quitpaaring", data);
		}
	}

	public void addScore(int score) {
        this.score+=score;
        Log.d(Main.TAG,"addScore() Die Score ist nun "+this.score);
		if (socket != null && socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("from_player", user);
				data.put("score", score);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("addscore", data);
		}
	}

	public void request(String to_player, String command, String game) {
		Log.d(Main.TAG, "GameServer request to_player=" + to_player
				+ " command=" + command);
		if (command.compareTo("request_acknowledged") == 0) {
			// state=GameStates.PLAY;
			// this.pendingRequest=false;
		} else {
			// state=GameStates.REQUEST_PENDING;
		}
		if (socket != null && socket.isConnected()) {
			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("from_player", user);
				data.put("to_player", to_player);
				data.put("command", command);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (socket != null)
				socket.emit("request", data);
		}
	}

	public void disconnect() {
		Log.d(Main.TAG, "disconnect GameServer socker=" + socket + " state="
				+ state + " pendingRequest=" + pendingRequest);
		if (socket != null && socket.isConnected()
				&& state != GameStates.DISCONNECTED && !pendingRequest) {

			JSONObject data = new JSONObject();
			try {
				data.put("game", game);
				data.put("from_player", user);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			socket.emit("disconnect", data);
			state = GameStates.CONNECTED;
			gamechathandler = null;
			gamehighscorehandler = null;
			gameuserhandler = null;
			handler = null;
			if (socket != null) {
				socket.disconnect();
				Log.d(Main.TAG, "Socket Disconnect");

			}

			Log.d(Main.TAG, "game Server Disconnect");
		}
	}

	public GameStates getState() {
		return state;
	}

	public void setState(GameStates s) {
		state = s;
	}

	public boolean isConnected() {
		if (socket != null)
			return socket.isConnected();
		return false;
	}

	public String getUser() {
		return user;
	}

	public ArrayList<User> getUserList() {
		return userlist;
	}

    public void resetUserList() {
        userlist = new ArrayList<User>();
    }

	public void setActivityVisible(boolean b) {
		activityVisible = b;
	}



    public int getScore() {
        return score;
    }

}
