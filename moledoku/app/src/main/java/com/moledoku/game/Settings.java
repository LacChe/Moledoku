package com.moledoku.game;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import com.moledoku.framework.FileIO;
import com.moledoku.framework.impl.AndroidGame;
import com.moledoku.framework.impl.Triplet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// this class manages saving, loading etc
public class Settings {

    public static final boolean TRIAL_VERSION = false;

    public static int soundEnabled = 1;
    public static int coin = 0;
    public static int hasSavedGame = 0;
    public static Board board;
    public static HomeScreen homeScreen;
    public static int currentLevel = 3;

    public static void load(FileIO files) {
        homeScreen = new HomeScreen(AndroidGame.AGame);
        try {
            Log.d("loading", "load");
            InputStream inputStream = AndroidGame.AGame.openFileInput("moleDoku.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader in = new BufferedReader(inputStreamReader);

                soundEnabled = Integer.parseInt(in.readLine());
                coin = Integer.parseInt(in.readLine());
                hasSavedGame = Integer.parseInt(in.readLine());
                currentLevel = Integer.parseInt(in.readLine());

                if (hasSavedGame == 1) {
                    board = new Board(Integer.parseInt(in.readLine()));

                    int[][] sol = new int[board.size][board.size];
                    for(int x = 0; x < board.size; x++){
                        for(int y = 0; y < board.size; y++){
                            sol[x][y] = Integer.parseInt(in.readLine());
                        }
                    }
                    board.solution = sol;
                    int[][] nums = new int[board.size][board.size];
                    for(int x = 0; x < board.size; x++){
                        for(int y = 0; y < board.size; y++){
                            nums[x][y] = Integer.parseInt(in.readLine());
                        }
                    }
                    board.numbers = nums;
                    boolean[][][] notes = new boolean[board.size][board.size][10];
                    for(int x = 0; x < board.size; x++){
                        for(int y = 0; y < board.size; y++){
                            for(int z = 0; z < 10; z++){
                                notes[x][y][z] = Boolean.parseBoolean(in.readLine());
                            }
                        }
                    }
                    board.numberNotes = notes;

                    List<Triplet<Board.Operation, String, List<Point>>> cages = new ArrayList<Triplet<Board.Operation, String, List<Point>>>();
                    int s = Integer.parseInt(in.readLine());
                    for(int x = 0; x < s; x++){
                        Board.Operation op = Board.Operation.EQUAL;
                        int opp = Integer.parseInt(in.readLine());
                        switch(opp){
                            case 0:
                                op = Board.Operation.ADD;
                                break;
                            case 1:
                                op = Board.Operation.SUBTRACT;
                                break;
                            case 2:
                                op = Board.Operation.MULTIPLY;
                                break;
                            case 3:
                                op = Board.Operation.DIVIDE;
                                break;
                            case 4:
                                op = Board.Operation.EQUAL;
                                break;
                        }
                        String answer = in.readLine();
                        List<Point> points = new ArrayList<Point>();
                        int ss = Integer.parseInt(in.readLine());
                        for(int y = 0; y < ss; y++){
                            points.add(new Point(Integer.parseInt(in.readLine()), Integer.parseInt(in.readLine())));
                        }
                        cages.add(new Triplet< Board.Operation, String, List<Point>>(op, answer, points));
                    }
                    board.cages = cages;
                    GameScreen.board = board;
                }
                // confirm
                int confirm = Integer.parseInt(in.readLine());
                if(confirm == 1) {
                    homeScreen = new HomeScreen(AndroidGame.AGame);
                    // residenceGrid;
                    homeScreen.residenceGrid = new boolean[HomeScreen.SIZE][6][9];
                    for (int x = 0; x < HomeScreen.SIZE; x++) {
                        for (int y = 0; y < 6; y++) {
                            for (int z = 0; z < 9; z++) {
                                homeScreen.residenceGrid[x][y][z] = (Integer.parseInt(in.readLine()) == 1);
                            }
                        }
                    }
                    // location
                    homeScreen.location = Integer.parseInt(in.readLine());
                    homeScreen.locationsUnlocked = new boolean[8];
                    for (int x = 0; x < 8; x++) {
                        homeScreen.locationsUnlocked[x] = (Integer.parseInt(in.readLine()) == 1);
                    }
                    // item
                    homeScreen.itemIds = new int[HomeScreen.SIZE][6];
                    for (int x = 0; x < HomeScreen.SIZE; x++) {
                        for (int y = 0; y < 6; y++) {
                            homeScreen.itemIds[x][y] = Integer.parseInt(in.readLine());
                        }
                    }
                    homeScreen.itemsOwned = new int[HomeScreen.ITEM_MAX];
                    for (int x = 0; x < HomeScreen.ITEM_MAX; x++) {
                        homeScreen.itemsOwned[x] = Integer.parseInt(in.readLine());
                    }
                    // residents
                    int amount = Integer.parseInt(in.readLine());
                    homeScreen.residents = new ArrayList<HomeScreen.Resident>();
                    for (int x = 0; x < amount; x++) {
                        int id = Integer.parseInt(in.readLine());
                        int xPos = Integer.parseInt(in.readLine());
                        int yPos = Integer.parseInt(in.readLine());
                        homeScreen.residents.add(new HomeScreen.Resident(id, xPos, yPos));
                    }
                    homeScreen.residentsUnlocked = new boolean[8][20];
                    for (int x = 0; x < 8; x++) {
                        for (int y = 0; y < 20; y++) {
                            homeScreen.residentsUnlocked[x][y] = (Integer.parseInt(in.readLine()) == 1);
                        }
                    }
                    homeScreen.residentsAmountUnlocked = new int[8];
                    for (int x = 0; x < 8; x++) {
                        homeScreen.residentsAmountUnlocked[x] = Integer.parseInt(in.readLine());
                    }
                    HomeScreen.screen = homeScreen;
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
    }

    public static void save(FileIO files) {
        try {
            Log.d("saving", "save");
            OutputStreamWriter out = new OutputStreamWriter(AndroidGame.AGame.openFileOutput("moleDoku.txt", Context.MODE_PRIVATE));
            // BufferedWriter out = new BufferedWriter(outputStreamWriter);

            out.write(soundEnabled + "\n");
            out.write(coin + "\n");
            out.write(hasSavedGame + "\n");
            out.write(currentLevel + "\n");

            if (hasSavedGame == 1) {
                out.write(GameScreen.board.size + "\n");
                for(int x = 0; x < GameScreen.board.size; x++){
                    for(int y = 0; y < GameScreen.board.size; y++){
                        out.write(GameScreen.board.solution[x][y] + "\n");
                    }
                }
                for(int x = 0; x < GameScreen.board.size; x++){
                    for(int y = 0; y < GameScreen.board.size; y++){
                        out.write(GameScreen.board.numbers[x][y] + "\n");
                    }
                }
                for(int x = 0; x < GameScreen.board.size; x++){
                    for(int y = 0; y < GameScreen.board.size; y++){
                        for(int z = 0; z < 10; z++){
                            out.write(GameScreen.board.numberNotes[x][y][z] + "\n");
                        }
                    }
                }

                out.write(GameScreen.board.cages.size() + "\n");
                for(int x = 0; x < GameScreen.board.cages.size(); x++){
                    switch(GameScreen.board.cages.get(x).x){
                        case ADD:
                            out.write(0 + "\n");
                            break;
                        case SUBTRACT:
                            out.write(1 + "\n");
                            break;
                        case MULTIPLY:
                            out.write(2 + "\n");
                            break;
                        case DIVIDE:
                            out.write(3 + "\n");
                            break;
                        case EQUAL:
                            out.write(4 + "\n");
                            break;
                    }
                    out.write(GameScreen.board.cages.get(x).y + "\n");
                    out.write(Integer.toString(GameScreen.board.cages.get(x).z.size()) + "\n");
                    for(int y = 0; y < GameScreen.board.cages.get(x).z.size(); y++){
                        out.write(GameScreen.board.cages.get(x).z.get(y).x + "\n");
                        out.write(GameScreen.board.cages.get(x).z.get(y).y + "\n");
                    }
                }
            }
            // confirm
            out.write(((HomeScreen.screen != null) ? 1 : 0) + "\n");
            if(HomeScreen.screen != null) {
                // residenceGrid;
                for(int x = 0; x < HomeScreen.SIZE; x++){
                    for(int y = 0; y < 6; y++){
                        for(int z = 0; z < 9; z++){
                            out.write((HomeScreen.screen.residenceGrid[x][y][z] ? 1 : 0) + "\n");
                        }
                    }
                }
                // location
                out.write(HomeScreen.screen.location + "\n");
                for(int x = 0; x < 8; x++){
                    out.write((HomeScreen.screen.locationsUnlocked[x] ? 1 : 0) + "\n");
                }
                // item
                for(int x = 0; x < HomeScreen.SIZE; x++){
                    for(int y = 0; y < 6; y++){
                        out.write(HomeScreen.screen.itemIds[x][y] + "\n");
                    }
                }
                for(int x = 0; x < HomeScreen.ITEM_MAX; x++){
                    out.write(HomeScreen.screen.itemsOwned[x] + "\n");
                }
                // residents
                out.write(HomeScreen.screen.residents.size() + "\n");
                for(int x = 0; x < HomeScreen.screen.residents.size(); x++){
                    out.write(HomeScreen.screen.residents.get(x).id + "\n");
                    out.write((int)HomeScreen.screen.residents.get(x).x + "\n");
                    out.write((int)HomeScreen.screen.residents.get(x).y + "\n");
                }
                for(int x = 0; x <  8; x++){
                    for(int y = 0; y < 20; y++){
                        out.write((HomeScreen.screen.residentsUnlocked[x][y] ? 1 : 0) + "\n");
                    }
                }
                for(int x = 0; x < 8; x++){
                    out.write(HomeScreen.screen.residentsAmountUnlocked[x] + "\n");
                }
            }
            out.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}