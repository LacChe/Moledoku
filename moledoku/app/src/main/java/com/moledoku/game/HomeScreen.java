package com.moledoku.game;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input.TouchEvent;
import com.moledoku.framework.Screen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

// this is where the user sees their residence and can display their achievements
public class HomeScreen extends Screen {

    public static HomeScreen screen;

    public static final int SIZE = 20;
    public static final int ITEM_MAX = 200;
    static final int FILL_COST = 10;
    static final int DIG_COST = 100;

    boolean buttonHeld = false;
    int downX = -1, downY = -1;
    int upX = -1, upY = -1;
    int scrollDownX = 0, scrollDownCurrent = 0;

    int buttonSelected = 5;
    int toggleWall = 1;
    int toggleDig = 0;

    float selectedNumberScale = (172.0f / 9.5f) / 32.0f;
    float buttonScale = (172.0f / 6.0f) / 32.0f;
    float boardScale = (172.0f / 6.0f) / 32.0f;

    // rect object to help garbage collection
    RectF clickCheckRect = new RectF(0,0,0,0);
    Rect resSrcRect = new Rect(0,0,0,0);

    float boardOffX;
    float boardOffY;

    public boolean[][][] residenceGrid;
    public int location = 3;
    public boolean[] locationsUnlocked = new boolean[8];
    public int[][] itemIds = new int[SIZE][6];
    public int[] itemsOwned = new int[ITEM_MAX];
    public List<Resident> residents = new ArrayList<Resident>();
    public boolean[][] residentsUnlocked = new boolean[8][20];
    public int[] residentsAmountUnlocked = new int[10];

    public HomeScreen(Game game) {
        super(game);

        Graphics g = game.getGraphics();
        boardOffX = ((SIZE / 2.0f - 3) * -32 * boardScale * g.getScale()) + (8 * g.getScale());
        boardOffY = g.getHeight() - (224 * g.getScale()) + (8 * g.getScale());

        location = 3;
        locationsUnlocked = new boolean[]{true, false, false, false, false, false, false, false};
        itemIds = new int[SIZE][6];
        for (int x = 0; x < HomeScreen.SIZE; x++) {
            for (int y = 0; y < 6; y++) {
                itemIds[x][y] = -1;
            }
        }
        itemsOwned = new int[ITEM_MAX];
        for(int x = 0; x < ITEM_MAX; x++) {
            itemsOwned[x] = -1;
        }
        residents = new ArrayList<Resident>();
        // easy adjustments for testing
        residentsUnlocked = new boolean[][]{
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, },
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, },
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, },
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, },
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, },
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, },
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, },
                {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, },
        };
        residentsAmountUnlocked = new int[]{0,0,0,0,0,0,0,0,0,0};
        // last for save check
        residenceGrid = new boolean[SIZE][6][9];

        residenceGrid[SIZE / 2][0] = new boolean[]{true, false, false, false, false, false, false, true, false};
        residenceGrid[SIZE / 2 - 1][0] = new boolean[]{true, false, false, true, false, true, false, false, false};
        residenceGrid[SIZE / 2 - 1][1] = new boolean[]{true, true, false, false, false, false, false, false, false};

        Settings.homeScreen = this;
        screen = this;
    }

    public HomeScreen(Game game, HomeScreen save) {
        super(game);

        Graphics g = game.getGraphics();
        boardOffX = ((SIZE / 2.0f - 3) * -32 * boardScale * g.getScale()) + (8 * g.getScale());
        boardOffY = g.getHeight() - (224 * g.getScale()) + (8 * g.getScale());

        this.location = save.location;
        this.locationsUnlocked = save.locationsUnlocked;
        this.itemIds = save.itemIds;
        this.itemsOwned = save.itemsOwned;
        this.residents = save.residents;
        this.residentsUnlocked = save.residentsUnlocked;
        this.residentsAmountUnlocked = save.residentsAmountUnlocked;
        // this is set last as file saver uses this to check for completion
        this.residenceGrid = save.residenceGrid;

        Settings.homeScreen = this;
        screen = this;
    }

    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();

        for(Resident r : residents){
            r.update(deltaTime, residenceGrid);
        }

        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        game.getInput().getKeyEvents();
        int len = touchEvents.size();

        for(int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            // back
            if (inBounds(event, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()))) {
                if (event.type == TouchEvent.TOUCH_UP) {
                    Settings.homeScreen = this;
                    game.setScreen(new MainMenuScreen(game));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
            }

            if (event.type == TouchEvent.TOUCH_UP) {
                float buttonOffX = (8 * g.getScale());
                float buttonOffY = g.getHeight() - (256 * g.getScale()) + (8 * g.getScale());
                float midButtonOffset = g.getWidth() / 2.0f - (34.0f * (6.5f) * buttonScale * g.getScale()) / 2.0f;

                clickCheckRect.left = midButtonOffset + buttonOffX + ((2 * g.getScale() + 34 * 0 * buttonScale * g.getScale()));
                clickCheckRect.top = ((176 + 16) * g.getScale()) + buttonOffY + 32 * selectedNumberScale * g.getScale();
                clickCheckRect.right = clickCheckRect.left + (32 * buttonScale * g.getScale());
                clickCheckRect.bottom = clickCheckRect.top + (32 * buttonScale * g.getScale());
                if (clickCheckRect.contains(event.x, event.y)) {
                    game.setScreen(new SelectLocationScreen(game, this));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }

                clickCheckRect.left = midButtonOffset + buttonOffX + ((2 * g.getScale() + 34 * 1 * buttonScale * g.getScale()));
                clickCheckRect.top = ((176 + 16) * g.getScale()) + buttonOffY + 32 * selectedNumberScale * g.getScale();
                clickCheckRect.right = clickCheckRect.left + (32 * buttonScale * g.getScale());
                clickCheckRect.bottom = clickCheckRect.top + (32 * buttonScale * g.getScale());
                if (clickCheckRect.contains(event.x, event.y)) {
                    game.setScreen(new AddResidentScreen(game, this));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    return;
                }

                for (int offI = 1; offI < 6; offI++) {
                    clickCheckRect.left = midButtonOffset + buttonOffX + ((2 * g.getScale() + 34 * offI * buttonScale * g.getScale()));
                    clickCheckRect.top = ((176 + 16) * g.getScale()) + buttonOffY + 32 * selectedNumberScale * g.getScale();
                    clickCheckRect.right = clickCheckRect.left + (32 * buttonScale * g.getScale());
                    clickCheckRect.bottom = clickCheckRect.top + (32 * buttonScale * g.getScale());
                    if (clickCheckRect.contains(event.x, event.y)) {
                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                        if (buttonSelected == offI) {
                            if (buttonSelected == 3) {
                                toggleWall = ((toggleWall == 1) ? 0 : 1);
                                return;
                            }
                            if (buttonSelected == 4) {
                                toggleDig = ((toggleDig == 1) ? 0 : 1);
                                return;
                            }
                        }
                        buttonSelected = offI;
                        return;
                    }
                }
            }

            // manipulate residence
            if(inBounds(event, (8 * g.getScale()), g.getHeight() - (224 * g.getScale()) + (8 * g.getScale()), (168 * g.getScale()), (176 * g.getScale()))) {
                for(int x = 0; x < residenceGrid.length; x++){
                    for(int y = 0; y < 6; y++){
                        clickCheckRect.left = boardOffX + ((x * 32 * boardScale * g.getScale()));
                        clickCheckRect.top = boardOffY + ((y * 32 * boardScale * g.getScale()));
                        clickCheckRect.right = boardOffX + ((x * 32 * boardScale * g.getScale())) + (32 * boardScale * g.getScale());
                        clickCheckRect.bottom = boardOffY + ((y * 32 * boardScale * g.getScale())) + (32 * boardScale * g.getScale());
                        if(clickCheckRect.contains(event.x, event.y)){
                            switch(buttonSelected){
                                case 2:
                                    // item setting
                                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                                    if(residenceGrid[x][y][0]) game.setScreen(new SetItemScreen(game, x, y, this));
                                    return;
                                case 3:
                                    // wall hall
                                    if(event.type == TouchEvent.TOUCH_DOWN) {
                                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                                        downX = x;
                                        downY = y;
                                        buttonHeld = true;
                                    }
                                    if(event.type == TouchEvent.TOUCH_UP) {
                                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                                        upX = x;
                                        upY = y;
                                        buttonHeld = false;
                                        // check all opened
                                        if(residenceGrid[upX][upY][0] && residenceGrid[downX][downY][0]) {
                                            if (upX == downX) {
                                                if (upY == downY - 1) {
                                                    residenceGrid[upX][upY][5] = (toggleWall == 0);
                                                    residenceGrid[downX][downY][1] = (toggleWall == 0);
                                                }
                                                if (upY == downY + 1) {
                                                    residenceGrid[upX][upY][1] = (toggleWall == 0);
                                                    residenceGrid[downX][downY][5] = (toggleWall == 0);
                                                }
                                            }
                                            if (upY == downY) {
                                                if (upX == downX - 1) {
                                                    residenceGrid[upX][upY][3] = (toggleWall == 0);
                                                    residenceGrid[downX][downY][7] = (toggleWall == 0);
                                                }
                                                if (upX == downX + 1) {
                                                    residenceGrid[upX][upY][7] = (toggleWall == 0);
                                                    residenceGrid[downX][downY][3] = (toggleWall == 0);
                                                }
                                            }
                                        }
                                        // reset
                                        downX = -1;
                                        downY = -1;
                                        upX = -1;
                                        upY = -1;
                                    }
                                    break;
                                case 4:
                                    if(Settings.TRIAL_VERSION) break;
                                    // dig fill
                                    if(event.type == TouchEvent.TOUCH_DOWN) {
                                        if((Settings.coin >= DIG_COST && toggleDig == 1) || (Settings.coin >= FILL_COST && toggleDig == 0)) {
                                            if (Settings.soundEnabled == 1) Assets.pop.play(1);
                                            if(toggleDig == 1 && !residenceGrid[x][y][0]) Settings.coin -= DIG_COST;
                                            if(toggleDig == 0 && residenceGrid[x][y][0]) Settings.coin -= FILL_COST;
                                            residenceGrid[x][y][0] = (toggleDig == 1);
                                            if(itemIds[x][y] >= 0){
                                                itemsOwned[itemIds[x][y]]++;
                                                itemIds[x][y] = -1;
                                            }
                                            if (!residenceGrid[x][y][0]) {
                                                residenceGrid[x][y] = new boolean[]{false, false, false, false, false, false, false, false, false,};
                                                if (x - 1 >= 0) residenceGrid[x - 1][y][3] = false;
                                                if (x + 1 < SIZE)
                                                    residenceGrid[x + 1][y][7] = false;
                                                if (y - 1 >= 0) residenceGrid[x][y - 1][5] = false;
                                                if (y + 1 < 6) residenceGrid[x][y + 1][1] = false;
                                            }
                                        }
                                    }
                                    break;
                                case 5:
                                    // scroll
                                    if(event.type == TouchEvent.TOUCH_DOWN) {
                                        scrollDownX = event.x;
                                        scrollDownCurrent = event.x;
                                        buttonHeld = true;
                                    }
                                    if(event.type == TouchEvent.TOUCH_UP) {
                                        // Log.d("offSet check",boardOffX + ((buttonHeld && (buttonSelected) == 5) ? (scrollDownCurrent - scrollDownX) : 0) + " ");
                                        boardOffX += (scrollDownCurrent - scrollDownX);
                                        if(boardOffX + (scrollDownCurrent - scrollDownX) > 8 * g.getScale()) boardOffX = 8 * g.getScale();
                                        if(boardOffX + (scrollDownCurrent - scrollDownX) < -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale()))) boardOffX = -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale()));
                                        buttonHeld = false;

                                        // reset
                                        scrollDownCurrent = 0;
                                        scrollDownX = 0;
                                    }
                                    if(buttonHeld && event.type == TouchEvent.TOUCH_DRAGGED) {
                                        if((boardOffX + (event.x - scrollDownX) < 8 * g.getScale()) && (boardOffX + (event.x - scrollDownX) > -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale())))) scrollDownCurrent = event.x;
                                    }
                                    break;
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
        // draw background
        g.drawRect(0, 0, g.getWidth(), g.getHeight(), getBackgroundColor());


        // buttons
        drawButtons(g);
        // draw grid
        drawBoard(g, boardOffX, boardOffY);
        // draw top
        drawTop(g, boardOffX, boardOffY);

        // draw items
        drawItems(g, boardOffX, boardOffY);
        // draw residents
        drawResidents(g);

        // money
        drawMoney(g);

        // crop
        g.drawRect(0, g.getHeight() - (224 * g.getScale()) + (8 * g.getScale()), (8 * g.getScale()), (176 * g.getScale()), getBackgroundColor());
        g.drawRect((8 * g.getScale()) + (176 * g.getScale()), g.getHeight() - (224 * g.getScale()) + (8 * g.getScale()), (8 * g.getScale()), (176 * g.getScale()), getBackgroundColor());

        // draw back button
        g.drawPixmap(Assets.main, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 0, 0, 16, 16);
    }

    // render top bar graphics
    private void drawTop(Graphics g, float boardOffX, float boardOffY){
        // fade
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        float percent;
        percent = minute + hour * 60;
        percent /= (12.0f * 60.0f);
        if(cal.get(Calendar.AM_PM) == Calendar.PM){
            percent = 1.0f - percent;
        }

        float fadePercent;
        fadePercent = percent * 1.2f;
        if(fadePercent < 0.2) fadePercent = 0.2f;
        if(fadePercent > 1) fadePercent = 1.0f;
        // Log.d("time",minute + " " + hour + " " + percent);

        // orbit
        int moon = 1;
        hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour >= 6 && hour < 18){
            hour -= 6;
            moon = 0;
        } else if(hour < 6){
            hour += 6;
        }else if(hour >= 18){
            hour -= 18;
        }
        minute = cal.get(Calendar.MINUTE);
        percent = minute + hour * 60;
        percent /= (12.0f * 60.0f);
        if(cal.get(Calendar.AM_PM) == Calendar.PM){
            percent = 1.0f - percent;
        }

        float orbitPercent;
        orbitPercent = percent;

        switch(location){
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
                g.drawRect(0, 0, 192 * g.getScale(), boardOffY - 8 * g.getScale(), Color.rgb((int)(124 * fadePercent), (int)(174 * fadePercent), (int)(255 * fadePercent)));
                g.drawPixmap(Assets.main,-30 * g.getScale() + ((g.getWidth() + 30 * g.getScale()) * orbitPercent), 36 * g.getScale(), 20 * g.getScale(), 20 * g.getScale(),
                        64, 176 + moon * 20, 20, 20);
                break;
            case 6:
                g.drawRect(0, 0, 192 * g.getScale(), boardOffY - 8 * g.getScale(), Color.rgb(0, 40, 4));
                break;
        }
        g.drawPixmap(Assets.main,0, boardOffY - 72 * g.getScale(), 192 * g.getScale(), 64 * g.getScale(),
                (location - 3) * 192, 1120, 192, 64);
    }

    // draw little animals
    private void drawResidents(Graphics g){
        for(Resident r : residents){
            resSrcRect = r.getSrcRect();

            float tempOffsetX = (scrollDownCurrent - scrollDownX);
            if(boardOffX + (scrollDownCurrent - scrollDownX) > 8 * g.getScale()) tempOffsetX = 8 * g.getScale();
            if(boardOffX + (scrollDownCurrent - scrollDownX) < -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale()))) tempOffsetX = -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale()));

            g.drawPixmap(Assets.main, (r.x * boardScale * g.getScale() + boardOffX + ((buttonHeld && (buttonSelected) == 5) ? tempOffsetX : 0)), ((r.y) * boardScale * g.getScale() + boardOffY), (16 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), resSrcRect.left, resSrcRect.top, resSrcRect.right - resSrcRect.left, resSrcRect.bottom - resSrcRect.top);
        }
    }

    // draw reward items
    private void drawItems(Graphics g, float boardOffX, float boardOffY){
        for(int x = 0; x < SIZE; x++){
            for(int y = 0; y < 6; y++){
                // draw boxBackground
                float tempOffsetX = (scrollDownCurrent - scrollDownX);
                if(boardOffX + (scrollDownCurrent - scrollDownX) > 8 * g.getScale()) tempOffsetX = 8 * g.getScale();
                if(boardOffX + (scrollDownCurrent - scrollDownX) < -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale()))) tempOffsetX = -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale()));
                if(itemIds[x][y] != -1) drawItem(g, itemIds[x][y],boardOffX + ((buttonHeld && (buttonSelected) == 5) ? tempOffsetX : 0) + ((2 * g.getScale() + x * 32 * boardScale * g.getScale())), boardOffY + ((2 * g.getScale() + y * 32 * boardScale * g.getScale())), boardScale);
            }
        }
    }

    // draw individual reward items
    private void drawItem(Graphics g, int id, float x, float y, float boardScale) {
        Rect src = SetItemScreen.getItemRect(id, this);
        g.drawPixmap(Assets.main, (x + 8 * boardScale * g.getScale()), (y + 8 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), src.left, src.top, src.right - src.left, src.bottom - src.top);
    }

    // draw money graphics
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

    private void drawButtons(Graphics g){
        float boardOffX = (8 * g.getScale());
        float boardOffY = g.getHeight() - (256 * g.getScale()) + (8 * g.getScale());
        float midOffset = g.getWidth() / 2.0f - (34.0f * (6.5f) * buttonScale * g.getScale()) / 2.0f;

        int i = 0;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 336, 16, 16);
        i = 1;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 336 + 16, 16, 16);
        i = 2;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 336 + 2 * 16, 16, 16);
        i = 3;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), toggleWall == 1 ? 0 : 16, 336 + 3 * 16, 16, 16);
        i = 4;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), toggleDig == 1 ? 0 : 16, 336 + 4 * 16, 16, 16);
        drawPrice(g, toggleDig == 1 ? 100 : 10, (int)(midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale()))),(int)(((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale() + (24 * buttonScale * g.getScale())));
        i = 5;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 336 + 5 * 16, 16, 16);
        if(buttonSelected >= 2 && buttonSelected <= 5)
            g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * buttonSelected * buttonScale * g.getScale())),
                    ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                    (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
    }

    // draw price for build, dig etc.
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
            g.drawPixmap(Assets.main,(x) + 12 * i * selectedNumberScale * g.getScale(),
                    (y),
                    (16 * selectedNumberScale * g.getScale()), (16 * selectedNumberScale * g.getScale()),
                    src.left, src.top, src.right - src.left, src.bottom - src.top);
        }
    }

    // draw residence grid
    private void drawBoard(Graphics g, float boardOffX, float boardOffY){
        // draw background
        float backOffX = (8 * g.getScale());
        float backOffY = g.getHeight() - (224 * g.getScale()) + (8 * g.getScale());
        g.drawRect(backOffX, backOffY, (176 * g.getScale()), (176 * g.getScale()), getBackgroundColor());
        for(int x = 0; x < SIZE; x++){
            for(int y = 0; y < 6; y++){
                // draw boxBackground
                float tempOff = (scrollDownCurrent - scrollDownX);
                if(boardOffX + (scrollDownCurrent - scrollDownX) > 8 * g.getScale()) tempOff = 8 * g.getScale();
                if(boardOffX + (scrollDownCurrent - scrollDownX) < -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale()))) tempOff = -((SIZE * 32 * boardScale * g.getScale()) - (g.getWidth() - 12 * g.getScale()));
                drawBox(g, x, y, residenceGrid[x][y],boardOffX + ((buttonHeld && (buttonSelected) == 5) ? tempOff : 0) + ((2 * g.getScale() + x * 32 * boardScale * g.getScale())), boardOffY + ((2 * g.getScale() + y * 32 * boardScale * g.getScale())), boardScale);
            }
        }
    }

    // draw dugout box tile graphics
    private void drawBox(Graphics g, int pX, int pY, boolean[] surrounding, float x, float y, float boardScale){
        if(surrounding[0]){
            // draw middle
            // Log.d("rect check", board.getNumber(point) + "");
            g.drawRect((x), (y), (32 * boardScale * g.getScale()), (32 * boardScale * g.getScale()), getForegroundColor());

            // draw 0 -1
            if(surrounding[1]){ // up
                // up open
                g.drawRect((x + 8 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), getForegroundColor());
            } else {
                // up closed
                g.drawPixmap(Assets.main, (x), (y), (32 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1416, 16, 8);
            }

            // draw -1 0
            if(surrounding[7]){ // left
                // left open
                g.drawRect((x - 1 * boardScale * g.getScale()), (y + 8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), getForegroundColor());
            } else {
                // left closed
                g.drawPixmap(Assets.main, (x), (y), (8 * boardScale * g.getScale()), (32 * boardScale * g.getScale()), 8 + 16 * (location - 3), 1424, 8, 16);
            }

            // draw 1 0
            if(surrounding[3]){ // right
                // right open
                g.drawRect((x + 25 * boardScale * g.getScale()), (y + 8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), getForegroundColor());
            } else {
                // right closed
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y), (8 * boardScale * g.getScale()), (32 * boardScale * g.getScale()), 16 * (location - 3), 1424, 8, 16);
            }

            // draw 0 1
            if(surrounding[5]){ // down
                // down open
                g.drawRect((x + 8 * boardScale * g.getScale()), (y + 25 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), getForegroundColor());
            } else {
                // down closed
                g.drawPixmap(Assets.main, (x), (y + 24 * boardScale * g.getScale()), (32 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1408, 16, 8);
            }

            // draw -1 -1
            if((pX - 1 >= 0 && pY - 1 >= 0) &&
                    residenceGrid[pX - 1][pY - 1][3] &&
                    residenceGrid[pX - 1][pY - 1][5] &&
                    residenceGrid[pX - 1][pY][1] &&
                    residenceGrid[pX][pY - 1][7] &&
                    residenceGrid[pX][pY][1] &&
                    residenceGrid[pX][pY][7]) {
                // all open
                g.drawRect((x - 1 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), getForegroundColor());
            } else if(surrounding[1] && // up
                    surrounding[7]){ // left
                // two sides open
                g.drawPixmap(Assets.main, (x), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 8 + 16 * (location - 3), 1400, 8, 8);
            } else if(surrounding[7]){ // left
                // left open
                g.drawPixmap(Assets.main, (x - 1 * boardScale * g.getScale()), (y), (9 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1416, 8, 8);
            } else if(surrounding[1]){ // up
                // up open
                g.drawPixmap(Assets.main, (x), (y - 1 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), 8 + 16 * (location - 3), 1424, 8, 8);
            } else {
                // closed
                g.drawPixmap(Assets.main, (x), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1376, 8, 8);
            }

            // draw 1 -1
            if((pX + 1 < SIZE && pY - 1 >= 0) &&
                    residenceGrid[pX + 1][pY - 1][7] &&
                    residenceGrid[pX + 1][pY - 1][5] &&
                    residenceGrid[pX + 1][pY][1] &&
                    residenceGrid[pX][pY - 1][3] &&
                    residenceGrid[pX][pY][1] &&
                    residenceGrid[pX][pY][3]) {
                // all open
                g.drawRect((x + 24 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), getForegroundColor());
            } else if(surrounding[1] && // up
                    surrounding[3]){ // right
                // two sides open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1400, 8, 8);
            } else if(surrounding[3]){ // right
                // right open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y), (9 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1416, 8, 8);
            } else if(surrounding[1]){ // up
                // up open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), 16 * (location - 3), 1424, 8, 8);
            } else {
                // closed
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 8 + 16 * (location - 3), 1376, 8, 8);
            }

            // draw -1 1
            if((pX - 1 >= 0 && pY + 1 < 6) &&
                    residenceGrid[pX - 1][pY + 1][1] &&
                    residenceGrid[pX - 1][pY + 1][3] &&
                    residenceGrid[pX - 1][pY][5] &&
                    residenceGrid[pX][pY + 1][7] &&
                    residenceGrid[pX][pY][5] &&
                    residenceGrid[pX][pY][7]) {
                // all open
                g.drawRect((x - 1 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), getForegroundColor());
            } else if(surrounding[5] && // down
                    surrounding[7]){ // left
                // two sides open
                g.drawPixmap(Assets.main, (x), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 8 + 16 * (location - 3), 1392, 8, 8);
            } else if(surrounding[7]){ // left
                // left open
                g.drawPixmap(Assets.main, (x - 1 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1408, 8, 8);
            } else if(surrounding[5]){ // down
                // down open
                g.drawPixmap(Assets.main, (x), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), 8 + 16 * (location - 3), 1424, 8, 8);
            } else {
                // closed
                g.drawPixmap(Assets.main, (x), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1384, 8, 8);
            }

            // draw 1 1
            if((pX + 1 < SIZE && pY + 1 < 6) &&
                    residenceGrid[pX + 1][pY + 1][1] &&
                    residenceGrid[pX + 1][pY + 1][7] &&
                    residenceGrid[pX + 1][pY][5] &&
                    residenceGrid[pX][pY + 1][3] &&
                    residenceGrid[pX][pY][5] &&
                    residenceGrid[pX][pY][3]) {
                // all open
                g.drawRect((x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), getForegroundColor());
            } else if(surrounding[5] && // down
                    surrounding[3]){ // right
                // two sides open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1392, 8, 8);
            } else if(surrounding[3]){ // right
                // right open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (location - 3), 1408, 8, 8);
            } else if(surrounding[5]){ // down
                // down open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), 16 * (location - 3), 1424, 8, 8);
            } else {
                // closed
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 8 + 16 * (location - 3), 1384, 8, 8);
            }
        }
    }

    private int getBackgroundColor(){
        switch(location){
            case 3:
                return Color.rgb(99,50,19);
            case 4:
                return Color.rgb(135,69,59);
            case 5:
                return Color.rgb(122,62,20);
            case 6:
                return Color.rgb(99,55,94);
            case 7:
                return Color.rgb(76,76,76);
            case 8:
                return Color.rgb(99,80,54);
            case 9:
                return Color.rgb(83,134,158);
            case 10:
                return Color.rgb(146,168,168);
        }
        return 0;
    }

    private int getForegroundColor(){
        switch(location) {
            case 3:
                return Color.rgb(168,106,67);
            case 4:
                return Color.rgb(198,102,87);
            case 5:
                return Color.rgb(195,127,32);
            case 6:
                return Color.rgb(173,81,162);
            case 7:
                return Color.rgb(112,165,51);
            case 8:
                return Color.rgb(160,139,112);
            case 9:
                return Color.rgb(162,215,239);
            case 10:
                return Color.rgb(213,230,237);
        }
        return 0;
    }

    public void drawText(Graphics g, String line, int x, int y) {
        int len = line.length();
        for (int i = 0; i < len; i++) {
            char character = line.charAt(i);
            if (character == ' ') {
                x += 20;
                continue;
            }
            int srcX = 0;
            int srcWidth = 0;
            if (character == '.') {
                srcX = 200;
                srcWidth = 10;
            } else {
                srcX = (character - '0') * 20;
                srcWidth = 20;
            }
            // g.drawPixmap(Assets.numbers, x, y, srcX, 0, srcWidth, 32);
            x += srcWidth;
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

    // little animal class
    public static class Resident {
        final static int SPEED = 50;
        final static int MOVE_DURATION_DEFAULT = 5000;

        Random rand;

        int id;
        float x, y;
        int currentMoveDuration = 0;
        int moveDuration = MOVE_DURATION_DEFAULT;

        float srcRectOffset = 0;
        int srcRectMoveOffset = 0;

        //   1
        // 4 0 2
        //   3
        int facingDir = 2;

        public Resident(int id, int x, int y){
            this.id = id;
            this.x = x;
            this.y = y;
            rand = new Random();
        }

        public Resident(int id){
            this.id = id;
            this.x = (SIZE / 2.0f - 1) * 32 + 10;
            this.y = 10;
            rand = new Random();
        }

        public void update(float deltaTime, boolean[][][] residenceGrid) {
            // reset if stuck
            boolean stuckUL = checkCollide(-4, -4, residenceGrid);
            boolean stuckUR = checkCollide(20, -4, residenceGrid);
            boolean stuckDL = checkCollide(-4, 20, residenceGrid);
            boolean stuckDR = checkCollide(20, 20, residenceGrid);
            if (stuckUL || stuckUR || stuckDL || stuckDR) {
                x = (SIZE / 2.0f - 1) * 32 + 10;
                this.y = 10;
            }

            // move
            // new dir
            for(int s = 0; s < SPEED; s++) {
                currentMoveDuration++;
                // Log.d("move duration",currentMoveDuration + " / " + moveDuration);
                if (currentMoveDuration >= moveDuration) {
                    // Log.d("old dir","" + facingDir);
                    currentMoveDuration = 0;
                    if (facingDir == 0) {
                        facingDir = rand.nextInt(4) + 1;
                        moveDuration = rand.nextInt(MOVE_DURATION_DEFAULT);
                    }
                    else {
                        facingDir = 0;
                        moveDuration = rand.nextInt(MOVE_DURATION_DEFAULT) + MOVE_DURATION_DEFAULT;
                    }
                }
                if (facingDir != 0) {
                    int nextX = 0, nextY = 0;
                    // check collide
                    switch (facingDir) {
                        case 1:
                            nextY = -1;
                            stuckUL = checkCollide(-4, -4 + nextY, residenceGrid);
                            stuckUR = checkCollide(20, -4 + nextY, residenceGrid);
                            stuckDL = checkCollide(-4, 20 + nextY, residenceGrid);
                            stuckDR = checkCollide(20, 20 + nextY, residenceGrid);
                            // check same grid;
                            if((int)((x - 4) / (32.0f)) != (int)((x + 20) / (32.0f))) stuckUL = stuckUR = stuckDL = stuckDR = true;
                            break;
                        case 2:
                            nextX = 1;
                            stuckUL = checkCollide(-4 + nextX, -4, residenceGrid);
                            stuckUR = checkCollide(20 + nextX, -4, residenceGrid);
                            stuckDL = checkCollide(-4 + nextX, 20, residenceGrid);
                            stuckDR = checkCollide(20 + nextX, 20, residenceGrid);
                            // check same grid;
                            if((int)((y - 4) / (32.0f)) != (int)((y + 20) / (32.0f))) stuckUL = stuckUR = stuckDL = stuckDR = true;
                            break;
                        case 3:
                            nextY = 1;
                            stuckUL = checkCollide(-4, -4 + nextY, residenceGrid);
                            stuckUR = checkCollide(20, -4 + nextY, residenceGrid);
                            stuckDL = checkCollide(-4, 20 + nextY, residenceGrid);
                            stuckDR = checkCollide(20, 20 + nextY, residenceGrid);
                            // check same grid;
                            if((int)((x - 4) / (32.0f)) != (int)((x + 20) / (32.0f))) stuckUL = stuckUR = stuckDL = stuckDR = true;
                            break;
                        case 4:
                            nextX = -1;
                            stuckUL = checkCollide(-4 + nextX, -4, residenceGrid);
                            stuckUR = checkCollide(20 + nextX, -4, residenceGrid);
                            stuckDL = checkCollide(-4 + nextX, 20, residenceGrid);
                            stuckDR = checkCollide(20 + nextX, 20, residenceGrid);
                            // check same grid;
                            if((int)((y - 4) / (32.0f)) != (int)((y + 20) / (32.0f))) stuckUL = stuckUR = stuckDL = stuckDR = true;
                            break;
                    }

                    // move if success;
                    if (!stuckUL && !stuckUR && !stuckDL && !stuckDR) {
                        x += nextX * deltaTime;
                        y += nextY * deltaTime;
                    } else {
                        currentMoveDuration = moveDuration;
                    }
                }
            }

            srcRectOffset += 200 * deltaTime;
            if(srcRectOffset >= 400) srcRectOffset = 0;

            switch(facingDir){
                case 0:
                    srcRectMoveOffset = 0;
                    break;
                case 1:
                    srcRectMoveOffset = 64 * 2;
                    break;
                case 2:
                    srcRectMoveOffset = 64 * 4;
                    break;
                case 3:
                    srcRectMoveOffset = 64;
                    break;
                case 4:
                    srcRectMoveOffset = 64 * 3;
                    break;
            }
            //   1
            // 4 0 2
            //   3

        }

        public Rect getSrcRect(){
            Rect srcRect = new Rect();
            srcRect.left = (id % 10) * 320 + (int)(srcRectOffset / 100) * 16 + srcRectMoveOffset;
            srcRect.top = 800 + (id / 10) * 16;
            srcRect.right = srcRect.left + 16;
            srcRect.bottom = srcRect.top + 16;

            return srcRect;
        }

        private boolean checkCollide(float xOff, float yOff, boolean[][][] residenceGrid){
            if((int)((x + xOff) / (32.0f)) < 0 || (x + xOff) < 0) return true;
            if((int)((x + xOff) / (32.0f)) >= SIZE) return true;
            if((int)((y + yOff) / (32.0f)) < 0 || (y + yOff) < 0) return true;
            if((int)((y + yOff) / (32.0f)) >= 6) return true;
            // Log.d("collide check", (int)((x + xOff)) + " " + (int)((y + yOff)) + " | " + !residenceGrid[(int)((x + xOff) / (32.0f))][(int)((y + yOff) / (32.0f))][0]);

            if(!residenceGrid[(int)((x + xOff) / (32.0f))][(int)((y + yOff) / (32.0f))][0]) return true;

            if(xOff == 21) { // going right
                if(!residenceGrid[(int)((x + xOff) / (32.0f))][(int)((y + yOff) / (32.0f))][7] && !residenceGrid[(int)((x) / (32.0f))][(int)((y) / (32.0f))][3]){
                    // Log.d("collide check","right");
                    return true;
                }
            }
            if(xOff == -5) { // going left
                if(!residenceGrid[(int)((x + xOff) / (32.0f))][(int)((y + yOff) / (32.0f))][3] && !residenceGrid[(int)((x) / (32.0f))][(int)((y) / (32.0f))][7]){
                    // Log.d("collide check","left");
                    return true;
                }
            }
            if(yOff == 21) { // going down
                if(!residenceGrid[(int)((x + xOff) / (32.0f))][(int)((y + yOff) / (32.0f))][1] && !residenceGrid[(int)((x) / (32.0f))][(int)((y) / (32.0f))][5]){
                    // Log.d("collide check","down");
                    return true;
                }
            }
            if(yOff == -5) { // going up
                if(!residenceGrid[(int)((x + xOff) / (32.0f))][(int)((y + yOff) / (32.0f))][5] && !residenceGrid[(int)((x) / (32.0f))][(int)((y) / (32.0f))][1]){
                    // Log.d("collide check","up");
                    return true;
                }
            }

            return false;
        }
    }
}