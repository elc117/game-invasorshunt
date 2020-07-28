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

public class Enemy {
	
	private Body enemyBody;

	private int enemyHits = 0;
	
	public Enemy (World world) {
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        
        bdef.position.set((100+getRand(getRand(-50,50),200)) / PPM,(120+getRand(getRand(-50,50),100)) / PPM);
        bdef.type = BodyDef.BodyType.KinematicBody;
        enemyBody = world.createBody(bdef);
        enemyBody.setGravityScale(0f);

        shape.setAsBox(5 / PPM,5 / PPM);
        fdef.shape = shape;
        //to change the category
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY;
        enemyBody.createFixture(fdef).setUserData("Enemy");

        // create a foot sensor
        shape.setAsBox(5/PPM, 2/PPM, new Vector2(0, -2/ PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        enemyBody.createFixture(fdef).setUserData("Foot_Enemy");
		
	}
   
    public void FollowPlayer (Body player) {

    	Vector2 position = player.getPosition();
    	Vector2 positionEnemy = enemyBody.getPosition();

    	float px = position.x - positionEnemy.x;
    	float py = position.y - positionEnemy.y;

    	float hipotenusa = (float) Math.sqrt((px*px) + (py * py));

    	float cos = (px / hipotenusa)*0.2f;
    	float sin = (py / hipotenusa)*0.2f;
    
    	//System.out.println("hipot: " + hipotenusa);

    	enemyBody.setLinearVelocity(cos, sin);
    }

 
    public Body getBody() {
    	return enemyBody;
    }
    
    /**
     * Gets a range and returns a random value in between
     * @param min x
     * @param max y
     * @return random number
     */
    public int getRand(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public void setEnemyHits() {
    	enemyHits++;
    }
    public void setEnemyHits(int hits) {
    	enemyHits = hits;
    }
    
    public boolean destroyEnemy() {
    	return (enemyHits >= 5) ? true : false;
    }
    public int getHits() {
    	return enemyHits;
    }
   
}
