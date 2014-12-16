package com.mygdx.gameworld;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.gameobjects.Level;
import com.mygdx.gameobjects.Player;
import com.mygdx.gameobjects.Projectile;
import com.mygdx.gameobjects.enemies.Enemy;

public class GameWorld {

	private Player player;
	private GameRenderer renderer;
	private GameState currentState;
	private Level currentLevel;
	
	//Not sure if it's the right place for this
	private float timer;

	public enum GameState {
		MENU, READY, RUNNING, GAMEOVER, HIGHSCORE
	}

	public GameWorld() {
		currentState = GameState.MENU;
		player = new Player();
		currentLevel = new Level("level1.tmx", new Vector2(16, 17));
		
	    timer = 0f;
	}

	public void update(float delta) {

		switch (currentState) {
		case READY:
		case MENU:
		case RUNNING:
		    System.out.println("run " + currentLevel.getPlayerSpawnPosition());
			updateRunning(delta);
			break;
		default:
			break;
		}

	}
	
	public void setRenderer(GameRenderer renderer) {
	    this.renderer = renderer;
	}
	
	private void updateRunning(float delta) {
	    // projectile are thrown each second
	    boolean throwProjectile = false;
        timer += delta;
        if (timer >= 1f) {
            throwProjectile = true;
            timer -= 1f;
        }
	        
	    //Updating all enemies
        for(Enemy enemy : currentLevel.getEnemyList()){
            enemy.updateEnemy(delta, currentLevel, player, throwProjectile);
        } 
        
	    //Updating all bullets threw by enemies
        for(Projectile bullet : currentLevel.getEnemyProjectileList()){
            bullet.updateProjectile(delta, currentLevel);
        } 
        
        //Finally update the player
		player.update(delta, currentLevel);
		
		//Checking for player's status
		if(player.isDead()){
		    currentState = GameState.GAMEOVER;
		}
	}
	
	   
    public void restart() {
        currentLevel.restart();
        player.restart(currentLevel.getPlayerSpawnPosition());
        //running the game
        start();
    }

	public void start() {
		currentState = GameState.RUNNING;
	}

	public void ready() {
		currentState = GameState.READY;
		//renderer.prepareTransition(0, 0, 0, 1f);
	}

	public boolean isReady() {
		return currentState == GameState.READY;
	}

	public boolean isGameOver() {
		return currentState == GameState.GAMEOVER;
	}

	public boolean isHighScore() {
		return currentState == GameState.HIGHSCORE;
	}

	public boolean isMenu() {
		return currentState == GameState.MENU;
	}

	public boolean isRunning() {
		return currentState == GameState.RUNNING;
	}

	public Player getPlayer() {
		return player;
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(Level currentLevel) {
		this.currentLevel = currentLevel;
	}
		
}
