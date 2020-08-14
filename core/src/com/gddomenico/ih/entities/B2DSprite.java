package com.gddomenico.ih.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.gddomenico.ih.handlers.Animation;

import static com.gddomenico.ih.handlers.B2DVars.PPM;

/**
 * Attaches animated sprites to box2d bodies
 */
public class B2DSprite {

    protected TextureRegion[] punch;
    protected TextureRegion[] walk;
    protected TextureRegion[] death;

    protected int flash = 0;
    protected Integer playerHits = 0;

    protected boolean rightArm = false;

    protected Body body;
    protected Animation animation;
    protected float width;
    protected float height;

    protected float timeStop = 0f;
    public static final float delay = 1/3f;

    protected boolean stop = false;
    protected boolean isDead;

    public B2DSprite(Body body) {
        this.body = body;
        animation = new Animation();
    }

    public void setAnimation(TextureRegion[] reg) {
        setAnimation(reg , 1/ 12f);
    }

    public void setAnimation(TextureRegion reg, float delay) {
        setAnimation(new TextureRegion[] { reg }, delay);
    }

    public void setAnimation(TextureRegion[] reg, float delay) {
        animation.setFrames(reg, delay);
        width = reg[0].getRegionWidth();
        height = reg[0].getRegionHeight();
    }

    public void update(float dt) {

        if(stop)
            timeStop += dt;
        if(timeStop >= delay) {
            stop = false;
            timeStop -= delay;
        }

        animation.update(dt);
    }

    public void render(SpriteBatch sb) {

        // flip sprite
        boolean flip = rightArm;

        sb.begin();
        sb.draw(animation.getFrame(),
                flip ? (body.getPosition().x * PPM - 10 - width / 2) : (body.getPosition().x * PPM + 10 + width / 2),
                (int) (body.getPosition().y * PPM - 10 - height / 2),
                flip ? width + 20 : -width - 20,
                height + 20);
        sb.end();

    }

    public void setFlash (int flash) {
        this.flash = flash;
    }
    public void setFlash () {
        this.flash++;
    }
    public int getFlash () { return flash; }

    public Body getBody() { return body; }
    public Vector2 getPosition() { return body.getPosition(); }

    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public boolean getStop() { return stop;}
    public void setStop(boolean stop) { this.stop = stop; }

    public int getPlayerHits(){
        return playerHits;
    }

    public TextureRegion[] animateCharacter(Texture tex, int cols, int rows){

        TextureRegion[][] tmp = new TextureRegion(tex).split(
                tex.getWidth() / cols,
                tex.getHeight() / rows);

        TextureRegion[] sprites = new TextureRegion[cols*rows];

        int index = 0;
        for (int i=0; i<rows; i++) {
            for (int j=0; j<cols; j++) {
                sprites[index++]=tmp[i][j];
            }
        }
        return sprites;
    }

    public void setDeathCondition() {
        animation.setWalk(true);
        isDead = true;
        stop = true;
        timeStop = 0;
        setAnimation(death);
    }
    public boolean getDeathCondition() {  return isDead; }

    public boolean getRightArm () {
        return rightArm;
    }
    public void setRightArm (boolean right) {
        rightArm = right;
    }

}
