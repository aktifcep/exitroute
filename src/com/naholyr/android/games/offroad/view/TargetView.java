package com.naholyr.android.games.offroad.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;

import com.naholyr.android.games.offroad.R;

public class TargetView extends TileView implements OnTouchListener, OnClickListener, OnFocusChangeListener {

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

	// Set to false by calling cancelBehaviors(), this will lead all touch,
	// click, and focus events to be ignored
	private boolean interactive;

	public TargetView(Context context, int x, int y, int cellSize) {
		super(context, getBitmap(context), x, y, cellSize, cellSize);

		reset(false);

		setFocusable(true);
		setClickable(true);

		setOnTouchListener(this);
		setOnClickListener(this);
		setOnFocusChangeListener(this);
		interactive = true;

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
		if (_listener != null && interactive) {
			_listener.onSelect(this);
		}
		step = STEP_CONFIRM;
	}

	public void unSelect() {
		if (_listener != null && interactive) {
			_listener.onUnSelect(this);
		}
		step = STEP_SELECT;
	}

	public void confirmSelect() {
		if (interactive) {
			if (_listener != null) {
				_listener.onConfirm(this);
			} else {
				// Default behavior
				reset(true);
			}
		}
		step = STEP_END;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!interactive) {
			return false;
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			click();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if (!interactive) {
			return;
		}
		((TargetView) v).confirmSelect();
	}

	@Override
	public void onFocusChange(View v, boolean focus) {
		if (!interactive) {
			return;
		}
		if (focus) {
			((TargetView) v).select();
		} else {
			((TargetView) v).unSelect();
		}
	}

	public void disableBehaviors() {
		interactive = false;
	}

	public void enableBehaviors() {
		interactive = false;
	}

}
