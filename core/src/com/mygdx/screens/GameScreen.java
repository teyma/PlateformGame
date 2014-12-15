package com.mygdx.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.mygdx.gameworld.GameRenderer;
import com.mygdx.gameworld.GameWorld;
import com.mygdx.helpers.InputHandler;

public class GameScreen implements Screen {

	private GameWorld world;
	private GameRenderer renderer;

	public GameScreen() {
		world = new GameWorld();
		Gdx.input.setInputProcessor(new InputHandler(world));
		renderer = new GameRenderer(world);
		world.setRenderer(renderer);
	}

	@Override
	public void render(float delta) {
		//workaround for when the window is draged
		if(delta < 0.1){
			world.update(delta);
			renderer.render(delta);
		}
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("Resize");
	}

	@Override
	public void show() {
		System.out.println("Show");
	}

	@Override
	public void hide() {
		System.out.println("Hide");
	}

	@Override
	public void pause() {
		System.out.println("Pause");
	}

	@Override
	public void resume() {
		System.out.println("Resume");
	}

	@Override
	public void dispose() {
		System.out.println("Dispose");
	}
}
