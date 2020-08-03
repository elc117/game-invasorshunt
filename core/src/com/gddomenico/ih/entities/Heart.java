package com.gddomenico.ih.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.invasorsHunt;

public class Heart extends B2DSprite{

    public Heart(Body body){
        super(body);

        int column = 1;
        int row = 1;

        Texture tex = invasorsHunt.res.getTexture("heart");
        TextureRegion[][] tmp = new TextureRegion(tex).split(
                tex.getWidth() / column,
                tex.getHeight() / row);

        TextureRegion[] sprites = new TextureRegion[column*row];

        int index = 0;
        for (int i=0; i<row; i++) {
            for (int j=0; j<column; j++) {
                sprites[index++]=tmp[i][j];
            }
        }
        setAnimation(sprites, 20f);

    }

}
