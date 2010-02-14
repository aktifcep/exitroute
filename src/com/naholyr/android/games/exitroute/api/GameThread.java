package com.naholyr.android.games.exitroute.api;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.ViewGroup;

import com.naholyr.android.games.exitroute.R;
import com.naholyr.android.games.exitroute.view.GameView;
import com.naholyr.android.games.exitroute.view.TargetView;

public class GameThread extends Thread {

	// Game has not yet started
	public static final int STATE_NOT_STARTED = 0;
	// Game is ready to start
	public static final int STATE_READY = 1;
	// Game is running
	public static final int STATE_RUNNING = 2;
	// A player is currently playing his turn, restore to RUNNING to play next
	// turn
	public static final int STATE_PLAYING_TURN = 3;
	// Game is paused (settings, dialog, ...)
	public static final int STATE_PAUSED = 4;
	// A player has won the game
	public static final int STATE_WIN = 5;

	private Object holder;
	private Context context;
	private Handler handler;
	private GameParameters gameParameters;
	private boolean running;

	public int state;
	private Player currentPlayer;
	private int currentPlayerIndex;

	public static interface ErrorListener {
		public void handle(Throwable e);
	}

	public ErrorListener errorListener = null;

	public GameThread(Object holder, Context context, Handler handler) {
		this.holder = holder;
		this.context = context;
		this.handler = handler;

		running = false;
		gameParameters = null;
		state = STATE_NOT_STARTED;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

	public void setSurfaceSize(int width, int height) {
		// TODO Auto-generated method stub
	}

	public void setState(int state) {
		this.state = state;
	}

	public void initialize(GameParameters gameParameters) {
		this.gameParameters = gameParameters;
		setCurrentPlayer(0);
	}

	public boolean initialized() {
		return gameParameters != null;
	}

	public void restoreState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

	public void pause() {
		// TODO Auto-generated method stub

	}

	public void saveState(Bundle outState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		while (running) {
			/*Canvas c = null;
			try {*/
				synchronized (this) {
					if (!initialized()) {
						continue;
					}
					if (state == STATE_RUNNING) {
						playTurn();
					}
					if (state == STATE_WIN) {
						endOfGame(currentPlayer.name + " est le vainqueur !");
					}
				}
			/*} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					surfaceHolder.unlockCanvasAndPost(c);
				}
			}*/
		}
	}

	private void sendMessage(Bundle b) {
		Message msg = handler.obtainMessage();
		msg.setData(b);
		handler.sendMessage(msg);
	}
	
	private void endOfGame(String message) {
		Bundle b = new Bundle();
		b.putString("text", message);
		sendMessage(b);
		
		running = false;
	}

	private void playTurn() {
		setState(STATE_PLAYING_TURN);

		Bundle b = new Bundle();
		b.putInt("draw", GameView.DRAW_PLAYER_TARGETS);
		sendMessage(b);
	}

	private void setCurrentPlayer(int playerIndex) {
		if (playerIndex < 0) {
			playerIndex = 0;
		}
		if (playerIndex >= gameParameters.players.length) {
			playerIndex = 0;
		}
		currentPlayer = gameParameters.players[playerIndex];
		currentPlayerIndex = playerIndex;
	}

	public void nextTurn() {
		nextPlayer();
		state = STATE_RUNNING;
	}

	public Player nextPlayer() {
		setCurrentPlayer(currentPlayerIndex + 1);

		return currentPlayer;
	}

	public void handleError(Throwable e) {
		if (errorListener != null) {
			errorListener.handle(e);
		} else {
			throw new RuntimeException(e);
		}
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public GameParameters getParameters() {
		return gameParameters;
	}

}
