package uj.wmii.pwj.collections;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class BattleshipGeneratorImpl implements BattleshipGenerator {
    private static final char SHIP = '#';
    private static final char WATER = '.';
    private static final char FORBIDDEN = '*';
    private static final int SIZE = 10;
    private final Random rand = new Random();

    @Override
    public String generateMap() {
        char[][] board = new char[SIZE][SIZE];
        for (char[] row : board) Arrays.fill(row, WATER);

        int[] ships = {4,3,3,2,2,2,1,1,1,1};

        for (int shipSize : ships) {
            placeShip(board, shipSize);
        }

        // Convert to String and replace '*' to '.'
        StringBuilder result = new StringBuilder(100);
        for (char[] row : board)
            for (char c : row)
                result.append(c == FORBIDDEN ? WATER : c);
        return result.toString();
    }

    // I am using something similar to BFS algorithm to place ships with different shapes
    // maybe it's not the simplest approach, but I honestly couldn't figure out another way
    private void placeShip(char[][] board, int shipSize) {
        boolean placed = false;
        while (!placed) {
            int x = rand.nextInt(SIZE);
            int y = rand.nextInt(SIZE);
            if (board[x][y] != WATER) continue;

            List<Point> ship = new ArrayList<>();
            ship.add(new Point(x, y));
            board[x][y] = SHIP;

            while (ship.size() < shipSize) {
                List<Point> options = validNeighbors(ship, board);
                if (options.isEmpty()) {// if the ship is stuck and cannot be completed
                    ship.forEach(p -> board[p.x][p.y] = WATER); // rollback
                    ship.clear();
                    break;
                }
                Point next = options.get(rand.nextInt(options.size()));
                ship.add(next);
                board[next.x][next.y] = SHIP;
            }

            if (!ship.isEmpty() && ship.size() == shipSize) {
                markForbidden(board, ship);
                placed = true;
            }
        }
    }

    private List<Point> validNeighbors(List<Point> ship, char[][] board) {
        List<Point> res = new ArrayList<>();
        for (Point p : ship) {
            for (int[] d : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
                int nx = p.x + d[0], ny = p.y + d[1];
                if (nx>=0 && ny>=0 && nx<SIZE && ny<SIZE && board[nx][ny]==WATER) {
                    res.add(new Point(nx, ny));
                }
            }
        }
        return res;
    }

    private void markForbidden(char[][] board, List<Point> ship) {
        for (Point p : ship) {
            for (int dx=-1; dx<=1; dx++) {
                for (int dy=-1; dy<=1; dy++) {
                    int nx = p.x + dx, ny = p.y + dy;
                    if (nx>=0 && ny>=0 && nx<SIZE && ny<SIZE && board[nx][ny]==WATER)
                        board[nx][ny] = FORBIDDEN;
                }
            }
        }
    }

    record Point(int x, int y) {}
}