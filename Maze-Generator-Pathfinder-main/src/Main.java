
import javax.swing.SwingUtilities;
import ui.MazeFrame;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new MazeFrame();
        });
    }
}
