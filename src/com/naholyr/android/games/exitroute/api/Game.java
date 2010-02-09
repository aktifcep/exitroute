package com.naholyr.android.games.exitroute.api;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.ViewGroup;

import com.naholyr.android.games.exitroute.view.TargetView;

public class Game {

	public GameParameters params;

	private int _currentPlayerIndex = 0;

	private Player _currentPlayer = null;

	public static interface ErrorListener {
		public void handle(Throwable e);
	}

	public ErrorListener errorListener = null;

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
		TargetView[] targetViews = new TargetView[targets.length];
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
		final int savedX = Math.max(0, params.map.getRealX(player.position.x - Math.abs(player.speed.x)));
		final int savedY = Math.max(0, params.map.getRealY(player.position.y - Math.abs(player.speed.y)));
		final int savedW = params.map.getRealX(2 * (Math.abs(player.speed.x) + player.maxAcceleration));
		final int savedH = params.map.getRealY(2 * (Math.abs(player.speed.y) + player.maxAcceleration));
		final Bitmap savedBitmap = Bitmap.createBitmap(params.map.getImageView().mImageView.mBitmap, savedX, savedY, savedW, savedH);
		views[i].setListener(new TargetView.Listener() {

			@Override
			public void onUnSelect(TargetView view) {
				try {
					view.reset(true);
					params.map.getView(player).setAlpha(255);
				} catch (Exception e) {
					Game.this.handleError(e);
				}
			}

			@Override
			public void onSelect(TargetView view) {
				try {
					int x = params.map.getCoordsX(views[i].getLeft());
					int y = params.map.getCoordsY(views[i].getTop());
					// Unselect all other targets
					for (int j = 0; j < views.length; j++) {
						if (i == j) {
							continue;
						}
						if (views[j].step == TargetView.STEP_CONFIRM) {
							views[j].unSelect();
						}
					}
					// Change target view to mark it's selected
					view.setImageBitmap(player.icon.getBitmap());
					view.setAlpha(255);
					Speed speed = player.getNewSpeedForTarget(x, y);
					float angle = Player.getOrientationAngle(speed);
					view.rotate(angle);
					params.map.getView(player).setAlpha(127);

					// Redraw saved original bitmap rect
					params.map.getImageView().mImageView.mCanvas.drawBitmap(savedBitmap, savedX, savedY, new Paint());
					// Draw a thick line between position and target
					Paint paint = new Paint();
					paint.setStrokeWidth(4.0f);
					paint.setColor(player.color);
					params.map.getImageView().mImageView.mCanvas.drawLine(params.map.getRealXCenter(player.position.x), params.map
							.getRealYCenter(player.position.y), params.map.getRealXCenter(x), params.map.getRealYCenter(y), paint);
				} catch (Exception e) {
					Game.this.handleError(e);
				}
			}

			@Override
			public void onConfirm(TargetView view) {
				try {
					int x = params.map.getCoordsX(views[i].getLeft());
					int y = params.map.getCoordsY(views[i].getTop());
					// Redraw saved original bitmap rect
					params.map.getImageView().mImageView.mCanvas.drawBitmap(savedBitmap, savedX, savedY, new Paint());
					// Draw a thin line between position and target, and a
					// marker for original position
					Paint paint = new Paint();
					paint.setStrokeWidth(2.0f);
					paint.setColor(player.color);
					paint.setStyle(Paint.Style.FILL_AND_STROKE);
					params.map.getImageView().mImageView.mCanvas.drawLine(params.map.getRealXCenter(player.position.x), params.map
							.getRealYCenter(player.position.y), params.map.getRealXCenter(x), params.map.getRealYCenter(y), paint);
					params.map.getImageView().mImageView.mCanvas.drawCircle(params.map.getRealXCenter(player.position.x), params.map
							.getRealYCenter(player.position.y), 3.0f, paint);
					// Move player
					player.moveTo(x, y);
					// Redraw
					params.map.drawPlayer(player);
					// Remove all targets
					for (int j = 0; j < views.length; j++) {
						((ViewGroup) views[j].getParent()).removeView(views[j]);
					}
					// Next turn
					Game.this.nextPlayer();
					Game.this.run();
				} catch (Exception e) {
					Game.this.handleError(e);
				}
			}
		});
	}

	private void handleError(Throwable e) {
		if (errorListener != null) {
			errorListener.handle(e);
		} else {
			throw new RuntimeException(e);
		}
	}

}
