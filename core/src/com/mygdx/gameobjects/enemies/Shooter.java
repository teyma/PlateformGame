package com.mygdx.gameobjects.enemies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.gameobjects.Level;
import com.mygdx.gameobjects.Player;
import com.mygdx.gameobjects.Projectile;

public class Shooter extends Enemy {
	

	public Shooter(Vector2 position) {
	    super(position);
	}
	
	@Override
    public void updateEnemy(float delta, Level currentLevel, Player player, boolean throwProctile) {
        if(throwProctile){
            Projectile bullet = null;
            // check distance and if player is behind enemy
            if (position.x - player.getPosition().x <= 12 && position.x > player.getPosition().x) {
                bullet = new Projectile(new Vector2(position.x, position.y + height * 0.7f));
                bullet.setWidth(player.getWidth() / 4);
                bullet.setHeight(player.getWidth() / 4);
                bullet.setVelocity(new Vector2(-Player.MAX_VELOCITY-2, 0));
                currentLevel.getEnemyProjectileList().add(bullet);
            } 
        }
    }
}