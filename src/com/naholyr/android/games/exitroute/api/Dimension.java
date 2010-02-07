package com.naholyr.android.games.exitroute.api;

public class Dimension {

	public int x;

	public int y;

	public Dimension(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public boolean equals(Dimension o) {
		return o.x == x && o.y == y;
	}

}
