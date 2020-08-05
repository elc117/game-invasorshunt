package com.gddomenico.ih.handlers;

public class MyInput {

    public static boolean[] keys;
    public static boolean[] pkeys;

    public static final int NUM_KEYS = 12;
    public static final int BUTTON_W = 0;
    public static final int BUTTON_S = 1;
    public static final int BUTTON_A = 2;
    public static final int BUTTON_D = 3;
    public static final int BUTTON_SPACE = 4;
    public static final int BUTTON_K = 5;
    public static final int BUTTON_ENTER = 6;
    public static final int BUTTON_UP = 7;
    public static final int BUTTON_DOWN = 8;
    public static final int BUTTON_RIGHT = 9;
    public static final int BUTTON_LEFT = 10;
    public static final int BUTTON_ESC = 11;

    static {
        keys = new boolean[NUM_KEYS];
        pkeys = new boolean[NUM_KEYS];
    }

    public static void update() {
        System.arraycopy(keys, 0, pkeys, 0, NUM_KEYS);
    }

    public static void setKey(int i, boolean b){
        keys[i] = b;
    }

    public static boolean isDown(int i){ return keys[i]; }

    public static boolean isUp(int i){ return !keys[i] && pkeys[i]; }

    public static boolean isPressed(int i){
        return keys[i] && !pkeys[i];
    }
}
