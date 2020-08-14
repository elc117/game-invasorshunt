package com.gddomenico.ih.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.PauseableThread;
import com.gddomenico.ih.entities.Heart;
import com.gddomenico.ih.handlers.*;
import com.gddomenico.ih.invasorsHunt;
import com.gddomenico.ih.entities.Enemy;
import com.gddomenico.ih.entities.Player;

import java.util.Random;

import static com.gddomenico.ih.handlers.B2DVars.*;


public class Play extends GameState {

    private static final boolean debug = false;

    private final World world;
    private final Box2DDebugRenderer b2dr;

    private final OrthographicCamera b2dCam;

    // Max enemies and enemies iterator
    private static final int NUM_ENEMIES = 30;
    private int activeEnemies = 5;
    private int destroyedEnemies = 0;
    private float timer = 0;

    private boolean pause = false;

    private TextureRegion[] lifeBar;
    private Animation enemiesIterator;
    private TextureRegion enemyIcon;

    private Player player;
    private final Array<Enemy> enemyBody = new Array<>();
    private final Array<Heart> hearts = new Array<>();
    private final Array<Enemy> bodyToRemove = new Array<>();

    public Play(GameStateManager gsm) {
        super(gsm);

        invasorsHunt.res.getMusic("play").play();

        world = new World(new Vector2(0, 0), true);

        createPlayer();
        createBorder();
        createLifeBar(invasorsHunt.res.getTexture("life"), 1 , 11);
        createEnemyIterator(invasorsHunt.res.getTexture("enemyIcon"), invasorsHunt.res.getTexture("numbers"), 10 , 1);

        world.setContactListener(player.getContactListener());

        //It's going to receive the number to make the enemy spawn more far from the main
        for(int i = 0; i < activeEnemies; i++){
            enemyBody.add(createEnemy((int) (cam.position.x / PPM) + 1));
        }

        //set up b2d cam
        b2dr = new Box2DDebugRenderer();
        b2dCam = new OrthographicCamera();
        b2dCam.setToOrtho(false, invasorsHunt.V_WIDTH / PPM, invasorsHunt.V_HEIGHT / PPM);
    }

    public void handleInput() {
        // Movement Inputs
        if(MyInput.isPressed(MyInput.BUTTON_ESC)) {
            pause = !pause;
            if(pause) invasorsHunt.res.getMusic("play").pause();
            else invasorsHunt.res.getMusic("play").play();
        }
    }

    public void update(float dt) {

        handleInput();

        if(pause) return;

        world.step(dt, 6, 2);

        timer += dt;
        // Spawn a new enemy each a few seconds
        if (timer >= 1.7f && activeEnemies < NUM_ENEMIES && enemyBody.size < 6) {
            enemyBody.add(createEnemy((int) (cam.position.x / PPM) + 2));
            activeEnemies++;

            timer -= 1.7f;
        }

        //Set a hit to the player every enemy delay, each enemy has one
        if (player.getContactListener().isPlayerOnContact())
            setPlayerHits(dt);

        // Punches if player delay is not set
        if (!player.getWinCondition() && !player.getDeathCondition() && !player.getStop() && player.handleInput())
            getBodiesToRemove(dt);


        // Update each player/enemy
    	player.update(dt);
        for(int i = 0; i < enemyBody.size; i++)
            enemyBody.get(i).update(dt, player.getBody());
        
        // Seeks if an enemy or heart needs to be destroyed and destroys the box2d
        for(int i=0;i<bodyToRemove.size;i++)
            if(bodyToRemove.get(i).destroyEnemy() && !bodyToRemove.get(i).getDeathCondition()) {
                bodyToRemove.get(i).setDeathCondition();
                world.destroyBody(bodyToRemove.get(i).getBody());
            }
        // Destroys the enemy data
        for(int i=0;i<bodyToRemove.size;i++){
            if(bodyToRemove.get(i).getPlayerHits() == -1){
                if(getRand(0,13)%4==0)
                    createHearts(bodyToRemove.get(i).getPosition());
                enemyBody.removeValue(bodyToRemove.get(i), true);
                bodyToRemove.removeIndex(i);
                destroyedEnemies++;
            }
        }


        //Perdeu o jogo
        if(player.getPlayerHits()>=PLAYER_LIVES && !player.getDeathCondition()) {
            player.setDeathCondition();
        }
        if(player.getDeathCondition() && player.getDefeat()) {
            gsm.setState(GameStateManager.ENDLOSE);
        }
        //Ganhou o jogo
        if((NUM_ENEMIES - destroyedEnemies) == 0 && !player.getWinCondition()) {
            player.setWinCondition();
        }
        if(player.getWinCondition() && player.getVictory()) {
            gsm.setState(GameStateManager.ENDWIN);
        }

        //removing hearts and giving one life each heart
        if (player.getContactListener().getHeathsOnContact() > 0) {
            Array<Body> heartsToRemove = player.getContactListener().getHeartsToRemove();
            for (int i = 0; i < player.getContactListener().getHeathsOnContact(); i++) {
                hearts.removeValue((Heart) heartsToRemove.get(i).getUserData(), true);
                world.destroyBody(heartsToRemove.get(i));
                player.getLife();
            }
            player.getContactListener().setHeathsOnContact(0);
            heartsToRemove.clear();
        }

    }

    public void render() {

        // updates the camera
        float newPos = player.getBody().getPosition().x * PPM;
        if(newPos > invasorsHunt.V_WIDTH / 5f && newPos < invasorsHunt.V_WIDTH + 50)
            cam.position.x = newPos;
        cam.update();

        //clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        sb.setProjectionMatrix(cam.combined);

        int height = lifeBar[0].getRegionHeight();
        int width = lifeBar[0].getRegionWidth();

        int denm = NUM_ENEMIES - destroyedEnemies;
        int width2 = enemiesIterator.getFrame(0).getRegionWidth();
        int height2 = enemiesIterator.getFrame(0).getRegionHeight();

        sb.begin();
        // draw background
        sb.draw(invasorsHunt.res.getTexture("background"), -220, 0,1200, invasorsHunt.V_HEIGHT);
        // draw life bar
        sb.draw(player.getPlayerHits() <= 10 ? lifeBar[player.getPlayerHits()] : lifeBar[10],
                cam.position.x - invasorsHunt.V_WIDTH / 2f + 5,
                invasorsHunt.V_HEIGHT - height - 2,
                width / 2f + 60,
                height / 2f + 10);
        // draw enemy icon
        sb.draw(enemyIcon,
                cam.position.x - 1.75f * enemyIcon.getRegionWidth() + invasorsHunt.V_WIDTH / 2f - 15,
                invasorsHunt.V_HEIGHT - enemyIcon.getRegionHeight() / 2f - 20,
                enemyIcon.getRegionWidth(),
                enemyIcon.getRegionHeight());
        // draw enemy iterator ten
        sb.draw(enemiesIterator.getFrame(denm/10),
                cam.position.x - 1.5f * width2 + invasorsHunt.V_WIDTH / 2f,
                invasorsHunt.V_HEIGHT - height2 / 2f - 15,
                width2 / 2f + 10,
                height2 / 2f + 10);
        // draw enemy iterato unity
        sb.draw(enemiesIterator.getFrame(denm%10),
                cam.position.x - width2 + invasorsHunt.V_WIDTH / 2f,
                invasorsHunt.V_HEIGHT - height2 / 2f - 15,
                width2 / 2f + 10,
                height2 / 2f + 10);
        sb.end();
        //draw a heart
        if(hearts.notEmpty())
            for(int i=0;i < hearts.size; i++)
                hearts.get(i).render(sb);

        // enemies render
        for(int i = 0; i < enemyBody.size; i++) {
            if(enemyBody.get(i).getFlash() > 0) {
                if (enemyBody.get(i).getFlash() % 3 == 0)
                    enemyBody.get(i).render(sb);
                enemyBody.get(i).setFlash();
                if (enemyBody.get(i).getFlash() > 6)
                    enemyBody.get(i).setFlash(0);
            }
            else
                enemyBody.get(i).render(sb);
        }

        //player render
        if(player.getFlash() > 0) {
            if (player.getFlash() % 3 == 0)
                player.render(sb);
            player.setFlash();
            if (player.getFlash() > 10)
                player.setFlash(0);
        }
        else
            player.render(sb);

        sb.begin();
        sb.draw(invasorsHunt.res.getTexture("background2"), -100, 0,600, invasorsHunt.V_HEIGHT);
        if(pause) {
            String title = "Pause";

            BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/fontTitle.fnt"));

            GlyphLayout layout = new GlyphLayout();
            layout.setText(font, title);
            font.setColor(Color.BLACK);
            float layoutWidth = layout.width;

            font.draw(
                    sb,
                    title,
                    cam.position.x - layoutWidth / 2f,
                    invasorsHunt.V_HEIGHT / 2f
            );
        }
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
        for (int j = 0; j < player.getContactListener().getContacts(); j++) {
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
        for (int j = 0; j < player.getContactListener().getContacts(); j++)
	    for (int i = 0;i<enemyBody.size;i++){
	        if(enemyBody.get(i).getBody() == player.getContactListener().currentEnemy.get(j).getBody()
                    && ( enemyBody.get(i).getPlayerHits() != -1 && enemyBody.get(i).getPlayerHits() < 5)
                    && enemyBody.get(i).getSide() == !player.getRightArm())
	        {
                punch = true;
                enemyBody.get(i).setFlash(1);
                enemyBody.get(i).countTimer(-dt);
                enemyBody.get(i).setEnemyHits();
                enemyBody.get(i).getBody().applyForceToCenter(enemyBody.get(i).getSide() ? -250 : 250,0, true);
            }
	        else if (enemyBody.get(i).getBody() == player.getContactListener().currentEnemy.get(j).getBody()
                    && enemyBody.get(i).getPlayerHits() != -1 && enemyBody.get(i).getPlayerHits() >= 5) {
	            bodyToRemove.add(enemyBody.get(i));
            }
	    }
	    if (punch) invasorsHunt.res.getSound("punch").play(0.5f);
        else invasorsHunt.res.getSound("miss").play(0.5f);
	}

    public void createEnemyIterator(Texture icon, Texture numbers, int cols, int rows) {
        // Initialize animation to enemy counter
        TextureRegion[][] tmp = new TextureRegion(numbers).split(
                numbers.getWidth() / cols,
                numbers.getHeight() / rows);
        TextureRegion[] sprites = new TextureRegion[cols*rows];
        int index = 0;
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                sprites[index++]=tmp[i][j];
            }
        }
        enemiesIterator = new Animation(sprites);
        enemyIcon = new TextureRegion(icon);
    }

    public void createLifeBar (Texture icon, int cols, int rows) {

        // Initialize sprites to life bar

        TextureRegion[][] tmp2 = new TextureRegion(icon).split(
                icon.getWidth() / cols,
                icon.getHeight() / rows);

        lifeBar = new TextureRegion[rows*cols];

        int index = 0;
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                lifeBar[index++]=tmp2[i][j];
            }
        }
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

    public void createBorder(){
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        //Border Up
        bdef.position.set(-100 / PPM,invasorsHunt.V_HEIGHT / PPM);
        bdef.type =  BodyDef.BodyType.StaticBody;
        Body borderUpBody = world.createBody(bdef);

        shape.setAsBox((invasorsHunt.V_WIDTH*1.75f) / PPM,80 * invasorsHunt.SCALE / PPM);
        fdef.shape = shape;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        //to change the category
        borderUpBody.createFixture(fdef).setUserData("Border_Up");

        //Border Left
        bdef.position.set(-175 / PPM, invasorsHunt.V_HEIGHT / PPM);
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

        shape.setAsBox(invasorsHunt.SCALE * 7.5f / PPM,invasorsHunt.SCALE * 7.5f / PPM);
        fdef.shape = shape;
        fdef.friction = 1000000000000f;
        body.createFixture(fdef).setUserData("Player");

        player = new Player(body);
    }
    public Enemy createEnemy(Integer dist){
        BodyDef bdef = new BodyDef();
        CircleShape cshape = new CircleShape();
        FixtureDef fdef = new FixtureDef();

        int i = getRand(1,3)*2-3;
        bdef.position.set( i > 0 ? ((100+getRand(50,150))*dist*i) / PPM : ((getRand(0,100))*dist*i) / PPM,
                (getRand(0,80)) / PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(bdef);
        body.setGravityScale(0f);

        cshape.setRadius(invasorsHunt.SCALE * 7.5f / PPM);
        fdef.shape = cshape;
        //to change the category
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_ENEMY;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("Enemy");

        // create a foot sensor
        cshape.setRadius(invasorsHunt.SCALE * 7.5f / PPM);
        fdef.shape = cshape;
        fdef.filter.categoryBits = B2DVars.BIT_ENEMY;
        fdef.filter.maskBits = B2DVars.BIT_PLAYER;
        body.createFixture(fdef).setUserData("Foot_Enemy");

        return new Enemy(body);
    }

    public void dispose() {
        invasorsHunt.res.getMusic("play").stop();
    }
}
