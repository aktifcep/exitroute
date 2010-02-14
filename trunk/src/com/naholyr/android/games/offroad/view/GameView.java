package com.naholyr.android.games.offroad.view;

import android.content.Context;

import com.naholyr.android.games.offroad.api.Game;
import com.naholyr.android.games.offroad.api.GameParameters;

public class GameView extends ScrollingImageView {

	private Game game;

	public GameView(Context context, GameParameters gameParameters) {
		super(context, gameParameters.map.getBitmap(), gameParameters.map.getBitmap().getWidth(), gameParameters.map.getBitmap().getHeight());
		
		game = new Game(gameParameters);
		game.draw(this);
	}
	
	public Game getGame() {
		return game;
	}

}