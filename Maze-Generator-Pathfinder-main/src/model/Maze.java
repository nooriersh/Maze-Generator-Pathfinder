
package model;

public class Maze {

    private int rows;
    private int cols;
    private Cell[][] grid;

    // Start and End coordinates
    private int startRow;
    private int startCol;
    private int endRow;
    private int endCol;

    // Constructor
    public Maze(int rows, int cols) {

        if (rows < 5 || cols < 5 ||
                rows % 2 == 0 || cols % 2 == 0) {

            throw new IllegalArgumentException(
                "Maze dimensions must be odd and at least 5 x 5"
            );
        }

        this.rows = rows;
        this.cols = cols;

        // Default Start and End positions
        startRow = 1;
        startCol = 1;

        endRow = rows - 2;
        endCol = cols - 2;

        // Create the grid
        grid = new Cell[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                grid[i][j] = new Cell(i, j, true);
            }
        }
    }

    // Get maze dimensions
    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    // Get a cell from the grid
    public Cell getCell(int row, int col) {
        return grid[row][col];
    }

    // Reset all cells to walls
    public void resetMaze() {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                grid[i][j].setWall(true);
            }
        }
    }

    // Display maze in terminal
    public void displayMaze() {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                if (grid[i][j].isWall()) {
                    System.out.print("# ");
                } else {
                    System.out.print(". ");
                }
            }

            System.out.println();
        }
    }

    // Get Start coordinates
    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    // Get End coordinates
    public int getEndRow() {
        return endRow;
    }

    public int getEndCol() {
        return endCol;
    }

    // Change Start position
    public void setStart(int row, int col) {

        if (row < 0 || row >= rows ||
                col < 0 || col >= cols ||
                grid[row][col].isWall()) {

            throw new IllegalArgumentException(
                "Start must be an open cell inside the maze."
            );
        }

        if (row == endRow && col == endCol) {

            throw new IllegalArgumentException(
                "Start and End cannot be the same cell."
            );
        }

        startRow = row;
        startCol = col;
    }

    // Change End position
    public void setEnd(int row, int col) {

        if (row < 0 || row >= rows ||
                col < 0 || col >= cols ||
                grid[row][col].isWall()) {

            throw new IllegalArgumentException(
                "End must be an open cell inside the maze."
            );
        }

        if (row == startRow && col == startCol) {

            throw new IllegalArgumentException(
                "Start and End cannot be the same cell."
            );
        }

        endRow = row;
        endCol = col;
    }
}
