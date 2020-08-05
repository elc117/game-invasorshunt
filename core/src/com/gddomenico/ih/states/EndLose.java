package com.gddomenico.ih.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gddomenico.ih.handlers.GameStateManager;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.states.GameState;
import com.gddomenico.ih.invasorsHunt;

public class EndLose extends GameState {



    private final TextureRegion loseImage;


    public EndLose(GameStateManager gsm) {
        super(gsm);

        loseImage = new TextureRegion(invasorsHunt.res.getTexture("lose_text"));

    }

    public void handleInput() {
        if(MyInput.isPressed(MyInput.BUTTON_K)){
            gsm.setState(GameStateManager.PLAY);
        }
        if(MyInput.isPressed(MyInput.BUTTON_ESC)){
            Gdx.app.exit();
        }
    }

    public void update(float dt) {
        handleInput();
    }


    public void render() {

        GlyphLayout layout = new GlyphLayout();

        float width = layout.width;

        this.sb.setProjectionMatrix(this.cam.combined);

        sb.begin();
        sb.draw(loseImage,
                cam.position.x - invasorsHunt.V_WIDTH / 2.1f,
                invasorsHunt.V_HEIGHT - loseImage.getRegionHeight() / 2.1f,
                loseImage.getRegionWidth() / 2.5f,
                loseImage.getRegionHeight() / 2.5f
        );
        sb.end();

    };
    public void dispose() {};
}