package com.naholyr.android.games.offroad.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.view.ViewGroup;

import com.naholyr.android.games.offroad.R;
import com.naholyr.android.games.offroad.view.PlayerView;
import com.naholyr.android.games.offroad.view.ScrollingImageView;
import com.naholyr.android.games.offroad.view.TargetView;

public class Map {

	private String _name;
	private Dimension _size;

	private static Context _context;
	private BitmapDrawable _drawable;
	private ScrollingImageView gameView;

	private static java.util.Map<String, Map> _maps = new HashMap<String, Map>();

	private int _viewWidth = 0;
	private int _viewHeight = 0;
	private ViewGroup _layout;
	private boolean _gotViewSizeFromLayout = false;
	private int _cellSize = Constants.CELL_SIZE;

	private char[][] _cells;
	private Position[] _starts;
	private Position[] _ends;

	private java.util.Map<Player, PlayerView> _playerViews = new HashMap<Player, PlayerView>();

	public Map(String name, InputStream mapInfo, BitmapDrawable drawable) {
		if (_context == null) {
			throw new RuntimeException("Context undefined. Call Map.setContext(Context) before creating a new map !");
		}

		_name = name;
		setDrawable(drawable);
		setMapInfo(mapInfo, _context.getResources());
	}

	public static void setContext(Context context) {
		_context = context;
	}

	public static Map get(String mapName) {
		if (!_maps.containsKey(mapName)) {
			int drawableResId = _context.getResources().getIdentifier(mapName, "drawable", _context.getPackageName());
			int rawResId = _context.getResources().getIdentifier(mapName, "raw", _context.getPackageName());
			BitmapDrawable drawable = (BitmapDrawable) _context.getResources().getDrawable(drawableResId);
			InputStream mapInfo = _context.getResources().openRawResource(rawResId);
			Map map = new Map(mapName, mapInfo, drawable);
			_maps.put(mapName, map);
		}

		return _maps.get(mapName);
	}

	private void setViewSize(int w, int h) {
		_viewWidth = w;
		_viewHeight = h;
	}

	private void setDrawable(BitmapDrawable drawable) {
		setDrawable(drawable, drawable.getMinimumWidth(), drawable.getMinimumHeight());
	}

	private void setMapInfo(InputStream description, Resources resources) {
		_cells = new char[_size.x][_size.y];
		try {
			description.reset();
			char symbol;
			int x = 0;
			int y = 0;
			List<Position> starts = new ArrayList<Position>();
			List<Position> ends = new ArrayList<Position>();
			while (true) {
				int c = description.read();
				// End of file
				if (c == -1) {
					break;
				}
				symbol = (char) c;
				if (c == 10) { // \r : skip
					continue;
				}
				if (c == 13) { // \n : end of line
					// Fill missing characters on the line
					for (int i = x + 1; i < _size.x; i++) {
						_cells[i][y] = Constants.MAP_SYMBOL_WALL;
					}
					// New line
					y += 1;
					x = 0;
					// Additional lines in the file
					if (y >= _size.y) {
						break;
					} else {
						continue;
					}
				} else if (x >= _size.x) {
					// Additional characters on the line : read until next line
					continue;
				} else {
					// Unknown symbol
					if (symbol != Constants.MAP_SYMBOL_WALL && symbol != Constants.MAP_SYMBOL_START && symbol != Constants.MAP_SYMBOL_END
							&& symbol != Constants.MAP_SYMBOL_ROAD) {
						symbol = Constants.MAP_SYMBOL_WALL;
					}
					// Special symbol
					if (symbol == Constants.MAP_SYMBOL_START) {
						starts.add(new Position(x, y));
					}
					if (symbol == Constants.MAP_SYMBOL_END) {
						ends.add(new Position(x, y));
					}
					// store the cell information, and go to next character
					_cells[x][y] = symbol;
					x += 1;
				}
			}
			// Fill missing characters of last line
			if (y < _size.y) {
				for (int i = x + 1; i < _size.x; i++) {
					_cells[i][y] = Constants.MAP_SYMBOL_WALL;
				}
			}
			// Fill missing lines
			for (int j = y + 1; j < _size.y; j++) {
				for (int i = 0; i < _size.x; i++) {
					_cells[i][j] = Constants.MAP_SYMBOL_WALL;
				}
			}
			// Check and store start/end information
			if (starts.size() == 0) {
				throw new RuntimeException(resources.getString(R.string.error_no_start));
			}
			if (ends.size() == 0) {
				throw new RuntimeException(resources.getString(R.string.error_no_end));
			}
			_starts = starts.toArray(new Position[] {});
			_ends = ends.toArray(new Position[] {});
		} catch (IOException e) {
			throw new RuntimeException(resources.getString(R.string.error_map_io));
		}
		return;
	}

	private void setDrawable(BitmapDrawable drawable, int width, int height) {
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

	public void draw(ScrollingImageView gameView, boolean showGrid) {
		this.gameView = gameView;
		
		// FIXME Dynamic size
		setViewSize(Constants.DEFAULT_MAP_WIDTH, Constants.DEFAULT_MAP_HEIGHT);

		// Draw grid
		if (showGrid) {
			Canvas canvas = gameView.mImageView.mCanvas;

			for (int i = 0; i < _size.x; i++) {
				for (int j = 0; j < _size.y; j++) {
					int x = getRealX(i);
					int y = getRealY(j);
					char cell = getCell(i, j);
					canvas.drawRect(x, y, x + _cellSize, y + _cellSize, Map.getCellPaint(cell));
				}
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
		gameView.scrollTo(x, y);
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
			gameView.addView(view);
			_playerViews.put(player, view);
		} else {
			PlayerView view = _playerViews.get(player);
			view.moveTo(rx, ry);
		}
	}

	public ScrollingImageView getImageView() {
		return gameView;
	}

	public TargetView drawTarget(Position position) {
		return drawTarget(position.x, position.y);
	}

	public TargetView drawTarget(int x, int y) {
		int rx = getRealX(x);
		int ry = getRealY(y);
		TargetView targetView = new TargetView(_context, rx, ry, _cellSize);
		gameView.addView(targetView);

		return targetView;
	}

	public int getCoordsX(float rx) {
		int x = Math.round(rx);

		return (x - (x % _cellSize)) / _cellSize;
	}

	public int getCoordsY(float ry) {
		int y = Math.round(ry);

		return (y - (y % _cellSize)) / _cellSize;
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

	public char getCell(int x, int y) {
		return _cells[x][y];
	}

	public boolean isCellAccessible(int x, int y) {
		return getCell(x, y) != Constants.MAP_SYMBOL_WALL;
	}

	public boolean isCellStart(int x, int y) {
		return getCell(x, y) == Constants.MAP_SYMBOL_START;
	}

	public boolean isCellEnd(int x, int y) {
		return getCell(x, y) == Constants.MAP_SYMBOL_END;
	}

	public boolean isCellRoad(int x, int y) {
		return getCell(x, y) == Constants.MAP_SYMBOL_ROAD;
	}

	public Position[] getStartCells() {
		return _starts;
	}

	public Position[] getEndCells() {
		return _ends;
	}

	private static java.util.Map<Character, Paint> _cellPaints = new HashMap<Character, Paint>();

	private static Paint getCellPaint(char cell) {
		if (!_cellPaints.containsKey(cell)) {
			Paint paint = new Paint();
			switch (cell) {
				case Constants.MAP_SYMBOL_WALL:
					paint.setColor(0x00ffffff);
					paint.setStrokeWidth(0.0f);
					break;
				case Constants.MAP_SYMBOL_END:
					paint.setStyle(Style.FILL_AND_STROKE);
					paint.setColor(0x66FF6666);
					paint.setStrokeWidth(1.0f);
					break;
				case Constants.MAP_SYMBOL_START:
					paint.setStyle(Style.FILL_AND_STROKE);
					paint.setColor(0x6666FF66);
					paint.setStrokeWidth(1.0f);
					break;
				case Constants.MAP_SYMBOL_ROAD:
				default:
					paint.setStyle(Style.STROKE);
					paint.setColor(0x33999999);
					paint.setStrokeWidth(1.0f);
					break;
			}
			_cellPaints.put(cell, paint);
		}

		return _cellPaints.get(cell);
	}

	/**
	 * Returns int-coords of cells you pass through between real coordinates
	 * x1,y1 to x2,y2.
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public Position[] getCellsThrough(float x1, float y1, float x2, float y2) {
		List<Position> positions = new ArrayList<Position>();

		Float[] ab = getLineEquation(x1, y1, x2, y2);
		float x = x1;
		float y = y1;
		int stepX = ab[1] == null ? 0 : ((x1 > x2 ? -1 : +1) * (_cellSize / 2));
		int stepY = ab[0] == 0 ? 0 : ((y1 > y2 ? -1 : +1) * (_cellSize / 2));
		boolean finish = false;
		while (!finish) {
			int i = getCoordsX(x);
			int j = getCoordsY(y);
			Position position = new Position(i, j);
			if (!positions.contains(position)) {
				positions.add(position);
			}
			x += stepX;
			if (ab[0] == 0) {
				y = ab[1];
			} else if (ab[1] == null) {
				y += stepY;
			} else {
				y += stepY;
				float newY = ab[0] * x + ab[1];
				if ((y1 < y2 && y > newY) || (y1 > y2 && y < newY)) {
					y = newY;
				} else {
					// Calculate x from y
					x = (y - ab[1]) / ab[0];
				}
			}
			if ((x1 <= x2 && x > x2) || (x1 >= x2 && x < x2) || (y1 <= y2 && y > y2) || (y1 >= y2 && y < y2)) {
				finish = true;
			}
		}

		return positions.toArray(new Position[] {});
	}

	/**
	 * Returns [a,b] such as y = a*x + b for the line given If line is vertical,
	 * returns [x,null]
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	private static Float[] getLineEquation(float x1, float y1, float x2, float y2) {
		if (x1 == x2) {
			return new Float[] { x1, null };
		}
		// Cas D horizontale : y1 == y2 ==> [0, y]
		else if (y1 == y2) {
			return new Float[] { 0f, y1 };
		}
		// Cas général
		else {
			// y2-y1 = a*(x2-x1) ==>
			float a = (y2 - y1) / (x2 - x1);
			// y1*x2 - y2*x1 = a*x1*x2 + b*x2 - a*x2*x1 - b*x1 = b*(x2-x1) ==>
			float b = (y1 * x2 - y2 * x1) / (x2 - x1);

			return new Float[] { a, b };
		}

	}

	public Bitmap getBitmap() {
		return _drawable.getBitmap();
	}

}
