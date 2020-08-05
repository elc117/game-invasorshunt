package com.gddomenico.ih.handlers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter {

    public boolean keyDown(int k){
        if(k == Input.Keys.W){
            MyInput.setKey(MyInput.BUTTON_W, true);
        }
        if(k == Input.Keys.S){
            MyInput.setKey(MyInput.BUTTON_S, true);
        }
        if(k == Input.Keys.A){
            MyInput.setKey(MyInput.BUTTON_A, true);
        }
        if(k == Input.Keys.D){
            MyInput.setKey(MyInput.BUTTON_D, true);
        }
        if(k == Input.Keys.K){
            MyInput.setKey(MyInput.BUTTON_K, true);
        }
        if(k == Input.Keys.SPACE){
            MyInput.setKey(MyInput.BUTTON_SPACE, true);
        }
        if(k == Input.Keys.ENTER){
            MyInput.setKey(MyInput.BUTTON_ENTER, true);
        }
        if(k == Input.Keys.UP){
            MyInput.setKey(MyInput.BUTTON_UP, true);
        }
        if(k == Input.Keys.DOWN){
            MyInput.setKey(MyInput.BUTTON_DOWN, true);
        }
        if(k == Input.Keys.RIGHT){
            MyInput.setKey(MyInput.BUTTON_RIGHT, true);
        }
        if(k == Input.Keys.LEFT){
            MyInput.setKey(MyInput.BUTTON_LEFT, true);
        }
        if(k == Input.Keys.ESCAPE){
            MyInput.setKey(MyInput.BUTTON_ESC, true);
        }
        return true;
    }

    public boolean keyUp(int k){
        if(k == Input.Keys.W){
            MyInput.setKey(MyInput.BUTTON_W, false);
        }
        if(k == Input.Keys.S){
            MyInput.setKey(MyInput.BUTTON_S, false);
        }
        if(k == Input.Keys.A){
            MyInput.setKey(MyInput.BUTTON_A, false);
        }
        if(k == Input.Keys.D){
            MyInput.setKey(MyInput.BUTTON_D, false);
        }
        if(k == Input.Keys.K){
            MyInput.setKey(MyInput.BUTTON_K, false);
        }
        if(k == Input.Keys.SPACE){
            MyInput.setKey(MyInput.BUTTON_SPACE, false);
        }
        if(k == Input.Keys.ENTER){
            MyInput.setKey(MyInput.BUTTON_ENTER, false);
        }
        if(k == Input.Keys.UP){
            MyInput.setKey(MyInput.BUTTON_UP, false);
        }
        if(k == Input.Keys.DOWN){
            MyInput.setKey(MyInput.BUTTON_DOWN, false);
        }
        if(k == Input.Keys.RIGHT){
            MyInput.setKey(MyInput.BUTTON_RIGHT, false);
        }
        if(k == Input.Keys.LEFT){
            MyInput.setKey(MyInput.BUTTON_LEFT, false);
        }
        if(k == Input.Keys.ESCAPE){
            MyInput.setKey(MyInput.BUTTON_ESC, true);
        }
        return true;
    }

}
