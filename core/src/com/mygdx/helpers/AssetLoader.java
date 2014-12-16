package com.mygdx.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetLoader {

	public static Texture texture, logoTexture;
	
	public static TextureRegion logo, playerIdleLeft, playerIdleRight, playerJumpLeft, 
								playerJumpRight, enemyFrame,bulletFrame, playerFrame, fullHeart, halfHeart, emptyHeart;

	public static Animation playerWalkLeftAnimation, playerWalkRightAnimation;
	
	//TODO Where to put this?
	public static final float RUNNING_FRAME_DURATION = 0.09f;
	
	//save datas
	//private static Preferences prefs;

	public static void load() {

		logoTexture = new Texture(Gdx.files.internal("marioNES.jpg"));
		logoTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

		logo = new TextureRegion(logoTexture, 0, 0, 512, 114);
		
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("solbrain.pack"));

		/* Standing */
		playerIdleLeft = atlas.findRegion("1");
		// same as left but mirrored
		playerIdleRight = new TextureRegion(playerIdleLeft);
		playerIdleRight.flip(true, false);

		TextureRegion[] walkLeftFrames = new TextureRegion[6];
		TextureRegion[] walkRightFrames = new TextureRegion[6];
		
		for (int i = 0; i < 6; i++) {
			walkLeftFrames[i] = atlas.findRegion(((i + 6) + ""));
			walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
			walkRightFrames[i].flip(true, false);
		}

		playerWalkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);
		playerWalkRightAnimation = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);

		playerJumpLeft = atlas.findRegion("3");
		playerJumpRight = new TextureRegion(playerJumpLeft);
		playerJumpRight.flip(true, false);
		enemyFrame = new TextureRegion(new Texture("shooter.png"));
		enemyFrame.flip(true, false);
		bulletFrame = new TextureRegion(new Texture("bullet.png"));
		
		halfHeart = new TextureRegion(new Texture("halfHeart.jpg"));
		fullHeart = new TextureRegion(new Texture("fullHeart.jpg"));
		emptyHeart = new TextureRegion(new Texture("emptyHeart.jpg"));

		// Create (or retrieve existing) preferences file
		/*prefs = Gdx.app.getPreferences("ZombieBird");

		if (!prefs.contains("highScore")) {
			prefs.putInteger("highScore", 0);
		}*/
	}

	/*public static void setHighScore(int val) {
		prefs.putInteger("highScore", val);
		prefs.flush();
	}

	public static int getHighScore() {
		return prefs.getInteger("highScore");
	}*/

	public static void dispose() {
		// We must dispose of the texture when we are finished.
		//texture.dispose();

		// Dispose sounds
		/*dead.dispose();
		flap.dispose();
		coin.dispose();

		font.dispose();
		shadow.dispose();*/
	}

}