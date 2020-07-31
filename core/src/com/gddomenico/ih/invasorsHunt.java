package com.gddomenico.ih;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gddomenico.ih.handlers.Content;
import com.gddomenico.ih.handlers.GameStateManager;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.handlers.MyInputProcessor;

public class invasorsHunt extends ApplicationAdapter {

	SpriteBatch batch;
	Texture img;

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

		res = new Content();

		res.loadSound("sounds/punch.wav", "punch");
		res.loadSound("sounds/missed.wav", "miss");

		res.loadMusic("sounds/menu.wav","menu");
		res.getMusic("menu").setLooping(false);
		res.getMusic("menu").setVolume(0.2f);

		res.loadTexture("images/background2.png","background2");
		res.loadTexture("images/background.jpg","background");
		res.loadTexture("images/badlogic.png","menu");
		res.loadTexture("images/bunny.png", "bunny");
		res.loadTexture("images/slime.png", "slime");
		res.loadTexture("images/lives.png", "life");

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
