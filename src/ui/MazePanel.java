
package ui;

import model.Maze;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.function.Consumer;

public class MazePanel extends JPanel {

    private Maze maze;

    private String selectionMode = "";

    private int cellSize;
    private int offsetX;
    private int offsetY;

    private final int padding = 20;

    private Consumer<String> statusListener;

    // Pixel-art colours
    private static final Color WALL_DARK =
        new Color(57, 82, 65);

    private static final Color WALL_LIGHT =
        new Color(120, 145, 105);

    private static final Color MOSS =
        new Color(77, 108, 68);

    private static final Color PATH_LIGHT =
        new Color(242, 229, 197);

    private static final Color PATH_DARK =
        new Color(196, 177, 141);

    public MazePanel(Maze maze) {

        this.maze = maze;

        setBackground(Theme.BACKGROUND);
        setPreferredSize(new Dimension(600, 500));

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (selectionMode.isEmpty()) {
                    return;
                }

                updateCellSize();

                int x = e.getX() - offsetX;
                int y = e.getY() - offsetY;

                if (x < 0 || y < 0) {
                    updateStatus("Please click inside the maze.");
                    return;
                }

                int col = x / cellSize;
                int row = y / cellSize;

                if (row >= maze.getRows()
                        || col >= maze.getCols()) {

                    updateStatus("Please click inside the maze.");
                    return;
                }

                if (maze.getCell(row, col).isWall()) {

                    updateStatus(
                        "Cannot select a wall. Choose an open cell."
                    );
                    return;
                }

                try {

                    if (selectionMode.equals("START")) {

                        maze.setStart(row, col);
                        updateStatus("Start position updated.");

                    } else if (selectionMode.equals("END")) {

                        maze.setEnd(row, col);
                        updateStatus("End position updated.");
                    }

                    selectionMode = "";
                    repaint();

                } catch (IllegalArgumentException ex) {
                    updateStatus(ex.getMessage());
                }
            }
        });
    }

    public void selectStart() {

        selectionMode = "START";
        updateStatus("Select a cell for START.");
    }

    public void selectEnd() {

        selectionMode = "END";
        updateStatus("Select a cell for END.");
    }

    public void setStatusListener(
            Consumer<String> listener) {

        statusListener = listener;
    }

    private void updateStatus(String message) {

        if (statusListener != null) {
            statusListener.accept(message);
        }
    }

    private void updateCellSize() {

        int availableWidth =
            Math.max(1, getWidth() - 2 * padding);

        int availableHeight =
            Math.max(1, getHeight() - 2 * padding);

        int cellWidth =
            availableWidth / maze.getCols();

        int cellHeight =
            availableHeight / maze.getRows();

        cellSize = Math.max(
            1,
            Math.min(cellWidth, cellHeight)
        );

        int mazeWidth = maze.getCols() * cellSize;
        int mazeHeight = maze.getRows() * cellSize;

        offsetX = (getWidth() - mazeWidth) / 2;
        offsetY = (getHeight() - mazeHeight) / 2;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        updateCellSize();

        Graphics2D g2 = (Graphics2D) g.create();

        // Keep the artwork sharp and pixelated
        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF
        );

        for (int row = 0; row < maze.getRows(); row++) {

            for (int col = 0; col < maze.getCols(); col++) {

                int x = offsetX + col * cellSize;
                int y = offsetY + row * cellSize;

                boolean isWall =
                    maze.getCell(row, col).isWall();

                if (isWall) {
                    drawWall(g2, x, y, row, col);
                } else {
                    drawPath(g2, x, y, row, col);
                }

                if (row == maze.getStartRow()
                        && col == maze.getStartCol()) {

                    drawMarker(g2, x, y, "S", Theme.START);

                } else if (row == maze.getEndRow()
                        && col == maze.getEndCol()) {

                    drawMarker(g2, x, y, "E", Theme.END);
                }
            }
        }

        g2.dispose();
    }

    // Moss-covered stone wall
    private void drawWall(
            Graphics2D g, int x, int y,
            int row, int col) {

        g.setColor(Theme.WALL);
        g.fillRect(x, y, cellSize, cellSize);

        if (cellSize >= 12) {

            int pixel = Math.max(2, cellSize / 8);

            // Stone highlight
            g.setColor(WALL_LIGHT);
            g.fillRect(
                x + pixel,
                y + pixel,
                cellSize - 2 * pixel,
                pixel
            );

            // Dark stone shadow
            g.setColor(WALL_DARK);
            g.fillRect(
                x + pixel,
                y + cellSize - 2 * pixel,
                cellSize - 2 * pixel,
                pixel
            );

            // Small moss patches
            if ((row * 7 + col * 11) % 4 == 0) {

                g.setColor(MOSS);
                g.fillRect(
                    x + pixel,
                    y + 2 * pixel,
                    pixel * 2,
                    pixel
                );
            }
        }

        // Dark border between tiles
        g.setColor(Theme.BORDER);
        g.drawRect(x, y, cellSize - 1, cellSize - 1);
    }

    // Weathered garden pathway
    private void drawPath(
            Graphics2D g, int x, int y,
            int row, int col) {

        g.setColor(Theme.PATH);
        g.fillRect(x, y, cellSize, cellSize);

        if (cellSize >= 12) {

            int pixel = Math.max(2, cellSize / 9);

            // Light stone highlight
            g.setColor(PATH_LIGHT);
            g.fillRect(
                x + pixel,
                y + pixel,
                pixel * 2,
                pixel
            );

            // Small weathered stone marks
            if ((row * 5 + col * 3) % 3 == 0) {

                g.setColor(PATH_DARK);
                g.fillRect(
                    x + cellSize - 3 * pixel,
                    y + cellSize - 3 * pixel,
                    pixel,
                    pixel
                );
            }
        }

        g.setColor(Theme.BORDER);
        g.drawRect(x, y, cellSize - 1, cellSize - 1);
    }

    // Pixel-style Start and End markers
    private void drawMarker(
            Graphics2D g, int x, int y,
            String label, Color color) {

        int inset = cellSize >= 12
            ? Math.max(1, cellSize / 10)
            : 0;

        // Dark outer border
        g.setColor(Theme.BACKGROUND);
        g.fillRect(
            x + inset,
            y + inset,
            cellSize - 2 * inset,
            cellSize - 2 * inset
        );

        // Coloured inner marker
        int border = cellSize >= 12 ? 2 : 1;

        g.setColor(color);
        g.fillRect(
            x + inset + border,
            y + inset + border,
            Math.max(1, cellSize - 2 * inset - 2 * border),
            Math.max(1, cellSize - 2 * inset - 2 * border)
        );

        // Draw S or E
        g.setColor(Theme.BACKGROUND);

        g.setFont(new Font(
            "Monospaced",
            Font.BOLD,
            Math.max(9, cellSize / 2)
        ));

        FontMetrics fm = g.getFontMetrics();

        int textX =
            x + (cellSize - fm.stringWidth(label)) / 2;

        int textY =
            y + (cellSize + fm.getAscent()
                - fm.getDescent()) / 2;

        g.drawString(label, textX, textY);
    }
}
