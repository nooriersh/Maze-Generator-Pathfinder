
import model.Maze;
import model.Difficulty;
import algorithm.MazeGenerator;

import java.util.LinkedList;
import java.util.Queue;

public class MazeTest {

    public static boolean pathExists(Maze maze) {

        int rows = maze.getRows();
        int cols = maze.getCols();

        boolean[][] visited = new boolean[rows][cols];

        Queue<int[]> queue = new LinkedList<>();

        int startRow = maze.getStartRow();
        int startCol = maze.getStartCol();

        int endRow = maze.getEndRow();
        int endCol = maze.getEndCol();

        if (maze.getCell(startRow, startCol).isWall()
                || maze.getCell(endRow, endCol).isWall()) {
            return false;
        }

        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {

            int[] current = queue.poll();

            int row = current[0];
            int col = current[1];

            if (row == endRow && col == endCol) {
                return true;
            }

            for (int i = 0; i < 4; i++) {

                int newRow = row + dr[i];
                int newCol = col + dc[i];

                if (newRow >= 0 && newRow < rows
                        && newCol >= 0 && newCol < cols
                        && !visited[newRow][newCol]
                        && !maze.getCell(newRow, newCol).isWall()) {

                    visited[newRow][newCol] = true;

                    queue.add(new int[]{newRow, newCol});
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {

        int[][] sizes = {
            {5, 5},
            {15, 15},
            {21, 31},
            {31, 51},
            {51, 51}
        };

        int total = 0;
        int passed = 0;

        for (Difficulty difficulty : Difficulty.values()) {

            for (int[] size : sizes) {

                Maze maze = new Maze(size[0], size[1]);

                MazeGenerator generator = new MazeGenerator(maze);

                generator.generateMaze(difficulty);

                boolean valid = pathExists(maze);

                total++;

                if (valid) {
                    passed++;
                }

                System.out.println(
                    difficulty + " | "
                    + size[0] + "x" + size[1]
                    + " | " + (valid ? "PASS" : "FAIL")
                );
            }
        }

        System.out.println();

        System.out.println(
            "Tests passed: " + passed + "/" + total
        );
    }
}

