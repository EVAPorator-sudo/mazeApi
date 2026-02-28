package mazeGenerator;

import java.util.ArrayList;

public class SolveThread extends Thread {

    private final Cell gridStart;
    private final Cell gridEnd;
    private final Grid gridMaze;
    private final String gridAlgorithm;
    private final boolean steps;

    public SolveThread(Cell Start, Cell End, Grid Maze, String Algorithm, boolean step) {

        gridStart = Start;
        gridEnd = End;
        gridMaze = Maze;
        gridAlgorithm = Algorithm;
        steps = step;

    }

    @Override
    public void run() {
        ArrayList<Cell> Solution = null;

        switch (gridAlgorithm) {
            case "Dijkstra's" -> Solution = Solver.Dijkstras(gridStart, gridEnd, gridMaze);
            case "A*" -> Solution = Solver.A(gridStart, gridEnd, gridMaze);
        }
        Controller.currentSolution = Solution;

        if (!steps) {
            DrawThread solutionDraw = new DrawThread(Solution, gridMaze);
            solutionDraw.start();
        }
    }

}