package com.moledoku.game;

import com.moledoku.framework.Screen;
import com.moledoku.framework.impl.AndroidGame;

// root class that android runs
public class MoleDoku extends AndroidGame {

    public Screen getStartScreen() {
        return new LoadingScreen(this);
    }

    @Override
    public void onBackPressed() {
        if(getCurrentScreen().getClass() == MainMenuScreen.class){
            super.onBackPressed();
            return;
        }
        if(getCurrentScreen().getClass() == AddResidentScreen.class ||
                getCurrentScreen().getClass() == SelectLocationScreen.class ||
                getCurrentScreen().getClass() == SetItemScreen.class){
            setScreen(Settings.homeScreen);
            return;
        }
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.load(fileIO);
        screen.resume();
        renderView.resume();

        if(Settings.soundEnabled == 1 && MainMenuScreen.music != -1) {
            switch (MainMenuScreen.music) {
                case 0:
                    Assets.main0.play();
                    break;
                case 1:
                    Assets.main1.play();
                    break;
                case 2:
                    Assets.main2.play();
                    break;
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
    }

    @Override
    public void onPause() {
        super.onPause();
        Settings.save(fileIO);
        renderView.pause();
        screen.pause();
        if(Settings.soundEnabled == 1) {
            switch (MainMenuScreen.music) {
                case 0:
                    Assets.main0.stop();
                    break;
                case 1:
                    Assets.main1.stop();
                    break;
                case 2:
                    Assets.main2.stop();
                    break;
                case 3:
                    Assets.lvl3.stop();
                    break;
                case 4:
                    Assets.lvl4.stop();
                    break;
                case 5:
                    Assets.lvl5.stop();
                    break;
                case 6:
                    Assets.lvl6.stop();
                    break;
                case 7:
                    Assets.lvl7.stop();
                    break;
                case 8:
                    Assets.lvl8.stop();
                    break;
                case 9:
                    Assets.lvl9.stop();
                    break;
                case 10:
                    Assets.lvl10.stop();
                    break;
            }
        }
        if (isFinishing()){
            screen.dispose();
            Assets.main.dispose();
            Assets.pop.dispose();
            Assets.win.dispose();
            Assets.lose.dispose();
            Assets.lvl3.dispose();
            Assets.lvl4.dispose();
            Assets.lvl5.dispose();
            Assets.lvl6.dispose();
            Assets.lvl7.dispose();
            Assets.lvl8.dispose();
            Assets.lvl9.dispose();
            Assets.lvl10.dispose();
            Assets.main0.dispose();
            Assets.main1.dispose();
            Assets.main2.dispose();
        }
    }
}

// 192 : 256