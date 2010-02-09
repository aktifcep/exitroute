package com.naholyr.android.games.exitroute.api;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;

public class Player {

	public String name;

	public BitmapDrawable icon;

	public int iconResourceId;

	public Position position;

	public Speed speed;

	public int maxAcceleration = 1;
	public int maxDeceleration = 1;

	public int color = 0xAA000000;

	public Player(String name) {
		this.name = name;
		speed = new Speed(Constants.INITIAL_SPEED_X, Constants.INITIAL_SPEED_Y);
	}

	public void setIcon(int resourceId, Resources resources) {
		iconResourceId = resourceId;
		icon = (BitmapDrawable) resources.getDrawable(resourceId);
	}

	public void setIcon(int resourceId, Context context) {
		setIcon(resourceId, context.getResources());
	}

	public Speed getNewSpeedForTarget(int x, int y) {
		return new Speed(x - position.x, y - position.y);
	}

	public void moveTo(int x, int y, boolean recalculateSpeed) {
		if (recalculateSpeed) {
			speed = getNewSpeedForTarget(x, y);
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
		// FIXME Calculate depending on maxAcceleration & maxDeceleration
		List<Position> positions = new ArrayList<Position>();

		// X
		positions.add(new Position(position.x + speed.x, position.y + speed.y));
		// NW
		positions.add(new Position(position.x + speed.x - 1, position.y + speed.y - 1));
		// N
		positions.add(new Position(position.x + speed.x, position.y + speed.y - 1));
		// NE
		positions.add(new Position(position.x + speed.x + 1, position.y + speed.y - 1));
		// E
		positions.add(new Position(position.x + speed.x + 1, position.y + speed.y));
		// SE
		positions.add(new Position(position.x + speed.x + 1, position.y + speed.y + 1));
		// S
		positions.add(new Position(position.x + speed.x, position.y + speed.y + 1));
		// SW
		positions.add(new Position(position.x + speed.x - 1, position.y + speed.y + 1));
		// W
		positions.add(new Position(position.x + speed.x - 1, position.y + speed.y));

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

	public static float getOrientationAngle(Speed speed) {
		// P
		// |\
		// |a\
		// |  \
		// |   \
		// Y----X
		// X² + Y² = H²
		// sin(a) = X/H
		// => a = asin(X/sqrt(X²+Y²))
		double x = speed.x;
		double y = speed.y;
		
		if (x == 0) {
			return 0;
		}
		
		double h = Math.sqrt(x * x + y * y);
		double a = Math.asin(x / h);

		return (float) ((2 * Math.PI - a) * 180 / Math.PI);
	}

	public float getOrientationAngle() {
		return getOrientationAngle(speed);
	}

}
