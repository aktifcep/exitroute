package com.naholyr.android.games.exitroute.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.naholyr.android.games.exitroute.R;

public class Example extends Activity {

	private RelativeLayout container;
	private int currentX;
	private int currentY;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.example);

		container = (RelativeLayout) findViewById(R.id.container);

		int top = 0;
		int left = 0;

		ImageView image1 = new ImageView(this);
		image1.setImageDrawable(getResources().getDrawable(R.drawable.map1));
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				1200, 1200);// RelativeLayout.LayoutParams.WRAP_CONTENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT);

		layoutParams.setMargins(left, top, 0, 0);
		container.addView(image1, layoutParams);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				currentX = (int) event.getRawX();
				currentY = (int) event.getRawY();
				break;
			}
			case MotionEvent.ACTION_MOVE: {
				int x2 = (int) event.getRawX();
				int y2 = (int) event.getRawY();
				container.scrollBy(currentX - x2, currentY - y2);
				currentX = x2;
				currentY = y2;
				break;
			}
			case MotionEvent.ACTION_UP: {
				break;
			}
		}
		return true;
	}

}
