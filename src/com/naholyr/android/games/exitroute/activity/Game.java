package com.naholyr.android.games.exitroute.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.naholyr.android.games.exitroute.R;
import com.naholyr.android.games.exitroute.api.Constants;
import com.naholyr.android.games.exitroute.api.GameParameters;
import com.naholyr.android.games.exitroute.api.Map;
import com.naholyr.android.games.exitroute.api.Player;
import com.naholyr.android.games.exitroute.view.ScrollingImageView;

public class Game extends Activity {

	private int _nbPlayers;

	ScrollingImageView scrollImageView;

	GameParameters gameParameters;

	com.naholyr.android.games.exitroute.api.Game game;

	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		try {
			initializeData();
		} catch (Exception e) {
			showError(e.getMessage());
			finish();
		}

		setContentView(R.layout.game);

		((ViewGroup) findViewById(R.id.GameFrame)).post(new Runnable() {
			@Override
			public void run() {
				game = new com.naholyr.android.games.exitroute.api.Game(
						gameParameters);
				game.draw((ViewGroup) findViewById(R.id.GameFrame));
				game.run();
			}
		});
	}

	private void showError(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle(
				android.R.string.dialog_alert_title).setIcon(
				android.R.drawable.ic_dialog_alert).setCancelable(true);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void initializeData() throws Exception {
		Map.setContext(this);

		// Retrieve nb players
		Integer nbPlayersNullable = (Integer) getIntent().getExtras().get(
				Constants.EXTRA_NB_PLAYERS);
		if (nbPlayersNullable == null || nbPlayersNullable < 1
				|| nbPlayersNullable > Constants.MAX_PLAYERS) {
			throw new Exception(getString(R.string.error_invalid_nb_players));
		}
		_nbPlayers = nbPlayersNullable.intValue();

		// Generate game parameters
		gameParameters = new GameParameters("map1", _nbPlayers);
		for (int i = 0; i < _nbPlayers; i++) {
			Player player = new Player("Player " + (i + 1));
			player.setPosition(25 + 2 * i, 1);
			player.setIcon(Constants.CAR_DRAWABLES[i
					% Constants.CAR_DRAWABLES.length], this);
			gameParameters.players[i] = player;
		}
	}

}
