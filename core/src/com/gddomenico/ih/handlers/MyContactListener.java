package com.gddomenico.ih.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;


public class MyContactListener implements ContactListener {

    private int playerOnContact;
    private int heathsOnContact;
    public Array<Fixture> currentEnemy = new Array<>();
    private final Array<Body> heartsToRemove = new Array<>();
    public Fixture fa;
    public Fixture fb;

    public MyContactListener() {
        super();
    }

    public void beginContact(Contact c) {
        fa = c.getFixtureA();
        fb = c.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("Foot_Enemy")){
            playerOnContact++;
            currentEnemy.add(fa);
        }
        if(fb.getUserData() != null && fb.getUserData().equals("Foot_Enemy")){
            playerOnContact++;
            currentEnemy.add(fb);
        }
        if(fa.getUserData() != null && fa.getUserData().equals("Hearts")){
            heathsOnContact++;
            heartsToRemove.add(fa.getBody());
        }
        if(fb.getUserData() != null && fb.getUserData().equals("Hearts")){
            heathsOnContact++;
            heartsToRemove.add(fb.getBody());
        }
    }

    public void endContact(Contact c) {

        fa = c.getFixtureA();
        fb = c.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("Foot_Enemy")){
            for(int i = 0; i < playerOnContact; i++)
                if(currentEnemy.get(i) == fa){
                    currentEnemy.removeIndex(i);
                    break;
                }
            playerOnContact--;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("Foot_Enemy")){
            for(int i = 0; i < playerOnContact; i++)
                if(currentEnemy.get(i) == fb){
                    currentEnemy.removeIndex(i);
                    break;
                }
            playerOnContact--;
        }
    }

    public boolean isPlayerOnContact() { return playerOnContact > 0; }

    public Array<Body> getHeartsToRemove() { return heartsToRemove;}

    public void preSolve(Contact c, Manifold m) {}

    public int getHeathsOnContact () { return heathsOnContact; }
    public void setHeathsOnContact (int num) { heathsOnContact = num; }

    public int getContacts() { return playerOnContact; }
    public void postSolve(Contact c, ContactImpulse ci) {}

}
