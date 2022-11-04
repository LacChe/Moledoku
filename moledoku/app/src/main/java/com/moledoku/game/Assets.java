package com.moledoku.game;

import android.graphics.Rect;

import com.moledoku.framework.Music;
import com.moledoku.framework.Pixmap;
import com.moledoku.framework.Sound;

// access assets here
public class Assets {
    public static Pixmap main;
    public static Sound pop;
    public static Sound win;
    public static Sound lose;
    public static Music lvl3;
    public static Music lvl4;
    public static Music lvl5;
    public static Music lvl6;
    public static Music lvl7;
    public static Music lvl8;
    public static Music lvl9;
    public static Music lvl10;
    public static Music main0;
    public static Music main1;
    public static Music main2;

    public static Rect getSmallNumberRect(int i){
        Rect src = new Rect();
        src.top = (i - 1) * 16;
        src.left = 64;
        src.right = src.left + 16;
        src.bottom = src.top + 16;
        if(i == 0){
            src.top = (10 - 1) * 16;
            src.left = 64;
            src.right = src.left + 16;
            src.bottom = src.top + 16;
        }
        return src;
    }

    public static Rect getCoinNumberRect(int i){
        Rect src = new Rect();
        src.top = (i - 1) * 16;
        src.left = 80;
        src.right = src.left + 16;
        src.bottom = src.top + 16;
        if(i == 0){
            src.top = (10 - 1) * 16;
            src.left = 80;
            src.right = src.left + 16;
            src.bottom = src.top + 16;
        }
        return src;
    }

    public static Rect getLargeNumberRect(int i){
        Rect src = new Rect();
        src.top = (i - 1) * 32;
        src.left = 32;
        src.right = src.left + 32;
        src.bottom = src.top + 32;
        if(i == 0){
            src.top = (10 - 1) * 32;
            src.left = 32;
            src.right = src.left + 32;
            src.bottom = src.top + 32;
        }
        return src;
    }

    public static Rect getOperation(Board.Operation op){
        Rect src = new Rect();
        switch(op){
            case ADD:
                src.top = 192;
                break;
            case SUBTRACT:
                src.top = 192 + 16;
                break;
            case MULTIPLY:
                src.top = 192 + 2 * 16;
                break;
            case DIVIDE:
                src.top = 192 + 3 * 16;
                break;
            case EQUAL:
                src.top = 192 + 4 * 16;
                break;
        }
        src.left = 0;
        src.right = src.left + 16;
        src.bottom = src.top + 16;
        return src;
    }

    public static void stopMusic(){
        Assets.main0.stop();
        Assets.main1.stop();
        Assets.main2.stop();
        Assets.lvl3.stop();
        Assets.lvl4.stop();
        Assets.lvl5.stop();
        Assets.lvl6.stop();
        Assets.lvl7.stop();
        Assets.lvl8.stop();
        Assets.lvl9.stop();
        Assets.lvl10.stop();
    }

    public static void playMusic(){
        switch(MainMenuScreen.music){
            case 0:
                Assets.main0.play();
                break;
            case 1:
                Assets.main1.play();
                break;
            case 2:
                Assets.main2.play();
                break;
            case 3:
                Assets.lvl3.play();
                break;
            case 4:
                Assets.lvl4.play();
                break;
            case 5:
                Assets.lvl5.play();
                break;
            case 6:
                Assets.lvl6.play();
                break;
            case 7:
                Assets.lvl7.play();
                break;
            case 8:
                Assets.lvl8.play();
                break;
            case 9:
                Assets.lvl9.play();
                break;
            case 10:
                Assets.lvl10.play();
                break;
        }
    }
}