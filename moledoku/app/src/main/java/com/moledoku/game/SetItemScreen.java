package com.moledoku.game;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input.TouchEvent;
import com.moledoku.framework.Screen;
import com.moledoku.framework.impl.AndroidGame;

import java.util.List;

// user sets the selected reward item on this screen
public class SetItemScreen extends Screen {

    HomeScreen ret;

    float selectedNumberScale = (172.0f / 9.5f) / 32.0f;
    float buttonScale = (172.0f / 5) / 32.0f;

    int x, y;
    int pageNum, pageNumMax = 10;

    public SetItemScreen(Game game, int x, int y, HomeScreen ret) {
        super(game);
        this.x = x;
        this.y = y;
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
            if(event.type == TouchEvent.TOUCH_DOWN) {
                // buttons
                for(int x = 0; x < 4; x++) {
                    for (int y = 0; y < 5; y++) {
                        if (inBounds(event, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()))) {
                            if (Settings.soundEnabled == 1) Assets.pop.play(1);
                            if (ret.itemsOwned[pageNum * 4 * 5 + 4 * y + x] != -1) {
                                // Log.d("set item",HomeScreen.itemIds[this.x][this.y] + " -> " + (pageNum * 4 * 5 + 4 * y + x));
                                if (ret.itemIds[this.x][this.y] == pageNum * 4 * 5 + 4 * y + x) {
                                    ret.itemsOwned[pageNum * 4 * 5 + 4 * y + x]++;
                                    ret.itemIds[this.x][this.y] = -1;
                                } else {
                                    if (ret.itemsOwned[pageNum * 4 * 5 + 4 * y + x] > 0) {
                                        if(ret.itemIds[this.x][this.y] != -1) ret.itemsOwned[ret.itemIds[this.x][this.y]]++;
                                        ret.itemsOwned[pageNum * 4 * 5 + 4 * y + x]--;
                                        ret.itemIds[this.x][this.y] = pageNum * 4 * 5 + 4 * y + x;
                                    }
                                }
                                return;
                            }
                        }
                    }
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
        // draw last button
        g.drawPixmap(Assets.main, (10 * g.getScale()), (g.getHeight() - 16 * g.getScale() - 10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 0, 0, 16, 16);
        // draw next button
        g.drawPixmap(Assets.main, (g.getWidth() - 16 * g.getScale() - 10 * g.getScale()), (g.getHeight() - 16 * g.getScale() - 10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 16, 0, 16, 16);

        drawButtons(g);

    }

    // render item selection button
    private void drawButtons(Graphics g){
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 5; y++){
                g.drawPixmap(Assets.main, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 32, 320, 32, 32);
                g.drawPixmap(Assets.main, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale() + 4 * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale() + 4 * buttonScale * g.getScale()), (24 * buttonScale * g.getScale()), (24 * buttonScale * g.getScale()), pageNum * 128 + x * 32 + (ret.itemsOwned[pageNum * 4 * 5 + 4 * y + x] != -1 ? 0 : 16), 704 + y * 16, 16, 16);
                if(ret.itemIds[this.x][this.y] == pageNum * 4 * 5 + 4 * y + x)
                    g.drawPixmap(Assets.main, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
                if(ret.itemsOwned[pageNum * 4 * 5 + 4 * y + x] != -1)
                    drawAmount(g, ret.itemsOwned[pageNum * 4 * 5 + 4 * y + x], x, y);
            }
        }
    }

    // render how much of this item is owned
    private void drawAmount(Graphics g, int amt, int x, int y){
        StringBuilder sb;
        StringBuilder sbamt;
        sb = new StringBuilder();
        sbamt = new StringBuilder();
        sbamt.append(amt);
        int amtLength = sbamt.toString().length();

        for(int i = 0; i < amtLength; i++){
            Rect src = new Rect();
            sb = new StringBuilder();
            sb.append(sbamt.toString().charAt(i));
            src = Assets.getCoinNumberRect(Integer.parseInt(sb.toString()));
            g.drawPixmap(Assets.main,(x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())) + 12 * i * selectedNumberScale * g.getScale(),
                    (y * 40 * buttonScale * g.getScale() + 16 * buttonScale * g.getScale() + 56 * buttonScale * g.getScale()),
                    (16 * selectedNumberScale * g.getScale()), (16 * selectedNumberScale * g.getScale()),
                    src.left, src.top, src.right - src.left, src.bottom - src.top);
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

    public static Rect getItemRect(int id, HomeScreen ret){
        int pageNumS = id / 20;
        int xS = (id % 20) % 4;
        int yS = (id % 20) / 4;
        return new Rect(pageNumS * 128 + xS * 32 + (ret.itemsOwned[pageNumS * 4 * 5 + 4 * yS + xS] != -1 ? 0 : 16),704 + yS * 16,pageNumS * 128 + xS * 32 + (ret.itemsOwned[pageNumS * 4 * 5 + 4 * yS + xS] != -1 ? 0 : 16) + 16,704 + yS * 16 + 16);
    }
}