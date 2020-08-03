package com.gddomenico.ih.entities;

import static com.gddomenico.ih.handlers.B2DVars.ENEMY_LIVES;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.invasorsHunt;
import com.gddomenico.ih.states.Play;

public class Enemy extends B2DSprite {

	private boolean isOnRight;

	private float attackDelay;
    private float timer = 0;
	
	public Enemy (Body body) {
	    super(body);

        attackDelay = Play.getRand(3,12)/(float) Play.getRand(3,5);

        int column = 12;
        int row = 1;

        Texture tex = invasorsHunt.res.getTexture("enemies");
        TextureRegion[][] tmp = new TextureRegion(tex).split(
                tex.getWidth() / column,
                tex.getHeight() / row);

        TextureRegion[] sprites = new TextureRegion[column*row];

        int index = 0;
        for (int i=0; i<row; i++) {
            for (int j=0; j<column; j++) {
                sprites[index++]=tmp[i][j];
            }
        }
        setAnimation(sprites, 1 / 12f);
        animation.setWalk(true);
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

        float cos = (px / hipotenusa) * 0.5f;
        float sin = (py / hipotenusa) * 0.5f;

        //System.out.println("hipot: " + hipotenusa);

        System.out.println(stop);
        if(hipotenusa < 0.2 || stop)
            body.setLinearVelocity(0, 0);
        else
            //First check if is player on the wall, then check if the player is pressing to move and final check if there is still map to move
            if(((onWall == 1 || onWall == 2) &&
               (MyInput.isDown(MyInput.BUTTON_D) || MyInput.isDown(MyInput.BUTTON_A))) &&
                (edgeMap != -280 && edgeMap != 0)){
                if(cos < 0 && onWall == 2)
                    body.setLinearVelocity(cos*1, sin);
                else if(cos > 0 && onWall == 1)
                    body.setLinearVelocity(cos*-1, sin);
                else
                    body.setLinearVelocity(cos*-0.5f, 0);
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

    public void hasPunched () { timer -= attackDelay; }
    public void countTimer (float dt) { timer += dt; }
    public boolean canPunch () {
        if (timer >= attackDelay) {
            hasPunched();
            stop = true;
            attackDelay = Play.getRand(3,12)/(float) Play.getRand(3,5);
            return true;
        }
        return false;
    }

    /**
     * @return true if the enemy is on the right side of the player
     */
    public boolean getSide () { return isOnRight; }
    public boolean destroyEnemy() {
        return playerHits >= ENEMY_LIVES;
    }
   
}
