package com.gddomenico.ih.entities;

import static com.gddomenico.ih.handlers.B2DVars.PPM;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.gddomenico.ih.handlers.MyContactListener;
import com.gddomenico.ih.handlers.MyInput;

public class Player{
	
	private Body playerBody;

    private Integer playerHits = 0;
    private boolean rightArm = false;

    private Sound punch;
    private Sound miss;
    
    private MyContactListener cl;
    

	public Player(World world) {
		
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        
        punch = Gdx.audio.newSound(Gdx.files.internal("punch.wav"));
        miss = Gdx.audio.newSound(Gdx.files.internal("missed.wav"));  

        //player
        cl = new MyContactListener();
        world.setContactListener(cl);
        
        bdef.position.set(160 / PPM,120 / PPM);
        bdef.type =  BodyDef.BodyType.KinematicBody;
        playerBody = world.createBody(bdef);
        
        shape.setAsBox(5 / PPM,5 / PPM);
        fdef.shape = shape;
        fdef.friction = 100000000000000f;
        //to change the category
        playerBody.createFixture(fdef).setUserData("Player");
        playerBody.setLinearDamping(20f); 
		// TODO Auto-generated constructor stub
	}
	

    public boolean handleInput() {
        if(MyInput.isDown(MyInput.BUTTON_SPACE)){
            System.out.println("SPACE");
        }
        if(!MyInput.isDown(MyInput.BUTTON_D) && !MyInput.isDown(MyInput.BUTTON_W) && !MyInput.isDown(MyInput.BUTTON_A) && !MyInput.isDown(MyInput.BUTTON_S)) {
            playerBody.setLinearVelocity(0,0);        	
        }
        if(MyInput.isDown(MyInput.BUTTON_W)) {
            playerBody.applyForceToCenter(0, 2, true);
        }
        if(MyInput.isUp(MyInput.BUTTON_W)) {
            playerBody.setLinearVelocity(playerBody.getLinearVelocity().x, 0);
        }
        if(MyInput.isDown(MyInput.BUTTON_S)){
            playerBody.applyForceToCenter(0, -2, true);
        }
        if(MyInput.isUp(MyInput.BUTTON_S)) {
            playerBody.setLinearVelocity(playerBody.getLinearVelocity().x, 0);
        }
        if(MyInput.isDown(MyInput.BUTTON_A)){
            rightArm = true;
            playerBody.applyForceToCenter(-2, 0, true);
        }
        if(MyInput.isUp(MyInput.BUTTON_A)) {
            playerBody.setLinearVelocity(0, playerBody.getLinearVelocity().y);
        }
        if(MyInput.isDown(MyInput.BUTTON_D)){
            rightArm = false;
            playerBody.applyForceToCenter(2, 0, true);
        }
        if(MyInput.isUp(MyInput.BUTTON_D)) {
            playerBody.setLinearVelocity(0, playerBody.getLinearVelocity().y);
        }
        if(MyInput.isPressed(MyInput.BUTTON_K)){
            if(cl.isPlayerOnContact()){
            	punch.play(0.1f);
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
            	miss.play(0.1f);
                System.out.println("Missed!!");
            }
        }
        return false;
    }
    
    public Body getBody() {
    	return playerBody;
    }
    
    public void setPlayerHits(){
        if(cl.isPlayerOnContact() && (cl.isLeftContact() || cl.isRightContact()))
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
    	punch.dispose();
    	miss.dispose();
    }
    

}
