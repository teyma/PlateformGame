package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.mygdx.gameobjects.Player;
import com.mygdx.gameworld.GameWorld;

public class InputHandler implements InputProcessor {
	private Player player;
	private GameWorld myWorld;
	
	private boolean jumpingPressed;
	private long jumpPressedTime;

	public InputHandler(GameWorld myWorld) {
		this.myWorld = myWorld;
		player = myWorld.getPlayer();
	}

	@Override
	public boolean keyDown(int keycode) {
	    //TODO only state should be changed here, position/velocity/acceleration should be in the player update method 
	    
		// check input and apply to velocity & state
		if (keycode == Keys.SPACE && player.isGrounded() && player.getState() != Player.State.Falling) {
			if (!player.getState().equals(Player.State.Jumping)) {
				jumpingPressed = true;
				player.setGrounded(false);
				jumpPressedTime = System.currentTimeMillis();
				player.setState(Player.State.Jumping);
				player.getVelocity().y = Player.MAX_JUMP_SPEED;
			} else {
				if ((jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= Player.LONG_JUMP_PRESS))) {
					jumpingPressed = false;
				} else {
					if (jumpingPressed) {
						player.getVelocity().y = Player.MAX_JUMP_SPEED;
					}
				}
			}
		}
		
		if (keycode == Keys.LEFT) {
		    player.setFacingRight(false);
		    player.setLeftKeypressed(true);
			if (player.isGrounded()) {
				player.setState(Player.State.Walking);
			}
		}

		if (keycode == Keys.RIGHT) {
			player.setFacingRight(true);
			player.setRightKeypressed(true);
			if (player.isGrounded()) {
				player.setState(Player.State.Walking);
			}
		}	
		
		if (keycode == Keys.CONTROL_RIGHT) {
		    player.shield();
		}
		
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
	    if (keycode == Keys.LEFT) {
	    	player.setLeftKeypressed(false);
		}

		if (keycode == Keys.RIGHT) {
			player.setRightKeypressed(false);
		}

		if (keycode == Keys.SPACE && player.getState() == Player.State.Jumping) {
			player.getAcceleration().y = Player.GRAVITY;
			player.getAcceleration().scl(Gdx.graphics.getDeltaTime());
			player.getVelocity().add(player.getAcceleration().x, player.getAcceleration().y);
			player.setState(Player.State.Falling);
			jumpingPressed = false;
		}		

	    if (keycode == Keys.CONTROL_RIGHT) {
	    	player.unshield();
		}	

	    if (keycode == Keys.A) {
	    	player.attack();
		}
	    
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
