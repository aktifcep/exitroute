package com.naholyr.android.games.exitroute.view;

import android.content.Context;

import com.naholyr.android.games.exitroute.api.Player;

public class PlayerView extends TileView {

	private Player _player;

	public PlayerView(Context context, Player player, int cellSize) {
		super(context, player.icon.getBitmap(), player.position.x * cellSize, player.position.y * cellSize, cellSize, cellSize);
		_player = player;

		rotate(_player.getOrientationAngle());
	}

	@Override
	public void moveTo(int x, int y) {
		super.moveTo(x, y);
		// Redraw image
		setImageBitmap(_player.icon.getBitmap());
		rotate(_player.getOrientationAngle());
	}

}
