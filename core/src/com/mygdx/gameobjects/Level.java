package com.mygdx.gameobjects;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
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
	private Array<Projectile> enemyProjectileList;

	public Level(String tilemapName) {
		enemyList = new Array<Enemy>();
		enemyProjectileList = new Array<Projectile>();
		
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
	
	public Map<Rectangle, Enemy> getEnemyRectangles(int startX, int startY, int endX, int endY) {
		
	    Map<Rectangle, Enemy> rectangles = new HashMap<Rectangle, Enemy>();
		//Array<Rectangle> tiles = new Array<Rectangle>();
		//rectPool.freeAll(tiles);
		for (Enemy enemy : enemyList) {
			if (startX < enemy.getPosition().x && endX > enemy.getPosition().x) {
				Rectangle rect = rectPool.obtain();
				rect.set(enemy.getPosition().x, enemy.getPosition().y, enemy.getWidth(), enemy.getHeight());
				rectangles.put(rect,enemy);
			}
		}
		
		return rectangles;
	}

	public Array<Rectangle> getProjectileRectangles(int startX, int startY, int endX, int endY) {
		Array<Rectangle> rectangles = new Array<Rectangle>();
		
		for (Projectile projectile : enemyProjectileList) {
			if (startX < projectile.getPosition().x && endX > projectile.getPosition().x) {
				Rectangle rect = rectPool.obtain();
				rect.set(projectile.getPosition().x, projectile.getPosition().y, projectile.getWidth(), projectile.getHeight());
				rectangles.add(rect);
			}
		}
		
		return rectangles;
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

    public Array<Projectile> getEnemyProjectileList() {
        return enemyProjectileList;
    }

    public void setEnemyProjectileList(Array<Projectile> enemyProjectileList) {
        this.enemyProjectileList = enemyProjectileList;
    }

}
