package com.gddomenico.ih.entities;

import static com.gddomenico.ih.handlers.B2DVars.ENEMY_LIVES;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.invasorsHunt;
import com.gddomenico.ih.states.Play;

public class Enemy extends B2DSprite {

	private boolean isOnRight;

	private float attackDelay;
    private float timer = 0;

	public Enemy (Body body) {
	    super(body);

        attackDelay = Play.getRand(3,12)/(float) Play.getRand(3,5);

        walk = animateCharacter(invasorsHunt.res.getTexture("enemies"), 12, 1);
        punch = animateCharacter(invasorsHunt.res.getTexture("enemies_punch"), 8, 1);
        death = animateCharacter(invasorsHunt.res.getTexture("enemies_death"), 11, 1);

        animation.setWalk(true);
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

        float sqrt = (float) Math.sqrt((px * px) + (py * py));

        float cos = (px / sqrt) * 0.5f;
        float sin = (py / sqrt) * 0.5f;

        if(sqrt < 0.25 || stop)
            body.setLinearVelocity(0, 0);
        else
            body.setLinearVelocity(cos, sin);

    }

    public void update(float dt, Body Player) {
        super.update(dt);

        // Analyzes if the player is punching to set punch animation
        if(animation.getFrame() == punch[0] && !stop) {
            setAnimation(walk);
        }
        // Analyzes if the player is dead to set death animation
        if(animation.getFrame() == death[0] && !stop) {
            playerHits = -1;
        }

        if(!isDead)
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
            invasorsHunt.res.getSound("enemyPunch").play(0.5f);
            setAnimation(punch);
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

    public boolean destroyEnemy() {
        return playerHits >= ENEMY_LIVES;
    }
   
}
