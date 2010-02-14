package com.naholyr.android.games.offroad.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.naholyr.android.games.offroad.R;

public class TargetView extends TileView implements OnTouchListener {

	public static final int STEP_SELECT = 0;
	public static final int STEP_CONFIRM = 1;
	public static final int STEP_END = 2;

	public int step;

	public interface Listener {
		void onSelect(TargetView view);

		void onConfirm(TargetView view);

		void onUnSelect(TargetView view);
	}

	private Listener _listener = null;

	public TargetView(Context context, int x, int y, int cellSize) {
		super(context, getBitmap(context), x, y, cellSize, cellSize);

		reset(false);

		setFocusable(true);
		setClickable(true);

		setOnTouchListener(this);

		step = STEP_SELECT;
	}

	public void setListener(Listener listener) {
		_listener = listener;
	}

	public void reset(boolean resetBitmap) {
		if (resetBitmap) {
			setImageBitmap(getBitmap(getContext()));
		}
		setAlpha(127);
	}

	private static Bitmap getBitmap(Context context) {
		Resources r = context.getResources();

		return BitmapFactory.decodeResource(r, R.drawable.icon);
	}

	private void click() {
		switch (step) {
			case STEP_SELECT:
				select();
				break;
			case STEP_CONFIRM:
				confirmSelect();
				break;
		}
	}

	public void select() {
		if (_listener != null) {
			_listener.onSelect(this);
		}
		step = STEP_CONFIRM;
	}

	public void unSelect() {
		if (_listener != null) {
			_listener.onUnSelect(this);
		}
		step = STEP_SELECT;
	}

	public void confirmSelect() {
		if (_listener != null) {
			_listener.onConfirm(this);
		} else {
			// Default behavior
			reset(true);
		}
		step = STEP_END;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			click();
		}
		return true;
	}

}
