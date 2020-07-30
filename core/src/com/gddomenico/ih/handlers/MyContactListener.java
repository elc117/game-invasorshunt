package com.gddomenico.ih.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;


public class MyContactListener implements ContactListener {

    private int playerOnContact;
    private int playerOnTheWall;
    private boolean rightContact;
    private boolean leftContact;
    public Array<Fixture> currentEnemy = new Array<Fixture>();
    public Fixture fa;
    public Fixture fb;


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
        if(fa.getUserData() != null && fa.getUserData().equals("Border_Left")){
            playerOnTheWall=1;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("Border_Left")){
            playerOnTheWall=1;
        }
        if(fa.getUserData() != null && fa.getUserData().equals("Border_Right")){
            playerOnTheWall=2;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("Border_Right")){
            playerOnTheWall=2;
        }
    }

    public void endContact(Contact c) {

        fa = c.getFixtureA();
        fb = c.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("Foot_Enemy")){
            for(int i = 0; i < currentEnemy.size; i++)
                if(currentEnemy.get(i) == fa){
                    currentEnemy.removeIndex(i);
                    break;
                }
            playerOnContact--;
            leftContact = false;
            rightContact = false;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("Foot_Enemy")){
            for(int i = 0; i < currentEnemy.size; i++)
                if(currentEnemy.get(i) == fb){
                    currentEnemy.removeIndex(i);
                    break;
                }
            playerOnContact--;
            leftContact = false;
            rightContact = false;
        }
        if(fa.getUserData() != null && fa.getUserData().equals("Border_Left")){
            playerOnTheWall=0;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("Border_Left")){
            playerOnTheWall=0;
        }
        if(fa.getUserData() != null && fa.getUserData().equals("Border_Right")){
            playerOnTheWall=0;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("Border_Right")){
            playerOnTheWall=0;
        }
    }

    public boolean isPlayerOnContact() { return playerOnContact > 0; }

    public int isPlayerOnTheWall() { return playerOnTheWall; }

    public void preSolve(Contact c, Manifold m) {

        if(m.getLocalNormal().epsilonEquals(1,-0)){
            leftContact = true;
            rightContact = false;
        }
        if(m.getLocalNormal().epsilonEquals(-1,-0)){
            rightContact = true;
            leftContact = false;
        }

    }
    public boolean isRightContact() { return rightContact; }
    public boolean isLeftContact() { return leftContact; }

    public int getContacts() { return playerOnContact; }
    public void postSolve(Contact c, ContactImpulse ci) {}

}
