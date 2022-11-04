package com.moledoku.game;

import android.graphics.Point;
import android.util.Log;

import com.moledoku.framework.impl.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// store and process game board dataclass
public class Board {

    // max amount of single cell cages
    private static final int SINGLE_CELL_MAX = 10;

    // test cases
    public static Board grid3;
    public static Board grid5;

    public static enum Operation {ADD, SUBTRACT, MULTIPLY, DIVIDE, EQUAL};

    int[][] solution;
    int[][] numbers;
    boolean[][][] numberNotes;
    int size;
    List<Triplet<Operation, String, List<Point>>> cages;

    Random rand = new Random();

    public Board(int size){
        this.size = size;
        numbers = new int[size][size];
        numberNotes = new boolean[size][size][10];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++) {
                numbers[i][j] = -1;
                for (int k = 0; k < 10; k++) {
                    numberNotes[i][j][k] = false;
                }
            }
        }

        solution = genBoard();
        cages = genCages();
    }

    private ArrayList<Triplet<Operation, String, List<Point>>> genCages(){
        ArrayList<Triplet<Operation, String, List<Point>>> c = new ArrayList<Triplet<Operation, String, List<Point>>>();
        int singleCellCount = 0;
        while(c.size() <= 0 || (float)singleCellCount / (float)c.size() > SINGLE_CELL_MAX){
            // set free points
            ArrayList<Point> freePoints = new ArrayList<Point>();
            for(int x = 0; x < size; x++){
                for(int y = 0; y < size; y++){
                    freePoints.add(new Point(x,y));
                }
            }
            while(freePoints.size() > 0){
                // rand cage length
                int cageSize = rand.nextInt(100);
                if(cageSize < 15) cageSize = 1;
                else if(cageSize < 50) cageSize = 2;
                else if(cageSize < 85) cageSize = 3;
                else if(cageSize < 98) cageSize = 4;
                else if(cageSize < 100) cageSize = 5;
                else cageSize = 5;

                // get cage points
                ArrayList<Point> points = new ArrayList<Point>();
                points.add(freePoints.remove(rand.nextInt(freePoints.size())));
                for(int x = 1; x < cageSize; x++){
                    // check has connections
                    if(!freePoints.contains(new Point(points.get(points.size() - 1).x - 1, points.get(points.size() - 1).y)) &&
                            !freePoints.contains(new Point(points.get(points.size() - 1).x + 1, points.get(points.size() - 1).y)) &&
                            !freePoints.contains(new Point(points.get(points.size() - 1).x, points.get(points.size() - 1).y - 1)) &&
                            !freePoints.contains(new Point(points.get(points.size() - 1).x, points.get(points.size() - 1).y + 1))){
                        break;
                    }
                    int dir = rand.nextInt(4);
                    int i = -1, j = -1;
                    switch(dir){
                        case 0:
                            i = points.get(points.size() - 1).x - 1;
                            j = points.get(points.size() - 1).y;
                            break;
                        case 1:
                            i = points.get(points.size() - 1).x + 1;
                            j = points.get(points.size() - 1).y;
                            break;
                        case 2:
                            i = points.get(points.size() - 1).x;
                            j = points.get(points.size() - 1).y - 1;
                            break;
                        case 3:
                            i = points.get(points.size() - 1).x;
                            j = points.get(points.size() - 1).y + 1;
                            break;
                    }
                    if(freePoints.contains(new Point(i, j))){
                        points.add(freePoints.get(freePoints.indexOf(new Point(i, j))));
                        freePoints.remove(new Point(i, j));
                    } else {
                        x -= 1;
                    }
                }

                // rand operation
                Operation ops = Operation.EQUAL;
                if(points.size() == 2){
                    int randOp = rand.nextInt(100);
                    if(randOp < 15) ops = Operation.ADD;
                    else if(randOp <= 30) ops = Operation.MULTIPLY;
                    else if(randOp <= 65) ops = Operation.SUBTRACT;
                    else if(randOp <= 100) ops = Operation.DIVIDE;
                } else if(points.size() == 3){
                    int randOp = rand.nextInt(2);
                    if(randOp == 0) ops = Operation.ADD;
                    else if(randOp == 1) ops = Operation.MULTIPLY;
                } else if(points.size() > 3){
                    ops = Operation.ADD;
                }

                // get cage solution
                String answer = "0";
                float s = -1.0f;
                switch(ops){
                    case ADD:
                        for(Point p : points){
                            if(s == -1) s = solution[p.x][p.y];
                            else s += solution[p.x][p.y];
                        }
                        if(s < 0) s *= -1.0f;
                        answer = ((int)s) + "";
                        break;
                    case SUBTRACT:
                        for(Point p : points){
                            if(s == -1) s = solution[p.x][p.y];
                            else s -= solution[p.x][p.y];
                        }
                        if(s < 0) s *= -1.0f;
                        answer = ((int)s) + "";
                        break;
                    case MULTIPLY:
                        for(Point p : points){
                            if(s == -1) s = solution[p.x][p.y];
                            else s *= solution[p.x][p.y];
                        }
                        if(s < 0) s *= -1.0f;
                        answer = ((int)s) + "";
                        break;
                    case DIVIDE:
                        for(Point p : points){
                            if(solution[p.x][p.y] == 0){
                                s = 0.5f;
                                break;
                            }
                            if(s == -1) s = solution[p.x][p.y];
                            else s /= solution[p.x][p.y];
                        }
                        if(s - (int)s != 0){
                            ops = Operation.SUBTRACT;
                            s = -1;
                            for(Point p : points){
                                if(s == -1) s = solution[p.x][p.y];
                                else s -= solution[p.x][p.y];
                            }
                        }
                        if(s < 0) s *= -1.0f;
                        answer = ((int)s) + "";
                        break;
                    case EQUAL:
                        answer = solution[points.get(0).x][points.get(0).y] + "";
                        break;
                }

                // add new cage
                c.add(new Triplet<Operation, String, List<Point>>(ops, answer, points));
            }
        }
        return c;
    }

    // generate a random board of size
    private int[][] genBoard() {
        int[][] b = new int[size][size];
        int offset = 1;
        if(size == 10) offset = 0;
        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                int num = x + y + offset;
                while(num > (size == 10 ? size - 1 : size)) num -= size;
                b[x][y] = num;
            }
        }

        for(int x = 0; x < 5; x++){
            int col1 = rand.nextInt(size);
            int col2 = col1;
            while (col1 == col2){
                col2 = rand.nextInt(size);
            }
            int[] temp = b[col1];
            b[col1] = b[col2];
            b[col2] = temp;
        }

        for(int y = 0; y < 5; y++){
            int row1 = rand.nextInt(size);
            int row2 = row1;
            while (row1 == row2){
                row2 = rand.nextInt(size);
            }
            // Log.d("rand",row1 + " Y " + row2);
            for(int s = 0; s < size; s++){
                int temp = b[s][row1];
                b[s][row1] = b[s][row2];
                b[s][row2] = temp;
            }
        }

        return b;
    }

    public void setNumber(Point point, int number){
        if(point.x < 0 || point.y < 0 || point.x >= size || point.y >= size) return;
        this.numbers[point.x][point.y] = number;
    }

    public int getNumber(Point point){
        if(point.x < 0 || point.y < 0 || point.x >= size || point.y >= size) return -1;
        return this.numbers[point.x][point.y];
    }

    public int getNumber(int x, int y){
        if(x < 0 || y < 0 || x >= size || y >= size) return -1;
        return this.numbers[x][y];
    }

    public int getSolution(Point point){
        if(point.x < 0 || point.y < 0 || point.x >= size || point.y >= size) return -1;
        return this.solution[point.x][point.y];
    }

    public int getSolution(int x, int y){
        if(x < 0 || y < 0 || x >= size || y >= size) return -1;
        return this.solution[x][y];
    }

    public void setNumberNote(Point point, int number, boolean b){
        if(number < 0 || number > 9) return;
        if(point.x < 0 || point.y < 0 || point.x >= size || point.y >= size) return;
        this.numberNotes[point.x][point.y][number] = b;
    }

    public void setNumberNote(Point point, int number){
        if(number < 0 || number > 9) return;
        if(point.x < 0 || point.y < 0 || point.x >= size || point.y >= size) return;
        this.numberNotes[point.x][point.y][number] = !this.numberNotes[point.x][point.y][number];
    }

    public boolean getNumberNote(Point point, int number){
        if(number < 0 || number > 9) return false;
        if(point.x < 0 || point.y < 0 || point.x >= size || point.y >= size) return false;
        return this.numberNotes[point.x][point.y][number];
    }

    public int getSize(){
        return size;
    }

    // get answer for cells in cage
    public int getAnswer(Triplet<Operation, String, List<Point>> cage){
        int ret = -1;
        switch(cage.x){
            case ADD:
                for(Point p : cage.z){
                    if(ret == -1) ret = this.numbers[p.x][p.y];
                    else ret += this.numbers[p.x][p.y];
                }
                break;
            case SUBTRACT:
                for(Point p : cage.z){
                    if(ret == -1) ret = this.numbers[p.x][p.y];
                    else ret -= this.numbers[p.x][p.y];
                }
                break;
            case MULTIPLY:
                for(Point p : cage.z){
                    if(ret == -1) ret = this.numbers[p.x][p.y];
                    else ret *= this.numbers[p.x][p.y];
                }
                break;
            case DIVIDE:
                for(Point p : cage.z){
                    if(ret == -1) ret = this.numbers[p.x][p.y];
                    else ret /= this.numbers[p.x][p.y];
                }
                break;
            case EQUAL:
                for(Point p : cage.z){
                    ret = this.numbers[p.x][p.y];
                }
                break;
        }
        if(ret < 0) ret *= -1;
        return ret;
    }

    public boolean checkWin(){
        // check complete
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(numbers[i][j] == -1) {
                    Log.d("checkWin", "not finished");
                    return false;
                }
            }
        }
        // check cage answers
        for(Triplet<Board.Operation, String, List<Point>> t : cages){
            if(getAnswer(t) != Integer.parseInt(t.y)) {
                Log.d("checkWin", "cage answer incorrect: " + getAnswer(t) + " != " + Integer.parseInt(t.y));
                return false;
            }
        }
        // check uniqueness
        // columns
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                for(int yy = 0; yy < size; yy++){
                    if(numbers[x][y] == numbers[x][yy] && y != yy) {
                        Log.d("checkWin", "column duplicate: (" + x + "," + y + ") (" + x + "," + yy + ")");
                        return false;
                    }
                }
            }
        }
        // rows
        for(int y = 0; y < size; y++){
            for(int x = 0; x < size; x++){
                for(int xx = 0; xx < size; xx++){
                    if(numbers[x][y] == numbers[xx][y] && x != xx) {
                        Log.d("checkWin", "row duplicate: (" + x + "," + y + ") (" + xx + "," + y + ")");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // return true if cage contains point
    public static boolean cageContains(Triplet<Operation, String, List<Point>> cage, Point point){
        for(Point p: cage.z){
            if(p.x == point.x && p.y == point.y) return true;
        }
        return false;
    }

    // find cell to display cage operation rendering
    public static Point getCageOperationBox(Triplet<Operation, String, List<Point>> cage){
        if (cage.z.size() <= 0) return null;
        Point ret = new Point();
        ret.x = cage.z.get(0).x;
        ret.y = cage.z.get(0).y;
        for(Point p: cage.z){
            if(p.y <= ret.y){
                if(p.x <= ret.x){
                    ret.x = p.x;
                    ret.y = p.y;
                }
            }
        }
        return ret;
    }

    // init test cases
    public static void InitDefault(){
        Triplet<Operation, String, List<Point>> t;
        ArrayList<Point> a;

        grid3 = new Board(3);
        grid3.size = 3;
        grid3.solution = new int[][]{
                {3, 1, 2},
                {1, 2, 3},
                {2, 3, 1},
        };
        grid3.cages = new ArrayList<Triplet<Operation, String, List<Point>>>();

        a = new ArrayList<Point>();
        a.add(new Point(0, 0));
        a.add(new Point(1, 0));
        a.add(new Point(1, 1));
        t = new Triplet<Operation, String, List<Point>>(Operation.ADD, "6", a);
        grid3.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(2, 0));
        a.add(new Point(2, 1));
        t = new Triplet<Operation, String, List<Point>>(Operation.ADD, "5", a);
        grid3.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(0, 1));
        a.add(new Point(0, 2));
        t = new Triplet<Operation, String, List<Point>>(Operation.ADD, "3", a);
        grid3.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(1, 2));
        a.add(new Point(2, 2));
        t = new Triplet<Operation, String, List<Point>>(Operation.ADD, "4", a);
        grid3.cages.add(t);

        grid5 = new Board(5);
        grid5.size = 5;
        grid5.solution = new int[][]{
                {2, 4, 5, 1, 3},
                {4, 3, 1, 5, 2},
                {5, 2, 3, 4, 1},
                {1, 5, 2, 3, 4},
                {3, 1, 4, 2, 5},
        };
        grid5.cages = new ArrayList<Triplet<Operation, String, List<Point>>>();

        a = new ArrayList<Point>();
        a.add(new Point(0, 0));
        a.add(new Point(0, 1));
        a.add(new Point(1, 1));
        a.add(new Point(2, 1));
        t = new Triplet<Operation, String, List<Point>>(Operation.ADD, "11", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(1, 0));
        a.add(new Point(2, 0));
        a.add(new Point(3, 0));
        t = new Triplet<Operation, String, List<Point>>(Operation.ADD, "10", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(4, 0));
        a.add(new Point(4, 1));
        a.add(new Point(4, 2));
        t = new Triplet<Operation, String, List<Point>>(Operation.MULTIPLY, "12", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(3, 1));
        a.add(new Point(3, 2));
        t = new Triplet<Operation, String, List<Point>>(Operation.MULTIPLY, "10", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(0, 2));
        a.add(new Point(0, 3));
        t = new Triplet<Operation, String, List<Point>>(Operation.DIVIDE, "5", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(1, 2));
        t = new Triplet<Operation, String, List<Point>>(Operation.EQUAL, "1", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(2, 2));
        a.add(new Point(2, 3));
        a.add(new Point(2, 4));
        t = new Triplet<Operation, String, List<Point>>(Operation.ADD, "8", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(1, 3));
        a.add(new Point(1, 4));
        t = new Triplet<Operation, String, List<Point>>(Operation.SUBTRACT, "3", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(3, 3));
        a.add(new Point(4, 3));
        t = new Triplet<Operation, String, List<Point>>(Operation.SUBTRACT, "1", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(0, 4));
        t = new Triplet<Operation, String, List<Point>>(Operation.EQUAL, "3", a);
        grid5.cages.add(t);

        a = new ArrayList<Point>();
        a.add(new Point(3, 4));
        a.add(new Point(4, 4));
        t = new Triplet<Operation, String, List<Point>>(Operation.MULTIPLY, "20", a);
        grid5.cages.add(t);
    }

}