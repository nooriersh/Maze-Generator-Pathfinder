
package algorithm;

import model.Maze;
import model.Difficulty;

import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import java.util.LinkedList;

public class MazeGenerator {

    private Maze maze;
    private Random random = new Random();

    private final int[] dr = {-1, 1, 0, 0};
    private final int[] dc = {0, 0, -1, 1};

    public MazeGenerator(Maze maze) {
        this.maze = maze;
    }

    // Generate maze according to selected difficulty
    public void generateMaze(Difficulty difficulty) {

        if (difficulty == Difficulty.HARD) {

            boolean[][] bestLayout = null;
            int highestScore = -1;

            for (int attempt = 0; attempt < 30; attempt++) {

                maze.resetMaze();

                carvePassages(1, 1);

                int deadEnds = countDeadEnds();
                int misleadingDeadEnds = countMisleadingDeadEnds();
                int pathLength = calculatePathLength();

                int complexityScore =
                    (misleadingDeadEnds * 10)
                    + (deadEnds * 3)
                    + pathLength;

                if (complexityScore > highestScore) {

                    highestScore = complexityScore;
                    bestLayout = saveLayout();
                }
            }

            if (bestLayout != null) {
                restoreLayout(bestLayout);
            }

        } else {

            maze.resetMaze();

            carvePassages(1, 1);

            if (difficulty == Difficulty.EASY) {
                createExtraPassages(0.15);
            }
        }
    }

    // Randomized DFS maze generation
    private void carvePassages(int row, int col) {

        maze.getCell(row, col).setWall(false);

        ArrayList<Integer> directions = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            directions.add(i);
        }

        Collections.shuffle(directions, random);

        for (int direction : directions) {

            int newRow = row + dr[direction] * 2;
            int newCol = col + dc[direction] * 2;

            if (newRow > 0
                    && newRow < maze.getRows() - 1
                    && newCol > 0
                    && newCol < maze.getCols() - 1
                    && maze.getCell(newRow, newCol).isWall()) {

                int wallRow = row + dr[direction];
                int wallCol = col + dc[direction];

                maze.getCell(wallRow, wallCol).setWall(false);

                carvePassages(newRow, newCol);
            }
        }
    }

    // Create additional connections for Easy difficulty
    private void createExtraPassages(double probability) {

        for (int row = 1; row < maze.getRows() - 1; row++) {

            for (int col = 1; col < maze.getCols() - 1; col++) {

                if (maze.getCell(row, col).isWall()
                        && random.nextDouble() < probability) {

                    boolean horizontal =
                        !maze.getCell(row, col - 1).isWall()
                        && !maze.getCell(row, col + 1).isWall();

                    boolean vertical =
                        !maze.getCell(row - 1, col).isWall()
                        && !maze.getCell(row + 1, col).isWall();

                    if (horizontal || vertical) {
                        maze.getCell(row, col).setWall(false);
                    }
                }
            }
        }
    }

    // Calculate shortest path length using BFS
    private int calculatePathLength() {

        int rows = maze.getRows();
        int cols = maze.getCols();

        int startRow = maze.getStartRow();
        int startCol = maze.getStartCol();

        int endRow = maze.getEndRow();
        int endCol = maze.getEndCol();

        boolean[][] visited = new boolean[rows][cols];
        int[][] distance = new int[rows][cols];

        Queue<int[]> queue = new LinkedList<>();

        if (maze.getCell(startRow, startCol).isWall()
                || maze.getCell(endRow, endCol).isWall()) {
            return -1;
        }

        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        while (!queue.isEmpty()) {

            int[] current = queue.poll();

            int row = current[0];
            int col = current[1];

            if (row == endRow && col == endCol) {
                return distance[row][col];
            }

            for (int i = 0; i < 4; i++) {

                int newRow = row + dr[i];
                int newCol = col + dc[i];

                if (newRow >= 0 && newRow < rows
                        && newCol >= 0 && newCol < cols
                        && !visited[newRow][newCol]
                        && !maze.getCell(newRow, newCol).isWall()) {

                    visited[newRow][newCol] = true;

                    distance[newRow][newCol] =
                        distance[row][col] + 1;

                    queue.add(new int[]{newRow, newCol});
                }
            }
        }

        return -1;
    }

    // Count cells that have only one open neighbour
    private int countDeadEnds() {

        int deadEnds = 0;

        for (int row = 1; row < maze.getRows() - 1; row++) {

            for (int col = 1; col < maze.getCols() - 1; col++) {

                if (maze.getCell(row, col).isWall()) {
                    continue;
                }

                int openNeighbours = 0;

                for (int i = 0; i < 4; i++) {

                    int newRow = row + dr[i];
                    int newCol = col + dc[i];

                    if (!maze.getCell(newRow, newCol).isWall()) {
                        openNeighbours++;
                    }
                }

                if (openNeighbours == 1) {
                    deadEnds++;
                }
            }
        }

        return deadEnds;
    }

    // Count dead ends outside the shortest solution path
    private int countMisleadingDeadEnds() {

        int rows = maze.getRows();
        int cols = maze.getCols();

        int startRow = maze.getStartRow();
        int startCol = maze.getStartCol();

        int endRow = maze.getEndRow();
        int endCol = maze.getEndCol();

        if (maze.getCell(startRow, startCol).isWall()
                || maze.getCell(endRow, endCol).isWall()) {
            return 0;
        }

        boolean[][] visited = new boolean[rows][cols];
        boolean[][] solutionPath = new boolean[rows][cols];

        int[][] parentRow = new int[rows][cols];
        int[][] parentCol = new int[rows][cols];

        Queue<int[]> queue = new LinkedList<>();

        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        while (!queue.isEmpty()) {

            int[] current = queue.poll();

            int row = current[0];
            int col = current[1];

            if (row == endRow && col == endCol) {
                break;
            }

            for (int i = 0; i < 4; i++) {

                int newRow = row + dr[i];
                int newCol = col + dc[i];

                if (newRow >= 0 && newRow < rows
                        && newCol >= 0 && newCol < cols
                        && !visited[newRow][newCol]
                        && !maze.getCell(newRow, newCol).isWall()) {

                    visited[newRow][newCol] = true;

                    parentRow[newRow][newCol] = row;
                    parentCol[newRow][newCol] = col;

                    queue.add(new int[]{newRow, newCol});
                }
            }
        }

        if (!visited[endRow][endCol]) {
            return 0;
        }

        // Reconstruct the shortest solution path
        int row = endRow;
        int col = endCol;

        while (row != startRow || col != startCol) {

            solutionPath[row][col] = true;

            int previousRow = parentRow[row][col];
            int previousCol = parentCol[row][col];

            row = previousRow;
            col = previousCol;
        }

        solutionPath[startRow][startCol] = true;

        // Count dead ends not belonging to the solution path
        int misleadingDeadEnds = 0;

        for (int r = 1; r < rows - 1; r++) {

            for (int c = 1; c < cols - 1; c++) {

                if (maze.getCell(r, c).isWall()
                        || solutionPath[r][c]) {
                    continue;
                }

                int openNeighbours = 0;

                for (int i = 0; i < 4; i++) {

                    int newRow = r + dr[i];
                    int newCol = c + dc[i];

                    if (!maze.getCell(newRow, newCol).isWall()) {
                        openNeighbours++;
                    }
                }

                if (openNeighbours == 1) {
                    misleadingDeadEnds++;
                }
            }
        }

        return misleadingDeadEnds;
    }

    // Save the current maze layout
    private boolean[][] saveLayout() {

        boolean[][] layout =
            new boolean[maze.getRows()][maze.getCols()];

        for (int row = 0; row < maze.getRows(); row++) {

            for (int col = 0; col < maze.getCols(); col++) {

                layout[row][col] =
                    maze.getCell(row, col).isWall();
            }
        }

        return layout;
    }

    // Restore a previously saved maze layout
    private void restoreLayout(boolean[][] layout) {

        for (int row = 0; row < maze.getRows(); row++) {

            for (int col = 0; col < maze.getCols(); col++) {

                maze.getCell(row, col).setWall(
                    layout[row][col]
                );
            }
        }
    }
}
