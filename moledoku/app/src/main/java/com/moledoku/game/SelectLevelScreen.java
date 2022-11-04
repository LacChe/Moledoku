package com.moledoku.game;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input.TouchEvent;
import com.moledoku.framework.Screen;
import com.moledoku.framework.impl.AndroidGame;

import java.util.List;

// user selects level to play here, as long as unlocked
public class SelectLevelScreen extends Screen {

    public SelectLevelScreen(Game game) {
        super(game);
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {

                // back
                if(inBounds(event, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()))) {
                    game.setScreen(new MainMenuScreen(game));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
                // levels
                if(Settings.currentLevel >= 10 && inBounds(event, ((356 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 20 * g.getScale()), (32 * g.getScale()), (32 * g.getScale())) ) {
                    game.setScreen(new GameScreen(game, 10));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
                if(Settings.currentLevel >= 9 && inBounds(event, ((376 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 76 * g.getScale()), (32 * g.getScale()), (32 * g.getScale())) ) {
                    game.setScreen(new GameScreen(game, 9));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
                if(Settings.currentLevel >= 8 && inBounds(event, ((240 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 98 * g.getScale()), (32 * g.getScale()), (32 * g.getScale())) ) {
                    game.setScreen(new GameScreen(game, 8));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
                if(Settings.currentLevel >= 7 && inBounds(event, ((294 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 122 * g.getScale()), (32 * g.getScale()), (32 * g.getScale())) ) {
                    game.setScreen(new GameScreen(game, 7));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
                if(Settings.currentLevel >= 6 && inBounds(event, ((360 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 132 * g.getScale()), (32 * g.getScale()), (32 * g.getScale())) ) {
                    game.setScreen(new GameScreen(game, 6));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
                if(Settings.currentLevel >= 5 && inBounds(event, ((240 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 154 * g.getScale()), (32 * g.getScale()), (32 * g.getScale())) ) {
                    game.setScreen(new GameScreen(game, 5));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
                if(Settings.currentLevel >= 4 && inBounds(event, ((296 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 178 * g.getScale()), (32 * g.getScale()), (32 * g.getScale())) ) {
                    game.setScreen(new GameScreen(game, 4));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
                if(Settings.currentLevel >= 3 && inBounds(event, ((360 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 208 * g.getScale()), (32 * g.getScale()), (32 * g.getScale())) ) {
                    game.setScreen(new GameScreen(game, 3));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        // render all unlocked levels
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, g.getWidth(), g.getHeight(), Color.rgb(124, 174, 255));
        // draw map
        g.drawPixmap(Assets.main, 0, g.getHeight() - (256 * g.getScale()), (192 * g.getScale()) , (256 * g.getScale()), 224, 0, 192, 256);
        // draw back button
        g.drawPixmap(Assets.main, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 0, 0, 16, 16);

        Rect srcRect;
        // level enabled
        if(Settings.currentLevel >= 10) {
            srcRect = Assets.getLargeNumberRect(0);
            g.drawPixmap(Assets.main, ((356 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 20 * g.getScale()), (32 * g.getScale()), (32 * g.getScale()), 64,448 + 32 * 7,32,32);
            g.drawPixmap(Assets.main, ((356 - 224) * g.getScale() + 8 * g.getScale()), g.getHeight() - (256 * g.getScale() - 20 * g.getScale()) + 8 * g.getScale(), (16 * g.getScale()) , (16 * g.getScale()), srcRect.left, srcRect.top, srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
        if(Settings.currentLevel >= 9) {
            srcRect = Assets.getLargeNumberRect(9);
            g.drawPixmap(Assets.main, ((376 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 76 * g.getScale()), (32 * g.getScale()), (32 * g.getScale()), 64,448 + 32 * 6,32,32);
            g.drawPixmap(Assets.main, ((376 - 224) * g.getScale() + 8 * g.getScale()), g.getHeight() - (256 * g.getScale() - 76 * g.getScale()) + 8 * g.getScale(), (16 * g.getScale()) , (16 * g.getScale()), srcRect.left, srcRect.top, srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
        if(Settings.currentLevel >= 8) {
            srcRect = Assets.getLargeNumberRect(8);
            g.drawPixmap(Assets.main, ((240 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 98 * g.getScale()), (32 * g.getScale()), (32 * g.getScale()), 64,448 + 32 * 5,32,32);
            g.drawPixmap(Assets.main, ((240 - 224) * g.getScale() + 8 * g.getScale()), g.getHeight() - (256 * g.getScale() - 98 * g.getScale()) + 8 * g.getScale(), (16 * g.getScale()) , (16 * g.getScale()), srcRect.left, srcRect.top, srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
        if(Settings.currentLevel >= 7) {
            srcRect = Assets.getLargeNumberRect(7);
            g.drawPixmap(Assets.main, ((294 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 122 * g.getScale()), (32 * g.getScale()), (32 * g.getScale()), 64,448 + 32 * 4,32,32);
            g.drawPixmap(Assets.main, ((294 - 224) * g.getScale() + 8 * g.getScale()), g.getHeight() - (256 * g.getScale() - 122 * g.getScale()) + 8 * g.getScale(), (16 * g.getScale()) , (16 * g.getScale()), srcRect.left, srcRect.top, srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
        if(Settings.currentLevel >= 6) {
            srcRect = Assets.getLargeNumberRect(6);
            g.drawPixmap(Assets.main, ((360 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 132 * g.getScale()), (32 * g.getScale()), (32 * g.getScale()), 64,448 + 32 * 3,32,32);
            g.drawPixmap(Assets.main, ((360 - 224) * g.getScale() + 8 * g.getScale()), g.getHeight() - (256 * g.getScale() - 132 * g.getScale()) + 8 * g.getScale(), (16 * g.getScale()) , (16 * g.getScale()), srcRect.left, srcRect.top, srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
        if(Settings.currentLevel >= 5) {
            srcRect = Assets.getLargeNumberRect(5);
            g.drawPixmap(Assets.main, ((240 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 154 * g.getScale()), (32 * g.getScale()), (32 * g.getScale()), 64,448 + 32 * 2,32,32);
            g.drawPixmap(Assets.main, ((240 - 224) * g.getScale() + 8 * g.getScale()), g.getHeight() - (256 * g.getScale() - 154 * g.getScale()) + 8 * g.getScale(), (16 * g.getScale()) , (16 * g.getScale()), srcRect.left, srcRect.top, srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
        if(Settings.currentLevel >= 4) {
            srcRect = Assets.getLargeNumberRect(4);
            g.drawPixmap(Assets.main, ((296 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 178 * g.getScale()), (32 * g.getScale()), (32 * g.getScale()), 64,448 + 32,32,32);
            g.drawPixmap(Assets.main, ((296 - 224) * g.getScale() + 8 * g.getScale()), g.getHeight() - (256 * g.getScale() - 178 * g.getScale()) + 8 * g.getScale(), (16 * g.getScale()) , (16 * g.getScale()), srcRect.left, srcRect.top, srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
        if(Settings.currentLevel >= 3) {
            srcRect = Assets.getLargeNumberRect(3);
            g.drawPixmap(Assets.main, ((360 - 224) * g.getScale()), g.getHeight() - (256 * g.getScale() - 208 * g.getScale()), (32 * g.getScale()), (32 * g.getScale()), 64,448,32,32);
            g.drawPixmap(Assets.main, ((360 - 224) * g.getScale() + 8 * g.getScale()), g.getHeight() - (256 * g.getScale() - 208 * g.getScale()) + 8 * g.getScale(), (16 * g.getScale()) , (16 * g.getScale()), srcRect.left, srcRect.top, srcRect.right - srcRect.left, srcRect.bottom - srcRect.top);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    private boolean inBounds(TouchEvent event, float x, float y, float width, float height) {
        if(event.x > x && event.x < x + width - 1 &&
                event.y > y && event.y < y + height - 1)
            return true;
        else
            return false;
    }
}