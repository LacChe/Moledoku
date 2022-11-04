package com.moledoku.game;

import android.graphics.Color;
import android.util.Log;

import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input.TouchEvent;
import com.moledoku.framework.Screen;

import java.util.List;
import java.util.Random;

// first screen user sees, select game, achievement page, help, audio toggle
public class MainMenuScreen extends Screen {

    // music selection var
    public static int music = -1;

    Random rand;

    public MainMenuScreen(Game game) {
        super(game);
        rand = new Random();
        if(Settings.soundEnabled == 1 && (music > 2 || music < 0)){
            music = rand.nextInt(3);
            setMusic();
        }
    }

    private void setMusic(){
        Assets.lvl3.stop();
        Assets.lvl4.stop();
        Assets.lvl5.stop();
        Assets.lvl6.stop();
        Assets.lvl7.stop();
        Assets.lvl8.stop();
        Assets.lvl9.stop();
        Assets.lvl10.stop();
        switch(music){
            case 0:
                Assets.main0.play();
                break;
            case 1:
                Assets.main1.play();
                break;
            case 2:
                Assets.main2.play();
                break;
        }
    }

    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                // sound button
                if(inBounds(event, g.getWidth() - (int)(16 * g.getScale()) - (int)(10 * g.getScale()), (int)(10 * g.getScale()), (int)(16 * g.getScale()), (int)(16 * g.getScale()))) {
                    // sound button
                    if(Settings.soundEnabled == 0){
                        Settings.soundEnabled = 1;
                        music = rand.nextInt(3);
                        setMusic();
                        Assets.playMusic();
                    }
                    else if(Settings.soundEnabled == 1){
                        Settings.soundEnabled = 0;
                        Assets.stopMusic();
                    }
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
                // help button
                if(inBounds(event, g.getWidth() - (int)(16 * g.getScale()) * 2 - (int)(10 * g.getScale()) * 2, (int)(10 * g.getScale()), (int)(16 * g.getScale()), (int)(16 * g.getScale()))) {
                    game.setScreen(new HelpScreen(game));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
                // game button
                if(inBounds(event, g.getWidth() / 2 - (int)(128 / 2 * g.getScale()), (int)(96 / 2 * g.getScale() + 8 * g.getScale()), (int)(64 * g.getScale()) , (int)(64 * g.getScale())) ) {
                    if(Settings.board != null && Settings.hasSavedGame == 1) game.setScreen(new GameScreen(game, Settings.board));
                    else game.setScreen(new SelectLevelScreen(game));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
                // home button
                if(inBounds(event, g.getWidth() / 2, (int)(96 / 2 * g.getScale() + 8 * g.getScale()), (int)(64 * g.getScale()) , (int)(64 * g.getScale())) ) {
                    if(Settings.homeScreen != null) game.setScreen(new HomeScreen(game, Settings.homeScreen));
                    else game.setScreen(new HomeScreen(game));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
            }
        }
    }

    private boolean inBounds(TouchEvent event, int x, int y, int width, int height) {
        if(event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1)
            return true;
        else
            return false;
    }

    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, g.getWidth(), g.getHeight(), Color.rgb(124, 174, 255));
        // draw map
        g.drawPixmap(Assets.main, 0, g.getHeight() - (256 * g.getScale()), (192 * g.getScale()) , (256 * g.getScale()), 224, 0, 192, 256);
        // draw main buttons
        g.drawPixmap(Assets.main, g.getWidth() / 2.0f - (int)(128 / 2 * g.getScale()), (int)(8 * g.getScale()), (int)(128 * g.getScale()) , (int)(128 * g.getScale()), 96, 0, 128, 128);
        // draw help
        g.drawPixmap(Assets.main, g.getWidth() - (int)(16 * g.getScale()) * 2 - (int)(10 * g.getScale()) * 2, (int)(10 * g.getScale()), (int)(16 * g.getScale()), (int)(16 * g.getScale()), 64, 160, 16, 16);
        // draw sound toggle
        g.drawPixmap(Assets.main, g.getWidth() - (int)(16 * g.getScale()) - (int)(10 * g.getScale()), (int)(10 * g.getScale()), (int)(16 * g.getScale()), (int)(16 * g.getScale()), 16, 16, 16, 16);
        if(Settings.soundEnabled == 1)
            ;
        else
            g.drawLine(g.getWidth() - (int)(16 * g.getScale()) - (int)(10 * g.getScale()), (int)(10 * g.getScale()), g.getWidth() - (int)(10 * g.getScale()), (int)(16 * g.getScale()) + (int)(10 * g.getScale()), Color.rgb(45, 23, 7), 10);
        if(Settings.TRIAL_VERSION)
            g.drawPixmap(Assets.main, 0, 0, (int)(56 * g.getScale()), (int)(24 * g.getScale()), 0, 1184, 56, 24);
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
    }
}