package com.naholyr.android.games.exitroute.api;

import android.view.ViewGroup;

import com.naholyr.android.games.exitroute.view.TargetView;

public class Game {

	public GameParameters params;

	private int _currentPlayerIndex = 0;

	private Player _currentPlayer = null;

	public Game(GameParameters gameParameters, int playerIndex) {
		params = gameParameters;
		setCurrentPlayer(playerIndex);
	}

	public Game(GameParameters gameParameters) {
		this(gameParameters, 0);
	}

	private void setCurrentPlayer(int playerIndex) {
		if (playerIndex < 0) {
			playerIndex = 0;
		}
		if (playerIndex >= params.players.length) {
			playerIndex = 0;
		}
		_currentPlayer = params.players[playerIndex];
		_currentPlayerIndex = playerIndex;
	}

	public Player nextPlayer() {
		setCurrentPlayer(_currentPlayerIndex + 1);

		return getCurrentPlayer();
	}

	public Player getCurrentPlayer() {
		return _currentPlayer;
	}

	public void draw(ViewGroup layout) {
		params.map.draw(layout, true);
		params.map.getImageView().invalidate();
		params.map.drawPlayers(params.players);
	}

	public void run() {
		Player player = getCurrentPlayer();
		params.map.focusPlayer(player);
		Position[] targets = player.getTargets(params.players);

		// Generate target views
		final TargetView[] targetViews = new TargetView[targets.length];
		for (int i = 0; i < targets.length; i++) {
			targetViews[i] = params.map.drawTarget(targets[i]);
		}

		// Add behaviors
		for (int i = 0; i < targetViews.length; i++) {
			setTargetViewBehaviors(targetViews, i);
		}
	}

	private void setTargetViewBehaviors(final TargetView[] views, final int i) {
		final Player player = getCurrentPlayer();
		views[i].setListener(new TargetView.Listener() {

			@Override
			public void onUnSelect(TargetView view) {
				view.reset(true);
				params.map.getView(player).setAlpha(255);
			}

			@Override
			public void onSelect(TargetView view) {
				// unselect all other targets
				for (int j = 0; j < views.length; j++) {
					if (i == j) {
						continue;
					}
					if (views[j].step == TargetView.STEP_CONFIRM) {
						views[j].unSelect();
					}
				}
				// Apply changes to the selected target
				view.setImageBitmap(player.icon.getBitmap());
				view.setAlpha(255);
				params.map.getView(player).setAlpha(127);
			}

			@Override
			public void onConfirm(TargetView view) {
				int rx = view.getLeft();
				int ry = view.getTop();
				int x = params.map.getCoordsX(rx);
				int y = params.map.getCoordsY(ry);
				player.moveTo(x, y);
				params.map.drawPlayer(player);
				for (int j = 0; j < views.length; j++) {
					((ViewGroup) views[j].getParent()).removeView(views[j]);
				}
				Game.this.nextPlayer();
				Game.this.run();
			}
		});
	}

}
