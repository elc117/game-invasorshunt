package com.gddomenico.ih.entities;

import static com.gddomenico.ih.handlers.B2DVars.ENEMY_LIVES;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.invasorsHunt;
import com.gddomenico.ih.states.Play;

public class Enemy extends B2DSprite {

	private boolean isOnRight;

	private float attackDelay;
    private float timer = 0;
    private float timer_walk = 0;

    private final TextureRegion[] punch;
    private final TextureRegion[] walk;
    private final TextureRegion[] death;

    private boolean isDead;
	
	public Enemy (Body body) {
	    super(body);

        attackDelay = Play.getRand(3,12)/(float) Play.getRand(3,5);

        walk = animateCharacter(invasorsHunt.res.getTexture("enemies"), 12, 1);
        punch = animateCharacter(invasorsHunt.res.getTexture("enemies_punch"), 13, 1);
        death = animateCharacter(invasorsHunt.res.getTexture("enemies_death"), 11, 1);
        setAnimation(walk);

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

        float cos = (px / hipotenusa) * 0.5f;
        float sin = (py / hipotenusa) * 0.5f;

        //System.out.println("hipot: " + hipotenusa);

        if(hipotenusa < 0.2 || stop)
            body.setLinearVelocity(0, 0);
        else
            body.setLinearVelocity(cos, sin);

    }

    public void update(float dt, Body Player) {
        super.update(dt);

        timer_walk += dt;
        // Spawn a new enemy each a few seconds
        if (timer_walk >= 1.08f) {
            if(isDead)
                setAnimation(death);
            else
                setAnimation(walk);

            timer_walk -= 1.08f;
        }

        animation.setWalk(true);
        FollowPlayer(Player);
    }

    public void setEnemyHits() {
        playerHits++;
    }
    public void setEnemyHits(int hits) {
        playerHits = hits;
    }

    public void hasPunched () {
        timer -= attackDelay;
    }

    public void countTimer (float dt) { timer += dt; }
    public boolean canPunch () {
        if (timer >= attackDelay) {
            setAnimation(punch, 1/30f);
            hasPunched();
            stop = true;
            attackDelay = Play.getRand(3,12)/(float) Play.getRand(3,5);
            return true;
        }
        return false;
    }

    /**
     * @return true if the enemy is on the left side of the player
     */
    public boolean getSide () { return isOnRight; }
    public void setDeathTextureRegion() {  isDead=true;}
    public boolean destroyEnemy() {
        return playerHits >= ENEMY_LIVES;
    }
    public boolean isAnimationFinished() { return animation.getFrame(0) == walk[0];}
   
}
