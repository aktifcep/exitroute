package com.naholyr.android.games.exitroute.api;

import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup;

import com.naholyr.android.games.exitroute.view.PlayerView;
import com.naholyr.android.games.exitroute.view.ScrollingImageView;
import com.naholyr.android.games.exitroute.view.TargetView;

public class Map {

	private String _name;
	private Dimension _size;

	private static Context _context;
	private BitmapDrawable _drawable;
	private ScrollingImageView _scrollImageView;

	private static java.util.Map<String, Map> _maps = new HashMap<String, Map>();

	private int _viewWidth = 0;
	private int _viewHeight = 0;
	private ViewGroup _layout;
	private boolean _gotViewSizeFromLayout = false;
	private int _cellSize = Constants.CELL_SIZE;

	private java.util.Map<Player, PlayerView> _playerViews = new HashMap<Player, PlayerView>();

	public Map(String name, BitmapDrawable drawable) {
		if (_context == null) {
			throw new RuntimeException("Context undefined. Call Map.setContext(Context) before creating a new map !");
		}

		_name = name;
		setDrawable(drawable);
	}

	public static void setContext(Context context) {
		_context = context;
	}

	public static Map get(String mapName) {
		if (!_maps.containsKey(mapName)) {
			int resourceId = _context.getResources().getIdentifier(mapName, "drawable", _context.getPackageName());
			BitmapDrawable drawable = (BitmapDrawable) _context.getResources().getDrawable(resourceId);
			Map map = new Map(mapName, drawable);
			_maps.put(mapName, map);
		}

		return _maps.get(mapName);
	}

	public void setViewSize(int w, int h) {
		_viewWidth = w;
		_viewHeight = h;
	}

	public void setDrawable(BitmapDrawable drawable) {
		setDrawable(drawable, drawable.getMinimumWidth(), drawable.getMinimumHeight());
	}

	public void setDrawable(BitmapDrawable drawable, int width, int height) {
		_drawable = drawable;

		int w = (width - (width % _cellSize)) / _cellSize;
		int h = (height - (height % _cellSize)) / _cellSize;
		_size = new Dimension(w, h);
	}

	public int getWidth() {
		return _size.x;
	}

	public int getHeight() {
		return _size.y;
	}

	public int getRealWidth() {
		return _drawable.getMinimumWidth();
	}

	public int getRealHeight() {
		return _drawable.getMinimumHeight();
	}

	private void refreshViewSize() {
		if (!_gotViewSizeFromLayout && _layout != null) {
			int w = _layout.getMeasuredWidth();
			int h = _layout.getMeasuredHeight();
			if (w != 0 && h != 0) {
				setViewSize(w, h);
				_gotViewSizeFromLayout = true;
			}
		}
	}

	public int getViewWidth() {
		refreshViewSize();

		return _viewWidth;
	}

	public int getViewHeight() {
		refreshViewSize();

		return _viewHeight;
	}

	public String getName() {
		return _name;
	}

	public void draw(ViewGroup layout, boolean showGrid) {
		_layout = layout;
		// If layout has already bean measured, we store its dimensions
		if (layout.getMeasuredWidth() != 0 && layout.getMeasuredHeight() != 0) {
			setViewSize(layout.getMeasuredWidth(), layout.getMeasuredHeight());
			_gotViewSizeFromLayout = true;
		}
		// Otherwise, we just take screen size, and remember that we should
		// later use real layout's dimensions for more precision
		else {
			setViewSize(Constants.DEFAULT_MAP_WIDTH, Constants.DEFAULT_MAP_HEIGHT);
			_gotViewSizeFromLayout = false;
		}

		// Draw image
		_scrollImageView = new ScrollingImageView(_context, _drawable);

		layout.addView(_scrollImageView);

		// Draw grid
		if (showGrid) {
			Canvas canvas = _scrollImageView.mImageView.mCanvas;
			Paint paint = _scrollImageView.mImageView.mPaint;

			for (int i = 0; i < _size.x; i++) {
				int x = getRealX(i);
				canvas.drawLine(x, 0, x, getRealHeight(), paint);
			}
			for (int j = 0; j < _size.y; j++) {
				int y = getRealY(j);
				canvas.drawLine(0, y, getRealWidth(), y, paint);
			}
		}
	}

	public int getRealX(int x) {
		return x * _cellSize;
	}

	public int getRealY(int y) {
		return y * _cellSize;
	}

	public void scrollToReal(int x, int y) {
		_scrollImageView.scrollTo(x, y);
	}

	public void scrollTo(int x, int y) {
		scrollToReal(getRealX(x), getRealY(y));
	}

	public void scrollTo(Position position) {
		scrollTo(position.x, position.y);
	}

	public void scrollToCenterReal(int x, int y) {
		int w = getViewWidth();
		int h = getViewHeight();

		scrollToReal(Math.max(0, x - w / 2), Math.max(0, y - h / 2));
	}

	public void scrollToCenter(int x, int y) {
		scrollToCenterReal(getRealX(x), getRealY(y));
	}

	public void scrollToCenter(Position position) {
		scrollToCenter(position.x, position.y);
	}

	public void scrollTo(Player player) {
		scrollToCenter(player.position);
	}

	public void drawPlayers(Player[] players) {
		for (int i = 0; i < players.length; i++) {
			drawPlayer(players[i]);
		}
	}

	public void drawPlayer(Player player) {
		int rx = getRealX(player.position.x);
		int ry = getRealY(player.position.y);
		if (!_playerViews.containsKey(player)) {
			PlayerView view = new PlayerView(_context, player, _cellSize);
			_scrollImageView.addView(view);
			_playerViews.put(player, view);
		} else {
			PlayerView view = _playerViews.get(player);
			view.moveTo(rx, ry);
		}
	}

	public ScrollingImageView getImageView() {
		return _scrollImageView;
	}

	public TargetView drawTarget(Position position) {
		return drawTarget(position.x, position.y);
	}

	public TargetView drawTarget(int x, int y) {
		int rx = getRealX(x);
		int ry = getRealY(y);
		TargetView targetView = new TargetView(_context, rx, ry, _cellSize);
		_scrollImageView.addView(targetView);

		return targetView;
	}

	public int getCoordsX(int rx) {
		return (rx - (rx % _cellSize)) / _cellSize;
	}

	public int getCoordsY(int ry) {
		return (ry - (ry % _cellSize)) / _cellSize;
	}

	public void focusPlayer(Player player) {
		scrollTo(player);
		for (Entry<Player, PlayerView> entry : _playerViews.entrySet()) {
			if (player.equals(entry.getKey())) {
				entry.getValue().setAlpha(255);
			} else {
				entry.getValue().setAlpha(127);
			}
		}
	}

	public PlayerView getView(Player player) {
		return _playerViews.get(player);
	}

	public float getRealXCenter(int x) {
		return getRealX(x) + _cellSize / 2;
	}

	public float getRealYCenter(int y) {
		return getRealY(y) + _cellSize / 2;
	}

}
