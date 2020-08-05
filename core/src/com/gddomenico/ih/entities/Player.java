package com.gddomenico.ih.entities;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.handlers.MyContactListener;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.invasorsHunt;

public class Player extends B2DSprite{

    private final MyContactListener cl;

    private final TextureRegion[] win;

    private boolean defeat = false;

    private boolean victory = false;
    private boolean isWinner = false;

	public Player(Body body) {
		super(body);

		cl = new MyContactListener();

		punch = animateCharacter(invasorsHunt.res.getTexture("main_punch"), 8, 1);
        walk = animateCharacter(invasorsHunt.res.getTexture("main"), 3, 1);
        death = animateCharacter(invasorsHunt.res.getTexture("main_death"), 11, 1);
        win = animateCharacter(invasorsHunt.res.getTexture("main_win"), 16, 1);

        setAnimation(walk);

	}


    public boolean handleInput() {
        if (animation.getFrame() == punch[0])
            setAnimation(walk);

        // Analyzes if the player is moving to set walk animation
        if(!MyInput.isDown(MyInput.BUTTON_D) && !MyInput.isDown(MyInput.BUTTON_W) && !MyInput.isDown(MyInput.BUTTON_A) && !MyInput.isDown(MyInput.BUTTON_S)) {

            float velX = body.getLinearVelocity().x;
            body.setLinearVelocity(velX - (velX/4f) , 0);

            if(animation.getFrame(0) == walk[0])
                animation.setWalk(false);
        }
        else {
            animation.setWalk(true);
        }

        // Movement Inputs
        if(MyInput.isDown(MyInput.BUTTON_W)) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0.75f);
        }
        if(MyInput.isUp(MyInput.BUTTON_W)) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }
        if(MyInput.isDown(MyInput.BUTTON_S)){
            body.setLinearVelocity(body.getLinearVelocity().x, -0.75f);
        }
        if(MyInput.isUp(MyInput.BUTTON_S)) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }
        if(MyInput.isDown(MyInput.BUTTON_A)){
            rightArm = false;
            body.setLinearVelocity(-0.75f, body.getLinearVelocity().y);
        }
        if(MyInput.isUp(MyInput.BUTTON_A)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
        if(MyInput.isDown(MyInput.BUTTON_D)){
            rightArm = true;
            body.setLinearVelocity(0.75f, body.getLinearVelocity().y);
        }
        if(MyInput.isUp(MyInput.BUTTON_D)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }

        // Returns true in case that player has hit some enemy
        if(MyInput.isPressed(MyInput.BUTTON_K)){
            setAnimation(punch);
            animation.setWalk(true);
            stop = true;
            body.setLinearVelocity(0,0);

            if(cl.isPlayerOnContact())
                return true;
            else
                invasorsHunt.res.getSound("miss").play(0.1f);

        }

        return false;
    }

    public void update(float dt) {
        super.update(dt);

        // Analyzes if the player is dead to set death animation
        if(isDead && !stop) {
            body.setLinearVelocity(0, 0);
            if(animation.getFrame() == death[0])
                defeat = true;
        }
        // Analyzes if the player has won to set victory animation
        if(isWinner && !stop) {
            body.setLinearVelocity(0, 0);
            if(animation.getFrame() == win[0])
                victory = true;
        }
    }

    public void setPlayerHits(int num){
	    playerHits += num;
    }
    public void getLife() { if(playerHits != 0) playerHits--; }

    public boolean getDefeat () { return defeat; }
    public boolean getVictory() { return victory; }

    public boolean getWinCondition(){ return isWinner; }
    public void setWinCondition(){
        invasorsHunt.res.getMusic("play").stop();
	    invasorsHunt.res.getSound("win").play(0.5f);
        setAnimation(win);
        animation.setWalk(true);
        isWinner = true;
        stop = true;
        timeStop = 0;
    }
    public void setDeathCondition() {
        super.setDeathCondition();

        invasorsHunt.res.getMusic("play").stop();
        invasorsHunt.res.getSound("lose").play(0.5f);
    }


	public MyContactListener getContactListener () {
		return cl;
	}

    public void dispose() {
    }


}
