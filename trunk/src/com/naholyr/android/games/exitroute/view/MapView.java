package com.naholyr.android.games.exitroute.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

public class MapView extends ScrollingSurfaceView {

	public static final int LAYER_DRAW = 0;
	public static final int LAYER_TEMP = 1;

	public MapView(Context context, BitmapDrawable drawable) {
		super(context, drawable, 2);
	}

	public Canvas getTempCanvas() {
		return getLayer(LAYER_TEMP);
	}

	public Canvas getDrawCanvas() {
		return getLayer(LAYER_DRAW);
	}

}
