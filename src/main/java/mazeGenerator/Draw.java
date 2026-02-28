package mazeGenerator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Draw {

    /**
     * draws grid onto bufferedimage
     *
     * @param grid input grid
     * @return image of grid
     */
    public static BufferedImage gridDraw(Grid grid) {

        int cellSize = 20;
        int wallThickness = 1;
        int cols = grid.getLength();
        int rows = grid.getHeight();
        int imageWidth = cols * cellSize + 2 * wallThickness;
        int imageHeight = rows * cellSize + 2 * wallThickness;
        BufferedImage gridImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = gridImage.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, imageWidth, imageHeight);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, imageWidth, 2 * wallThickness);
        graphics.fillRect(0, 0, 2 * wallThickness, imageHeight);
        graphics.fillRect(0, imageHeight - 2 * wallThickness, imageWidth, 2 * wallThickness);
        graphics.fillRect(imageWidth - 2 * wallThickness, 0, 2 * wallThickness, imageHeight);

        for (Row row : grid.getList().subList(0, grid.getHeight())) {
            for (Cell cell : row.getList()) {
                int[] pos = cell.getPosition();
                boolean[] walls = cell.getWalls();

                int X = wallThickness + pos[0] * cellSize;
                int Y = wallThickness + pos[1] * cellSize;

                if (walls[0]) {
                    graphics.fillRect(
                            X,
                            Y,
                            wallThickness,
                            cellSize + wallThickness);
                }

                if (walls[1]) {
                    graphics.fillRect(
                            X - wallThickness,
                            Y + cellSize - wallThickness,
                            cellSize + 2 * wallThickness,
                            2 * wallThickness);
                }

                if (walls[2]) {
                    graphics.fillRect(X + cellSize - wallThickness,
                            Y, 2 * wallThickness,
                            cellSize + wallThickness);
                }
            }
        }

        graphics.dispose();
        return gridImage;
    }

    /**
     * draws grid and solution path onto bufferedimage
     *
     * @param grid input grid
     * @param path path between points
     * @return image of grid and solution
     */
    public static BufferedImage solveDraw(Grid grid, ArrayList<Cell> path) {
        BufferedImage gridImage = gridDraw(grid);
        Graphics2D graphics = gridImage.createGraphics();

        graphics.setColor(Color.RED);
        graphics.setStroke(new BasicStroke(2)); // thicker path

        int cellSize = 20;
        int wallThickness = 1;

        Cell previous = null;

        for (Cell cell : path) {
            if (previous != null) {
                int[] prevPos = previous.getPosition();
                int[] currPos = cell.getPosition();

                int x1 = wallThickness + prevPos[0] * cellSize + cellSize / 2;
                int y1 = wallThickness + prevPos[1] * cellSize + cellSize / 2;

                int x2 = wallThickness + currPos[0] * cellSize + cellSize / 2;
                int y2 = wallThickness + currPos[1] * cellSize + cellSize / 2;

                graphics.drawLine(x1, y1, x2, y2);
            }
            previous = cell;
        }

        graphics.dispose();
        return gridImage;
    }

}
