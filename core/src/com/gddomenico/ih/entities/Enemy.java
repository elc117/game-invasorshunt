package com.gddomenico.ih.entities;

import static com.gddomenico.ih.handlers.B2DVars.ENEMY_LIVES;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.handlers.MyInput;

public class Enemy extends B2DSprite {

	private boolean isOnRight;
	
	public Enemy (Body body) {
	    super(body);
	}

	/**
     * Follow the player in the given parameter
	 */
    public void FollowPlayer (Body player, Integer onWall, Integer edgeMap) {

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
            //First check if is player on the wall, then check if the player is pressing to move and final check if there is still map to move
            if(((onWall == 1 || onWall == 2) &&
               (MyInput.isDown(MyInput.BUTTON_D) || MyInput.isDown(MyInput.BUTTON_A))) &&
                (edgeMap != -280 && edgeMap != 0)){
                if(cos < 0 && onWall == 2)
                    body.setLinearVelocity(cos*2, sin);
                else if(cos > 0 &&onWall == 1)
                    body.setLinearVelocity(cos*-2, sin);
                else
                    body.setLinearVelocity(cos*-1, 0);

            }

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
