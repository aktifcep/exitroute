package com.naholyr.android.games.exitroute.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.naholyr.android.games.exitroute.api.Player;

public class PlayerView extends TileView {

	public PlayerView(Context context, int resourceId, int x, int y,
			int cellSize) {
		super(context, getBitmap(context, resourceId), x, y, cellSize, cellSize);

		setAlpha(255);
	}

	public PlayerView(Context context, Player player, int cellSize) {
		super(context, player.iconResourceId, player.position.x * cellSize,
				player.position.y * cellSize, cellSize, cellSize);
	}

	private static Bitmap getBitmap(Context context, int resourceId) {
		Resources r = context.getResources();

		return BitmapFactory.decodeResource(r, resourceId);
	}

}
