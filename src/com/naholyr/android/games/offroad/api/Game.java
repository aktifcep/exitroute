package com.naholyr.android.games.offroad.api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.naholyr.android.games.offroad.R;
import com.naholyr.android.games.offroad.view.GameView;
import com.naholyr.android.games.offroad.view.TargetView;

public class Game implements TargetView.Listener {

	public GameParameters params;

	private int _currentPlayerIndex = 0;

	private Player _currentPlayer = null;

	public static interface ErrorListener {
		public void handle(Throwable e);
	}

	public ErrorListener errorListener = null;

	// Saved portion of view for fast restore
	private int savedX;
	private int savedY;
	private int savedW;
	private int savedH;
	private Canvas savedCanvas;
	private Bitmap savedBitmap;
	private Activity savedLauncher;

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

	public void draw(GameView gameView) {
		params.map.draw(gameView, true);
		params.map.getView().invalidate();
		params.map.drawPlayers(gameView.getContext(), params.players);
	}

	public void run(Activity launcher) {
		Player player = getCurrentPlayer();
		params.map.focusPlayer(player);

		// Remove all existing target views
		params.map.getView().removeAllTargetViews();

		// Backup portion of the view, to restore it easily
		savedX = params.map.getRealX(Math.max(0, player.position.x - Math.abs(player.speed.x) - player.maxAcceleration));
		savedY = params.map.getRealY(Math.max(0, player.position.y - Math.abs(player.speed.y) - player.maxAcceleration));
		savedW = params.map.getRealX(2 * (Math.abs(player.speed.x) + player.maxAcceleration) + 1);
		savedH = params.map.getRealY(2 * (Math.abs(player.speed.y) + player.maxAcceleration) + 1);
		savedCanvas = params.map.getView().mImageView.mCanvas;
		savedBitmap = Bitmap.createBitmap(params.map.getView().mImageView.mBitmap, savedX, savedY, savedW, savedH);
		savedLauncher = launcher;

		// Generate target views
		params.map.getView().generateTargetViews(params, this, launcher);
	}

	public void nextTurn(Activity launcher) {
		nextPlayer();
		run(launcher);
	}

	private void handleError(Throwable e) {
		if (errorListener != null) {
			errorListener.handle(e);
		} else {
			throw new RuntimeException(e);
		}
	}

	private static void showAlertPosition(final Game game, final Activity launcher, Player player, final boolean nextTurnOnCancel) {
		AlertDialog.Builder builder = new AlertDialog.Builder(launcher);
		builder.setMessage(R.string.alert_exitroute_description);
		builder.setTitle(R.string.alert_exitroute_title);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setNeutralButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				if (nextTurnOnCancel) {
					game.nextTurn(launcher);
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private static void showWinner(final Game game, final Activity launcher, Player player) {
		AlertDialog.Builder builder = new AlertDialog.Builder(launcher);
		builder.setMessage(R.string.alert_winner_description);
		builder.setTitle(R.string.alert_winner_title);
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setNeutralButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				launcher.finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void onUnSelect(TargetView view) {
		try {
			// Redraw saved original bitmap rect
			savedCanvas.drawBitmap(savedBitmap, savedX, savedY, new Paint());
			// Reset view state
			view.reset(true);
			// Reset player icon alpha
			params.map.getView(getCurrentPlayer()).setAlpha(255);
		} catch (RuntimeException e) {
			handleError(e);
		}
	}

	@Override
	public void onSelect(TargetView view) {
		Player player = getCurrentPlayer();
		TargetView[] views = params.map.getView().getTargetViews();
		try {
			int x = params.map.getCoordsX(view.getLeft());
			int y = params.map.getCoordsY(view.getTop());
			// Unselect all other targets
			for (int j = 0; j < views.length; j++) {
				if (views[j] == null || views[j] == view) {
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
			savedCanvas.drawBitmap(savedBitmap, savedX, savedY, new Paint());
			// Draw a thick line between position and target
			Paint paint = new Paint();
			paint.setStrokeWidth(4.0f);
			paint.setColor(player.color);
			float x1 = params.map.getRealXCenter(x);
			float y1 = params.map.getRealYCenter(y);
			float x2 = params.map.getRealXCenter(player.position.x);
			float y2 = params.map.getRealYCenter(player.position.y);
			savedCanvas.drawLine(x1, y1, x2, y2, paint);
			// If player is on road, and target is not accessible,
			// display a warning
			if (params.map.isCellAccessible(player.position.x, player.position.y) && !params.map.isCellAccessible(x, y)) {
				Bitmap warningImg = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.warning);
				savedCanvas.drawBitmap(warningImg, view.getLeft(), view.getTop(), new Paint());
			}
		} catch (RuntimeException e) {
			handleError(e);
		}
	}

	@Override
	public void onConfirm(TargetView view) {
		Player player = getCurrentPlayer();
		try {
			int x = params.map.getCoordsX(view.getLeft());
			int y = params.map.getCoordsY(view.getTop());
			boolean resetSpeedAfterMove = false;
			boolean doNotRunNextTurn = false;
			float x1 = params.map.getRealXCenter(player.position.x);
			float y1 = params.map.getRealYCenter(player.position.y);
			float x2 = params.map.getRealXCenter(x);
			float y2 = params.map.getRealYCenter(y);
			Position[] cellsThrough = params.map.getCellsThrough(x1, y1, x2, y2);
			if (params.map.isCellAccessible(player.position.x, player.position.y)) {
				// Player is on road : check the path
				// Get cells the player will get through, and check if
				// one is inaccessible
				for (int k = 0; k < cellsThrough.length; k++) {
					Position cell = cellsThrough[k];
					if (!cell.equals(player.position)) {
						// Check if player is on road end : winner !
						if (params.map.isCellEnd(cell.x, cell.y)) {
							// End game
							showWinner(this, savedLauncher, player);
						} else if (!params.map.isCellAccessible(cell.x, cell.y)) {
							doNotRunNextTurn = true;
							showAlertPosition(this, savedLauncher, player, true);
							resetSpeedAfterMove = true;
							x = cell.x;
							y = cell.y;
							break;
						}
					}
				}
			} else {
				// Player is out road, no acceleration allowed
				resetSpeedAfterMove = true;
				// And he cannot win ! we don't check end cell in this
				// case
			}
			Canvas canvas = params.map.getView().mImageView.mCanvas;
			// Redraw saved original bitmap rect
			canvas.drawBitmap(savedBitmap, savedX, savedY, new Paint());
			// Draw a thin line between position and target, and a
			// marker for original position
			Paint paint = new Paint();
			paint.setStrokeWidth(2.0f);
			paint.setColor(player.color);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawLine(params.map.getRealXCenter(player.position.x), params.map.getRealYCenter(player.position.y), params.map.getRealXCenter(x),
					params.map.getRealYCenter(y), paint);
			canvas.drawCircle(params.map.getRealXCenter(player.position.x), params.map.getRealYCenter(player.position.y), 3.0f, paint);
			// Move player
			player.moveTo(x, y);
			if (resetSpeedAfterMove) {
				player.speed = new Speed(0, 0);
			}
			// Redraw
			params.map.drawPlayer(view.getContext(), player);
			// Next turn
			if (!doNotRunNextTurn) {
				nextTurn(savedLauncher);
			}
		} catch (RuntimeException e) {
			handleError(e);
		}
	}

}
