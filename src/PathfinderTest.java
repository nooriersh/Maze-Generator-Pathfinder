
import algorithm.BFS;
import algorithm.DFS;
import algorithm.Pathfinder;
import model.Maze;
import model.Cell;

import java.util.List;

public class PathfinderTest {

    public static void main(String[] args) {

        // Test different maze sizes
        testOpenMaze(7, 9);
        testOpenMaze(11, 17);
        testOpenMaze(15, 25);

        // Test unreachable destination
        testNoPath();

        System.out.println("\nALL TESTS PASSED!");
    }

    private static void testOpenMaze(int size, int expectedBFSLength) {

        Maze maze = createOpenMaze(size);

        System.out.println("\n--- " + size + "x" + size + " MAZE ---");

        Pathfinder bfs = BFS.findPath(maze);
        Pathfinder dfs = DFS.findPath(maze);

        checkPath("BFS", maze, bfs);
        checkPath("DFS", maze, dfs);

        if (bfs.getPath().size() != expectedBFSLength) {
            throw new AssertionError(
                "BFS did not find the shortest path."
            );
        }

        System.out.println("BFS shortest path verified!");
        System.out.println("BFS path length: " + bfs.getPath().size());
        System.out.println("DFS path length: " + dfs.getPath().size());
    }

    private static void testNoPath() {

        Maze maze = new Maze(5, 5);

        // Make Start and End open
        maze.getCell(1, 1).setWall(false);
        maze.getCell(3, 3).setWall(false);

        // Surround Start with walls
        maze.getCell(0, 1).setWall(true);
        maze.getCell(2, 1).setWall(true);
        maze.getCell(1, 0).setWall(true);
        maze.getCell(1, 2).setWall(true);

        Pathfinder bfs = BFS.findPath(maze);
        Pathfinder dfs = DFS.findPath(maze);

        System.out.println("\n--- NO PATH TEST ---");

        if (bfs.isPathFound() || dfs.isPathFound()) {
            throw new AssertionError("No-path test failed.");
        }

        if (!bfs.getPath().isEmpty() || !dfs.getPath().isEmpty()) {
            throw new AssertionError(
                "Failed algorithms returned a path."
            );
        }

        System.out.println("BFS correctly reports no path.");
        System.out.println("DFS correctly reports no path.");
    }

    private static Maze createOpenMaze(int size) {

        Maze maze = new Maze(size, size);

        // Open all interior cells
        for (int row = 1; row < size - 1; row++) {
            for (int col = 1; col < size - 1; col++) {
                maze.getCell(row, col).setWall(false);
            }
        }

        return maze;
    }

    private static void checkPath(
            String algorithm,
            Maze maze,
            Pathfinder result) {

        if (!result.isPathFound()) {
            throw new AssertionError(
                algorithm + " failed to find a path."
            );
        }

        List<Cell> path = result.getPath();

        if (path.isEmpty()) {
            throw new AssertionError(
                algorithm + " returned an empty path."
            );
        }

        // Check Start
        Cell first = path.get(0);

        if (first.getRow() != maze.getStartRow() ||
            first.getCol() != maze.getStartCol()) {
            throw new AssertionError(
                "Path does not begin at Start."
            );
        }

        // Check End
        Cell last = path.get(path.size() - 1);

        if (last.getRow() != maze.getEndRow() ||
            last.getCol() != maze.getEndCol()) {
            throw new AssertionError(
                "Path does not end at End."
            );
        }

        // Check every cell and movement
        for (int i = 0; i < path.size(); i++) {

            Cell current = path.get(i);

            if (current.isWall()) {
                throw new AssertionError(
                    "Path passes through a wall."
                );
            }

            if (i > 0) {

                Cell previous = path.get(i - 1);

                int distance =
                    Math.abs(current.getRow() - previous.getRow()) +
                    Math.abs(current.getCol() - previous.getCol());

                if (distance != 1) {
                    throw new AssertionError(
                        "Path contains an invalid movement."
                    );
                }
            }
        }

        System.out.println(algorithm + " passed!");
        System.out.println(
            "Visited cells: " + result.getVisitedCells().size()
        );
        System.out.println(
            "Path length: " + path.size()
        );
    }
}
