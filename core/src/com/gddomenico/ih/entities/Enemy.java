package com.gddomenico.ih.entities;

import static com.gddomenico.ih.handlers.B2DVars.ENEMY_LIVES;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy extends B2DSprite {

	private int enemyHits = 0;
	
	public Enemy (Body body) {
	    super(body);
	}

	/**
     * Follow the player in the given parameter
	 */
    public void FollowPlayer (Body player) {

        Vector2 position = player.getPosition();
        Vector2 positionEnemy = body.getPosition();

        float px = position.x - positionEnemy.x;
        float py = position.y - positionEnemy.y;

        float hipotenusa = (float) Math.sqrt((px * px) + (py * py));

        float cos = (px / hipotenusa) * 0.2f;
        float sin = (py / hipotenusa) * 0.2f;

        //System.out.println("hipot: " + hipotenusa);

        if(hipotenusa < 0.1)
            body.setLinearVelocity(0, 0);
        else
            body.setLinearVelocity(cos, sin);
    }

    public void setEnemyHits() {
    	enemyHits++;
    }
    public void setEnemyHits(int hits) {
    	enemyHits = hits;
    }

    public int getHits() {
    	return enemyHits;
    }

    public boolean destroyEnemy() {
        return enemyHits >= ENEMY_LIVES;
    }
   
}
