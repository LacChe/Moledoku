package com.moledoku.game;

import android.graphics.Color;
import android.graphics.Rect;

import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input.TouchEvent;
import com.moledoku.framework.Screen;
import com.moledoku.framework.impl.AndroidGame;

import java.util.List;

// user selects their achievement page background here, as long as it is unlocked
public class SelectLocationScreen extends Screen {

    HomeScreen ret;

    float buttonScale = (172.0f / 4) / 32.0f;

    public SelectLocationScreen(Game game, HomeScreen ret) {
        super(game);
        this.ret = ret;
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
                    game.setScreen(ret);
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
                // buttons
                for(int x = 0; x < 3; x++){
                    for(int y = 0; y < 2; y++){
                        if(inBounds(event, x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale()), (y * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()))) {
                            if(ret.locationsUnlocked[x + y * 3]){
                                ret.location = x + y * 3 + 3;
                                if (Settings.soundEnabled == 1) Assets.pop.play(1);
                            }
                            return;
                        }
                    }
                }
                if(inBounds(event, 0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (2 * 32 + 8) / 2.0f * buttonScale * g.getScale()), (2 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()))) {
                    if(ret.locationsUnlocked[6]) {
                        ret.location = 9;
                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    }
                    return;
                }if(inBounds(event, 1 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (2 * 32 + 8) / 2.0f * buttonScale * g.getScale()), (2 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()))) {
                    if(ret.locationsUnlocked[7]) {
                        ret.location = 10;
                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, g.getWidth(), g.getHeight(), Color.rgb(160, 80, 27));
        // draw back button
        g.drawPixmap(Assets.main, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 0, 0, 16, 16);

        drawButtons(g);

    }

    private void drawButtons(Graphics g){
        // render unlocked locations, with black button for locked ones
        for(int x = 0; x < 3; x++){
            for(int y = 0; y < 2; y++){
                if(ret.locationsUnlocked[x + y * 3]) g.drawPixmap(Assets.main, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0,448 + (x + y * 3) * 32,32,32);
                else g.drawPixmap(Assets.main, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()),32,448,32,32);
            }
        }
        if(ret.locationsUnlocked[6]) g.drawPixmap(Assets.main, (0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (2 * 32 + 8) / 2.0f * buttonScale * g.getScale())), (2 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0,448 + (6) * 32,32,32);
        else g.drawPixmap(Assets.main, (0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (2 * 32 + 8) / 2.0f * buttonScale * g.getScale())), (2 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()),32,448,32,32);

        if(ret.locationsUnlocked[7]) g.drawPixmap(Assets.main, (1 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (2 * 32 + 8) / 2.0f * buttonScale * g.getScale())), (2  * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0,448 + (7) * 32,32,32);
        else g.drawPixmap(Assets.main, (1 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (2 * 32 + 8) / 2.0f * buttonScale * g.getScale())), (2 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()),32,448,32,32);

        switch(ret.location){
            case 3:
                g.drawPixmap(Assets.main, (0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale())), (0 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                break;
            case 4:
                g.drawPixmap(Assets.main, (1 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale())), (0 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                break;
            case 5:
                g.drawPixmap(Assets.main, (2 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale())), (0 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                break;
            case 6:
                g.drawPixmap(Assets.main, (0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale())), (1 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                break;
            case 7:
                g.drawPixmap(Assets.main, (1 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale())), (1 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                break;
            case 8:
                g.drawPixmap(Assets.main, (2 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (3 * 32 + 2 * 8) / 2.0f * buttonScale * g.getScale())), (1 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                break;
            case 9:
                g.drawPixmap(Assets.main, (0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (2 * 32 + 8) / 2.0f * buttonScale * g.getScale())), (2 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                break;
            case 10:
                g.drawPixmap(Assets.main, (1 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (2 * 32 + 8) / 2.0f * buttonScale * g.getScale())), (2 * 40 * buttonScale * g.getScale() + 68 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                break;
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