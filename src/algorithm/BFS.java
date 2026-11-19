
package algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.Maze;
import model.Cell;

public class BFS {

    // Directions: down, up, right, left
    private static final int[] ROW_DIRECTIONS = {1, -1, 0, 0};
    private static final int[] COL_DIRECTIONS = {0, 0, 1, -1};

    public static Pathfinder findPath(Maze maze) {

        List<Cell> visitedCells = new ArrayList<>();
        List<Cell> path = new ArrayList<>();

        int rows = maze.getRows();
        int cols = maze.getCols();

        // Get Start and End coordinates
        int startRow = maze.getStartRow();
        int startCol = maze.getStartCol();

        int endRow = maze.getEndRow();
        int endCol = maze.getEndCol();

        // Track visited positions
        boolean[][] visited = new boolean[rows][cols];

        // Store parent cells for path reconstruction
        Cell[][] parent = new Cell[rows][cols];

        // Queue for BFS
        Queue<Cell> queue = new LinkedList<>();

        Cell start = maze.getCell(startRow, startCol);

        // Handle invalid starting position
        if (start.isWall()) {
            return new Pathfinder(visitedCells, path, false);
        }

        // Start BFS
        queue.add(start);
        visited[startRow][startCol] = true;

        boolean found = false;

        while (!queue.isEmpty()) {

            Cell current = queue.poll();

            visitedCells.add(current);

            int currentRow = current.getRow();
            int currentCol = current.getCol();

            // Check whether End is reached
            if (currentRow == endRow && currentCol == endCol) {
                found = true;
                break;
            }

            // Explore all four neighbours
            for (int i = 0; i < 4; i++) {

                int newRow = currentRow + ROW_DIRECTIONS[i];
                int newCol = currentCol + COL_DIRECTIONS[i];

                // Check maze boundaries
                if (newRow < 0 || newRow >= rows ||
                    newCol < 0 || newCol >= cols) {
                    continue;
                }

                // Get neighbouring cell
                Cell neighbour = maze.getCell(newRow, newCol);

                // Skip walls and already visited cells
                if (neighbour.isWall() || visited[newRow][newCol]) {
                    continue;
                }

                // Mark visited when added to queue
                visited[newRow][newCol] = true;

                // Store parent for path reconstruction
                parent[newRow][newCol] = current;

                queue.add(neighbour);
            }
        }

        // Reconstruct path if End was reached
        if (found) {

            Cell current = maze.getCell(endRow, endCol);

            while (current != null) {

                path.add(current);

                current = parent[current.getRow()][current.getCol()];
            }

            // Reverse to get Start -> End order
            java.util.Collections.reverse(path);
        }

        return new Pathfinder(visitedCells, path, found);
    }
}

