package com.mygdx.gameobjects;

import com.badlogic.gdx.math.Vector2;

/**
 * Sert à rien pour le moment
 * @author Alexandre
 *
 */
public class Attack {
	
	private static final float MAX_VELOCITY = 10f;
	//For now, projectiles are circles
	//private boolean facingRight;
	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;

	private float width;
	private float height;

	public Attack(Vector2 position) {
		this.position = position;
		velocity = new Vector2();
		velocity.x = -Player.MAX_VELOCITY;
		acceleration = new Vector2();
	}
	
	/**
	 * Updating the projectile attributes.
	 * @param delta
	 * @param currentLevel
	 */
    public void updateProjectile(float delta, Level currentLevel) {
        if (position.x < 0) {
            //currentLevel.getEnemyProjectileList().removeValue(this, false);
        } else {
            velocity.scl(delta);
            position.add(velocity);
            velocity.scl(1 / delta);
        }
    }

	/**************************************************** Getters/Setters *******************************************************/

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
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

	public static float getMaxVelocity() {
		return MAX_VELOCITY;
	}
    
}
