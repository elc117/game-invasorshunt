package com.gddomenico.ih.entities;

import static com.gddomenico.ih.handlers.B2DVars.PPM;

import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.gddomenico.ih.handlers.B2DVars;

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

        if(hipotenusa < 0.15)
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
        return enemyHits >= 5;
    }
   
}
