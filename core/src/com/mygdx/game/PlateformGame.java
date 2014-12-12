package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.helpers.AssetLoader;
import com.mygdx.screens.SplashScreen;

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


	
