package com.moledoku.framework.impl;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.moledoku.framework.Audio;
import com.moledoku.framework.FileIO;
import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input;
import com.moledoku.framework.Screen;
import com.moledoku.game.MainMenuScreen;
import com.moledoku.game.MoleDoku;
import com.moledoku.game.Settings;

public abstract class AndroidGame extends Activity implements Game {


    public static AndroidGame AGame;

    public AndroidFastRenderView renderView;
    public Graphics graphics;
    public Audio audio;
    public Input input;
    public FileIO fileIO;
    public Screen screen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        boolean isLandscape = getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE;
        int frameBufferWidth = isLandscape ? Resources.getSystem().getDisplayMetrics().heightPixels : Resources.getSystem().getDisplayMetrics().widthPixels;
        int frameBufferHeight = isLandscape ? Resources.getSystem().getDisplayMetrics().widthPixels : Resources.getSystem().getDisplayMetrics().heightPixels;
        Bitmap frameBuffer = Bitmap.createBitmap(frameBufferWidth,
                frameBufferHeight, Config.RGB_565);
        float scaleX = (float) frameBufferWidth
                / getWindowManager().getDefaultDisplay().getWidth();
        float scaleY = (float) frameBufferHeight
                / getWindowManager().getDefaultDisplay().getHeight();
        renderView = new AndroidFastRenderView(this, frameBuffer);
        graphics = new AndroidGraphics(getAssets(), frameBuffer);
        fileIO = new AndroidFileIO(this);
        audio = new AndroidAudio(this);
        input = new AndroidInput(this, renderView, scaleX, scaleY);
        screen = getStartScreen();
        setContentView(renderView);
        AGame = this;
    }

    public Input getInput() {
        return input;
    }

    public FileIO getFileIO() {
        return fileIO;
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");
        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    public Screen getCurrentScreen() {
        return screen;
    }
}