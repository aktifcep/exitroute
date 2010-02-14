package com.naholyr.android.games.offroad.api;

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

	public GameParameters(String mapName, Player[] players) {
		this(Map.get(mapName), players);
	}

	public GameParameters(String mapName, int nbPlayers) {
		this(Map.get(mapName), nbPlayers);
	}

}
