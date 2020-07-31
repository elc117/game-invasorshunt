package com.gddomenico.ih.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.handlers.MyContactListener;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.invasorsHunt;

public class Player extends B2DSprite{

    private final MyContactListener cl;

    public int xWall = -100; //moves the background in the X axis
    public static final float delay = 1 / 6f;
    private boolean stop = false;

	public Player(Body body) {
		super(body);

		cl = new MyContactListener();

		int column = 5;
		int row = 2;

		Texture tex = invasorsHunt.res.getTexture("bunny");
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
            rightArm = false;
            body.setLinearVelocity(-0.5f, body.getLinearVelocity().y);
            if(cl.isPlayerOnTheWall() == 1 && xWall < 0)
                xWall++;
        }
        if(MyInput.isUp(MyInput.BUTTON_A)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);

        }
        if(MyInput.isDown(MyInput.BUTTON_D)){
            rightArm = true;
            body.setLinearVelocity(0.5f, body.getLinearVelocity().y);
            if(cl.isPlayerOnTheWall() == 2 && xWall > -280)
                xWall--;
        }
        if(MyInput.isUp(MyInput.BUTTON_D)) {
            body.setLinearVelocity(0, body.getLinearVelocity().y);
        }
        if(MyInput.isPressed(MyInput.BUTTON_K)){
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

    
    public void setPlayerHits(){
        if(cl.isPlayerOnContact())
            playerHits += cl.getContacts();
        System.out.println(playerHits);
    }

	public MyContactListener getContactListener () {
		return cl;
	}
    public boolean getStop() { return stop;}
    public void setStop(boolean stop) { this.stop = stop;}

    public void dispose() {
    }
    

}
