
package model;

public class Cell {

    private int row;
    private int col;
    private boolean wall;

    public Cell(int row, int col, boolean wall) {
        this.row = row;
        this.col = col;
        this.wall = wall;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isWall() {
        return wall;
    }

    public void setWall(boolean wall) {
        this.wall = wall;
    }
}
