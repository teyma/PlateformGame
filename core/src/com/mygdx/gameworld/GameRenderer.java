package com.mygdx.gameworld;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gameobjects.Level;
import com.mygdx.gameobjects.Player;
import com.mygdx.gameobjects.Projectile;
import com.mygdx.gameobjects.enemies.Enemy;
import com.mygdx.gameobjects.enemies.Shooter;
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
    private Batch hudBatch;  	

	/* Textures for Player */
	private TextureRegion playerIdleLeft;
	private TextureRegion playerIdleRight;
	private TextureRegion playerJumpLeft;
	private TextureRegion playerJumpRight;
	private TextureRegion enemyFrame;
	private TextureRegion bulletFrame;
	private TextureRegion attackFrame;
	private TextureRegion shieldFrame;
	private TextureRegion playerFrame;
	
	private TextureRegion fullHeart;
	private TextureRegion halfHeart;
	private TextureRegion emptyHeart;
	
	/* Animations for Player */
	private Animation playerWalkLeftAnimation;
	private Animation playerWalkRightAnimation;

	/* for debug rendering */
	ShapeRenderer debugRenderer;
	
	int score;
	private Stage stage;
	private Label label;
	private BitmapFont font;

	/**
	 * GameRenderer constructor
	 */
	public GameRenderer(GameWorld world) {
		myWorld = world;

        loadPlayerTextures();
        loadEnemiesTexture();
        loadHUDTextures();
		
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

		player.setPosition(currentLevel.getPlayerSpawnPosition());
		player.setWidth(UNIT_SCALE * playerIdleRight.getRegionWidth());
		player.setHeight(UNIT_SCALE * playerIdleRight.getRegionHeight());

		currentLevel.setEnemyList(loadEnemies());
		
		renderer = new OrthogonalTiledMapRenderer(currentLevel.getMap(), UNIT_SCALE);
		spriteBatch = renderer.getBatch();
		
		debugRenderer = new ShapeRenderer();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 30, 20);
		camera.update();
		
		//TODO Dirty way
        hudBatch = new SpriteBatch();
        Matrix4 uiMatrix = camera.combined.cpy();
        uiMatrix.setToOrtho2D(0, 0, 30, 20);
        hudBatch.setProjectionMatrix(uiMatrix);
	}

	/**
	 * Is called 1/delta time each second to render the game
	 * @param delta
	 */
	public void render(float delta) {
		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
		debugRenderer.setProjectionMatrix(camera.combined);

		renderer.setView(camera);

		//Blocking the camera on the edges of the map
	    if (player.getPosition().x >= camera.viewportWidth/2 && player.getPosition().x <= currentLevel.getMap().getProperties().get("width", Integer.class)-camera.viewportWidth/2){
	    	camera.position.x = player.getPosition().x;
	    }  
	    //TODO define this well
	    camera.position.y = player.getPosition().y + camera.viewportHeight/8;
		camera.update();
	
		renderer.render();
		spriteBatch.begin();
		drawPlayer();
		drawEnemies();
		drawBullets();
		drawShield();
		drawAttack();
		spriteBatch.end();
		
        hudBatch.begin();
        drawHUD();
        hudBatch.end();
	        
		drawDebug();
		
		
		/*score++;
		label.setText("Score : " + score);*/
		
		stage.draw();
	}

	private void loadPlayerTextures() {
		playerIdleLeft = AssetLoader.playerIdleLeft;
		playerIdleRight = AssetLoader.playerIdleRight;
		playerWalkLeftAnimation = AssetLoader.playerWalkLeftAnimation;
	    playerWalkRightAnimation = AssetLoader.playerWalkRightAnimation;
		playerJumpLeft = AssetLoader. playerJumpLeft;
		playerJumpRight = AssetLoader.playerJumpRight;
		attackFrame = AssetLoader.bulletFrame;
		shieldFrame = AssetLoader.shieldFrame;
	}
	
	private void loadEnemiesTexture(){
		enemyFrame = AssetLoader.enemyFrame;
		bulletFrame = AssetLoader.bulletFrame;
	}
	
    private void loadHUDTextures(){
        fullHeart = AssetLoader.fullHeart;
        halfHeart = AssetLoader.halfHeart;
        emptyHeart = AssetLoader.emptyHeart;
    }
	
	public Array<Enemy> loadEnemies() {
		Array<Enemy> enemyList = new Array<Enemy>();
		// Normal enemies
		Shooter en1 = new Shooter(new Vector2(33, 2));
		Shooter en2 = new Shooter(new Vector2(44, 2));
		Shooter en3 = new Shooter(new Vector2(55, 2));
		Shooter en4 = new Shooter(new Vector2(100, 2));
		Shooter en5 = new Shooter(new Vector2(100, 6));
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

			playerFrame = player.isFacingRight() ? playerWalkRightAnimation.getKeyFrame(player.getStateTime(), true)
					: playerWalkLeftAnimation.getKeyFrame(player.getStateTime(), true);

		} else if (player.getState() == Player.State.Jumping) {
			playerFrame = player.isFacingRight() ? playerJumpRight : playerJumpLeft;

		} else if (player.getState() == Player.State.Falling) {
			playerFrame = player.isFacingRight() ? playerJumpRight : playerJumpLeft;
		}

		spriteBatch.draw(playerFrame, player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());

	}

	public void drawEnemies() {
		for (Enemy enemy : currentLevel.getEnemyList()) {
			spriteBatch.draw(enemyFrame, enemy.getPosition().x,
					enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
		}
	}

	public void drawBullets() {
		for (Projectile enemy : currentLevel.getEnemyProjectileList()) {
			spriteBatch.draw(bulletFrame, enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
		}
	}
	
	public void drawAttack() {
		if (player.getAttack()) {
			if (player.isFacingRight()) {
				spriteBatch.draw(attackFrame, player.getPosition().x+player.getWidth(), player.getPosition().y, player.getWidth(), player.getHeight());
			} else {
				spriteBatch.draw(attackFrame, player.getPosition().x-player.getWidth(), player.getPosition().y, player.getWidth(), player.getHeight());
			}
			player.setAttack(false);
		}
	}

	public void drawShield() {
		if (player.getShield().isActive()) {
			spriteBatch.draw(shieldFrame, player.getShield().getPosition().x, player.getShield().getPosition().y, player.getWidth(), player.getHeight());
		}
	}
	
	/**
	 * HUD (Heads-Up Display)
	 */
    public void drawHUD() {
        //Draw players life
        float playerCurrentLife = player.getCurrentLife();
        int heartWidth = fullHeart.getRegionWidth();
        int heartHeight = fullHeart.getRegionHeight();
        for (int i = 1; i <= player.getMaximumLife(); i++) {
           if(playerCurrentLife >= i){
               hudBatch.draw(fullHeart,  15 + heartWidth/30 * i, 18, heartWidth/30, heartHeight/30);
           }else if(playerCurrentLife < i && playerCurrentLife > i-1){
               hudBatch.draw(halfHeart,  15 + heartWidth/30 * i, 18, heartWidth/30, heartHeight/30);
           }else{
               hudBatch.draw(emptyHeart,  15 + heartWidth/30 * i, 18, heartWidth/30, heartHeight/30);
           }
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
