package com.naholyr.android.games.exitroute.view;

import com.naholyr.android.games.exitroute.R;
import com.naholyr.android.games.exitroute.api.GameParameters;
import com.naholyr.android.games.exitroute.api.GameThread;
import com.naholyr.android.games.exitroute.api.Player;
import com.naholyr.android.games.exitroute.api.Position;
import com.naholyr.android.games.exitroute.api.Speed;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class GameView extends FrameLayout implements TargetView.Listener {

	public static final int DRAW_PLAYER_TARGETS = 1;
	
	private GameThread thread;

	// For redraw
	private int savedX;
	private int savedY;
	private Bitmap savedBitmap;
	private Canvas canvas;
	private TargetView[] targetViews;

	public GameView(final Context context, AttributeSet attrs) {
		super(context, attrs);

		// register our interest in hearing about changes to our surface
		/*SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create thread only; it's started in surfaceCreated()
		thread = new GameThread(holder, context, new Handler() {
			@Override
			public void handleMessage(Message m) {
				// TODO handleMessage
				String text = m.getData().getString("text");
				Toast.makeText(context, text, 5000).show();
			}
		});*/
		thread = new GameThread(this, context, new Handler() {
			@Override
			public void handleMessage(Message m) {
				// TODO handleMessage
				if (m.getData().containsKey("text")) {
					String text = m.getData().getString("text");
					Toast.makeText(context, text, 5000).show();
				}
				if (m.getData().containsKey("draw")) {
					int draw = m.getData().getInt("draw");
					switch (draw) {
						case DRAW_PLAYER_TARGETS:
							drawPlayerTargets();
							break;
					}
				}
			}
		});
		
		setFocusable(true); // make sure we get key events
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		thread.setSurfaceSize(w, h);
	}

	// FIXME @Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		thread.setRunning(false);
		while (retry) {
			try {
				thread.join();
				retry = false;
			} catch (InterruptedException e) {
			}
		}
	}

	public GameThread getThread() {
		return thread;
	}

	private void drawPlayerTargets() {
		Player player = thread.getCurrentPlayer();
		GameParameters gameParameters = thread.getParameters();

		// Focus current player
		gameParameters.map.focusPlayer(player);
		Position[] targets = player.getTargets(gameParameters.players);

		// Generate target views
		targetViews = new TargetView[targets.length];
		for (int i = 0; i < targets.length; i++) {
			targetViews[i] = gameParameters.map.drawTarget(targets[i]);
			targetViews[i].setListener(this);
		}

		// Save a portion of the display to restore it during turn manipulations
		int savedW = gameParameters.map.getRealX(2 * (Math.abs(player.speed.x) + player.maxAcceleration) + 1);
		int savedH = gameParameters.map.getRealY(2 * (Math.abs(player.speed.y) + player.maxAcceleration) + 1);
		savedX = gameParameters.map.getRealX(Math.max(0, player.position.x - Math.abs(player.speed.x) - player.maxAcceleration));
		savedY = gameParameters.map.getRealY(Math.max(0, player.position.y - Math.abs(player.speed.y) - player.maxAcceleration));
		canvas = gameParameters.map.getImageView().mImageView.mCanvas;
		savedBitmap = Bitmap.createBitmap(gameParameters.map.getImageView().mImageView.mBitmap, savedX, savedY, savedW, savedH);
	}

	@Override
	public void onUnSelectTarget(TargetView view) {
		Player currentPlayer = thread.getCurrentPlayer();
		GameParameters gameParameters = thread.getParameters();

		try {
			view.reset(true);
			gameParameters.map.getView(currentPlayer).setAlpha(255);
		} catch (Exception e) {
			thread.handleError(e);
		}
	}

	@Override
	public void onSelectTarget(TargetView view) {
		Player currentPlayer = thread.getCurrentPlayer();
		GameParameters gameParameters = thread.getParameters();

		try {
			int x = gameParameters.map.getCoordsX(view.getLeft());
			int y = gameParameters.map.getCoordsY(view.getTop());
			// Unselect all other targets
			for (int i = 0; i < targetViews.length; i++) {
				if (view.equals(targetViews[i])) {
					continue;
				}
				if (targetViews[i].step == TargetView.STEP_CONFIRM) {
					targetViews[i].unSelect();
				}
			}
			// Change target view to mark it's selected
			view.setImageBitmap(currentPlayer.icon.getBitmap());
			view.setAlpha(255);
			Speed speed = currentPlayer.getNewSpeedForTarget(x, y);
			float angle = Player.getOrientationAngle(speed);
			view.rotate(angle);
			gameParameters.map.getView(currentPlayer).setAlpha(127);

			// Redraw saved original bitmap rect
			canvas.drawBitmap(savedBitmap, savedX, savedY, new Paint());
			// Draw a thick line between position and target
			Paint paint = new Paint();
			paint.setStrokeWidth(4.0f);
			paint.setColor(currentPlayer.color);
			float x1 = gameParameters.map.getRealXCenter(x);
			float y1 = gameParameters.map.getRealYCenter(y);
			float x2 = gameParameters.map.getRealXCenter(currentPlayer.position.x);
			float y2 = gameParameters.map.getRealYCenter(currentPlayer.position.y);
			canvas.drawLine(x1, y1, x2, y2, paint);
			// If player is on road, and target is not accessible, display a
			// warning
			if (gameParameters.map.isCellAccessible(currentPlayer.position.x, currentPlayer.position.y) && !gameParameters.map.isCellAccessible(x, y)) {
				Bitmap warningImg = BitmapFactory.decodeResource(view.getContext().getResources(), R.drawable.warning);
				canvas.drawBitmap(warningImg, view.getLeft(), view.getTop(), new Paint());
			}
		} catch (Exception e) {
			thread.handleError(e);
		}
	}

	@Override
	public void onConfirmTarget(TargetView view) {
		Player currentPlayer = thread.getCurrentPlayer();
		GameParameters gameParameters = thread.getParameters();

		try {
			int x = gameParameters.map.getCoordsX(view.getLeft());
			int y = gameParameters.map.getCoordsY(view.getTop());
			boolean resetSpeedAfterMove = false;
			boolean doNotRunNextTurn = false;
			float x1 = gameParameters.map.getRealXCenter(currentPlayer.position.x);
			float y1 = gameParameters.map.getRealYCenter(currentPlayer.position.y);
			float x2 = gameParameters.map.getRealXCenter(x);
			float y2 = gameParameters.map.getRealYCenter(y);
			Position[] cellsThrough = gameParameters.map.getCellsThrough(x1, y1, x2, y2);
			if (gameParameters.map.isCellAccessible(currentPlayer.position.x, currentPlayer.position.y)) {
				// Player is on road : check the path
				// Get cells the player will get through, and check if one is
				// inaccessible
				for (int k = 0; k < cellsThrough.length; k++) {
					Position cell = cellsThrough[k];
					if (!cell.equals(currentPlayer.position)) {
						// Check if player is on road end : winner !
						if (gameParameters.map.isCellEnd(cell.x, cell.y)) {
							// End game
							setWinner(currentPlayer);
						} else if (!gameParameters.map.isCellAccessible(cell.x, cell.y)) {
							alertExitRoute(currentPlayer);
							doNotRunNextTurn = true;
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
				// And he cannot win ! we don't check end cell in this case
			}
			Canvas canvas = gameParameters.map.getImageView().mImageView.mCanvas;
			// Redraw saved original bitmap rect
			canvas.drawBitmap(savedBitmap, savedX, savedY, new Paint());
			// Draw a thin line between position and target, and a marker for
			// original position
			Paint paint = new Paint();
			paint.setStrokeWidth(2.0f);
			paint.setColor(currentPlayer.color);
			paint.setStyle(Paint.Style.FILL_AND_STROKE);
			canvas.drawLine(gameParameters.map.getRealXCenter(currentPlayer.position.x), gameParameters.map.getRealYCenter(currentPlayer.position.y),
					gameParameters.map.getRealXCenter(x), gameParameters.map.getRealYCenter(y), paint);
			canvas.drawCircle(gameParameters.map.getRealXCenter(currentPlayer.position.x), gameParameters.map
					.getRealYCenter(currentPlayer.position.y), 3.0f, paint);
			// Move player
			currentPlayer.moveTo(x, y);
			if (resetSpeedAfterMove) {
				currentPlayer.speed = new Speed(0, 0);
			}
			// Redraw
			gameParameters.map.drawPlayer(currentPlayer);
			// Remove all targets
			for (int i = 0; i < targetViews.length; i++) {
				targetViews[i].destroyDrawingCache();
				((ViewGroup) targetViews[i].getParent()).removeView(targetViews[i]);
			}
			// Next turn
			if (!doNotRunNextTurn) {
				thread.nextTurn();
			}
		} catch (Exception e) {
			thread.handleError(e);
		}
	}

	public void alertExitRoute(Player player) {
		thread.setState(GameThread.STATE_PAUSED);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		// TODO Localize
		builder.setMessage("Vous êtes sorti de la route ! vous revenez à la vitesse minimale tant que vous n'êtes pas revenu sur la route.");
		builder.setTitle("Sortie de route !");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setNeutralButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				thread.nextTurn();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void setWinner(Player player) {
		thread.setState(GameThread.STATE_PAUSED);
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		// TODO Localize
		builder.setMessage("Vous avez gagné la course !");
		builder.setTitle("Vainqueur !");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setNeutralButton(android.R.string.ok, new AlertDialog.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				thread.setState(GameThread.STATE_WIN);
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
