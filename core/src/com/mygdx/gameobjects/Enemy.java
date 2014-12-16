package com.mygdx.gameobjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
	public enum State {
		Standing, Walking, Jumping, Falling, Flying, Running
	}

	private static final float MAX_VELOCITY = 10f;
	private static final float DAMPING = 1.6f;
	private State state;
	private boolean facingRight;
	private boolean grounded;
	private float stateTime;
	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;

	private float width;
	private float height;

	private TextureRegion bossFrame;

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
	
    public void updateEnemy(float delta, Level currentLevel, Player player, boolean throwProctile) {
        if(throwProctile){
            Projectile bullet = null;
            // check distance and if player is behind enemy
            if (position.x - player.getPosition().x <= 12 && position.x > player.getPosition().x) {
                bullet = new Projectile(new Vector2(position.x, position.y + height * 0.7f));
                bullet.setWidth(player.getWidth() / 4);
                bullet.setHeight(player.getWidth() / 4);
                bullet.setVelocity(new Vector2(-Player.MAX_VELOCITY-2, 0));
                currentLevel.getEnemyProjectileList().add(bullet);
            } 
        }
    }

	/**************************************************** Getters/Setters *******************************************************/

	public TextureRegion getBossFrame() {
		return bossFrame;
	}

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