
package algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.Cell;

public class Pathfinder {

    private List<Cell> visitedCells;
    private List<Cell> path;
    private boolean pathFound;

    public Pathfinder() {
        visitedCells = new ArrayList<>();
        path = new ArrayList<>();
        pathFound = false;
    }

    public Pathfinder(List<Cell> visitedCells,
                      List<Cell> path,
                      boolean pathFound) {

        this.visitedCells = new ArrayList<>(visitedCells);
        this.path = new ArrayList<>(path);
        this.pathFound = pathFound;
    }

    public List<Cell> getVisitedCells() {
        return Collections.unmodifiableList(visitedCells);
    }

    public List<Cell> getPath() {
        return Collections.unmodifiableList(path);
    }

    public boolean isPathFound() {
        return pathFound;
    }
}

