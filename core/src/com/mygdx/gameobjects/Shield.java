package com.mygdx.gameobjects;

import com.badlogic.gdx.math.Vector2;

public class Shield {

	//For now, projectiles are circles
	//private boolean facingRight;
	private Vector2 position;

	private float width;
	private float height;
	private boolean active;

	public Shield(Vector2 position, float width, float height, boolean active) {
		this.position = position;
		this.active = active;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Updating the projectile attributes.
	 * @param delta
	 * @param currentLevel
	 */
    public void update(float delta, Level currentLevel) {
//        if (position.x < 0) {
//            currentLevel.getEnemyProjectileList().removeValue(this, false);
//        } else {
//            velocity.scl(delta);
//            position.add(velocity);
//            velocity.scl(1 / delta);
//        }
    }

	/**************************************************** Getters/Setters *******************************************************/

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
