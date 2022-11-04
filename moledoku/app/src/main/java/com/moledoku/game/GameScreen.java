package com.moledoku.game;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.moledoku.framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Input.TouchEvent;
import com.moledoku.framework.Screen;
import com.moledoku.framework.impl.AndroidGame;
import com.moledoku.framework.impl.AndroidPixmap;
import com.moledoku.framework.impl.Triplet;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

// screen where user plays the game
public class GameScreen extends Screen {

    enum GameState {
        PLAYING, WIN, LOSE
    }

    // change price each update for animation
    int priceAdd = 0;

    GameState state = GameState.PLAYING;
    public static Board board;

    boolean saveUpdated = false;
    boolean giveUpConfirm = false;
    boolean noteToggle = true;
    int buttonSelected = -1;

    int selectedNumber = -1;
    float selectedNumberScale = (172.0f / 9.5f) / 32.0f;

    float buttonScale = (172.0f / 6.0f) / 32.0f;

    // check where user clicks, to help garbage collection
    RectF clickCheckRect = new RectF(0,0,0,0);

    int winPage = 0;
    int winIndex = 0;
    int itemMove = -500;

    static final int FILL_COST = 10;
    static final int HINT_COST = 100;

    public GameScreen(Game game, Board b) {
        super(game);
        Settings.hasSavedGame = 1;
        board = b;
        Settings.board = board;

        if(Settings.soundEnabled == 1 && MainMenuScreen.music < 3) setMusic();

        // set to test level
        // board = Board.grid3;
        // board = Board.grid5;
    }

    public GameScreen(Game game, int levelSize) {
        super(game);
        Settings.hasSavedGame = 1;
        board = new Board(levelSize);
        Settings.board = board;

        if(Settings.soundEnabled == 1 && MainMenuScreen.music < 3) setMusic();

        // set to test level
        // board = Board.grid3;
        // board = Board.grid5;
    }

    private void setMusic(){
        switch(MainMenuScreen.music){
            case 0:
                Assets.main0.stop();
                break;
            case 1:
                Assets.main1.stop();
                break;
            case 2:
                Assets.main2.stop();
                break;
        }
        MainMenuScreen.music = board.size;
        switch(MainMenuScreen.music){
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
                    if(priceAdd > 0){
                        Settings.coin += priceAdd;
                        // prevent int overflow
                        if(Settings.coin > 1000000000)
                            Settings.coin = 1000000000;
                        priceAdd = 0;
                    }
                    game.setScreen(new MainMenuScreen(game));
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                }
            }
        }

        if(board.checkWin()) state = GameState.WIN;

        if(state == GameState.PLAYING)
            updatePlaying(g, touchEvents, deltaTime);
        if(state == GameState.WIN)
            updateWin(g, touchEvents, deltaTime);
        if(state == GameState.LOSE)
            updateLose(g, touchEvents, deltaTime);
    }

    private void updatePlaying(Graphics g, List<TouchEvent> touchEvents, float deltaTime) {
        float boardOffX = (8 * g.getScale());
        float boardOffY = g.getHeight() - (256 * g.getScale()) + (8 * g.getScale());
        float boardScale = (172.0f / board.size) / 32.0f;

        for(TouchEvent event : touchEvents){
            if(event.type == TouchEvent.TOUCH_UP){
                float midOffset = g.getWidth() / 2.0f - (34.0f * (4.5f) * buttonScale * g.getScale()) / 2.0f;
                clickCheckRect.left = midOffset + boardOffX + ((2 * g.getScale() + 34 * 0 * buttonScale * g.getScale()));
                clickCheckRect.top = ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale();
                clickCheckRect.right = clickCheckRect.left + (32 * buttonScale * g.getScale());
                clickCheckRect.bottom = clickCheckRect.top + (32 * buttonScale * g.getScale());
                if(giveUpConfirm && !clickCheckRect.contains(event.x, event.y)){
                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                    giveUpConfirm = false;
                    return;
                }
                for(Triplet<Board.Operation, String, List<Point>> t : board.cages){
                    for(Point p : t.z){
                        clickCheckRect.left = boardOffX + ((2 * g.getScale() + p.x * 32 * boardScale * g.getScale()));
                        clickCheckRect.top = boardOffY + ((2 * g.getScale() + p.y * 32 * boardScale * g.getScale()));
                        clickCheckRect.right = boardOffX + ((2 * g.getScale() + p.x * 32 * boardScale * g.getScale())) + (32 * boardScale * g.getScale());
                        clickCheckRect.bottom = boardOffY + ((2 * g.getScale() + p.y * 32 * boardScale * g.getScale())) + (32 * boardScale * g.getScale());
                        if(clickCheckRect.contains(event.x, event.y)){
                            switch(buttonSelected){
                                case 1:
                                    // set hint from solution
                                    if(Settings.coin >= HINT_COST && board.getNumber(p) == -1) {
                                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                                        Settings.coin -= HINT_COST;
                                        board.setNumber(p, board.getSolution(p));
                                    }
                                    break;
                                case 2:
                                    // fill block
                                    if(Settings.coin >= FILL_COST && board.getNumber(p) != -1) {
                                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                                        Settings.coin -= FILL_COST;
                                        board.setNumber(p, -1);
                                    }
                                    break;
                                case 3:
                                    if (Settings.soundEnabled == 1) Assets.pop.play(1);
                                    // set number
                                    if(board.getNumber(p) == -1 && !noteToggle && selectedNumber != -1) board.setNumber(p, selectedNumber + 1);
                                    // set notes
                                    else if(board.getNumber(p) == -1 && noteToggle && selectedNumber != -1) board.setNumberNote(p, selectedNumber != 9 ? selectedNumber + 1 : 0);
                                    break;
                            }
                        }
                    }
                }

                // set selected number
                midOffset = g.getWidth() / 2.0f - (34.0f * (board.size + 1) * selectedNumberScale * g.getScale()) / 2.0f;
                for(int i = 0; i < board.size; i++){
                    clickCheckRect.left = midOffset + boardOffX + ((2 * g.getScale() + 34 * i * selectedNumberScale * g.getScale()));
                    clickCheckRect.top = ((176 + 10) * g.getScale()) + boardOffY;
                    clickCheckRect.right = clickCheckRect.left + (32 * selectedNumberScale * g.getScale());
                    clickCheckRect.bottom = clickCheckRect.top + (32 * selectedNumberScale * g.getScale());
                    if(clickCheckRect.contains(event.x, event.y)){
                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                        if(selectedNumber == i) selectedNumber = -1;
                        else selectedNumber = i;
                        // Log.d("selected number clicked", selectedNumber + "");
                    }
                }

                // click button
                midOffset = g.getWidth() / 2.0f - (34.0f * (4.5f) * buttonScale * g.getScale()) / 2.0f;
                for(int i = 0; i < 4; i++){
                    clickCheckRect.left = midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale()));
                    clickCheckRect.top = ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale();
                    clickCheckRect.right = clickCheckRect.left + (32 * buttonScale * g.getScale());
                    clickCheckRect.bottom = clickCheckRect.top + (32 * buttonScale * g.getScale());
                    if(clickCheckRect.contains(event.x, event.y)){
                        if (Settings.soundEnabled == 1) Assets.pop.play(1);
                        switch(i){
                            case 0:
                                if(giveUpConfirm){
                                    state = GameState.LOSE;
                                } else {
                                    giveUpConfirm = true;
                                }
                                break;
                            case 1:
                                buttonSelected = 1;
                                break;
                            case 2:
                                buttonSelected = 2;
                                break;
                            case 3:
                                if(buttonSelected == 3) noteToggle = !noteToggle;
                                else buttonSelected = 3;
                                break;
                        }
                    }
                }
            }
        }
    }

    private void updateWin(Graphics g, List<TouchEvent> touchEvents, float deltaTime) {
        if(priceAdd > 0 && saveUpdated){
            Settings.coin += priceAdd/2;
            priceAdd-=priceAdd/2;
            if(priceAdd == 1) {
                priceAdd = 0;
                Settings.coin += 1;
            }
            // prevent int overflow
            if(Settings.coin > 100000000)
                Settings.coin = 100000000;
        }
        if(itemMove < (32 * g.getScale())) itemMove += 30;
        if(itemMove > (32 * g.getScale())) itemMove = (int)(32 * g.getScale());

        if(!saveUpdated){
            if (Settings.soundEnabled == 1) Assets.win.play(1);
            // money
            for(Triplet<Board.Operation, String, List<Point>> t : board.cages){
                 if(board.getAnswer(t) == Integer.parseInt(t.y)){
                     priceAdd += Integer.parseInt(t.y) * board.size * board.size;
                 }
            }

            // no more board
            Settings.hasSavedGame = 0;

            // unlock next level
            if(!Settings.TRIAL_VERSION && Settings.currentLevel == board.size) Settings.currentLevel++;

            // unlock current location
            // if(HomeScreen.screen.locationsUnlocked == null) HomeScreen.screen.locationsUnlocked = new boolean[8];
            Settings.homeScreen.locationsUnlocked[board.size - 3] = true;

            // unlock random item
            int max = 0;
            for(int i = 0; i < 40; i++) max += i;
            Random rand = new Random();
            int percent = rand.nextInt(max);
            int index;
            for(index = 0; index < 40; index++){
                percent -= (40 - index);
                if(percent < 0) break;
            }
            winIndex = index;
            int page = rand.nextInt(5);
            winPage = page;
            if(Settings.TRIAL_VERSION) {
                winIndex = index = 0;
                winPage = page = 0;
            }
            if(HomeScreen.screen.itemsOwned[page * 40 + index] < 0) HomeScreen.screen.itemsOwned[page * 40 + index] = 0;
            HomeScreen.screen.itemsOwned[page * 40 + index]++;
            Log.d("win item added","category " + page + " item " + index);

            // add resident max
            HomeScreen.screen.residentsAmountUnlocked[board.size - 3] += 1;
            if(Settings.TRIAL_VERSION) {
                HomeScreen.screen.residentsAmountUnlocked[board.size - 3] = 1;
            }
            Log.d("win resident amt added","category " + (board.size - 3) + " amt " + 1);

            // unlock resident
            if(Settings.TRIAL_VERSION) {
                if(!HomeScreen.screen.residentsUnlocked[board.size - 3][0]){
                    HomeScreen.screen.residentsUnlocked[board.size - 3][0] = true;
                    Log.d("win resident unlocked","category " + (board.size - 3) + " resident " + 0);
                }
            } else {
                for(int i = 0; i < HomeScreen.screen.residentsUnlocked[board.size - 3].length; i++){
                    if(!HomeScreen.screen.residentsUnlocked[board.size - 3][i]){
                        HomeScreen.screen.residentsUnlocked[board.size - 3][i] = true;
                        Log.d("win resident unlocked","category " + (board.size - 3) + " resident " + i);
                        break;
                    }
                }
            }

            Settings.save(AndroidGame.AGame.fileIO);
            saveUpdated = true;
        }
    }

    private void updateLose(Graphics g, List<TouchEvent> touchEvents, float deltaTime) {
        if(!saveUpdated){
            if (Settings.soundEnabled == 1) Assets.lose.play(1);
            boolean complete;
            for(Triplet<Board.Operation, String, List<Point>> t : board.cages){
                complete  = true;
                for(Point p : t.z){
                    if(board.getNumber(p) == -1){
                        complete = false;;
                        break;
                    }
                }
                if(complete){
                    if(board.getAnswer(t) == Integer.parseInt(t.y)){
                        Settings.coin += Integer.parseInt(t.y);
                    }
                }
            }
            Settings.hasSavedGame = 0;
            Settings.save(AndroidGame.AGame.fileIO);
            saveUpdated = true;
        }
    }

    @Override
    public void present(float deltaTime) {
        Graphics g = game.getGraphics();
        // draw background
        g.drawRect(0, 0, g.getWidth(), g.getHeight(), getBackgroundColor());

        float boardOffX = (8 * g.getScale());
        float boardOffY = g.getHeight() - (256 * g.getScale()) + (8 * g.getScale());

        // draw top
        drawTop(g, boardOffX, boardOffY);
        // draw money
        drawMoney(g);
        // draw board
        drawBoard(g, boardOffX, boardOffY);
        // draw number buttons
        drawNumberSelection(g, boardOffX, boardOffY);
        // draw buttons
        drawButtons(g, boardOffX, boardOffY);

        // draw back button
        g.drawPixmap(Assets.main, (10 * g.getScale()), (10 * g.getScale()), (16 * g.getScale()), (16 * g.getScale()), 0, 0, 16, 16);

        if(state == GameState.PLAYING)
            drawPlaying();
        if(state == GameState.WIN)
            drawWin();
        if(state == GameState.LOSE)
            drawLose();

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
        // Log.d("time",minute + " " + hour + " " + percent);

        switch(board.size){
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
                (board.size - 3) * 192, 1120, 192, 64);
    }

    // render money graphics
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

    // render buttons
    private void drawButtons(Graphics g, float boardOffX, float boardOffY){
        float midOffset = g.getWidth() / 2.0f - (34.0f * (4.5f) * buttonScale * g.getScale()) / 2.0f;
        int i = 0;
        if(giveUpConfirm){
            g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                    ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                    (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 272, 32, 32);
        } else {
            g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                    ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                    (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 32 + 4 * 32, 32, 32);
        }

        i = 1;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 32 + 3 * 32, 32, 32);
        drawPrice(g, 100, (int)(midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale()))), (int)(((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale() + (24 * buttonScale * g.getScale())));
        i = 2;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 32 + 2 * 32, 32, 32);
        drawPrice(g, 10, (int)(midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale()))), (int)(((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale() + (24 * buttonScale * g.getScale())));
        i = 3;
        g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * buttonScale * g.getScale())),
                ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, noteToggle ? 64 : 32, 32, 32);
        if(buttonSelected >= 1 && buttonSelected <= 3)
            g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * buttonSelected * buttonScale * g.getScale())),
                    ((176 + 16) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale(),
                    (32 * buttonScale * g.getScale()), (32 * buttonScale * g.getScale()), 0, 304, 32, 32);
    }

    // render prices
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

    // render numbers available for selection
    private void drawNumberSelection(Graphics g, float boardOffX, float boardOffY){
        float midOffset = g.getWidth() / 2.0f - (34.0f * (board.size + 1) * selectedNumberScale * g.getScale()) / 2.0f;
        for(int i = 0; i < board.size; i++){
            g.drawRect((midOffset + boardOffX + (2 * g.getScale()) + 34 * i * selectedNumberScale * g.getScale() - 0 * selectedNumberScale * g.getScale()), (((176 + 8) * g.getScale()) + boardOffY - 0 * selectedNumberScale * g.getScale()), (1 * selectedNumberScale * g.getScale()), (32 * selectedNumberScale * g.getScale()), Color.rgb(45, 23, 7));
            g.drawRect((midOffset + boardOffX + (2 * g.getScale()) + 34 * i * selectedNumberScale * g.getScale() + 32 * selectedNumberScale * g.getScale()), (((176 + 8) * g.getScale()) + boardOffY - 0 * selectedNumberScale * g.getScale()), (1 * selectedNumberScale * g.getScale()), (33 * selectedNumberScale * g.getScale()), Color.rgb(45, 23, 7));
            g.drawRect((midOffset + boardOffX + (2 * g.getScale()) + 34 * i * selectedNumberScale * g.getScale() - 0 * selectedNumberScale * g.getScale()), (((176 + 8) * g.getScale()) + boardOffY - 0 * selectedNumberScale * g.getScale()), (33 * selectedNumberScale * g.getScale()), (1 * selectedNumberScale * g.getScale()), Color.rgb(45, 23, 7));
            g.drawRect((midOffset + boardOffX + (2 * g.getScale()) + 34 * i * selectedNumberScale * g.getScale() - 0 * selectedNumberScale * g.getScale()), (((176 + 8) * g.getScale()) + boardOffY + 32 * selectedNumberScale * g.getScale()), (32 * selectedNumberScale * g.getScale()), (1 * selectedNumberScale * g.getScale()), Color.rgb(45, 23, 7));

            StringBuilder sb;
            Rect src = new Rect();
            sb = new StringBuilder();
            sb.append((i+1));
            src = Assets.getSmallNumberRect(Integer.parseInt(sb.toString()));
            g.drawPixmap(Assets.main,midOffset + boardOffX + ((2 * g.getScale() + 34 * i * selectedNumberScale * g.getScale())),
                    ((176 + 8) * g.getScale()) + boardOffY,
                    (33 * selectedNumberScale * g.getScale()), (33 * selectedNumberScale * g.getScale()),
                    src.left, src.top, src.right - src.left, src.bottom - src.top);
            if(i == selectedNumber){
                g.drawPixmap(Assets.main, midOffset + boardOffX + ((2 * g.getScale() + 34 * i * selectedNumberScale * g.getScale())),
                        ((176 + 8) * g.getScale()) + boardOffY,
                        (33 * selectedNumberScale * g.getScale()), (33 * selectedNumberScale * g.getScale()),0, 304, 32, 32);
            }
        }
    }

    // render game board
    private void drawBoard(Graphics g, float boardOffX, float boardOffY){
        // draw background
        g.drawRect(boardOffX, boardOffY, (176 * g.getScale()), (176 * g.getScale()), getBackgroundColor());
        float boardScale = (172.0f / board.size) / 32.0f;
        for(Triplet<Board.Operation, String, List<Point>> t : board.cages) {
            for (Point p : t.z) {
                // draw boxBackground
                drawBox(g, p, t,boardOffX + ((2 * g.getScale() + p.x * 32 * boardScale * g.getScale())), boardOffY + ((2 * g.getScale() + p.y * 32 * boardScale * g.getScale())), boardScale);
            }
        }
        for(Triplet<Board.Operation, String, List<Point>> t : board.cages) {
            for (Point p : t.z) {
                // draw grid
                drawGrid(g, p, t, boardOffX + ((2 * g.getScale() + p.x * 32 * boardScale * g.getScale())), boardOffY + ((2 * g.getScale() + p.y * 32 * boardScale * g.getScale())), boardScale);
            }
        }
        for(Triplet<Board.Operation, String, List<Point>> t : board.cages) {
            // draw operation
            drawOperation(g, t, boardOffX, boardOffY, boardScale);
        }
        for(Triplet<Board.Operation, String, List<Point>> t : board.cages) {
            for (Point p : t.z) {
                // draw number
                if(board.getNumber(p) != -1) drawNumber(g, p, t, boardOffX + ((2 * g.getScale() + p.x * 32 * boardScale * g.getScale())), boardOffY + ((2 * g.getScale() + p.y * 32 * boardScale * g.getScale())), boardScale);
                // draw notes
                else if(board.getNumber(p) == -1) drawNotes(g, p, t, boardOffX + ((2 * g.getScale() + p.x * 32 * boardScale * g.getScale())), boardOffY + ((2 * g.getScale() + p.y * 32 * boardScale * g.getScale())), boardScale);
            }
        }
    }

    // render user notes
    private void drawNotes(Graphics g, Point point, Triplet<Board.Operation, String, List<Point>> t, float x, float y, float boardScale){
        if(board.getNumber(point) == -1) {
            StringBuilder sb;
            Rect src = new Rect();
            for(int i = 0; i <= 9; i++){
                if(board.getNumberNote(point, i)) {
                    sb = new StringBuilder();
                    sb.append(i);
                    src = Assets.getSmallNumberRect(Integer.parseInt(sb.toString()));
                    if (i == 0) {
                        g.drawPixmap(Assets.main, (x + (3) * 8 * boardScale * g.getScale()), (y + (3) * 8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), src.left, src.top, src.right - src.left, src.bottom - src.top);
                    } else {
                        g.drawPixmap(Assets.main, (x + ((i - 1) % 3) * 8 * boardScale * g.getScale()), (y + (int) ((i - 1) / 3 + 1) * 8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), src.left, src.top, src.right - src.left, src.bottom - src.top);
                    }
                }
            }
        }
    }

    // render user solution numbers
    private void drawNumber(Graphics g, Point point, Triplet<Board.Operation, String, List<Point>> t, float x, float y, float boardScale){
        if(board.getNumber(point) != -1) {
            StringBuilder sb;
            Rect src = new Rect();
            sb = new StringBuilder();
            sb.append(board.getNumber(point));
            src = Assets.getLargeNumberRect(Integer.parseInt(sb.toString()));
            g.drawPixmap(Assets.main, (x + 8 * boardScale * g.getScale()), (y + 8 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), src.left, src.top, src.right - src.left, src.bottom - src.top);
        }
    }

    // render operations
    private void drawOperation(Graphics g, Triplet<Board.Operation, String, List<Point>> t, float boardOffX, float boardOffY, float boardScale){
        Point drawPos = Board.getCageOperationBox(t);
        if(drawPos == null) return;
        StringBuilder sb;
        float x = boardOffX + ((2 * g.getScale() + drawPos.x * 32 * boardScale * g.getScale()));
        float y = boardOffY + ((2 * g.getScale() + drawPos.y * 32 * boardScale * g.getScale()));
        Rect src = new Rect();
        for(int i = 0; i < t.y.length(); i++){
            sb = new StringBuilder();
            sb.append(t.y.charAt(i));
            src = Assets.getSmallNumberRect(Integer.parseInt(sb.toString()));
            g.drawPixmap(Assets.main, (x + i * 8 * boardScale * g.getScale()), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), src.left, src.top, src.right - src.left, src.bottom - src.top);
        }
        src = Assets.getOperation(t.x);
        g.drawPixmap(Assets.main, (x + (t.y.length()) * 8 * boardScale * g.getScale()), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), src.left, src.top, src.right - src.left, src.bottom - src.top);
    }

    // render cage, cell seperation grid
    private void drawGrid(Graphics g, Point point, Triplet<Board.Operation, String, List<Point>> t, float x, float y, float boardScale) {
        if(Board.cageContains(t, new Point(point.x, point.y - 1))) { // up
            g.drawRect((x + 1 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (30 * boardScale * g.getScale()), (2 * boardScale * g.getScale()), Color.argb(50, 255, 255, 255));
        } else {
            g.drawRect((x - 1 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (34 * boardScale * g.getScale()), (2 * boardScale * g.getScale()), Color.rgb(24, 44, 62));
        }
        if(Board.cageContains(t, new Point(point.x, point.y + 1))) { // down
            g.drawRect((x + 1 * boardScale * g.getScale()), (y + 31 * boardScale * g.getScale()), (30 * boardScale * g.getScale()), (2 * boardScale * g.getScale()), Color.argb(50, 255, 255, 255));
        } else {
            g.drawRect((x - 1 * boardScale * g.getScale()), (y + 31 * boardScale * g.getScale()), (34 * boardScale * g.getScale()), (2 * boardScale * g.getScale()), Color.rgb(24, 44, 62));
        }
        if(Board.cageContains(t, new Point(point.x - 1, point.y))) { // left
            g.drawRect((x - 1 * boardScale * g.getScale()), (y + 1 * boardScale * g.getScale()), (2 * boardScale * g.getScale()), (30 * boardScale * g.getScale()), Color.argb(50, 255, 255, 255));
        } else {
            g.drawRect((x - 1 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (2 * boardScale * g.getScale()), (34 * boardScale * g.getScale()), Color.rgb(24, 44, 62));
        }
        if(Board.cageContains(t, new Point(point.x + 1, point.y))) { // right
            g.drawRect((x + 31 * boardScale * g.getScale()), (y + 1 * boardScale * g.getScale()), (2 * boardScale * g.getScale()), (30 * boardScale * g.getScale()), Color.argb(50, 255, 255, 255));
        } else {
            g.drawRect((x + 31 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (2 * boardScale * g.getScale()), (34 * boardScale * g.getScale()), Color.rgb(24, 44, 62));
        }
    }

    // render dugout box tile graphics
    private void drawBox(Graphics g, Point point, Triplet<Board.Operation, String, List<Point>> t, float x, float y, float boardScale){
        if(board.getNumber(point) != -1){
            // draw middle
            // Log.d("rect check", board.getNumber(point) + "");
            g.drawRect((x), (y), (32 * boardScale * g.getScale()), (32 * boardScale * g.getScale()), getForegroundColor());

            // draw 0 -1
            if(Board.cageContains(t, new Point(point.x, point.y - 1)) && board.getNumber(point.x, point.y - 1) != -1){ // up
                // up open
                g.drawRect((x + 8 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), getForegroundColor());
            } else {
                // up closed
                g.drawPixmap(Assets.main, (x), (y), (32 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1416, 16, 8);
            }

            // draw -1 0
            if(Board.cageContains(t, new Point(point.x - 1, point.y)) && board.getNumber(point.x - 1, point.y) != -1){ // left
                // left open
                g.drawRect((x - 1 * boardScale * g.getScale()), (y + 8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), getForegroundColor());
            } else {
                // left closed
                g.drawPixmap(Assets.main, (x), (y), (8 * boardScale * g.getScale()), (32 * boardScale * g.getScale()), 8 + 16 * (board.size - 3), 1424, 8, 16);
            }

            // draw 1 0
            if(Board.cageContains(t, new Point(point.x + 1, point.y)) && board.getNumber(point.x + 1, point.y) != -1){ // right
                // right open
                g.drawRect((x + 25 * boardScale * g.getScale()), (y + 8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), getForegroundColor());
            } else {
                // right closed
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y), (8 * boardScale * g.getScale()), (32 * boardScale * g.getScale()), 16 * (board.size - 3), 1424, 8, 16);
            }

            // draw 0 1
            if(Board.cageContains(t, new Point(point.x, point.y + 1)) && board.getNumber(point.x, point.y + 1) != -1){ // down
                // down open
                g.drawRect((x + 8 * boardScale * g.getScale()), (y + 25 * boardScale * g.getScale()), (16 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), getForegroundColor());
            } else {
                // down closed
                g.drawPixmap(Assets.main, (x), (y + 24 * boardScale * g.getScale()), (32 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1408, 16, 8);
            }

            // draw -1 -1
            if(Board.cageContains(t, new Point(point.x - 1, point.y - 1)) && board.getNumber(point.x - 1, point.y - 1) != -1 && // up left
                    Board.cageContains(t, new Point(point.x, point.y - 1)) && board.getNumber(point.x, point.y - 1) != -1 && // up
                    Board.cageContains(t, new Point(point.x - 1, point.y)) && board.getNumber(point.x - 1, point.y) != -1) { // left
                // all open
                g.drawRect((x - 1 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), getForegroundColor());
            } else if(Board.cageContains(t, new Point(point.x, point.y - 1)) && board.getNumber(point.x, point.y - 1) != -1 && // up
                    Board.cageContains(t, new Point(point.x - 1, point.y)) && board.getNumber(point.x - 1, point.y) != -1){ // left
                // two sides open
                g.drawPixmap(Assets.main, (x), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 8 + 16 * (board.size - 3), 1400, 8, 8);
            } else if(Board.cageContains(t, new Point(point.x - 1, point.y)) && board.getNumber(point.x - 1, point.y) != -1){ // left
                // left open
                g.drawPixmap(Assets.main, (x - 1 * boardScale * g.getScale()), (y), (9 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1416, 8, 8);
            } else if(Board.cageContains(t, new Point(point.x, point.y - 1)) && board.getNumber(point.x, point.y - 1) != -1){ // up
                // up open
                g.drawPixmap(Assets.main, (x), (y - 1 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), 8 + 16 * (board.size - 3), 1424, 8, 8);
            } else {
                // closed
                g.drawPixmap(Assets.main, (x), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1376, 8, 8);
            }

            // draw 1 -1
            if(Board.cageContains(t, new Point(point.x + 1, point.y - 1)) && board.getNumber(point.x + 1, point.y - 1) != -1 && // up right
                    Board.cageContains(t, new Point(point.x, point.y - 1)) && board.getNumber(point.x, point.y - 1) != -1 && // up
                    Board.cageContains(t, new Point(point.x + 1, point.y)) && board.getNumber(point.x + 1, point.y) != -1) { // right
                // all open
                g.drawRect((x + 24 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), getForegroundColor());
            } else if(Board.cageContains(t, new Point(point.x, point.y - 1)) && board.getNumber(point.x, point.y - 1) != -1 && // up
                    Board.cageContains(t, new Point(point.x + 1, point.y)) && board.getNumber(point.x + 1, point.y) != -1){ // right
                // two sides open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1400, 8, 8);
            } else if(Board.cageContains(t, new Point(point.x + 1, point.y)) && board.getNumber(point.x + 1, point.y) != -1){ // right
                // right open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y), (9 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1416, 8, 8);
            } else if(Board.cageContains(t, new Point(point.x, point.y - 1)) && board.getNumber(point.x, point.y - 1) != -1){ // up
                // up open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y - 1 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), 16 * (board.size - 3), 1424, 8, 8);
            } else {
                // closed
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 8 + 16 * (board.size - 3), 1376, 8, 8);
            }

            // draw -1 1
            if(Board.cageContains(t, new Point(point.x - 1, point.y + 1)) && board.getNumber(point.x - 1, point.y + 1) != -1 && // down left
                    Board.cageContains(t, new Point(point.x, point.y + 1)) && board.getNumber(point.x, point.y + 1) != -1 && // down
                    Board.cageContains(t, new Point(point.x - 1, point.y)) && board.getNumber(point.x - 1, point.y) != -1) { // left
                // all open
                g.drawRect((x - 1 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), getForegroundColor());
            } else if(Board.cageContains(t, new Point(point.x, point.y + 1)) && board.getNumber(point.x, point.y + 1) != -1 && // down
                    Board.cageContains(t, new Point(point.x - 1, point.y)) && board.getNumber(point.x - 1, point.y) != -1){ // left
                // two sides open
                g.drawPixmap(Assets.main, (x), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 8 + 16 * (board.size - 3), 1392, 8, 8);
            } else if(Board.cageContains(t, new Point(point.x - 1, point.y)) && board.getNumber(point.x - 1, point.y) != -1){ // left
                // left open
                g.drawPixmap(Assets.main, (x - 1 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1408, 8, 8);
            } else if(Board.cageContains(t, new Point(point.x, point.y + 1)) && board.getNumber(point.x, point.y + 1) != -1){ // down
                // down open
                g.drawPixmap(Assets.main, (x), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), 8 + 16 * (board.size - 3), 1424, 8, 8);
            } else {
                // closed
                g.drawPixmap(Assets.main, (x), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1384, 8, 8);
            }


            // draw 1 1
            if(Board.cageContains(t, new Point(point.x + 1, point.y + 1)) && board.getNumber(point.x + 1, point.y + 1) != -1 && // down right
                    Board.cageContains(t, new Point(point.x, point.y + 1)) && board.getNumber(point.x, point.y + 1) != -1 && // down
                    Board.cageContains(t, new Point(point.x + 1, point.y)) && board.getNumber(point.x + 1, point.y) != -1) { // right
                // all open
                g.drawRect((x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), getForegroundColor());
            } else if(Board.cageContains(t, new Point(point.x, point.y + 1)) && board.getNumber(point.x, point.y + 1) != -1 && // down
                    Board.cageContains(t, new Point(point.x + 1, point.y)) && board.getNumber(point.x + 1, point.y) != -1){ // right
                // two sides open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1392, 8, 8);
            } else if(Board.cageContains(t, new Point(point.x + 1, point.y)) && board.getNumber(point.x + 1, point.y) != -1){ // right
                // right open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 16 * (board.size - 3), 1408, 8, 8);
            } else if(Board.cageContains(t, new Point(point.x, point.y + 1)) && board.getNumber(point.x, point.y + 1) != -1){ // down
                // down open
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (9 * boardScale * g.getScale()), 16 * (board.size - 3), 1424, 8, 8);
            } else {
                // closed
                g.drawPixmap(Assets.main, (x + 24 * boardScale * g.getScale()), (y + 24 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), (8 * boardScale * g.getScale()), 8 + 16 * (board.size - 3), 1384, 8, 8);
            }
        }
    }

    private int getBackgroundColor(){
        switch(board.size){
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
        switch(board.size) {
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

    private void drawPlaying() {
        Graphics g = game.getGraphics();
    }

    // render reward
    private void drawWin() {
        // TODO add stuff
        Graphics g = game.getGraphics();
        g.drawPixmap(Assets.main, (int)(g.getWidth() / 2) - (16 * g.getScale()), itemMove, (32 * g.getScale()), (32 * g.getScale()), 32, 352, 32, 32);
        g.drawPixmap(Assets.main, (int)(g.getWidth() / 2) - (12 * g.getScale()), itemMove + (4 * g.getScale()), (24 * g.getScale()), (24 * g.getScale()), winPage * 256 + ((int)(winIndex % 4) * 32), 704 + ((int)((winIndex >= 20 ? winIndex - 20 : winIndex) / 4) * 16), 16, 16);

        // pageNum * 128 + x * 32 + (ret.itemsOwned[pageNum * 4 * 5 + 4 * y + x] != -1 ? 0 : 16), 704 + y * 16, 16, 16);
    }

    private void drawLose() {
        // TODO add stuff
        Graphics g = game.getGraphics();
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
}