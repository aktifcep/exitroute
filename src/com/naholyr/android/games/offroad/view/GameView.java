package com.naholyr.android.games.offroad.view;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.naholyr.android.games.offroad.api.Game;
import com.naholyr.android.games.offroad.api.GameParameters;
import com.naholyr.android.games.offroad.api.Player;
import com.naholyr.android.games.offroad.api.Position;

public class GameView extends ScrollingImageView {

	private Game game;

	private TargetView[] targetViews;

	public GameView(Context context, GameParameters gameParameters) {
		super(context, gameParameters.map.getBitmap(), gameParameters.map.getBitmap().getWidth(), gameParameters.map.getBitmap().getHeight());

		game = new Game(gameParameters);
		game.draw(this);
	}

	public Game getGame() {
		return game;
	}

	public void removeAllTargetViews() {
		if (targetViews != null) {
			// Cancel all available behaviors, before removing view
			// We do this for all views now, before removing them, because when
			// a focused view is removed the focus is redistributed to nearby
			// view, which could lead in our case to really unexpected results
			// (like, a player moving twice, or even more funny events)
			for (int i = 0; i < targetViews.length; i++) {
				if (targetViews[i] != null) {
					targetViews[i].disableBehaviors();
				}
			}
			// Then we remove all views
			for (int i = 0; i < targetViews.length; i++) {
				if (targetViews[i] != null) {
					((ViewGroup) targetViews[i].getParent()).removeView(targetViews[i]);
				}
			}
		}
	}

	public TargetView[] getTargetViews() {
		return targetViews;
	}

	public void generateTargetViews(GameParameters params, TargetView.Listener listener, Activity launcher) {
		Player player = game.getCurrentPlayer();
		Position[] targets = player.getTargets(params.players);
		targetViews = new TargetView[targets.length];
		for (int i = 0; i < targetViews.length; i++) {
			Player playerAt = params.map.getPlayerAt(targets[i].x, targets[i].y, params.players);
			// Create and add target view
			int rx = params.map.getRealX(targets[i].x);
			int ry = params.map.getRealY(targets[i].y);
			TargetView targetView = params.map.getNewTargetView(launcher, rx, ry);
			targetView.setId(i);
			addView(targetView);
			// Here we play with visibility instead of not creating the view,
			// because this leads to yet unexplained FC...
			if (playerAt == null || playerAt == player) {
				targetView.setVisibility(View.VISIBLE);
				targetView.setListener(listener);
			} else {
				targetView.setVisibility(View.GONE);
			}
			targetViews[i] = targetView;
		}
	}

}
