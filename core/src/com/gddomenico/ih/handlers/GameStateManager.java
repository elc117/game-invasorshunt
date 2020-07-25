package com.gddomenico.ih.handlers;

import com.gddomenico.ih.invasorsHunt;
import com.gddomenico.ih.states.GameState;
import com.gddomenico.ih.states.Help;
import com.gddomenico.ih.states.Play;
import com.gddomenico.ih.states.Menu;

import java.util.Stack;

public class GameStateManager {

    private invasorsHunt game;
    private Stack<GameState> gameStates;

    public static final int HELP = 333;
    public static final int MENU = 420;
    public static final int PLAY = 666;

    public GameStateManager(invasorsHunt game) {
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(MENU);
    }

    public invasorsHunt game() { return game;}

    public void update(float dt){
        gameStates.peek().update(dt);
    }

    public void render(){
        gameStates.peek().render();
    }

    private GameState getState(int state) {
        if(state == MENU) return new Menu(this);
        if(state == PLAY) return new Play(this);
        if(state == PLAY) return new Help(this);
        return null;
    }

    public void setState(int state) {
        popState();
        pushState(state);
    }

    public void pushState(int state) {
        gameStates.push(getState(state));
    }

    public void popState() {
        GameState g = gameStates.pop();
        g.dispose();
    }
}
