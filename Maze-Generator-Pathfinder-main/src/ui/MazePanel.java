package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.function.Consumer;

import model.Maze;
import model.Cell;
import algorithm.Pathfinder;

public class MazePanel extends JPanel {

    private Maze maze;

    private boolean[][] visitedCells;
    private boolean[][] solutionPath;

    private String selectionMode = "";

    private Consumer<String> statusListener;

    private Timer animationTimer;

    private List<Cell> animatedVisited;
    private List<Cell> animatedPath;

    private int visitedIndex;
    private int pathIndex;

    private int animationSpeed = 50;
    private boolean showingPath = false;

    private final int cellSize = 30;

    private Color wallColor = new Color(45, 65, 45);
    private Color openColor = new Color(210, 220, 180);
    private Color visitedColor = new Color(120, 180, 130);
    private Color pathColor = new Color(255, 215, 80);
    private Color startColor = new Color(50, 180, 90);
    private Color endColor = new Color(220, 80, 80);

    public MazePanel(Maze maze) {

        this.maze = maze;

        visitedCells = new boolean[maze.getRows()][maze.getCols()];
        solutionPath = new boolean[maze.getRows()][maze.getCols()];

        setBackground(new Color(230, 235, 210));

        setPreferredSize(
            new Dimension(
                maze.getCols() * cellSize,
                maze.getRows() * cellSize
            )
        );

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int col = e.getX() / cellSize;
                int row = e.getY() / cellSize;

                if (!isValidCell(row, col)) {
                    return;
                }

                if (selectionMode.equals("START")) {

                    try {

                        maze.setStart(row, col);

                        selectionMode = "";

                        updateStatus("Start position selected.");

                        repaint();

                    } catch (IllegalArgumentException ex) {

                        updateStatus(ex.getMessage());
                    }

                } else if (selectionMode.equals("END")) {

                    try {

                        maze.setEnd(row, col);

                        selectionMode = "";

                        updateStatus("End position selected.");

                        repaint();

                    } catch (IllegalArgumentException ex) {

                        updateStatus(ex.getMessage());
                    }
                }
            }
        });

    }

    // Select Start position
    public void selectStart() {

        selectionMode = "START";

        updateStatus("Click an open cell to select Start.");

    }

    // Select End position
    public void selectEnd() {

        selectionMode = "END";

        updateStatus("Click an open cell to select End.");

    }

    // Set status listener
    public void setStatusListener(Consumer<String> listener) {

        this.statusListener = listener;

    }

    // Update status
    private void updateStatus(String message) {

        if (statusListener != null) {

            statusListener.accept(message);

        }
    }

    // Check valid cell
    private boolean isValidCell(int row, int col) {

        return row >= 0
            && row < maze.getRows()
            && col >= 0
            && col < maze.getCols();

    }

    // Display results immediately
    public void setPathfinderResult(Pathfinder result) {

        clearPathfinding();

        if (result == null) {
            return;
        }

        for (Cell cell : result.getVisitedCells()) {

            visitedCells[cell.getRow()][cell.getCol()] = true;

        }

        for (Cell cell : result.getPath()) {

            solutionPath[cell.getRow()][cell.getCol()] = true;

        }

        repaint();

    }

    // Clear pathfinding highlights
    public void clearPathfinding() {

        stopAnimation();

        visitedCells =
            new boolean[maze.getRows()][maze.getCols()];

        solutionPath =
            new boolean[maze.getRows()][maze.getCols()];

        repaint();

    }

    // Start animation
    public void animatePathfinderResult(
            Pathfinder result,
            int speed) {

        clearPathfinding();

        if (result == null) {
            return;
        }

        animatedVisited = result.getVisitedCells();
        animatedPath = result.getPath();

        animationSpeed = Math.max(1, speed);

        visitedIndex = 0;
        pathIndex = 0;

        showingPath = false;

        animationTimer = new Timer(
            animationSpeed,
            e -> animateNextCell()
        );

        animationTimer.start();

    }

    // Animate next cell
    private void animateNextCell() {

        if (!showingPath) {

            if (visitedIndex < animatedVisited.size()) {

                Cell cell = animatedVisited.get(visitedIndex);

                visitedCells[cell.getRow()][cell.getCol()] = true;

                visitedIndex++;

                repaint();

                return;

            }

            showingPath = true;

        }

        if (pathIndex < animatedPath.size()) {

            Cell cell = animatedPath.get(pathIndex);

            solutionPath[cell.getRow()][cell.getCol()] = true;

            pathIndex++;

            repaint();

            return;

        }

        stopAnimation();

        updateStatus("Pathfinding animation completed.");

    }

    // Pause animation
    public void pauseAnimation() {

        if (animationTimer != null
                && animationTimer.isRunning()) {

            animationTimer.stop();

            updateStatus("Animation paused.");

        }

    }

    // Resume animation
    public void resumeAnimation() {

        if (animationTimer != null
                && !animationTimer.isRunning()) {

            animationTimer.start();

            updateStatus("Animation resumed.");

        }

    }

    // Change speed
    public void setAnimationSpeed(int speed) {

        animationSpeed = Math.max(1, speed);

        if (animationTimer != null) {

            animationTimer.setDelay(animationSpeed);

        }

    }

    // Stop animation
    public void stopAnimation() {

        if (animationTimer != null) {

            animationTimer.stop();

            animationTimer = null;

        }

    }

    // Reset animation
    public void resetAnimation() {

        stopAnimation();

        clearPathfinding();

        updateStatus("Animation reset.");

    }

    // Draw maze
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (int row = 0; row < maze.getRows(); row++) {

            for (int col = 0; col < maze.getCols(); col++) {

                int x = col * cellSize;
                int y = row * cellSize;

                Cell cell = maze.getCell(row, col);

                // Draw wall or open cell
                if (cell.isWall()) {

                    g2.setColor(wallColor);

                } else {

                    g2.setColor(openColor);

                }

                g2.fillRect(
                    x,
                    y,
                    cellSize,
                    cellSize
                );

                // Draw visited cell
                if (visitedCells[row][col]) {

                    g2.setColor(visitedColor);

                    g2.fillRect(
                        x + 2,
                        y + 2,
                        cellSize - 4,
                        cellSize - 4
                    );

                }

                // Draw solution path
                if (solutionPath[row][col]) {

                    g2.setColor(pathColor);

                    g2.fillRect(
                        x + 4,
                        y + 4,
                        cellSize - 8,
                        cellSize - 8
                    );

                }

                // Grid border
                g2.setColor(new Color(100, 100, 100));

                g2.drawRect(
                    x,
                    y,
                    cellSize,
                    cellSize
                );

            }

        }

        // Draw Start
        drawMarker(
            g2,
            maze.getStartRow(),
            maze.getStartCol(),
            startColor
        );

        // Draw End
        drawMarker(
            g2,
            maze.getEndRow(),
            maze.getEndCol(),
            endColor
        );

    }

    // Draw Start and End marker
    private void drawMarker(
            Graphics2D g2,
            int row,
            int col,
            Color color) {

        int x = col * cellSize;
        int y = row * cellSize;

        g2.setColor(color);

        g2.fillRect(
            x + 5,
            y + 5,
            cellSize - 10,
            cellSize - 10
        );

    }

}