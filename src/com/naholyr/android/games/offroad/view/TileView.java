package com.naholyr.android.games.offroad.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.AbsoluteLayout.LayoutParams;

@SuppressWarnings("deprecation")
public class TileView extends ImageView {

	public TileView(Context context, int resourceId, int x, int y, int w, int h) {
		super(context);
		setImageResource(resourceId);
		initialize(x, y, w, h);
	}

	public TileView(Context context, Bitmap bitmap, int x, int y, int w, int h) {
		super(context);
		setImageBitmap(bitmap);
		initialize(x, y, w, h);
	}

	public TileView(Context context, Drawable drawable, int x, int y, int w, int h) {
		super(context);
		setImageDrawable(drawable);
		initialize(x, y, w, h);
	}

	public TileView(Context context, Uri uri, int x, int y, int w, int h) {
		super(context);
		setImageURI(uri);
		initialize(x, y, w, h);
	}

	private void initialize(int x, int y, int w, int h) {
		setAlpha(255);
		setAdjustViewBounds(true);
		setScaleType(ScaleType.CENTER_INSIDE);

		// Dimension & position
		setMinimumHeight(h);
		setMaxHeight(h);
		setMinimumWidth(w);
		setMaxWidth(w);
		LayoutParams params = new LayoutParams(w, h, x, y);

		setLayoutParams(params);
	}

	public void moveTo(int x, int y) {
		LayoutParams params = (LayoutParams) getLayoutParams();
		params.x = x;
		params.y = y;
		setLayoutParams(params);
	}

	public void rotate(float angle) {
		Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		setImageBitmap(newBitmap);
		requestLayout();
	}

}
