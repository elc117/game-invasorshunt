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
import com.gddomenico.ih.entities.Enemy;
import com.gddomenico.ih.entities.Player;

import java.util.ArrayList;
import java.util.Random;

public class Play extends GameState {

    private final World world;
    private final Box2DDebugRenderer b2dr;

    private final OrthographicCamera b2dCam;
    
    private static int NUM_ENEMIES = 5;

    private Player player;
    private Enemy enemyBody[] = new Enemy[NUM_ENEMIES];

    private Body borderUpBody;
    private Body borderDownBody;
    private Body borderRightBody;
    private Body borderLeftBody;

    private float timer = 0;
    

    public Play(GameStateManager gsm) {
        super(gsm);

        
        world = new World(new Vector2(0, 0), true);
        
        player = new Player(world);
        b2dr = new Box2DDebugRenderer();

        for(int i = 0; i < enemyBody.length; i++)
        	enemyBody[i] = new Enemy(world);

        createBorder();
      
        //set up b2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, invasorsHunt.V_WIDTH / PPM, invasorsHunt.V_HEIGHT / PPM);
    }
   
    public void handleInput() {
    }

    public void update(float dt) {
        
    	if (player.handleInput()) getBodiesToRemove();
           
        for(int i = 0; i < NUM_ENEMIES; i++)
        	if (enemyBody[i].getHits() > -1)
        		enemyBody[i].FollowPlayer(player.getBody());


        world.step(dt, 6, 2);
        

        for(int i=0;i<NUM_ENEMIES;i++){
            if(enemyBody[i].destroyEnemy()){
                Body b = enemyBody[i].getBody();
                if(b.getType()!=null){
                    world.destroyBody(b);
                    enemyBody[i].setEnemyHits(-1);
                }

            }
        }

        //Perdeu o jogo
        if(player.getPlayerHits()==10)
            gsm.setState(GameStateManager.END);
    }

    public void render() {
        //clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Set a hit to the player every 4 seconds
        timer += Gdx.graphics.getDeltaTime();
        if(timer >= 3.420f) {
            player.setPlayerHits();
            timer = 0f;
        }

        b2dr.render(world, b2dCam.combined);
    }
    
    public void dispose() {
    }

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

    public void getBodiesToRemove(){
	    for(int i=0;i<NUM_ENEMIES;i++){
	        if(enemyBody[i].getBody() == player.getContactListener().currentEnemy.getBody()){
	        	enemyBody[i].setEnemyHits();
	        	
	            return;
	        }
	    }
	}
}
