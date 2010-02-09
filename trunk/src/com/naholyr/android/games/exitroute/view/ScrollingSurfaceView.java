package com.naholyr.android.games.exitroute.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

/**
 * ScrollingSurfaceView is a ScrollingImageView with a support for multiple
 * canvas layers
 * 
 * @author naholyr
 */
public class ScrollingSurfaceView extends ScrollingImageView {

	private List<Canvas> _layers;

	public ScrollingSurfaceView(Context context, BitmapDrawable drawable,
			int nbLayers) {
		super(context, drawable);

		_layers = new ArrayList<Canvas>();
		for (int i = 0; i < nbLayers; i++) {
			addLayer();
		}
	}

	public ScrollingSurfaceView(Context context, BitmapDrawable drawable) {
		this(context, drawable, 0);
	}

	private Canvas newCanvas() {
		// TODO create a new canvas and add to view
		return null;
	}

	public int addLayer() {
		_layers.add(newCanvas());

		return _layers.size() - 1;
	}

	public Canvas getLayer(int index) {
		return _layers.get(index);
	}

}
