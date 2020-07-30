package com.gddomenico.ih.entities;

import static com.gddomenico.ih.handlers.B2DVars.ENEMY_LIVES;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy extends B2DSprite {

	private boolean isOnRight;
	
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

        isOnRight = px > 0;

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
        playerHits++;
    }
    public void setEnemyHits(int hits) {
        playerHits = hits;
    }

    /**
     * @return true if the enemy is on the right side of the player
     */
    public boolean getSide () { return isOnRight; }
    public boolean destroyEnemy() {
        return playerHits >= ENEMY_LIVES;
    }
   
}
