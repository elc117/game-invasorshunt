package com.gddomenico.ih.handlers;

import com.gddomenico.ih.invasorsHunt;
import com.gddomenico.ih.states.*;

import java.util.Stack;

public class GameStateManager {

    private final invasorsHunt game;
    private final Stack<GameState> gameStates;

    public static final int MENU = 420;
    public static final int PLAY = 666;
    public static final int ENDWIN = 6969;
    public static final int ENDLOSE = 9696;

    public GameStateManager(invasorsHunt game) {
        this.game = game;
        gameStates = new Stack<>();
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
        if(state == ENDWIN) return new EndWin(this);
        if(state == ENDLOSE) return new EndLose(this);
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
