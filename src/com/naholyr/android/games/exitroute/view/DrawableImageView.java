package com.naholyr.android.games.exitroute.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

public class DrawableImageView extends View {
	public Bitmap mBitmap;
	public Canvas mCanvas;
	public final Paint mPaint;

	public DrawableImageView(Context c, Bitmap img) {
		super(c);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(64, 0, 0, 0);
		mPaint.setStrokeWidth(1);

		Bitmap newBitmap = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.RGB_565);
		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		if (img != null) {
			newCanvas.drawBitmap(img, 0, 0, null);
		}
		mBitmap = newBitmap;
		mCanvas = newCanvas;

		mCanvas.setBitmap(mBitmap);
	}

	public DrawableImageView(Context c, Drawable drawable, int width, int height) {
		super(c);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setARGB(64, 0, 0, 0);
		mPaint.setStrokeWidth(1);

		Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas newCanvas = new Canvas();
		newCanvas.setBitmap(newBitmap);
		drawable.draw(newCanvas);

		mBitmap = newBitmap;
		mCanvas = newCanvas;

		mCanvas.setBitmap(mBitmap);
	}

	public DrawableImageView(Context c, Drawable drawable) {
		this(c, drawable, drawable.getMinimumWidth(), drawable.getMinimumHeight());
	}

	float scaleX;
	float scaleY;
	float scale;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		scaleX = (float) w / mBitmap.getWidth();
		scaleY = (float) h / mBitmap.getHeight();
		scale = scaleX > scaleY ? scaleY : scaleX;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mBitmap != null) {
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			canvas.drawBitmap(mBitmap, matrix, null);
		}
	}
}
