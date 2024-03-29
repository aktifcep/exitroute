package com.naholyr.android.games.offroad.api;

import com.naholyr.android.games.offroad.R;
import com.naholyr.android.games.offroad.activity.Game;

import android.app.Activity;

public class Constants {

	public static final int MAX_PLAYERS = 3;

	public static final String EXTRA_NB_PLAYERS = "com.naholyr.android.games.exitroute.NbPlayers";

	public static final Class<? extends Activity> NEW_GAME_ACTIVITY = Game.class;

	public static final int CELL_SIZE = 30;

	public static final String RESOURCE_PACKAGE = "com.naholyr.android.games.offroad";

	public static final int INITIAL_SPEED_X = 0;
	public static final int INITIAL_SPEED_Y = 0;

	public static final int DEFAULT_MAP_WIDTH = 240;
	public static final int DEFAULT_MAP_HEIGHT = 320;

	public static final int[] CAR_DRAWABLES = new int[] { R.drawable.car1, R.drawable.car2 };
	public static final int[] PLAYER_COLORS = new int[] { 0xAAFF0000, 0xAA0000FF };

	public static final char MAP_SYMBOL_WALL = '#';
	public static final char MAP_SYMBOL_ROAD = ' ';
	public static final char MAP_SYMBOL_START = '~';
	public static final char MAP_SYMBOL_END = '*';

}
