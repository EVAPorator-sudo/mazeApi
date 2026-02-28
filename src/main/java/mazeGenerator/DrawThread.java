package mazeGenerator;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class DrawThread extends Thread {

    private final ArrayList<Cell> gridSolution;
    private final Grid gridMaze;

    public DrawThread(ArrayList<Cell> Solution, Grid Maze) {

        gridSolution = Solution;
        gridMaze = Maze;

    }

    @Override
    public void run() {
        BufferedImage image = null;
        if (gridSolution.isEmpty()) {
            image = Draw.gridDraw(gridMaze);
        } else {
            image = Draw.solveDraw(gridMaze, gridSolution);
        }
        final Image displayImage = SwingFXUtils.toFXImage(image, null);
        Platform.runLater(() -> Controller.mazeProperty.set(displayImage));

    }

}
