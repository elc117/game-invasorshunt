package com.gddomenico.ih.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.handlers.MyContactListener;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.invasorsHunt;

import javax.swing.*;

public class Player extends B2DSprite{

    private final MyContactListener cl;

    public int xWall = -100; //moves the background in the X axis

    private final TextureRegion[] punch;
    private final TextureRegion[] walk;

	public Player(Body body) {
		super(body);

		cl = new MyContactListener();

		punch = animatePlayer(invasorsHunt.res.getTexture("main_punch"), 15, 1);
        walk = animatePlayer(invasorsHunt.res.getTexture("main"), 3, 1);
        System.out.println("index: "+ punch.length);
        System.out.println("sprites: "+ walk.length);

        setAnimation(walk);

	}
	

    public boolean handleInput() {

        if (animation.getFrame(0) == punch[0])
            setAnimation(walk);

        if(MyInput.isDown(MyInput.BUTTON_SPACE)){
            System.out.println("SPACE");
        }
        if(!MyInput.isDown(MyInput.BUTTON_D) && !MyInput.isDown(MyInput.BUTTON_W) && !MyInput.isDown(MyInput.BUTTON_A) && !MyInput.isDown(MyInput.BUTTON_S)) {
            float velX = body.getLinearVelocity().x;
            body.setLinearVelocity(velX - (velX/4f) , 0);

            animation.setWalk(false);
        }
        else {
            animation.setWalk(true);
        }
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
            if(cl.isPlayerOnTheWall() == 1 && xWall < 0)
                xWall++;
        }
        if(MyInput.isUp(MyInput.BUTTON_A)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);

        }
        if(MyInput.isDown(MyInput.BUTTON_D)){
            rightArm = true;
            body.setLinearVelocity(0.75f, body.getLinearVelocity().y);
            if(cl.isPlayerOnTheWall() == 2 && xWall > -280)
                xWall--;
        }
        if(MyInput.isUp(MyInput.BUTTON_D)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
        if(MyInput.isPressed(MyInput.BUTTON_K)){
            setAnimation(punch);
            animation.setWalk(true);
            body.setLinearVelocity(0,0);
            stop = true;
            if(cl.isPlayerOnContact()){
                if(cl.isLeftContact() && !rightArm) {
                    System.out.println("Punch Left!!");
                }else if(cl.isRightContact() && rightArm){
                    System.out.println("Punch Right!!");
                }
                else {
                    //System.out.println("left: "+cl.isLeftContact()+"\nright:"+cl.isRightContact()+"\narmRight: "+rightArm);
                }
                return true;
            }else{
                invasorsHunt.res.getSound("miss").play(0.1f);
                System.out.println("Missed!!");
            }
        }

        return false;
    }


    public void setPlayerHits(int num){
	    playerHits += num;
    }

    private TextureRegion[] animatePlayer(Texture tex, int cols, int rows){

        TextureRegion[][] tmp = new TextureRegion(tex).split(
                tex.getWidth() / cols,
                tex.getHeight() / rows);

        TextureRegion[] sprites = new TextureRegion[cols*rows];

        int index = 0;
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                sprites[index++]=tmp[i][j];
            }
        }
        return sprites;
    }

    public void getLife() { if(playerHits != 0) playerHits--; }

	public MyContactListener getContactListener () {
		return cl;
	}

    public void dispose() {
    }
    

}
