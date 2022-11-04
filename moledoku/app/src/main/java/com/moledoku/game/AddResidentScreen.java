package com.moledoku.game;

import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input.TouchEvent;
import com.moledoku.framework.Screen;

import java.util.List;

// user buys little animals to add to their residence on this screen
public class AddResidentScreen extends Screen {

    static final int PRICE_MULTIPLIER = 75;

    // change price each update for animation
    int priceMinus = 0;

    HomeScreen ret;

    float selectedNumberScale = (172.0f / 9.5f) / 32.0f;
    float buttonScale = (172.0f / 5) / 32.0f;

    int pageNum, pageNumMax = 8;

    public AddResidentScreen(Game game, HomeScreen ret) {
        super(game);
        this.ret = ret;
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();

        // if price animation ongoing, process
        if(priceMinus > 0){
            Settings.coin -= priceMinus/2;
            priceMinus-=priceMinus/2;
            if(priceMinus == 1) {
                priceMinus = 0;
                Settings.coin -= 1;
            }
        }

        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if(event.type == TouchEvent.TOUCH_UP) {
                // back
                if (inBounds(event, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()))) {
                    if(priceMinus > 0){
                        Settings.coin -= priceMinus;
                        priceMinus = 0;
                    }
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
                // Log.d("pageNum","" + pageNum);

            }
            if(event.type == TouchEvent.TOUCH_DOWN && priceMinus <= 0) {
                // buttons
                int amt = 0;
                for(HomeScreen.Resident r : ret.residents){
                    if(r.id % 10 == pageNum) amt++;
                }
                for(int x = 0; x < 4; x++) {
                    for (int y = 0; y < 5; y++) {
                        if (inBounds(event, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()))) {
                            // Log.d("add resident",pageNum + " " + ((y * 4) + x));
                            if (ret.residentsUnlocked[pageNum][x + y * 4] && amt < ret.residentsAmountUnlocked[pageNum] && Settings.coin >= (pageNum + 1) * PRICE_MULTIPLIER * 10 + PRICE_MULTIPLIER * (x + y * 4)) {
                                if (Settings.soundEnabled == 1) Assets.pop.play(1);
                                ret.residents.add(new HomeScreen.Resident(((y * 4) + x) * 10 + pageNum));
                                priceMinus = (pageNum + 1) * PRICE_MULTIPLIER * 10 + PRICE_MULTIPLIER * (x + y * 4);
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

        drawResidentAmount(g);

        drawMoney(g);

    }

    // render buy resident button
    private void drawButtons(Graphics g){
        for(int x = 0; x < 4; x++){
            for(int y = 0; y < 5; y++){
                // if unlocked
                if(ret.residentsUnlocked[pageNum][x + y * 4]) {
                    g.drawPixmap(Assets.main, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 32, 320, 32, 32);
                    g.drawPixmap(Assets.main, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale()) + 4 * g.getScale()), (y * 40 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale() + 4 * g.getScale()), (24 * buttonScale * g.getScale()), (24 * buttonScale * g.getScale()), pageNum * 320, 800 + ((y * 4) + x) * 16, 16, 16);
                    drawPrice(g,(pageNum + 1) * PRICE_MULTIPLIER * 10 + PRICE_MULTIPLIER * (x + y * 4), x, y);
                } else {
                    g.drawPixmap(Assets.main, (x * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())), (y * 40 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), pageNum * 16, 784, 16, 16);
                }
            }
        }
    }

    // render current resident amount and max amount unlocked
    private void drawResidentAmount(Graphics g){
        StringBuilder sb;
        StringBuilder sbamt;
        sb = new StringBuilder();
        sbamt = new StringBuilder();
        int amt = 0;
        for(HomeScreen.Resident r : ret.residents){
            if(r.id % 10 == pageNum) amt++;
        }
        sbamt.append(amt);
        int amtLength = sbamt.toString().length();

        for(int i = 0; i < amtLength; i++){
            Rect src = new Rect();
            sb = new StringBuilder();
            sb.append(sbamt.toString().charAt(i));
            src = Assets.getSmallNumberRect(Integer.parseInt(sb.toString()));
            g.drawPixmap(Assets.main,(0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())) + 12 * i * selectedNumberScale * g.getScale(),
                    (-1 * 40 * buttonScale * g.getScale() + 26 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()),
                    (16 * selectedNumberScale * g.getScale()), (16 * selectedNumberScale * g.getScale()),
                    src.left, src.top, src.right - src.left, src.bottom - src.top);
        }

        g.drawLine((0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())) + 8 * amtLength * selectedNumberScale * g.getScale() + 16 * selectedNumberScale * g.getScale(),
                (-1 * 40 * buttonScale * g.getScale() + 26 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()),
                (0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())) + 8 * amtLength * selectedNumberScale * g.getScale() + 8 * selectedNumberScale * g.getScale(),
                (-1 * 40 * buttonScale * g.getScale() + 26 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()) + 16 * selectedNumberScale * g.getScale(),
                Color.rgb(45, 23, 7), 2 * g.getScale());

        StringBuilder sb2;
        StringBuilder sbamt2;
        sb2 = new StringBuilder();
        sbamt2 = new StringBuilder();
        sbamt2.append(ret.residentsAmountUnlocked[pageNum]);
        int amtLength2 = sbamt2.toString().length();

        for(int i = 0; i < amtLength2; i++){
            Rect src = new Rect();
            sb2 = new StringBuilder();
            sb2.append(sbamt2.toString().charAt(i));
            src = Assets.getSmallNumberRect(Integer.parseInt(sb2.toString()));
            g.drawPixmap(Assets.main,(0 * 40 * buttonScale * g.getScale() + (g.getWidth() / 2.0f - (4 * 32 + 3 * 8) / 2.0f * buttonScale * g.getScale())) + 12 * (i + amtLength + 1) * selectedNumberScale * g.getScale(),
                    (-1 * 40 * buttonScale * g.getScale() + 26 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()),
                    (16 * selectedNumberScale * g.getScale()), (16 * selectedNumberScale * g.getScale()),
                    src.left, src.top, src.right - src.left, src.bottom - src.top);
        }
    }

    // render price to add resident
    private void drawPrice(Graphics g, int amt, int x, int y){
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
                    (y * 40 * buttonScale * g.getScale() + 26 * buttonScale * g.getScale() + 48 * buttonScale * g.getScale()),
                    (16 * selectedNumberScale * g.getScale()), (16 * selectedNumberScale * g.getScale()),
                    src.left, src.top, src.right - src.left, src.bottom - src.top);
        }
    }

    // render money amount
    private void drawMoney(Graphics g){
        StringBuilder sb;
        StringBuilder sbmoney;
        sb = new StringBuilder();
        sbmoney = new StringBuilder();
        sbmoney.append(Settings.coin);
        int moneyLength = sbmoney.toString().length();
        float offset = (76.0f * selectedNumberScale * g.getScale());
        g.drawPixmap(Assets.main,offset + 8 * g.getScale() + 32 * -1 * selectedNumberScale * g.getScale(),
                ((8) * g.getScale()),
                (33 * selectedNumberScale * g.getScale()), (33 * selectedNumberScale * g.getScale()),
                0, 16, 16, 16);
        for(int i = 0; i < moneyLength; i++){
            Rect src = new Rect();
            sb = new StringBuilder();
            sb.append(sbmoney.toString().charAt(i));
            src = Assets.getCoinNumberRect(Integer.parseInt(sb.toString()));
            g.drawPixmap(Assets.main,offset + 8 * g.getScale() + 32 * i * selectedNumberScale * g.getScale(),
                    ((8) * g.getScale()),
                    (33 * selectedNumberScale * g.getScale()), (33 * selectedNumberScale * g.getScale()),
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
}