package com.mygdx.gameobjects.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.gameobjects.Level;
import com.mygdx.gameobjects.Player;

public class Enemy {
	public enum State {
		Standing, Walking, Jumping, Falling, Flying, Running
	}

	protected static final float MAX_VELOCITY = 10f;
	protected static final float DAMPING = 1.6f;
	protected State state;
	protected boolean facingRight;
	protected boolean grounded;
	protected float stateTime;
	protected Vector2 position;
	protected Vector2 velocity;
	protected Vector2 acceleration;

	protected float width;
	protected float height;

	public Enemy(Vector2 position) {
		this.position = position;
		velocity = new Vector2();
		velocity.x = -Player.MAX_VELOCITY;
		acceleration = new Vector2();
		state = State.Standing;
		facingRight = true;
		stateTime = 0;
		grounded = true;
	}
	
	/**
	 * Has to be redefined TODO interface?
	 * @param delta
	 * @param currentLevel
	 * @param player
	 * @param throwProctile
	 */
    public void updateEnemy(float delta, Level currentLevel, Player player, boolean throwProctile) {

    }

	/**************************************************** Getters/Setters *******************************************************/

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isFacingRight() {
		return facingRight;
	}

	public void setFacingRight(boolean facingRight) {
		this.facingRight = facingRight;
	}

	public boolean isGrounded() {
		return grounded;
	}

	public void setGrounded(boolean grounded) {
		this.grounded = grounded;
	}

	public float getStateTime() {
		return stateTime;
	}

	public void setStateTime(float stateTime) {
		this.stateTime = stateTime;
	}

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
}