package com.mygdx.gameobjects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

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

	public Player() {
		position = new Vector2();
		velocity = new Vector2();
		acceleration = new Vector2();
		state = State.Standing;
		facingRight = true;
		stateTime = 0;
		grounded = true;
		rightKeypressed = leftKeypressed = false;
	}

	/**************************************************** Getters/Setters *******************************************************/
	public Rectangle getPlayerRect(){
		Pool<Rectangle> rectPool = new Pool<Rectangle>() {
			@Override
			protected Rectangle newObject() {
				return new Rectangle();
			}
		};
		Rectangle playerRect = rectPool.obtain();
		playerRect.set(this.getPosition().x, this.getPosition().y, this.getWidth(), this.getHeight());
		return playerRect;
	}
	
	public void update(float delta, Level level){
		
		// Setting x player velocity if a key was pressed
		if(rightKeypressed && facingRight){
			this.getVelocity().x = Player.MAX_VELOCITY;
		}
		if(leftKeypressed && !facingRight){
			this.getVelocity().x = -Player.MAX_VELOCITY;
		}
			
		//Slowing down the caracter
		this.getVelocity().x *= Player.DAMPING; 
		// clamp the velocity to 0 if it's < 1, and set the state to standing
		if (Math.abs(this.getVelocity().x) < 1) {
			this.getVelocity().x = 0;
			if (this.isGrounded()) {
				this.setState(Player.State.Standing);
			}
		}

		//Set player state as Falling if his y velocity is not 0
		if (this.getState() != Player.State.Falling && this.getVelocity().y < 0) {
			this.setState(Player.State.Falling);
			this.setGrounded(false);
		}
	
		this.getAcceleration().y = Player.GRAVITY;
		this.getAcceleration().scl(delta);
		this.getVelocity().add(this.getAcceleration().x, this.getAcceleration().y);

		//scale the velocity by the frame rate
		this.getVelocity().scl(delta);

		// perform collision detection & response, on each axis, separately
		// if the player is moving right, check the tiles to the right of it's
		// right bounding box edge, otherwise check the ones to the left
		Rectangle playerRect = this.getPlayerRect(); 

		int startX, startY, endX, endY;
		if (this.getVelocity().x > 0) {
			startX = endX = (int) (this.getPosition().x + this.getWidth() + this.getVelocity().x);
		} else {
			startX = endX = (int) (this.getPosition().x + this.getVelocity().x);
		}
		startY = (int) (this.getPosition().y);
		endY = (int) (this.getPosition().y + this.getHeight());
		
		Array<Rectangle> tiles= level.getWallsTiles(startX, startY, endX, endY);

		playerRect.x += this.getVelocity().x;

		for (Rectangle tile : tiles) {
			if (playerRect.overlaps(tile)) {
				this.getVelocity().x = 0;
				break;
			}
		}

		playerRect.set(this.getPosition().x, this.getPosition().y,
				this.getWidth(), this.getHeight());

		// if the player is moving upwards, check the tiles to the top of it's
		// top bounding box edge, otherwise check the ones to the bottom
		if (this.getVelocity().y > 0) {
			startY = endY = (int) (this.getPosition().y + this.getHeight() + this.getVelocity().y);
		} else {
			startY = endY = (int) (this.getPosition().y + this.getVelocity().y);
		}

		startX = (int) (this.getPosition().x);
		endX = (int) (this.getPosition().x + this.getWidth());
		tiles = level.getWallsTiles(startX, startY, endX, endY);
		playerRect.y += this.getVelocity().y;
		for (Rectangle tile : tiles) {
			if (playerRect.overlaps(tile)) {
				// we actually reset the player y-position here
				// so it is just below/above the tile we collided with
				// this removes bouncing :)
				if (this.getVelocity().y > 0) {
					this.getVelocity().y = tile.y - this.getHeight();
					// we hit a block jumping upwards, let's destroy it!
					// TiledMapTileLayer layer = (TiledMapTileLayer)level.getMap().getLayers().get(1);
					// layer.setCell((int)tile.x, (int)tile.y, null);
				} else {
					this.getPosition().y = tile.y + tile.height;
					// if we hit the ground, mark us as grounded so we can jump
					this.setGrounded(true);
					if(this.velocity.x != 0){
						this.state = State.Walking;
					} else {
						this.state = State.Standing;
					}
				}
				this.getVelocity().y = 0;
				break;
			}
		}
		startX = (int) (this.getPosition().x - this.getWidth());
		endX = (int) (this.getPosition().x + this.getWidth() * 2);

		startY = (int) (this.getPosition().y - this.getHeight());
		endY = (int) (this.getPosition().y + this.getHeight() * 2);
		
		Array<Rectangle> enemyTiles = level.getEnemyTiles(startX, startY, endX, endY);
		for (Rectangle tile : enemyTiles) {
			if (playerRect.overlaps(tile)) {
				this.setPosition(new Vector2(15, 17));
				level.getBulletList().clear();
			}
		}
		Array<Rectangle> bulletTiles = level.getBulletTiles(startX, startY, endX, endY);
		for (Rectangle tile : bulletTiles) {
			if (playerRect.overlaps(tile)) {
				this.setPosition(new Vector2(15, 17));
				level.getBulletList().clear();
			}
		}
		// set the latest position and unscale the velocity by the inverse delta time
		this.getPosition().add(this.getVelocity());
		this.getVelocity().scl(1 / delta);

		this.setStateTime(this.getStateTime() + delta);
		this.getPosition().y += this.getVelocity().cpy().scl(delta).y;
		if (this.getPosition().y < 0) {
			this.setPosition(new Vector2(15, 17));
		}
		
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
}