package com.mygdx.gameobjects;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Game level
 * 
 * @author Tristan
 *
 */
public class Level {

	private TiledMap map;
	private float tileWidth;
	private float tileHeight;
	
	private Array<Enemy> enemyList;
	private Array<Enemy> bulletList;

	public Level(String tilemapName) {
		enemyList = new Array<Enemy>();
		bulletList = new Array<Enemy>();
		
		map = new TmxMapLoader().load(tilemapName);
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
		tileWidth = layer.getTileWidth();
		tileHeight = layer.getTileHeight();
	}

	private Pool<Rectangle> rectPool = new Pool<Rectangle>() {
		@Override
		protected Rectangle newObject() {
			return new Rectangle();
		}
	};
	
	/**
	 * Retrieve all wall tiles around the player frame
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @return 
	 */
	public Array<Rectangle> getWallsTiles(int startX, int startY, int endX, int endY) {
		Array<Rectangle> tiles = new Array<Rectangle>();
		
		TiledMapTileLayer layer = (TiledMapTileLayer) this.getMap().getLayers().get(1);
		rectPool.freeAll(tiles);
		tiles.clear();
		for (int y = startY; y <= endY; y++) {
			for (int x = startX; x <= endX; x++) {
				Cell cell = layer.getCell(x, y);
				if (cell != null) {
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
		return tiles;
	}
	
	public Array<Rectangle> getEnemyTiles(int startX, int startY, int endX, int endY) {
		
		Array<Rectangle> tiles = new Array<Rectangle>();
		
		rectPool.freeAll(tiles);
		for (Enemy enemy : enemyList) {
			if (startX < enemy.getPosition().x && endX > enemy.getPosition().x) {
				Rectangle rect = rectPool.obtain();
				rect.set(enemy.getPosition().x, enemy.getPosition().y,
						enemy.getWidth(), enemy.getHeight());
				tiles.add(rect);
			}
		}
		
		return tiles;
	}

	public Array<Rectangle> getBulletTiles(int startX, int startY, int endX, int endY) {
		Array<Rectangle> tiles = new Array<Rectangle>();
		
		for (Enemy enemy : bulletList) {
			if (startX < enemy.getPosition().x && endX > enemy.getPosition().x) {
				Rectangle rect = rectPool.obtain();
				rect.set(enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
				tiles.add(rect);
			}
		}
		
		return tiles;
	}
	public TiledMap getMap() {
		return map;
	}

	public float getTileHeight() {
		return tileHeight;
	}

	public float getTileWidth() {
		return tileWidth;
	}

	public Array<Enemy> getEnemyList() {
		return enemyList;
	}

	public void setEnemyList(Array<Enemy> enemyList) {
		this.enemyList = enemyList;
	}

	public Array<Enemy> getBulletList() {
		return bulletList;
	}

	public void setBulletList(Array<Enemy> bulletList) {
		this.bulletList = bulletList;
	}
	
	
}
