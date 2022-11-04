package com.moledoku.game;

import android.graphics.Color;
import android.graphics.Rect;

import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input.TouchEvent;
import com.moledoku.framework.Screen;

import java.util.List;

// this screen flips though the help pages
public class HelpScreen extends Screen {

    int pageNum, pageNumMax = 13;

    public HelpScreen(Game game) {
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
                if (inBounds(event, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()))) {
                    game.setScreen(new MainMenuScreen(game));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
                // last
                if (inBounds(event, (10 * g.getScale()), (g.getHeight() - 16 * g.getScale() - 10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()))) {
                    pageNum--;
                    if(pageNum < 0) pageNum = 0;
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
                // next
                if (inBounds(event, (g.getWidth() - 16 * g.getScale() - 10 * g.getScale()), (g.getHeight() - 16 * g.getScale() - 10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()))) {
                    pageNum++;
                    if(pageNum >= pageNumMax) pageNum = pageNumMax-1;
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, g.getWidth(), g.getHeight(), Color.rgb(133, 208, 226));

        // center
        g.drawRect(0, 0, g.getWidth(), g.getHeight(), Color.rgb(160, 139, 112));

        // sides
        //g.drawPixmap(Assets.main, 0, 0, g.getWidth(), 12 * g.getScale(), 428, 0, 8, 12);
        //g.drawPixmap(Assets.main, 0, g.getHeight() - 12 * g.getScale(), g.getWidth(), 12 * g.getScale(), 428, 20, 8, 12);

        //g.drawPixmap(Assets.main, 0, 0, 12 * g.getScale(), g.getHeight(), 416, 12, 12, 8);
        //g.drawPixmap(Assets.main, g.getWidth() - 12 * g.getScale(), 0, 12 * g.getScale(), g.getHeight(), 416 + 20, 12, 12, 8);

        // corners
        //g.drawPixmap(Assets.main, 0, 0, 12 * g.getScale(), 12 * g.getScale(), 416, 0, 12, 12);
        //g.drawPixmap(Assets.main, g.getWidth() - 12 * g.getScale(), 0, 12 * g.getScale(), 12 * g.getScale(), 416 + 20, 0, 12, 12);
        //g.drawPixmap(Assets.main, 0, g.getHeight() - 12 * g.getScale(), 12 * g.getScale(), 12 * g.getScale(), 416, 20, 12, 12);
        //g.drawPixmap(Assets.main, g.getWidth() - 12 * g.getScale(), g.getHeight() - 12 * g.getScale(), 12 * g.getScale(), 12 * g.getScale(), 416 + 20, 20, 12, 12);

        // draw help
        g.drawPixmap(Assets.main, 0, g.getHeight() - (320 * g.getScale()), (192 * g.getScale()) , (256 * g.getScale()), 448 + pageNum * 192, 0, 192, 256);

        // draw back button
        g.drawPixmap(Assets.main, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 0, 0, 16, 16);
        // draw last button
        g.drawPixmap(Assets.main, (10 * g.getScale()), (g.getHeight() - 16 * g.getScale() - 10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 0, 0, 16, 16);
        // draw next button
        g.drawPixmap(Assets.main, (g.getWidth() - 16 * g.getScale() - 10 * g.getScale()), (g.getHeight() - 16 * g.getScale() - 10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 16, 0, 16, 16);

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