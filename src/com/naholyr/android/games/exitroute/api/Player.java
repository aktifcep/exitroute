package com.naholyr.android.games.exitroute.api;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

public class Player {

	public String name;

	public BitmapDrawable icon;

	public Position position;

	public Speed speed;

	public Player(String name) {
		this.name = name;
		setSpeed(Constants.INITIAL_SPEED_X, Constants.INITIAL_SPEED_Y);
	}

	public void setIcon(int resourceId, Resources resources) {
		icon = (BitmapDrawable) resources.getDrawable(resourceId);
	}

	public void setIcon(int resourceId, Context context) {
		setIcon(resourceId, context.getResources());
	}

	public void setSpeed(int xOffset, int yOffset) {
		speed = new Speed(xOffset, yOffset);
	}

	public void moveTo(int x, int y, boolean recalculateSpeed) {
		if (recalculateSpeed) {
			setSpeed(x - position.x, y - position.y);
		}
		setPosition(x, y);
	}

	public void moveTo(int x, int y) {
		moveTo(x, y, true);
	}

	public void setPosition(int x, int y) {
		position = new Position(x, y);
	}

	public void moveTo(int x, int y, boolean recalculateSpeed, Map map) {
		moveTo(x, y, recalculateSpeed);
		map.scrollTo(this);
	}

	public void moveTo(int x, int y, Map map) {
		moveTo(x, y);
		map.scrollTo(this);
	}

	public void setPosition(int x, int y, Map map) {
		setPosition(x, y);
		map.scrollTo(this);
	}

	public Position[] getTargets(Player[] players) {
		List<Position> positions = new ArrayList<Position>();

		// X
		positions.add(new Position(position.x + speed.x, position.y + speed.y));
		// NW
		positions.add(new Position(position.x + speed.x - 1, position.y
				+ speed.y - 1));
		// N
		positions.add(new Position(position.x + speed.x, position.y + speed.y
				- 1));
		// NE
		positions.add(new Position(position.x + speed.x + 1, position.y
				+ speed.y - 1));
		// E
		positions.add(new Position(position.x + speed.x + 1, position.y
				+ speed.y));
		// SE
		positions.add(new Position(position.x + speed.x + 1, position.y
				+ speed.y + 1));
		// S
		positions.add(new Position(position.x + speed.x, position.y + speed.y
				+ 1));
		// SW
		positions.add(new Position(position.x + speed.x - 1, position.y
				+ speed.y + 1));
		// W
		positions.add(new Position(position.x + speed.x - 1, position.y
				+ speed.y));

		// All players position, we will exclude all positions from this array
		List<Position> playerPositions = new ArrayList<Position>();
		for (int i = 0; i < players.length; i++) {
			playerPositions.add(players[i].position);
		}
		positions.removeAll(playerPositions);

		return positions.toArray(new Position[] {});
	}

	public Position[] getTargets() {
		return getTargets(new Player[] { this });
	}

}
