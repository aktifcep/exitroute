package com.naholyr.android.games.exitroute.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;

@SuppressWarnings("deprecation")
public class ScrollingImageView extends AbsoluteLayout implements OnGestureListener {

	public GestureDetector mGestureDetector;
	public DrawableImageView mImageView;

	public ScrollingImageView(Context context, int imageResId, int imageWidth, int imageHeight) {
		super(context);

		Bitmap img = BitmapFactory.decodeResource(context.getResources(), imageResId);
		mImageView = new DrawableImageView(context, img);
		this.addView(mImageView, new LinearLayout.LayoutParams(imageWidth, imageHeight));

		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setIsLongpressEnabled(false);
	}

	public ScrollingImageView(Context context, Bitmap img, int imageWidth, int imageHeight) {
		super(context);

		mImageView = new DrawableImageView(context, img);
		this.addView(mImageView, new LinearLayout.LayoutParams(imageWidth, imageHeight));

		mGestureDetector = new GestureDetector(this);
		mGestureDetector.setIsLongpressEnabled(false);
	}

	public ScrollingImageView(Context context, BitmapDrawable drawable) {
		this(context, drawable.getBitmap());
	}

	public ScrollingImageView(Context context, BitmapDrawable drawable, int width, int height) {
		this(context, drawable.getBitmap(), width, height);
	}

	public ScrollingImageView(Context context, Bitmap img) {
		this(context, img, img.getWidth(), img.getHeight());
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		int scrollWidth = mImageView.getWidth() - this.getWidth();
		if ((this.getScrollX() >= 0) && (this.getScrollX() <= scrollWidth) && (scrollWidth > 0)) {
			int moveX = (int) distanceX;
			if (((moveX + this.getScrollX()) >= 0) && ((Math.abs(moveX) + Math.abs(this.getScrollX())) <= scrollWidth)) {
				this.scrollBy(moveX, 0);
			} else {
				if (distanceX >= 0) {
					this.scrollBy(scrollWidth - Math.max(Math.abs(moveX), Math.abs(this.getScrollX())), 0);
				} else {
					this.scrollBy(-Math.min(Math.abs(moveX), Math.abs(this.getScrollX())), 0);
				}
			}
		}

		int scrollHeight = mImageView.getHeight() - this.getHeight();
		if ((this.getScrollY() >= 0) && (this.getScrollY() <= scrollHeight) && (scrollHeight > 0)) {
			int moveY = (int) distanceY;
			if (((moveY + this.getScrollY()) >= 0) && ((Math.abs(moveY) + Math.abs(this.getScrollY())) <= scrollHeight)) {
				this.scrollBy(0, moveY);
			} else {
				if (distanceY >= 0) {
					this.scrollBy(0, scrollHeight - Math.max(Math.abs(moveY), Math.abs(this.getScrollY())));
				} else {
					this.scrollBy(0, -Math.min(Math.abs(moveY), Math.abs(this.getScrollY())));
				}
			}
		}
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		float xTap = e.getX();
		float yTap = e.getY();
		int xOffset = getScrollX();
		int yOffset = getScrollY();
		int x = Math.round(xTap + xOffset);
		int y = Math.round(yTap + yOffset);
		int nbChildren = getChildCount();

		// For any child hit by the touch event, just dispatch to it
		for (int i = 0; i < nbChildren; i++) {
			View child = getChildAt(i);
			if (!child.equals(mImageView) && child.isClickable()) {
				Rect r = new Rect();
				child.getHitRect(r);
				if (r.contains(x, y)) {
					child.dispatchTouchEvent(e);
				}
			}
		}

		// Dispatch to myself
		mGestureDetector.onTouchEvent(e);

		return true;
	}

}
