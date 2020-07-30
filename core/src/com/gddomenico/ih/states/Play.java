package com.gddomenico.ih.states;

import static com.gddomenico.ih.handlers.B2DVars.PPM;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.gddomenico.ih.handlers.*;
import com.gddomenico.ih.invasorsHunt;
import com.gddomenico.ih.entities.Enemy;
import com.gddomenico.ih.entities.Player;

import java.util.Random;


public class Play extends GameState {

    private final World world;
    private final Box2DDebugRenderer b2dr;

    private final OrthographicCamera b2dCam;
    
    private static final int NUM_ENEMIES = 2;

    private Player player;
    private Enemy[] enemyBody = new Enemy[NUM_ENEMIES];

    private float timer = 0;

    public Play(GameStateManager gsm) {
        super(gsm);
        
        world = new World(new Vector2(0, 0), true);
        
        createPlayer();

        world.setContactListener(player.getContactListener());

        b2dr = new Box2DDebugRenderer();

        for(int i = 0; i < enemyBody.length; i++)
        	enemyBody[i] = createEnemy();

        createBorder();
      
        //set up b2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, invasorsHunt.V_WIDTH / PPM, invasorsHunt.V_HEIGHT / PPM);
    }
   
    public void handleInput() {
    }

    public void update(float dt) {
        
    	if (player.handleInput()) getBodiesToRemove();
    	player.update(dt);
           
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
        if (timer >= 3.420f) {
            player.setPlayerHits();
            timer = 0f;
        }
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

        b2dr.render(world, b2dCam.combined);
    }

    /**
     * Gets a range and returns a random value in between
     * @param min x
     * @param max y
     * @return random number
     */
    public int getRand(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public void createBorder(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        //Border Down
        bdef.position.set(invasorsHunt.V_WIDTH / PPM,invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderUpBody = world.createBody(bdef);

        shape.setAsBox(invasorsHunt.V_WIDTH / PPM,0 / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderUpBody.createFixture(fdef).setUserData("Border_Down");

        //Border Up
        bdef.position.set(0 / PPM,invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderDownBody = world.createBody(bdef);
        

        shape.setAsBox(0 / PPM,invasorsHunt.V_HEIGHT / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderDownBody.createFixture(fdef).setUserData("Border_Up");

        //Border Left
        bdef.position.set(invasorsHunt.V_WIDTH / PPM,0 / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderLeftBody = world.createBody(bdef);

        shape.setAsBox(invasorsHunt.V_WIDTH / PPM,0 / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderLeftBody.createFixture(fdef).setUserData("Border_Left");

        //Border Right
        bdef.position.set(invasorsHunt.V_WIDTH / PPM, invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderRightBody = world.createBody(bdef);

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

	public void createPlayer(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(160 / PPM,120 / PPM);
        bdef.type =  BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        shape.setAsBox(5 / PPM,5 / PPM);
        fdef.shape = shape;
        fdef.friction = 100000000000000f;
        //to change the category
        body.createFixture(fdef).setUserData("Player");

        player = new Player(body);
    }
    public Enemy createEnemy(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set((100+getRand(getRand(-50,50),200)) / PPM,(120+getRand(getRand(-50,50),100)) / PPM);
        bdef.type = BodyDef.BodyType.KinematicBody;
        Body body = world.createBody(bdef);
        body.setGravityScale(0f);

        shape.setAsBox(5 / PPM,5 / PPM);
        fdef.shape = shape;
        //to change the category
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY;
        body.createFixture(fdef).setUserData("Enemy");

        // create a foot sensor
        shape.setAsBox(5/PPM, 2/PPM, new Vector2(0, -2/ PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("Foot_Enemy");

        return new Enemy(body);
    }

    public void dispose() {
    }
}
