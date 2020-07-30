package com.gddomenico.ih.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.handlers.MyContactListener;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.invasorsHunt;

public class Player extends B2DSprite{

    private Integer playerHits = 0;
    private boolean rightArm = false;

    private final MyContactListener cl;

	public Player(Body body) {
		super(body);

		cl = new MyContactListener();

		Texture tex = invasorsHunt.res.getTexture("bunny");
        TextureRegion[] sprites = TextureRegion.split(tex, 128/5, 32)[0];

        setAnimation(sprites, 1 / 12f);
	}
	

    public boolean handleInput() {
        if(MyInput.isDown(MyInput.BUTTON_SPACE)){
            System.out.println("SPACE");
        }
        if(!MyInput.isDown(MyInput.BUTTON_D) && !MyInput.isDown(MyInput.BUTTON_W) && !MyInput.isDown(MyInput.BUTTON_A) && !MyInput.isDown(MyInput.BUTTON_S)) {
            body.setLinearVelocity(0,0);
            animation.setWalk(false);
        }
        else {
            animation.setWalk(true);
        }
        if(MyInput.isDown(MyInput.BUTTON_W)) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0.5f);
        }
        if(MyInput.isUp(MyInput.BUTTON_W)) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }
        if(MyInput.isDown(MyInput.BUTTON_S)){
            body.setLinearVelocity(body.getLinearVelocity().x, -0.5f);
        }
        if(MyInput.isUp(MyInput.BUTTON_S)) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        }
        if(MyInput.isDown(MyInput.BUTTON_A)){
            rightArm = true;
            body.setLinearVelocity(-0.5f, body.getLinearVelocity().y);
        }
        if(MyInput.isUp(MyInput.BUTTON_A)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
        if(MyInput.isDown(MyInput.BUTTON_D)){
            rightArm = false;
            body.setLinearVelocity(0.5f, body.getLinearVelocity().y);
        }
        if(MyInput.isUp(MyInput.BUTTON_D)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
        if(MyInput.isPressed(MyInput.BUTTON_K)){
            if(cl.isPlayerOnContact()){
                invasorsHunt.res.getSound("punch").play(0.1f);
                if(cl.isLeftContact() && !rightArm) {
                    System.out.println("Punch Left!!");
                }else if(cl.isRightContact() && rightArm){
                    System.out.println("Punch Right!!");
                }
                else {
                	System.out.println("Punch somewhere else");
                }
                return true;
            }else{
                invasorsHunt.res.getSound("miss").play(0.1f);
                System.out.println("Missed!!");
            }
        }
        return false;
    }

    
    public void setPlayerHits(){
        if(cl.isPlayerOnContact())
            playerHits++;
        System.out.println(playerHits);
    }

    public int getPlayerHits(){
        return playerHits;
    }
	public MyContactListener getContactListener () {
		return cl;
	}
	public boolean getRightArm () {
		return rightArm;
	}
	public void setRightArm (boolean right) {
		rightArm = right;
	}

    public void dispose() {
    }
    

}
