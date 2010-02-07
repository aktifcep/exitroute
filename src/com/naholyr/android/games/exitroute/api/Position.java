package com.naholyr.android.games.exitroute.api;

public class Position extends Dimension {

	public Position(int x, int y) {
		super(x, y);
	}

	public void move(int xOffset, int yOffset) {
		x = x + xOffset;
		y = y + yOffset;
	}

}
