package com.gddomenico.ih.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.invasorsHunt;

public class Heart extends B2DSprite{

    public Heart(Body body){
        super(body);

        TextureRegion tmp = new TextureRegion(invasorsHunt.res.getTexture("heart"));

        setAnimation(tmp, 1 / 12f);
    }

}
