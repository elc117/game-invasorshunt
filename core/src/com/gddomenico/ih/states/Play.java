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
import com.badlogic.gdx.utils.Array;
import com.gddomenico.ih.entities.Heart;
import com.gddomenico.ih.handlers.*;
import com.gddomenico.ih.invasorsHunt;
import com.gddomenico.ih.entities.Enemy;
import com.gddomenico.ih.entities.Player;

import java.util.Random;


public class Play extends GameState {

    private static final boolean debug = false;

    private final World world;
    private final Box2DDebugRenderer b2dr;

    private final OrthographicCamera b2dCam;
    
    private static final int NUM_ENEMIES = 50;
    private int activeEnemies = 5;
    private int destroyedEnemies = 0;

    private final TextureRegion[] lifeBar;
    private final BitmapFont enemiesIterator;

    private Player player;
    private final Array<Enemy> enemyBody = new Array<>();

    private float timer = 0;

    private final Array<Heart> hearts = new Array<>();

    public Play(GameStateManager gsm) {
        super(gsm);

        invasorsHunt.res.getMusic("play").play();

        world = new World(new Vector2(0, 0), true);
        enemiesIterator = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
        enemiesIterator.getData().setScale(invasorsHunt.SCALE/4f);
        
        createPlayer();

        world.setContactListener(player.getContactListener());

        b2dr = new Box2DDebugRenderer();
        //It's going to receive the number to make the enemy spawn more far from the main
        for(int i = 0; i < activeEnemies; i++){
            enemyBody.add(createEnemy(2));
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

    public void update(float dt) {

        //Set a hit to the player every enemy delay, each enemy has one
        if (player.getContactListener().isPlayerOnContact())
            setPlayerHits(dt);

        timer += dt;
        // Spawn a new enemy each a few seconds
        if (timer >= 1.7f && activeEnemies < NUM_ENEMIES && enemyBody.size < 6) {
            enemyBody.add(createEnemy(2));
            activeEnemies++;

            timer -= 3.4f;
        }

        // Punches if player delay is not set
        if (!player.getStop() && player.handleInput() ) {
            getBodiesToRemove(dt);
        }

        // Update each player/enemy
    	player.update(dt);
        for(int i = 0; i < enemyBody.size; i++){
            enemyBody.get(i).update(dt, player.getBody());
        }

        // Seeks if an enemy or hearth needs to be destroyed
        for(int i=0;i<enemyBody.size;i++){
            if(enemyBody.get(i).destroyEnemy()){
                Body b = enemyBody.get(i).getBody();
                if(b.getType()!=null){
                    if(getRand(0,101)%10==0)
                        createHearts(enemyBody.get(i).getPosition());
                    world.destroyBody(b);
                    enemyBody.removeIndex(i);
                    destroyedEnemies++;
                }
            }
        }

        boolean victory = destroyedEnemies >= NUM_ENEMIES;

        //Perdeu o jogo
        if(player.getPlayerHits()>=PLAYER_LIVES || victory)
            gsm.setState(GameStateManager.END);

        world.step(dt, 6, 2);

        //removing hearts and giving one life each heart
        Array<Body> heartsToRemove = player.getContactListener().getHeartsToRemove();
        for(int i=0;i<heartsToRemove.size;i++){
            Body h = heartsToRemove.get(i);
            hearts.removeValue((Heart) h.getUserData(), true);
            world.destroyBody(h);
            player.getLife();
        }
        heartsToRemove.clear();
        //if(victory) gsm.setState(GameStateManager.END);
    }

    public void render() {

        float newPos = player.getBody().getPosition().x * PPM;
        if(newPos > invasorsHunt.V_WIDTH / 5f && newPos < invasorsHunt.V_WIDTH + 10)
            cam.position.x = newPos;

        cam.update();

        //clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.setProjectionMatrix(cam.combined);

        int height = lifeBar[0].getRegionHeight();
        int width = lifeBar[0].getRegionWidth();

        String denm = "Inimigos Restando: " + (NUM_ENEMIES - destroyedEnemies);

        GlyphLayout layout = new GlyphLayout();
        layout.setText(enemiesIterator, denm);

        sb.begin();
        sb.draw(invasorsHunt.res.getTexture("background"), -100, 0,600, invasorsHunt.V_HEIGHT);
        sb.draw(player.getPlayerHits() <= 10 ? lifeBar[player.getPlayerHits()] : lifeBar[10],
                cam.position.x - invasorsHunt.V_WIDTH / 2f,
                invasorsHunt.V_HEIGHT - height + 10,
                width / 2f,
                height / 2f);
        enemiesIterator.draw(
                sb,
                denm,
                cam.position.x + 15,
                (invasorsHunt.V_HEIGHT - layout.height + 5)
        );
        sb.end();
        //draw a heart
        if(hearts.notEmpty())
            for(int i=0;i < hearts.size; i++)
                hearts.get(i).render(sb);

        // enemies render
        for(int i = 0; i < enemyBody.size; i++) {
            if(enemyBody.get(i).getFlash() > 0) {
                if (enemyBody.get(i).getFlash() % 3 == 0)
                    enemyBody.get(i).render(sb, cam);
                enemyBody.get(i).setFlash();
                if (enemyBody.get(i).getFlash() > 6)
                    enemyBody.get(i).setFlash(0);
            }
            else
                enemyBody.get(i).render(sb, cam);
        }

        //player render
        if(player.getFlash() > 0) {
            if (player.getFlash() % 3 == 0)
                player.render(sb, cam);
            player.setFlash();
            if (player.getFlash() > 10)
                player.setFlash(0);
        }
        else
            player.render(sb, cam);

        sb.begin();
        sb.draw(invasorsHunt.res.getTexture("background2"), -100, 0,600, invasorsHunt.V_HEIGHT);
        sb.end();

        if(debug)
            b2dr.render(world, b2dCam.combined);
    }

    /**
     * Gets a range and returns a random value in between
     * @param min x
     * @param max y
     * @return random number
     */
    public static int getRand(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public void setPlayerHits (float dt) {
        int hits = 0;
        for (int j = 0; j < player.getContactListener().currentEnemy.size; j++) {
            for(int i=0;i<enemyBody.size;i++){
                if(enemyBody.get(i).getBody() == player.getContactListener().currentEnemy.get(j).getBody() && !enemyBody.get(i).getStop()) {
                    Enemy aux = enemyBody.get(i);
                    aux.countTimer(dt);
                    if (aux.canPunch()) {
                        player.getBody().applyForceToCenter(aux.getSide() ? 50 : -50, 0, true);
                        hits++;
                        player.setFlash(1);
                    }
                }
            }
        }
        player.setPlayerHits(hits);
    }

    public void getBodiesToRemove(float dt){
        boolean punch = false;
        for (int j = 0; j < player.getContactListener().currentEnemy.size; j++)
	    for (int i = 0;i<enemyBody.size;i++){
	        if(enemyBody.get(i).getBody() == player.getContactListener().currentEnemy.get(j).getBody()
                    && enemyBody.get(i).getPlayerHits() != -1
                    && enemyBody.get(i).getSide() == !player.getRightArm())
	        {
                punch = true;
                enemyBody.get(i).setFlash(1);
                enemyBody.get(i).countTimer(-dt);
                enemyBody.get(i).setEnemyHits();
                enemyBody.get(i).getBody().applyForceToCenter(enemyBody.get(i).getSide() ? -250 : 250,0, true);
            }
	    }
	    if (punch) invasorsHunt.res.getSound("punch").play(0.1f);
        else invasorsHunt.res.getSound("miss").play(0.1f);
	}

    public void handleInput() {
    }

    public void createBorder(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        //Border Up
        bdef.position.set(-100 / PPM,invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderUpBody = world.createBody(bdef);

        shape.setAsBox((invasorsHunt.V_WIDTH*1.75f) / PPM,80 / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderUpBody.createFixture(fdef).setUserData("Border_Up");

        //Border Left
        bdef.position.set(-100 / PPM, invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderLeftBody = world.createBody(bdef);


        shape.setAsBox(0 / PPM,invasorsHunt.V_HEIGHT / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderLeftBody.createFixture(fdef).setUserData("Border_Left");

        //Border Down
        bdef.position.set(-100 / PPM, 0 / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderDownBody = world.createBody(bdef);

        shape.setAsBox((invasorsHunt.V_WIDTH*1.75f) / PPM,10 / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderDownBody.createFixture(fdef).setUserData("Border_Down");

        //Border Right
        bdef.position.set((invasorsHunt.V_WIDTH*1.575f) / PPM, invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderRightBody = world.createBody(bdef);

        shape.setAsBox(10 / PPM,invasorsHunt.V_HEIGHT  / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderRightBody.createFixture(fdef).setUserData("Border_Right");
    }

	public void createPlayer(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(160 / PPM,120 / PPM);
        bdef.type =  BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);

        shape.setAsBox(10 / PPM,10 / PPM);
        fdef.shape = shape;
        fdef.friction = 1000000000000f;
        //MassData mass = new MassData();
        //mass.I = body.getAngle();
        //mass.I = 5f;
        //body.setMassData(mass);
        //to change the category
        body.createFixture(fdef).setUserData("Player");

        player = new Player(body);
    }
    public Enemy createEnemy(Integer dist){
        BodyDef bdef = new BodyDef();
        CircleShape cshape = new CircleShape();
        FixtureDef fdef = new FixtureDef();

        //bdef.position.set(180 / PPM,(130) / PPM);
        int i = getRand(1,3)*2-3;
        bdef.position.set( i > 0 ? ((100+getRand(50,150))*dist*i) / PPM : ((getRand(0,100))*dist*i) / PPM,
                (getRand(0,80)) / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setGravityScale(0f);

        cshape.setRadius(10f / PPM);
        fdef.shape = cshape;
        //to change the category
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("Enemy");

        // create a foot sensor
        cshape.setRadius(10f / PPM);
        fdef.shape = cshape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("Foot_Enemy");

        return new Enemy(body);
    }

    private void createHearts(Vector2 heartsPos){

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        bdef.position.set(heartsPos);

        CircleShape cshape = new CircleShape();
        cshape.setRadius(2f / PPM);

        fdef.shape = cshape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = B2DVars.BIT_HEART;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;

        Body body = world.createBody(bdef);
        body.createFixture(fdef).setUserData("Hearts");

        Heart h = new Heart(body);
        hearts.add(h);

        body.setUserData(h);

    }

    public void dispose() {
        invasorsHunt.res.getMusic("play").stop();
    }
}
