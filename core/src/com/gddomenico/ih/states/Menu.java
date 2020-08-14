package com.gddomenico.ih.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gddomenico.ih.handlers.GameStateManager;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.invasorsHunt;

public class Menu extends GameState {

    private final BitmapFont titleFont;
    private final BitmapFont font;

    private final static String title = "Invasors Hunt";

    private boolean help = false;
    
    private int currentItem = 0;
    private final String[] menuItems;

    private final TextureRegion enemyIcon = new TextureRegion(invasorsHunt.res.getTexture("enemyIcon"));
	private final TextureRegion mainIcon = new TextureRegion(invasorsHunt.res.getTexture("mainIcon"));

	public static final int V_WIDTH = 620;
	public static final int V_HEIGHT = 440;

	public Menu(GameStateManager gsm) {
        super(gsm);

        titleFont = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));

        font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
        font.getData().setScale(invasorsHunt.SCALE/2.5f);

		invasorsHunt.res.getMusic("menu").play();

        menuItems = new String[] {
        	"Jogar",
        	"Ajuda"
        };
        
    }
    
    private void select() {
    	if(currentItem == 0) {
    		gsm.setState(GameStateManager.PLAY);
    	}
    	else if(currentItem == 1) {
    		help = true;
    		currentItem = 3;
			font.getData().setScale(invasorsHunt.SCALE/2.5f);
    	}
		else if(currentItem == 2) {
    		Gdx.app.exit();
    	}
		else if (currentItem == 3) {
			help = false;
			currentItem = 0;
			font.getData().setScale(invasorsHunt.SCALE/2.5f);
		}
    }

    public void handleInput() {


		if (!help) {
			if (MyInput.isPressed(MyInput.BUTTON_W) || MyInput.isPressed(MyInput.BUTTON_UP)) {
				if (currentItem > 0) currentItem--;
			}
			if (MyInput.isPressed(MyInput.BUTTON_S) || MyInput.isPressed(MyInput.BUTTON_DOWN)) {
				if (currentItem < menuItems.length - 1) currentItem++;
			}
		}
        if(MyInput.isPressed(MyInput.BUTTON_ENTER)){
        	select();
        }
    	
    }
    
    public void update(float dt) {
    	
    	handleInput();
    			
    }
    

    public void render() {
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
    	GlyphLayout layout = new GlyphLayout();
    	layout.setText(titleFont, title);
		titleFont.setColor(Color.YELLOW);
    	float width = layout.width;
    	
    	this.sb.setProjectionMatrix(this.cam.combined);

    	sb.begin();
    	
		sb.draw(invasorsHunt.res.getTexture("menu"), 0, 0,invasorsHunt.V_WIDTH, invasorsHunt.V_HEIGHT);

		if ( !help ) {
			titleFont.draw(
					sb,
					title,
					(invasorsHunt.V_WIDTH - width) / 2,
					(invasorsHunt.V_HEIGHT - invasorsHunt.SCALE * 30)
			);
			for (int i = 0; i < menuItems.length; i++) {
				layout.setText(font, menuItems[i]);
				width = layout.width;
				if (currentItem == i) font.setColor(Color.RED);
				else font.setColor(Color.YELLOW);

				font.draw(
						sb,
						menuItems[i],
						(invasorsHunt.V_WIDTH - width) / 2,
						250 - 40 * i
				);
			}
		}
		else {
			font.setColor(Color.YELLOW);
			String instrucoes = "Instrucoes:";
			layout.setText(titleFont, instrucoes);
			width = layout.width;
			titleFont.draw(
					sb,
					instrucoes,
					(invasorsHunt.V_WIDTH - width) / 2,
					invasorsHunt.V_HEIGHT - 15
			);
			String texto[] = new String[] {
					"Voce e um globulo branco dentro de um corpo humano e seu objetivo",
					" ",
					"e protege-lo dos parasitas. Acabe com todos para ganhar o jogo",
					" ",
					" ",
					"W        ->    Cima",
					" ",
					"A        ->    Esquerda",
					" ",
					"S        ->    Baixo",
					" ",
					"D        ->    Direita",
					" ",
					"K        ->    Ataca",
					" ",
					"ESC   ->    Pausa",
			};
			for (int i = 0; i < texto.length; i++) {
				font.draw(
						sb,
						texto[i],
						20,
						invasorsHunt.V_HEIGHT - 70 - 15 * i
				);
			}
			instrucoes = "Voce";
			font.draw(
					sb,
					instrucoes,
					invasorsHunt.V_WIDTH / 2f + 30,
					290
			);
			instrucoes = "Inimigo";
			font.draw(
					sb,
					instrucoes,
					invasorsHunt.V_WIDTH / 2f + 125,
					290
			);

			sb.draw(mainIcon, invasorsHunt.V_WIDTH / 2f + 24, 220);
			sb.draw(enemyIcon, invasorsHunt.V_WIDTH / 2f + 134, 220);

			layout.setText(font, "Voltar");
			font.setColor(Color.RED);
			width = layout.width;
			font.draw(
				sb,
					"Voltar",
				(invasorsHunt.V_WIDTH - width) / 2,
				40
			);
		}
    	
    	
    	sb.end();
    			
    }

    public void dispose() {
		invasorsHunt.res.getMusic("menu").stop();
    }
}