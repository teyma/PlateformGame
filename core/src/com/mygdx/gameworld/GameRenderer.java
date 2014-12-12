package com.mygdx.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gameobjects.Enemy;
import com.mygdx.gameobjects.Level;
import com.mygdx.gameobjects.Player;
import com.mygdx.helpers.AssetLoader;

public class GameRenderer {

	// Uses to scale the map of the currentLevel
	public static final float UNIT_SCALE = 1 / 16f;
	private GameWorld myWorld;

	private OrthogonalTiledMapRenderer renderer;
	private OrthographicCamera camera;
	private Level currentLevel;
	private Player player;

	private Batch spriteBatch;

	/* Textures for Player */
	private TextureRegion playerIdleLeft;
	private TextureRegion playerIdleRight;
	private TextureRegion playerJumpLeft;
	private TextureRegion playerJumpRight;
	private TextureRegion enemyFrame;
	private TextureRegion bulletFrame;
	private TextureRegion playerFrame;
	
	/* Animations for Player */
	private Animation playerWalkLeftAnimation;
	private Animation playerWalkRightAnimation;

	/* for debug rendering */
	ShapeRenderer debugRenderer;
	
	float timer = 0f;
	int time = 0;
	int score;
	private Stage stage;
	private Label label;
	private BitmapFont font;

	/**
	 * GameRenderer constructor
	 */
	public GameRenderer(GameWorld world) {
		myWorld = world;
		
		//Score 
		score = 0;
		font = new BitmapFont();
		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		stage = new Stage();
		label = new Label("Score : " + score, new Label.LabelStyle(font,Color.WHITE));
		label.setPosition(10, Gdx.graphics.getHeight() * 0.9f);
		stage.addActor(label);

		currentLevel = world.getCurrentLevel();
		player = world.getPlayer();

		loadPlayerTextures();
		loadEnemiesTexture();

		player.setPosition(new Vector2(15, 17));
		player.setWidth(UNIT_SCALE * playerIdleRight.getRegionWidth());
		player.setHeight(UNIT_SCALE * playerIdleRight.getRegionHeight());

		currentLevel.setEnemyList(loadEnemies());
		
		renderer = new OrthogonalTiledMapRenderer(currentLevel.getMap(), UNIT_SCALE);
		spriteBatch = renderer.getBatch();
		debugRenderer = new ShapeRenderer();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 20);
		camera.update();

	}

	/**
	 * Is called 1/delta time each second to render the game
	 * @param delta
	 */
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		updateBullets(delta);
		timer += delta;
		if (timer >= 1f) {
			updateEnemy(delta);
			time++;
			timer -= 1f;
		}
		
		debugRenderer.setProjectionMatrix(camera.combined);

		renderer.setView(camera);

		//Blocking the camera on the edges of the map
	    if (player.getPosition().x >= camera.viewportWidth/2 && player.getPosition().x <= currentLevel.getMap().getProperties().get("width", Integer.class)-camera.viewportWidth/2){
	    	camera.position.x = player.getPosition().x;
	    }     
		camera.update();
	
		renderer.render();
		spriteBatch.begin();
		drawPlayer();
		drawEnemies();
		drawBullets();
		spriteBatch.end();
		drawDebug();

		stage.draw();
	}

	
	public void updateEnemy(float delta) {
		Enemy bullet = null;
		for (Enemy enemy : currentLevel.getEnemyList()) {
			// check distance and if player is behind enemy
			if (enemy.getPosition().x - player.getPosition().x <= 12 && enemy.getPosition().x > player.getPosition().x) {
				bullet = new Enemy(new Vector2(enemy.getPosition().x, enemy.getPosition().y + enemy.getHeight() * 0.7f));
				bullet.setWidth(player.getWidth() / 4);
				bullet.setHeight(player.getWidth() / 4);
				bullet.setVelocity(new Vector2(-Player.MAX_VELOCITY-2, 0));
				currentLevel.getBulletList().add(bullet);
			}
		}
	}

	private void updateBullets(float delta) {
		Array<Enemy> bulletsToRemove = new Array<Enemy>();
		for (Enemy bullet : currentLevel.getBulletList()) {
			if (bullet.getPosition().x < 0) {
				bulletsToRemove.add(bullet);
			} else {
				bullet.getVelocity().scl(delta);
				bullet.getPosition().add(bullet.getVelocity());
				bullet.getVelocity().scl(1 / delta);
			}
		}
		currentLevel.getBulletList().removeAll(bulletsToRemove, false);
	}

	private void loadPlayerTextures() {
		playerIdleLeft = AssetLoader.playerIdleLeft;
		playerIdleRight = AssetLoader.playerIdleRight;
		playerWalkLeftAnimation = AssetLoader.playerWalkLeftAnimation;
	    playerWalkRightAnimation = AssetLoader.playerWalkRightAnimation;
		playerJumpLeft = AssetLoader. playerJumpLeft;
		playerJumpRight = AssetLoader.playerJumpRight;
	}
	
	private void loadEnemiesTexture(){
		enemyFrame = AssetLoader.enemyFrame;
		bulletFrame = AssetLoader.bulletFrame;
	}

	public Array<Enemy> loadEnemies() {
		Array<Enemy> enemyList = new Array<Enemy>();
		// Normal enemies
		Enemy en1 = new Enemy(new Vector2(33, 2));
		Enemy en2 = new Enemy(new Vector2(44, 2));
		Enemy en3 = new Enemy(new Vector2(55, 2));
		Enemy en4 = new Enemy(new Vector2(100, 2));
		Enemy en5 = new Enemy(new Vector2(100, 6));
		en1.setWidth(player.getWidth());
		en2.setWidth(player.getWidth());
		en3.setWidth(player.getWidth());
		en4.setWidth(player.getWidth());
		en5.setWidth(player.getWidth());
		en1.setHeight(player.getHeight());
		en2.setHeight(player.getHeight());
		en3.setHeight(player.getHeight());
		en4.setHeight(player.getHeight());
		en5.setHeight(player.getHeight());
		enemyList.add(en1);
		enemyList.add(en2);
		enemyList.add(en3);
		enemyList.add(en4);
		enemyList.add(en5);
		
		return enemyList;
	}

	public void drawPlayer() {

		playerFrame = player.isFacingRight() ? playerIdleRight : playerIdleLeft;

		if (player.getState() == Player.State.Walking) {

			playerFrame = player.isFacingRight() ? playerWalkRightAnimation
					.getKeyFrame(player.getStateTime(), true)
					: playerWalkLeftAnimation
							.getKeyFrame(player.getStateTime(), true);

		} else if (player.getState() == Player.State.Jumping) {

			playerFrame = player.isFacingRight() ? playerJumpRight
					: playerJumpLeft;

		} else if (player.getState() == Player.State.Falling) {

			playerFrame = player.isFacingRight() ? playerJumpRight
					: playerJumpLeft;

		}

		spriteBatch.draw(playerFrame, player.getPosition().x,
				player.getPosition().y, player.getWidth(), player.getHeight());

	}

	public void drawEnemies() {
		for (Enemy enemy : currentLevel.getEnemyList()) {
			spriteBatch.draw(enemyFrame, enemy.getPosition().x,
					enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
		}
	}

	public void drawBullets() {
		for (Enemy enemy : currentLevel.getBulletList()) {
			spriteBatch.draw(bulletFrame, enemy.getPosition().x,
					enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
		}
	}

	/**
	 * Rectangle around the player for debug purpose
	 */
	public void drawDebug() {
		debugRenderer.begin(ShapeType.Line);
		debugRenderer.setColor(new Color(0, 1, 0, 1));
		debugRenderer.rect(player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
		debugRenderer.end();

	}

}
