
package ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;

import java.awt.*;
import java.awt.event.*;

import model.Maze;
import model.Difficulty;

import algorithm.MazeGenerator;
import algorithm.BFS;
import algorithm.DFS;
import algorithm.Pathfinder;

public class MazeFrame extends JFrame {

    private Maze maze;
    private MazeGenerator generator;
    private MazePanel mazePanel;
    private JScrollPane scrollPane;
    private JLabel statusLabel;

    public MazeFrame() {

        setTitle("Maze Generator and Path Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(Theme.BACKGROUND);

        // Create initial maze
        maze = new Maze(15, 15);

        generator = new MazeGenerator(maze);
        generator.generateMaze(Difficulty.MEDIUM);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Theme.BACKGROUND);

        headerPanel.setBorder(
            BorderFactory.createEmptyBorder(8, 20, 5, 20)
        );

        JLabel titleLabel = new JLabel(
            "THE OVERGROWN MAZE",
            SwingConstants.CENTER
        );

        titleLabel.setFont(
            new Font("Serif", Font.BOLD, 26)
        );

        titleLabel.setForeground(Theme.TEXT);

        JLabel subtitleLabel = new JLabel(
            "A GARDEN OF TWISTING PATHS",
            SwingConstants.CENTER
        );

        subtitleLabel.setFont(
            new Font("SansSerif", Font.PLAIN, 11)
        );

        subtitleLabel.setForeground(Theme.STONE);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // Status label
        statusLabel = new JLabel(
            "Ready to explore.",
            SwingConstants.CENTER
        );

        statusLabel.setFont(
            new Font("Monospaced", Font.PLAIN, 12)
        );

        statusLabel.setForeground(Theme.TEXT);

        // Create maze panel
        mazePanel = new MazePanel(maze);

        mazePanel.setStatusListener(
            message -> statusLabel.setText(message)
        );

        scrollPane = createMazeScrollPane();

        add(scrollPane, BorderLayout.CENTER);

        // Bottom section
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Theme.BACKGROUND);

        // Control panel
        JPanel controlPanel = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 10, 12)
        );

        controlPanel.setBackground(Theme.PATH);

        controlPanel.setBorder(
            BorderFactory.createEmptyBorder(4, 12, 4, 12)
        );

        // Rows input
        JLabel rowsLabel = createLabel("Rows:");
        JTextField rowsField = createTextField("15");

        // Columns input
        JLabel colsLabel = createLabel("Columns:");
        JTextField colsField = createTextField("15");

        // Difficulty dropdown
        JLabel difficultyLabel = createLabel("Difficulty:");

        JComboBox<Difficulty> difficultyBox =
            createDifficultyBox();

        // Existing buttons
        JButton generateButton =
            createButton("Generate Maze");

        JButton startButton =
            createButton("Set Start");

        JButton endButton =
            createButton("Set End");

        // New pathfinding buttons
        JButton bfsButton =
            createButton("BFS");

        JButton dfsButton =
            createButton("DFS");

        JButton clearButton =
            createButton("Clear Path");

        // Add controls
        controlPanel.add(rowsLabel);
        controlPanel.add(rowsField);

        controlPanel.add(colsLabel);
        controlPanel.add(colsField);

        controlPanel.add(difficultyLabel);
        controlPanel.add(difficultyBox);

        controlPanel.add(generateButton);
        controlPanel.add(startButton);
        controlPanel.add(endButton);

        controlPanel.add(bfsButton);
        controlPanel.add(dfsButton);
        controlPanel.add(clearButton);

        bottomPanel.add(controlPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());

        statusPanel.setBackground(Theme.BACKGROUND);

        statusPanel.setBorder(
            BorderFactory.createEmptyBorder(6, 10, 8, 10)
        );

        statusPanel.add(statusLabel, BorderLayout.CENTER);

        bottomPanel.add(statusPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        // Generate Maze button
        generateButton.addActionListener(e -> {

            try {

                int rows = Integer.parseInt(
                    rowsField.getText().trim()
                );

                int cols = Integer.parseInt(
                    colsField.getText().trim()
                );

                // Validate dimensions
                if (rows < 5 || cols < 5
                        || rows % 2 == 0
                        || cols % 2 == 0
                        || rows > 51
                        || cols > 51) {

                    JOptionPane.showMessageDialog(
                        this,
                        "Enter odd dimensions between 5 and 51."
                    );

                    return;
                }

                Difficulty selectedDifficulty =
                    (Difficulty) difficultyBox.getSelectedItem();

                // Generate new maze
                maze = new Maze(rows, cols);

                generator = new MazeGenerator(maze);

                generator.generateMaze(selectedDifficulty);

                // Remove old maze display
                remove(scrollPane);

                // Create updated maze display
                mazePanel = new MazePanel(maze);

                mazePanel.setStatusListener(
                    message -> statusLabel.setText(message)
                );

                scrollPane = createMazeScrollPane();

                add(scrollPane, BorderLayout.CENTER);

                statusLabel.setText("A new maze awaits.");

                revalidate();
                repaint();

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid numbers for rows and columns."
                );
            }
        });

        // Set Start button
        startButton.addActionListener(e -> {
            mazePanel.selectStart();
        });

        // Set End button
        endButton.addActionListener(e -> {
            mazePanel.selectEnd();
        });

        // BFS button
        bfsButton.addActionListener(e -> {
            runBFS();
        });

        // DFS button
        dfsButton.addActionListener(e -> {
            runDFS();
        });

        // Clear Path button
        clearButton.addActionListener(e -> {

            mazePanel.clearPathfinding();

            statusLabel.setText(
                "Pathfinding highlights cleared."
            );
        });

        // Window settings
        setSize(900, 700);
        setMinimumSize(new Dimension(650, 500));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Run Breadth-First Search
    private void runBFS() {

        mazePanel.clearPathfinding();

        statusLabel.setText("BFS is exploring the maze...");

        Pathfinder result = BFS.findPath(maze);

        displayResult(result, "BFS");
    }

    // Run Depth-First Search
    private void runDFS() {

        mazePanel.clearPathfinding();

        statusLabel.setText("DFS is exploring the maze...");

        Pathfinder result = DFS.findPath(maze);

        displayResult(result, "DFS");
    }

    // Display pathfinding results
    private void displayResult(
            Pathfinder result,
            String algorithmName) {

        mazePanel.setPathfinderResult(result);

        if (result == null) {

            statusLabel.setText(
                algorithmName + ": No result returned."
            );

            return;
        }

        int visitedCount =
            result.getVisitedCells().size();

        if (result.isPathFound()) {

            int pathLength =
                result.getPath().size();

            statusLabel.setText(
                algorithmName
                + " | Path found! Visited: "
                + visitedCount
                + " | Path length: "
                + pathLength
            );

        } else {

            statusLabel.setText(
                algorithmName
                + " | No path found. Visited: "
                + visitedCount
            );
        }
    }

    // Create maze scroll pane
    private JScrollPane createMazeScrollPane() {

        JScrollPane pane = new JScrollPane(mazePanel);

        pane.setBorder(
            BorderFactory.createEmptyBorder()
        );

        pane.getViewport().setBackground(
            Theme.BACKGROUND
        );

        return pane;
    }

    // Styled labels
    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(
            new Font("Monospaced", Font.BOLD, 12)
        );

        label.setForeground(Theme.BACKGROUND);

        return label;
    }

    // Pixel-style text fields
    private JTextField createTextField(String text) {

        JTextField field = new JTextField(text, 3);

        field.setFont(
            new Font("Monospaced", Font.BOLD, 13)
        );

        field.setHorizontalAlignment(
            JTextField.CENTER
        );

        field.setBackground(Theme.TEXT);
        field.setForeground(Theme.BACKGROUND);
        field.setCaretColor(Theme.BACKGROUND);

        field.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    Theme.BACKGROUND, 3
                ),
                BorderFactory.createEmptyBorder(
                    7, 6, 7, 6
                )
            )
        );

        return field;
    }

    // Pixel-style difficulty dropdown
    private JComboBox<Difficulty> createDifficultyBox() {

        JComboBox<Difficulty> box =
            new JComboBox<>(Difficulty.values());

        box.setSelectedItem(Difficulty.MEDIUM);

        box.setFont(
            new Font("Monospaced", Font.BOLD, 12)
        );

        box.setBackground(Theme.TEXT);
        box.setForeground(Theme.BACKGROUND);
        box.setFocusable(false);

        box.setUI(new BasicComboBoxUI() {

            @Override
            protected JButton createArrowButton() {

                JButton arrow = new JButton("▼");

                arrow.setFont(
                    new Font("SansSerif", Font.BOLD, 10)
                );

                arrow.setForeground(Theme.TEXT);
                arrow.setBackground(Theme.WALL);
                arrow.setFocusPainted(false);

                arrow.setBorder(
                    BorderFactory.createMatteBorder(
                        0, 3, 0, 0,
                        Theme.BACKGROUND
                    )
                );

                return arrow;
            }
        });

        box.setBorder(
            BorderFactory.createLineBorder(
                Theme.BACKGROUND, 3
            )
        );

        box.setPreferredSize(
            new Dimension(125, 42)
        );

        return box;
    }

    // Pixel-style button
    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setFont(
            new Font("Monospaced", Font.BOLD, 12)
        );

        button.setBackground(Theme.WALL);
        button.setForeground(Theme.TEXT);

        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        // Square borders and block shadow
        button.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                    Theme.BACKGROUND, 3
                ),
                BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(
                        0, 0, 4, 4,
                        Theme.STONE
                    ),
                    BorderFactory.createEmptyBorder(
                        10, 14, 10, 14
                    )
                )
            )
        );

        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        // Hover and click effects
        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                button.setBackground(
                    Theme.BACKGROUND
                );
            }

            @Override
            public void mouseExited(MouseEvent e) {

                button.setBackground(
                    Theme.WALL
                );
            }

            @Override
            public void mousePressed(MouseEvent e) {

                button.setBackground(
                    Theme.STONE
                );
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                button.setBackground(
                    button.contains(e.getPoint())
                        ? Theme.BACKGROUND
                        : Theme.WALL
                );
            }
        });

        return button;
    }
}
