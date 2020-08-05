package com.gddomenico.ih;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gddomenico.ih.handlers.Content;
import com.gddomenico.ih.handlers.GameStateManager;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.handlers.MyInputProcessor;

public class invasorsHunt extends ApplicationAdapter {

	public static final  String TITLE = "Invasors Hunt";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int SCALE = 2;

	public static final float STEP = 1 / 60f;
	private float accum;

	private SpriteBatch sb;
	private OrthographicCamera cam;
	private OrthographicCamera hudCam;

	private GameStateManager gsm;

	public static Content res;

	@Override
	public void create () {

		Gdx.input.setInputProcessor(new MyInputProcessor());

		// Load all assets
		res = new Content();

		// Sound effects
		res.loadSound("sounds/punch.wav", "punch");
		res.loadSound("sounds/missed.wav", "miss");
		res.loadSound("sounds/lose.wav", "lose");
		res.loadSound("sounds/win.wav", "win");

		// Music
		res.loadMusic("sounds/menu.wav","menu");
		res.loadMusic("sounds/play.wav","play");
		res.getMusic("menu").setLooping(false);
		res.getMusic("menu").setVolume(0.5f);
		res.getMusic("play").setLooping(true);
		res.getMusic("play").setVolume(0.5f);

		// Enemy textures
		res.loadTexture("images/enemyIcon.png","enemyIcon");
		res.loadTexture("images/enemies.png","enemies");
		res.loadTexture("images/enemies_punch.png","enemies_punch");
		res.loadTexture("images/enemies_death.png","enemies_death");

<<<<<<< Updated upstream
=======
		// Player textures
		res.loadTexture("images/mainIcon.png","mainIcon");
>>>>>>> Stashed changes
		res.loadTexture("images/main.png","main");
		res.loadTexture("images/main_win.png","main_win");
		res.loadTexture("images/main_punch.png","main_punch");
		res.loadTexture("images/main_death.png","main_death");

		// Game textures
		res.loadTexture("images/background2.png","background2");
		res.loadTexture("images/background.jpg","background");
		res.loadTexture("fonts/numbers.png", "numbers");
		res.loadTexture("images/heart.png","heart");
		res.loadTexture("images/lives.png", "life");

		// Menu and end game conditions
		res.loadTexture("images/badlogic.png","menu");
		res.loadTexture("images/win_text.png","win_text");
		res.loadTexture("images/lose_text.png","lose_text");

		sb = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);


		gsm = new GameStateManager(this);

	}

	@Override
	public void render () {

		accum += Gdx.graphics.getDeltaTime();
		while(accum >= STEP){
			accum -= STEP;
			gsm.update(STEP);
			gsm.render();
			MyInput.update();
		}

	}
	
	@Override
	public void dispose () {
		res.removeAll();
	}

	public SpriteBatch getSpriteBatch() { return sb; }

	public OrthographicCamera getCam() { return cam; }

	public OrthographicCamera getHudCam() { return hudCam; }

	@Override
	public void pause () {

	}

	@Override
	public void resume () {

	}
}
