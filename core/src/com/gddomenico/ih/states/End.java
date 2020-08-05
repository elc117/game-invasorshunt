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

public class End extends GameState {

    private BitmapFont titleFont;

    private final String title = "Morreu";

    private int currentItem;
    private String[] menuItems;

    public End(GameStateManager gsm) {
        super(gsm);

        currentItem = 0;

        titleFont = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));

        //titleFont.setColor(Color.WHITE);

    }

    public void handleInput() {};

    public void update(float dt) {};


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

        sb.end();

    };
    public void dispose() {};
}