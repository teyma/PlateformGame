package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.helpers.AssetLoader;
import com.mygdx.screens.SplashScreen;
/**
 * Main class, load assets and set the first screen of the game.
 * @author teyma
 *
 */
public class PlateformGame extends Game {
	
	@Override
	public void create() {
		AssetLoader.load();
		setScreen(new SplashScreen(this));
	}

	@Override
	public void dispose() {
		super.dispose();
		AssetLoader.dispose();
	}
}


	
