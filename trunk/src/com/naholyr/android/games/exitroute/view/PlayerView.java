package com.naholyr.android.games.exitroute.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.naholyr.android.games.exitroute.R;

public class PlayerView extends TileView {

	public PlayerView(Context context, int x, int y, int cellSize) {
		super(context, getBitmap(context), x, y, cellSize, cellSize);

		setAlpha(255);
	}

	private static Bitmap getBitmap(Context context) {
		Resources r = context.getResources();

		return BitmapFactory.decodeResource(r, R.drawable.car);
	}

}
