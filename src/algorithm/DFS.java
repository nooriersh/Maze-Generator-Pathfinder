
package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import model.Maze;
import model.Cell;

public class DFS {

    // Directions: down, up, right, left
    private static final int[] ROW_DIRECTIONS = {1, -1, 0, 0};
    private static final int[] COL_DIRECTIONS = {0, 0, 1, -1};

    public static Pathfinder findPath(Maze maze) {

        List<Cell> visitedCells = new ArrayList<>();
        List<Cell> path = new ArrayList<>();

        int rows = maze.getRows();
        int cols = maze.getCols();

        int startRow = maze.getStartRow();
        int startCol = maze.getStartCol();

        int endRow = maze.getEndRow();
        int endCol = maze.getEndCol();

        boolean[][] visited = new boolean[rows][cols];

        Cell[][] parent = new Cell[rows][cols];

        Stack<Cell> stack = new Stack<>();

        Cell start = maze.getCell(startRow, startCol);

        if (start.isWall()) {
            return new Pathfinder(visitedCells, path, false);
        }

        stack.push(start);
        visited[startRow][startCol] = true;

        boolean found = false;

        while (!stack.isEmpty()) {

            Cell current = stack.pop();

            visitedCells.add(current);

            int currentRow = current.getRow();
            int currentCol = current.getCol();

            if (currentRow == endRow && currentCol == endCol) {
                found = true;
                break;
            }

            for (int i = 0; i < 4; i++) {

                int newRow = currentRow + ROW_DIRECTIONS[i];
                int newCol = currentCol + COL_DIRECTIONS[i];

                if (newRow < 0 || newRow >= rows ||
                    newCol < 0 || newCol >= cols) {
                    continue;
                }

                Cell neighbour = maze.getCell(newRow, newCol);

                if (neighbour.isWall() || visited[newRow][newCol]) {
                    continue;
                }

                visited[newRow][newCol] = true;

                parent[newRow][newCol] = current;

                stack.push(neighbour);
            }
        }

        if (found) {

            Cell current = maze.getCell(endRow, endCol);

            while (current != null) {

                path.add(current);

                current = parent[current.getRow()][current.getCol()];
            }

            Collections.reverse(path);
        }

        return new Pathfinder(visitedCells, path, found);
    }
}

