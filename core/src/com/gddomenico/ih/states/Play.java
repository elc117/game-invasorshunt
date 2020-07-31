package com.gddomenico.ih.states;

import static com.gddomenico.ih.handlers.B2DVars.PPM;
import static com.gddomenico.ih.handlers.B2DVars.PLAYER_LIVES;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    
    private static final int NUM_ENEMIES = 50;
    private int activeEnemies = 5;
    private int destroyedEnemies = 0;

    private final TextureRegion[] lifeBar;
    private final BitmapFont enemiesIterator;

    private Player player;
    private final Enemy[] enemyBody = new Enemy[NUM_ENEMIES];

    private float timer = 0;
    private float timeStop = 0;

    public Play(GameStateManager gsm) {
        super(gsm);

        //invasorsHunt.res.getMusic("play").play();

        world = new World(new Vector2(0, 0), true);
        enemiesIterator = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
        enemiesIterator.getData().setScale((float) (invasorsHunt.SCALE/4f));
        
        createPlayer();

        world.setContactListener(player.getContactListener());

        b2dr = new Box2DDebugRenderer();
        //It's going to receive the number to make the enemy spawn more far from the main
        for(int i = 0; i < activeEnemies; i++){
            enemyBody[i] = createEnemy(2);
        }


        createBorder();

        int row = 11;
        int column = 1;

        Texture tex = invasorsHunt.res.getTexture("life");
        TextureRegion[][] tmp = new TextureRegion(tex).split(
                tex.getWidth() / column,
                tex.getHeight() / row);

        lifeBar = new TextureRegion[row*column];

        int index = 0;
        for (int i=0; i<row; i++) {
            for (int j=0; j<column; j++) {
                lifeBar[index++]=tmp[i][j];
            }
        }
        //set up b2d cam
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, invasorsHunt.V_WIDTH / PPM, invasorsHunt.V_HEIGHT / PPM);
    }
   
    public void handleInput() {
    }

    public void update(float dt) {

        //Set a hit to the player every 4 seconds
        timer += Gdx.graphics.getDeltaTime();
        if (timer >= 3.420f) {
            player.setPlayerHits();
            if(activeEnemies < NUM_ENEMIES) {
                enemyBody[activeEnemies] = createEnemy(2);
                activeEnemies++;
            }
            timer = 0f;
        }

        if(!player.getStop()) {
            if (player.handleInput()) {
                getBodiesToRemove();
            }
        }
        else {
            timeStop += dt;
            if(timeStop >= Player.delay) {
                player.setStop(false);
                timeStop = 0f;
            }
        }

    	player.update(dt);
        for(int i = 0; i < activeEnemies; i++)
        	if (enemyBody[i].getPlayerHits() > -1) {
                enemyBody[i].FollowPlayer(player.getBody(), player.getContactListener().isPlayerOnTheWall(), player.xWall);
        	    enemyBody[i].update(dt);
            }

        for(int i=0;i<activeEnemies;i++){
            if(enemyBody[i].destroyEnemy()){
                Body b = enemyBody[i].getBody();
                if(b.getType()!=null){
                    world.destroyBody(b);
                    enemyBody[i].setEnemyHits(-1);
                    destroyedEnemies++;
                }
            }
        }

        boolean victory = destroyedEnemies >= NUM_ENEMIES;

        //Perdeu o jogo
        if(player.getPlayerHits()>=PLAYER_LIVES || victory)
            gsm.setState(GameStateManager.END);

        world.step(dt, 6, 2);
        //if(victory) gsm.setState(GameStateManager.END);
    }

    public void render() {
        //clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.setProjectionMatrix(cam.combined);

        String denm = "Inimigos Restando: " + Integer.toString(NUM_ENEMIES - destroyedEnemies);

        GlyphLayout layout = new GlyphLayout();
        layout.setText(enemiesIterator, denm);

        int height = lifeBar[0].getRegionHeight();
        int width = lifeBar[0].getRegionWidth();


        sb.begin();
        sb.draw(invasorsHunt.res.getTexture("background"), player.xWall, 0,600, invasorsHunt.V_HEIGHT);
        sb.draw(player.getPlayerHits() <= 10 ? lifeBar[player.getPlayerHits()] : lifeBar[10],
                10,
                invasorsHunt.V_HEIGHT - height + 10,
                width / 2f,
                height / 2f);
        sb.draw(invasorsHunt.res.getTexture("background2"), player.xWall, 0,600, invasorsHunt.V_HEIGHT);
        enemiesIterator.draw(
                sb,
                denm,
                (invasorsHunt.V_WIDTH - layout.width - 25),
                (invasorsHunt.V_HEIGHT - layout.height + 5)
        );
        sb.end();


        for(int i = 0; i < activeEnemies; i++)
            if (enemyBody[i].getPlayerHits() > -1) {
                enemyBody[i].render(sb);
            }

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

        //Border Up
        bdef.position.set(invasorsHunt.V_WIDTH / PPM,invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderUpBody = world.createBody(bdef);

        shape.setAsBox(invasorsHunt.V_WIDTH / PPM,80 / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderUpBody.createFixture(fdef).setUserData("Border_Up");

        //Border Left
        bdef.position.set(10 / PPM,invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderDownBody = world.createBody(bdef);
        

        shape.setAsBox(0 / PPM,invasorsHunt.V_HEIGHT / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderDownBody.createFixture(fdef).setUserData("Border_Left");

        //Border Down
        bdef.position.set(invasorsHunt.V_WIDTH / PPM,0 / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderLeftBody = world.createBody(bdef);

        shape.setAsBox(invasorsHunt.V_WIDTH / PPM,10 / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderLeftBody.createFixture(fdef).setUserData("Border_Down");

        //Border Right
        bdef.position.set(invasorsHunt.V_WIDTH / PPM, invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderRightBody = world.createBody(bdef);

        shape.setAsBox(10 / PPM,invasorsHunt.V_HEIGHT  / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderRightBody.createFixture(fdef).setUserData("Border_Right");
    }

    public void getBodiesToRemove(){
        boolean punch = false;
	    for(int i=0;i<activeEnemies;i++){
	        for (int j = 0; j < player.getContactListener().currentEnemy.size; j++)
	        if(enemyBody[i].getBody() == player.getContactListener().currentEnemy.get(j).getBody()
                    && enemyBody[i].getPlayerHits() != -1
                    && enemyBody[i].getSide() == !player.getRightArm()){
	            punch = true;
	        	enemyBody[i].setEnemyHits();
	        }
	    }
	    if (punch) invasorsHunt.res.getSound("punch").play(0.1f);
        else invasorsHunt.res.getSound("miss").play(0.1f);
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
    public Enemy createEnemy(Integer dist){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        //bdef.position.set(180 / PPM,(130) / PPM);
        int i = getRand(1,3)*2-3;
        bdef.position.set( i > 0 ? ((100+getRand(50,150))*dist*i) / PPM : ((getRand(0,100))*dist*i) / PPM,
                (getRand(0,80)) / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setGravityScale(0f);

        shape.setAsBox(5 / PPM,5 / PPM);
        fdef.shape = shape;
        //to change the category
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("Enemy");

        // create a foot sensor
        shape.setAsBox(5/PPM, 2/PPM, new Vector2(0, -2/ PPM), 0);
        fdef.shape = shape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("Foot_Enemy");

        return new Enemy(body);
    }

    public void dispose() { invasorsHunt.res.getMusic("play").stop(); }
}
