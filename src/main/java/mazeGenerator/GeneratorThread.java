package mazeGenerator;

import java.util.ArrayList;

public class GeneratorThread extends Thread {

    private final int gridLength;
    private final int gridHeight;
    private final String gridAlgorithm;
    private final double gridWeight;

    public GeneratorThread(int Length, int Height, String Algorithm, double Weight) {

        gridLength = Length;
        gridHeight = Height;
        gridAlgorithm = Algorithm;
        gridWeight = Weight;

    }

    @Override
    public void run() {
        Grid grid = new Grid(gridLength, gridHeight);

        switch (gridAlgorithm) {
            case "Eller's" -> Generator.Ellers(grid);
            case "Growing Tree" -> Generator.growingTree(grid, gridWeight);
        }
        Controller.currentGrid = grid;
        DrawThread gridDraw = new DrawThread(new ArrayList<Cell>(), grid);
        gridDraw.start();

    }

}
