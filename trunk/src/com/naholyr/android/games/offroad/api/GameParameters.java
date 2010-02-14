package com.naholyr.android.games.offroad.api;

import android.content.res.Resources;

public class GameParameters {

	public Map map;

	public Player[] players;

	public boolean showGrid = true;

	public GameParameters(Map map, Player[] players) {
		this.players = players;
		this.map = map;
	}

	public GameParameters(Map map, int nbPlayers) {
		this.players = new Player[nbPlayers];
		this.map = map;
	}

	public GameParameters(Resources resources, String mapName, Player[] players) {
		this(Map.get(resources, mapName), players);
	}

	public GameParameters(Resources resources, String mapName, int nbPlayers) {
		this(Map.get(resources, mapName), nbPlayers);
	}

}
