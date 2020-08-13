package com.gddomenico.ih.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.gddomenico.ih.handlers.GameStateManager;
import com.gddomenico.ih.handlers.MyInput;
import com.gddomenico.ih.invasorsHunt;

public class EndWin extends GameState {

    private final TextureRegion winImage;

    public EndWin(GameStateManager gsm) {
        super(gsm);

        winImage = new TextureRegion(invasorsHunt.res.getTexture("win_text"));
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

        this.sb.setProjectionMatrix(this.cam.combined);

        sb.begin();
        sb.draw(winImage,
                cam.position.x - invasorsHunt.V_WIDTH / 3.8f,
                invasorsHunt.V_HEIGHT - winImage.getRegionHeight() / 1.5f,
                winImage.getRegionWidth() / 2.5f,
                winImage.getRegionHeight() / 2.5f
        );
        sb.end();

    }
    public void dispose() {}
}