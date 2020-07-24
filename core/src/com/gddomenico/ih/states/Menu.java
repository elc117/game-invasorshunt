package com.gddomenico.ih.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gddomenico.ih.handlers.GameStateManager;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.states.GameState;
import com.gddomenico.ih.invasorsHunt;

public class Menu extends GameState {
	
    private BitmapFont titleFont;
    private BitmapFont font;

    private final String title = "Ivasors Hunt";
    
    private int currentItem;
    private String[] menuItems;
    
    public Menu(GameStateManager gsm) {
        super(gsm);
        
        currentItem = 0;
        
        titleFont = new BitmapFont(Gdx.files.internal("font.fnt"));
        
        //titleFont.setColor(Color.WHITE);
        
        font = new BitmapFont(Gdx.files.internal("font.fnt"));
        font.getData().setScale(0.5f);
    	
        
        menuItems = new String[] {
        	"Jogar",
        	"Ajuda",
        	"Sair"
        };
        
    }
    
    private void select() {
    	if(currentItem == 0) {
    		gsm.setState(GameStateManager.PLAY);
    	}
    	else if(currentItem == 1) {
    		//gsm.setState(GameStateManager.HELP);
    	}
		else if(currentItem == 2) {
    		Gdx.app.exit();
    	}
    }

    public void handleInput() {
    	
    	
        if(MyInput.isPressed(MyInput.BUTTON_W)) {
        	if(currentItem > 0) currentItem--;
        }
        if(MyInput.isPressed(MyInput.BUTTON_S)){
        	if(currentItem < menuItems.length - 1) currentItem++;
        }
        if(MyInput.isPressed(MyInput.BUTTON_ENTER)){
        	select();
        }
    	
    };
    
    public void update(float dt) {
    	
    	handleInput();
    			
    };
    

    public void render() {
    	
    	GlyphLayout layout = new GlyphLayout();
    	layout.setText(titleFont, title);
    	float width = layout.width;
    	
    	this.sb.setProjectionMatrix(this.cam.combined);
    	
    	sb.begin();
    	
    	titleFont.draw(
			sb, 
			title, 
			(invasorsHunt.V_WIDTH - width) / 2, 
			(invasorsHunt.V_HEIGHT - 30)
    	);
    	
    	for(int i = 0; i < menuItems.length; i++) {
        	layout.setText(font, menuItems[i]);
        	width = layout.width;
    		if(currentItem == i) font.setColor(Color.RED);
    		else font.setColor(Color.WHITE);
        	
        	font.draw(
    			sb, 
    			menuItems[i], 
    			(invasorsHunt.V_WIDTH - width) / 2, 
    			150 - 35 * i
        	);
    			
    	}
    	
    	
    	sb.end();
    			
    };
    public void dispose() {};
}