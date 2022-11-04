package com.moledoku.game;

import com.moledoku. framework.Game;
import com.moledoku.framework.Graphics;
import com.moledoku.framework.Graphics.PixmapFormat;
import com.moledoku.framework.Music;
import com.moledoku.framework.Screen;

// load assets here
public class LoadingScreen extends Screen {

    public LoadingScreen(Game game) {
        super(game);
    }

    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        Assets.main = g.newPixmap("main.png", PixmapFormat.ARGB8888);
        Assets.pop = game.getAudio().newSound("pop.mp3");
        Assets.win = game.getAudio().newSound("win.mp3");
        Assets.lose = game.getAudio().newSound("lose.mp3");
        Assets.lvl3 = game.getAudio().newMusic("lvl3.mp3");
        Assets.lvl4 = game.getAudio().newMusic("lvl4.mp3");
        Assets.lvl5 = game.getAudio().newMusic("lvl5.mp3");
        Assets.lvl6 = game.getAudio().newMusic("lvl6.mp3");
        Assets.lvl7 = game.getAudio().newMusic("lvl7.mp3");
        Assets.lvl8 = game.getAudio().newMusic("lvl8.mp3");
        Assets.lvl9 = game.getAudio().newMusic("lvl9.mp3");
        Assets.lvl10 = game.getAudio().newMusic("lvl10.mp3");
        Assets.main0 = game.getAudio().newMusic("main0.mp3");
        Assets.main1 = game.getAudio().newMusic("main1.mp3");
        Assets.main2 = game.getAudio().newMusic("main2.mp3");
        Assets.lvl3.setLooping(true);
        Assets.lvl4.setLooping(true);
        Assets.lvl5.setLooping(true);
        Assets.lvl6.setLooping(true);
        Assets.lvl7.setLooping(true);
        Assets.lvl8.setLooping(true);
        Assets.lvl9.setLooping(true);
        Assets.lvl10.setLooping(true);
        Assets.main0.setLooping(true);
        Assets.main1.setLooping(true);
        Assets.main2.setLooping(true);
        Settings.load(game.getFileIO());
        // use if test cases needed
        // Board.InitDefault();
        game.setScreen(new MainMenuScreen(game));
    }

    public void present(float deltaTime) {
    }

    public void pause() {
    }

    public void resume() {
    }

    public void dispose() {
    }
}