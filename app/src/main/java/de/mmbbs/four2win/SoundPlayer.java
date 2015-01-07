package de.mmbbs.four2win;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

import de.mmbbs.R;

/**
 * Created by Jörg on 07.01.2015.
 */
public class SoundPlayer {


    public static enum Sounds {GAMEOVER,REQUEST,BOUNCE};

    private final MediaPlayer playerGameOver;
    private final MediaPlayer playerRequest;
    private final MediaPlayer playerBounce;

    public SoundPlayer(Context c) {
        playerGameOver = MediaPlayer.create(c, R.raw.gameover);
        playerRequest = MediaPlayer.create(c, R.raw.alert);
        playerBounce = MediaPlayer.create(c, R.raw.bounce);
    }

    public void play(Sounds s) {
        switch (s) {
            case GAMEOVER:
                    playerGameOver.start();
                break;
            case REQUEST:
                    playerRequest.start();
                break;
            case BOUNCE:
                    playerBounce.start();
        }
    }
}
