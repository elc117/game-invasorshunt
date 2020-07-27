package com.gddomenico.ih.states;

import static com.gddomenico.ih.handlers.B2DVars.PPM;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.gddomenico.ih.handlers.*;
import com.gddomenico.ih.invasorsHunt;

import java.util.ArrayList;
import java.util.Random;

public class Play extends GameState {

    private final World world;
    private final Box2DDebugRenderer b2dr;

    private final OrthographicCamera b2dCam;

    public boolean rightArm = false;

    private Body playerBody;
    private Body enemyBody;

    private Body borderUpBody;
    private Body borderDownBody;
    private Body borderRightBody;
    private Body borderLeftBody;

    private final MyContactListener cl;

    public ArrayList<Tuple<Body,Integer>> bodiesToRemove = new ArrayList<>();

    public Integer playerHits = 0;

    private float timer = 0;

    public Play(GameStateManager gsm) {
        super(gsm);

        world = new World(new Vector2(0, 0), true);
        cl = new MyContactListener();
        world.setContactListener(cl);
        b2dr = new Box2DDebugRenderer();

        createPlayer();

        createEnemies();

        createBorder();
      
        //set up b2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, invasorsHunt.V_WIDTH / PPM, invasorsHunt.V_HEIGHT / PPM);
    }
   
    public void FollowPlayer (Body enemy) {

    	Vector2 position = playerBody.getPosition();
    	Vector2 positionEnemy = enemy.getPosition();

    	float px = position.x - positionEnemy.x;
    	float py = position.y - positionEnemy.y;

    	float hipotenusa = (float) Math.sqrt((px*px) + (py * py));

    	float cos = (px / hipotenusa)*0.5f;
    	float sin = (py / hipotenusa)*0.5f;

    	enemy.setLinearVelocity(cos, sin);
    }

    public void handleInput() {
        if(MyInput.isDown(MyInput.BUTTON_W)) {
            playerBody.applyForceToCenter(0, 2, true);
        }
        if(MyInput.isUp(MyInput.BUTTON_W)) {
            playerBody.setLinearVelocity(0,0);
        }
        if(MyInput.isDown(MyInput.BUTTON_S)){
            playerBody.applyForceToCenter(0, -2, true);
        }
        if(MyInput.isUp(MyInput.BUTTON_S)) {
            playerBody.setLinearVelocity(0,0);
        }
        if(MyInput.isDown(MyInput.BUTTON_A)){
            rightArm = true;
            playerBody.applyForceToCenter(-2, 0, true);
        }
        if(MyInput.isUp(MyInput.BUTTON_A)) {
            playerBody.setLinearVelocity(0,0);
        }
        if(MyInput.isDown(MyInput.BUTTON_D)){
            rightArm = false;
            playerBody.applyForceToCenter(2, 0, true);
        }
        if(MyInput.isUp(MyInput.BUTTON_D)) {
            playerBody.setLinearVelocity(0,0);
        }
        if(MyInput.isPressed(MyInput.BUTTON_K)){
            if(cl.isPlayerOnContact()){
                getBodiesToRemove();
                if(cl.isLeftContact() && !rightArm) {

                    System.out.println("Punch Left!!");
                }else if(cl.isRightContact() && rightArm){
                    System.out.println("Punch Right!!");
                }
                else {
                	System.out.println("Punch somewhere else");
                }
            }else{
                System.out.println("Missed!!");
            }

        }
        if(MyInput.isDown(MyInput.BUTTON_SPACE)){
            System.out.println("SPACE");
        }
    }

    public void update(float dt) {
        
    	handleInput();

        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
           
        for(int i = 0; i < bodies.size; i++)
//        	if (bodies.get(i) != playerBody)
//        	FollowPlayer(bodies.get(i));


        world.step(dt, 6, 2);
        

        for(int i=0;i<bodiesToRemove.size();i++){
            if(bodiesToRemove.get(i).getY()==5){
                Body b = bodiesToRemove.get(i).getX();
                if(b.getType()!=null){
                    world.destroyBody(b);
                    bodiesToRemove.remove(i);
                }

            }
        }

        //Perdeu o jogo
        if(playerHits==10)
            gsm.setState(GameStateManager.END);
    }

    public void render() {
        //clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Set a hit to the player every 4 seconds
        timer += Gdx.graphics.getDeltaTime();
        if(timer >= 3.420f) {
            setPlayerHits();
            timer = 0f;
        }

        b2dr.render(world, b2dCam.combined);
    }
    public void dispose() {}

    public void createBorder(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        //Border Down
        bdef.position.set(invasorsHunt.V_WIDTH / PPM,invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        borderUpBody = world.createBody(bdef);

        shape.setAsBox(invasorsHunt.V_WIDTH / PPM,0 / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderUpBody.createFixture(fdef).setUserData("Border_Down");

        //Border Up
        bdef.position.set(0 / PPM,invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        borderDownBody = world.createBody(bdef);

        shape.setAsBox(0 / PPM,invasorsHunt.V_HEIGHT / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderDownBody.createFixture(fdef).setUserData("Border_Up");

        //Border Left
        bdef.position.set(invasorsHunt.V_WIDTH / PPM,0 / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        borderLeftBody = world.createBody(bdef);

        shape.setAsBox(invasorsHunt.V_WIDTH / PPM,0 / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderLeftBody.createFixture(fdef).setUserData("Border_Left");

        //Border Right
        bdef.position.set(invasorsHunt.V_WIDTH / PPM, invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        borderRightBody = world.createBody(bdef);

        shape.setAsBox(0 / PPM,invasorsHunt.V_HEIGHT  / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderRightBody.createFixture(fdef).setUserData("Border_Right");
    }

    public void createPlayer(){

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();


        //player
        bdef.position.set(160 / PPM,120 / PPM);
        bdef.type =  BodyDef.BodyType.DynamicBody;
        playerBody = world.createBody(bdef);

        shape.setAsBox(5 / PPM,5 / PPM);
        fdef.shape = shape;
        fdef.friction = 100000000000000f;
        //to change the category
        playerBody.createFixture(fdef).setUserData("Player");


    }
    //Just a func to get an random int
    public int getRand(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public void createEnemies(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        for (int i=0;i<=5;i++){
            //enemy
            bdef.position.set((100+getRand(getRand(-50,50),200)) / PPM,(120+getRand(getRand(-50,50),100)) / PPM);
            bdef.type =  BodyDef.BodyType.KinematicBody;
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
    }

    // @Body X
    // @Integer Y
    public void getBodiesToRemove(){
        for(int i=0;i<bodiesToRemove.size();i++){
            if(bodiesToRemove.get(i).getX() == cl.currentEnemy.getBody()){
                bodiesToRemove.get(i).setY(bodiesToRemove.get(i).getY()+1);
              
                System.out.println("to remove: " + bodiesToRemove.get(i).getY());
                System.out.println("to remove: " + bodiesToRemove.get(i).getX());
                return;
            }
        }
        bodiesToRemove.add(new Tuple(cl.currentEnemy.getBody(),1));
    }

    public void setPlayerHits(){
        if(cl.isPlayerOnContact() && (cl.isLeftContact() || cl.isRightContact()))
            playerHits++;
        System.out.println(playerHits);
    }
}
