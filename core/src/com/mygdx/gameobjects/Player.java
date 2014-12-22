package com.mygdx.gameobjects;

import java.util.Map;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.gameobjects.enemies.Enemy;

public class Player {

	public static final float MAX_VELOCITY = 10f;
	public static final float DAMPING = 0.87f;
	public static final float GRAVITY = -22.0f;
	public static final float MAX_JUMP_SPEED = 10f;
	public static final long LONG_JUMP_PRESS = 150l;

	public enum State {
		Standing, Walking, Jumping, Falling
	}

	private State state;
	private boolean facingRight;
	private boolean grounded;
	private float stateTime;

	private Vector2 position;
	private Vector2 velocity;
	private Vector2 acceleration;

	private float width;
	private float height;

	private boolean rightKeypressed;
	private boolean leftKeypressed;

	private Shield shield;

	private boolean attack;

	Rectangle playerRect;


	private boolean dead;
	/**************************************************** Players stats *******************************************************/
	private int maximumLife;
	private float currentLife;


	public Player() {
		position = new Vector2();
		velocity = new Vector2();
		acceleration = new Vector2();
		state = State.Standing;
		facingRight = true;
		stateTime = 0;
		grounded = true;
		rightKeypressed = leftKeypressed = false;
		attack = false;

		shield = new Shield(position, width, height, false);

		dead = false;
		maximumLife = 3;
		currentLife = (float)maximumLife;

		Pool<Rectangle> rectPool = new Pool<Rectangle>() {
			@Override
			protected Rectangle newObject() {
				return new Rectangle();
			}
		};
		playerRect = rectPool.obtain();
		playerRect.set(position.x, position.y, width, height);
	}

	/**************************************************** Getters/Setters *******************************************************/

	public void update(float delta, Level level){

		// Setting x player velocity if a key was pressed
		if(rightKeypressed && facingRight){
			velocity.x = Player.MAX_VELOCITY;
		}
		if(leftKeypressed && !facingRight){
			velocity.x = -Player.MAX_VELOCITY;
		}

		//Slowing down the player
		velocity.x *= Player.DAMPING; 
		// clamp the velocity to 0 if it's < 1, and set the state to standing
		if (Math.abs(velocity.x) < 1) {
			velocity.x = 0;
			if (grounded) {
				state = Player.State.Standing; 
			}
		}

		//Set player state as Falling if his y velocity is not 0
		if (state != Player.State.Falling && velocity.y < 0) {
			state = Player.State.Falling;
			grounded = false;
		}

		acceleration.y = Player.GRAVITY;
		acceleration.scl(delta);
		velocity.add(acceleration.x, acceleration.y);

		//scale the velocity by the frame rate
		velocity.scl(delta);

		// perform collision detection & response, on each axis, separately
		// if the player is moving right, check the tiles to the right of it's
		// right bounding box edge, otherwise check the ones to the left

		int startX, startY, endX, endY;
		if (this.getVelocity().x > 0) {
			startX = endX = (int) (position.x + width + velocity.x);
		} else {
			startX = endX = (int) (position.x + velocity.x);
		}
		startY = (int) (position.y);
		endY = (int) (position.y + height);

		Array<Rectangle> tiles= level.getWallsTiles(startX, startY, endX, endY);

		playerRect.x += velocity.x;

		for (Rectangle tile : tiles) {
			if (playerRect.overlaps(tile)) {
				velocity.x = 0;
				break;
			}
		}

		playerRect.set(position.x, position.y, width, height);

		// if the player is moving upwards, check the tiles to the top of it's
		// top bounding box edge, otherwise check the ones to the bottom
		if (velocity.y > 0) {
			startY = endY = (int) (position.y + height + velocity.y);
		} else {
			startY = endY = (int) (position.y + velocity.y);
		}

		startX = (int) (position.x);
		endX = (int) (position.x + width);
		tiles = level.getWallsTiles(startX, startY, endX, endY);
		playerRect.y += velocity.y;
		for (Rectangle tile : tiles) {
			if (playerRect.overlaps(tile)) {
				// we actually reset the player y-position here
				// so it is just below/above the tile we collided with
				// this removes bouncing :)
				if (velocity.y > 0) {
					velocity.y = tile.y - height;
					// we hit a block jumping upwards, let's destroy it!
					TiledMapTileLayer layer = (TiledMapTileLayer)level.getMap().getLayers().get(1);
					layer.setCell((int)tile.x, (int)tile.y, null);
				} else {
					position.y = tile.y + tile.height;
					// if we hit the ground, mark us as grounded so we can jump
					grounded = true;
					if(velocity.x != 0){
						state = State.Walking;
					} else {
						state = State.Standing;
					}
				}
				velocity.y = 0;
				break;
			}
		}
		startX = (int) (position.x - width);
		endX = (int) (position.x + width * 2);

		startY = (int) (position.y - height);
		endY = (int) (position.y + height * 2);

		Map<Rectangle,Enemy> enemyRectangles = level.getEnemyRectangles(startX, startY, endX, endY);    

		Rectangle intersection = new Rectangle();                  

		for (Map.Entry<Rectangle, Enemy> entry : enemyRectangles.entrySet()){
			if (playerRect.overlaps(entry.getKey())) {
				//let's check that the collision with the enemy is on its top 
				Intersector.intersectRectangles(playerRect, entry.getKey(), intersection);                               
				if(intersection.height<0.4) {
					//kill the enemy
					level.getEnemyList().removeValue(entry.getValue(), true);
					//rebound
					velocity.y = 0.15f;
				}else{ 
					removeLife(0.5f);
				}                           
			} 
			if (attack) {
				Pool<Rectangle> rectPool = new Pool<Rectangle>() {
					@Override
					protected Rectangle newObject() {
						return new Rectangle();
					}
				};
				Rectangle attackRect = rectPool.obtain();
				if (facingRight) {
					attackRect.set(position.x+width, position.y, width, height);
				} else {
					attackRect.set(position.x-width, position.y, width, height);
				}
				if (attackRect.overlaps(entry.getKey())) {
					//kill the enemy
					level.getEnemyList().removeValue(entry.getValue(), true);
				}
			}
		}		

		Map<Rectangle,Projectile> bulletRectangles = level.getProjectileRectangles(startX, startY, endX, endY);
		for (Map.Entry<Rectangle, Projectile> entry : bulletRectangles.entrySet()) {
			if (playerRect.overlaps(entry.getKey())) {
				if (!shield.isActive()) {
					removeLife(0.5f);
				}
				level.getEnemyProjectileList().removeValue(entry.getValue(), true);
			}
		}

		//Prevent the player from getting out of the level by the left side
		if(position.x < 0 && velocity.x < 0){
			velocity.x = 0;
		}
		// set the latest position and unscale the velocity by the inverse delta time
		position.add(velocity);
		velocity.scl(1 / delta);

		stateTime += delta;
		position.y += velocity.cpy().scl(delta).y;
		//The player dies here
		if (position.y < 0) {
			dead = true;
		}

		shield.setPosition(position);
	}

	private void removeLife(float value){
		currentLife -= value;
		if(currentLife <= 0){
			dead=true;
		}
	}

	/**
	 * Re intitialize player
	 * @param position
	 */
	public void restart(Vector2 spawnPosition){
		dead = false;
		position = spawnPosition;
		acceleration = new Vector2();
		velocity = new Vector2();
		currentLife = maximumLife;
	}

	/**************************************************** Getters/Setters *******************************************************/

	public void attack(){
		this.attack = true;
	}
	
	public void setAttack(boolean attack){
		this.attack = attack;
	}

	public boolean getAttack(){
		return this.attack;
	}

	public void shield() {
		shield.setActive(true);
	}

	public void unshield() {
		shield.setActive(false);
	}	

	public Shield getShield() {
		return shield;
	}

	public Vector2 getAcceleration() {
		return acceleration;
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

	public boolean isRightKeypressed() {
		return rightKeypressed;
	}

	public void setRightKeypressed(boolean rightKeypressed) {
		this.rightKeypressed = rightKeypressed;
	}

	public boolean isLeftKeypressed() {
		return leftKeypressed;
	}

	public void setLeftKeypressed(boolean leftKeypressed) {
		this.leftKeypressed = leftKeypressed;
	}

	public int getMaximumLife() {
		return maximumLife;
	}

	public void setMaximumLife(int maximumLife) {
		this.maximumLife = maximumLife;
	}

	public float getCurrentLife() {
		return currentLife;
	}

	public void setCurrentLife(float currentLife) {
		this.currentLife = currentLife;
	}

	public boolean isDead() {
		return dead;
	}
}