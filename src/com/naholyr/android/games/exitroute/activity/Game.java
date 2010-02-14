package com.naholyr.android.games.exitroute.activity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.naholyr.android.games.exitroute.R;
import com.naholyr.android.games.exitroute.api.Constants;
import com.naholyr.android.games.exitroute.api.GameParameters;
import com.naholyr.android.games.exitroute.api.Map;
import com.naholyr.android.games.exitroute.api.Player;
import com.naholyr.android.games.exitroute.api.Position;
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

			setContentView(R.layout.game);

			((ViewGroup) findViewById(R.id.GameFrame)).post(new Runnable() {
				@Override
				public void run() {
					try {
						game = new com.naholyr.android.games.exitroute.api.Game(gameParameters);
						game.errorListener = new com.naholyr.android.games.exitroute.api.Game.ErrorListener() {
							@Override
							public void handle(Throwable e) {
								Game.this.handleError(e);
							}
						};
						game.draw((ViewGroup) findViewById(R.id.GameFrame));
						game.run(Game.this);
					} catch (Exception e) {
						Game.this.handleError(e);
					}
				}
			});
		} catch (Exception e) {
			handleError(e);
		}
	}

	private void handleError(final Throwable e) {
		AlertDialog.OnClickListener onNo = new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Game.this.finish();
			}
		};
		AlertDialog.OnClickListener onYes = new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String errorMessage = e.getMessage() == null ? e.getClass().getName() : e.getMessage();
				String body = "\n\n\n---\n" + errorMessage + "\n---\n";

				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw, true);
				e.printStackTrace(pw);
				pw.flush();
				sw.flush();
				body += sw.toString();

				final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { getString(R.string.author_email) });
				intent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.bug_report_subject);
				intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
				Game.this.startActivity(Intent.createChooser(intent, "SendBugReport"));
				Game.this.finish();
			}
		};
		String errorMessage = e.getLocalizedMessage() == null ? e.getClass().getName() : e.getLocalizedMessage();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.an_error_occurred) + " : " + errorMessage + "\n\n" + getString(R.string.would_you_send_report));
		builder.setTitle(R.string.error);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton(R.string.yes, onYes);
		builder.setNegativeButton(R.string.no, onNo);
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void initializeData() throws Exception {
		Map.setContext(this);

		// Retrieve nb players
		Integer nbPlayersNullable = (Integer) getIntent().getExtras().get(Constants.EXTRA_NB_PLAYERS);
		if (nbPlayersNullable == null || nbPlayersNullable < 1 || nbPlayersNullable > Constants.MAX_PLAYERS) {
			throw new Exception(getString(R.string.error_invalid_nb_players));
		}
		_nbPlayers = nbPlayersNullable.intValue();

		// Generate game parameters
		gameParameters = new GameParameters("map1", _nbPlayers);
		Position[] starts = gameParameters.map.getStartCells();
		if (starts.length < _nbPlayers) {
			// TODO localize
			throw new RuntimeException("Not enough start positions for the expected number of players !");
		}
		// Generate random start positions
		List<Position> startsList = new Vector<Position>();
		for (int i = 0; i < starts.length; i++) {
			startsList.add(starts[i]);
		}
		Position[] randomStarts = new Position[_nbPlayers];
		for (int i = 0; i < randomStarts.length; i++) {
			int k = (int) Math.floor(Math.random() * startsList.size());
			randomStarts[i] = startsList.get(k);
			startsList.remove(k);
		}
		// Generate players
		for (int i = 0; i < _nbPlayers; i++) {
			Player player = new Player("Player " + (i + 1));
			player.color = Constants.PLAYER_COLORS[i % Constants.CAR_DRAWABLES.length];
			player.setPosition(randomStarts[i]);
			player.setIcon(Constants.CAR_DRAWABLES[i % Constants.CAR_DRAWABLES.length], this);
			gameParameters.players[i] = player;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
	
}
