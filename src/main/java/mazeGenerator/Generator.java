package mazeGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class Generator {

    /**
     * applies ellers algorithm to generate maze on a grid
     *
     * @param grid initialised grid
     * @return maze grid
     */
    public static Grid Ellers(Grid grid) {
        Random random = new Random();
        for (Row row : grid.getList().subList(0, grid.getHeight())) {
            for (Cell cell : row.getList()) {
                if (random.nextBoolean() && cell.getPosition()[0] != 0) {
                    grid.Merge(cell, 0);
                } else if (cell.getPosition()[1] != grid.getLength() - 1) {
                    grid.Merge(cell, 1);
                }
            }
            ArrayList<ArrayList<Cell>> sets = new ArrayList<>();
            sets.add(new ArrayList<>());
            int set = 0;
            for (Cell cell : row.getList()) {
                sets.get(set).add(cell);
                if (cell.getWalls()[2]) {
                    if (cell.getPosition()[0] < row.getLength() - 1) {
                        sets.add(new ArrayList<>());
                        set++;
                    }
                }
            }
            for (ArrayList<Cell> workingSet : sets) {
                int carves;
                if (workingSet.size() < 2) {
                    carves = 1;
                } else {
                    carves = random.nextInt(1, workingSet.size());
                }

                ArrayList<Integer> carvePoints = new ArrayList<>();
                while (carvePoints.size() < carves) {
                    int newCarve = random.nextInt(workingSet.size());
                    if (!carvePoints.contains(newCarve)) {
                        carvePoints.add(newCarve);
                    }
                }
                for (int index : carvePoints) {
                    grid.Merge(workingSet.get(index), 1);
                }
            }
        }
        List<Cell> lastRow = grid.getList().getLast().getList().subList(0, grid.getLength() - 1);
        for (Cell cell : lastRow) {
            grid.Merge(cell, 2);
        }
        return grid;
    }

    /**
     * applies growing tree algorithm to generate a maze on a grid
     *
     * @param grid      initialised grid
     * @param Weighting weighting between recursive backtracking and Primm's
     * @return maze grid
     */
    public static Grid growingTree(Grid grid, double Weighting) {
        Random random = new Random();
        ArrayList<Cell> activeList = new ArrayList<>();
        HashSet<Cell> visited = new HashSet<>();
        int randomX = random.nextInt(0, grid.getLength());
        int randomY = random.nextInt(0, grid.getHeight());
        activeList.add(grid.getCell(new int[]{randomX, randomY}));
        while (!activeList.isEmpty()) {
            Cell activeCell;
            if (random.nextInt(100) < Weighting && activeList.size() > 1) {
                activeCell = activeList.get(random.nextInt(activeList.size()));
            } else {
                activeCell = activeList.getLast();

            }
            visited.add(activeCell);
            ArrayList<Integer> neighbours = grid.findNeighbours(activeCell, visited);
            if (neighbours.isEmpty()) activeList.remove(activeCell);
            else if (neighbours.size() == 1) {
                int direction = neighbours.getFirst();
                Cell neighbour = grid.findCell(activeCell, direction);
                grid.Merge(activeCell, direction);
                if (!visited.contains(neighbour)) {
                    activeList.add(neighbour);
                    visited.add(neighbour);
                }
            } else {
                int direction = neighbours.get(random.nextInt(neighbours.size()));
                Cell neighbour = grid.findCell(activeCell, direction);
                grid.Merge(activeCell, direction);
                if (!visited.contains(neighbour)) {
                    activeList.add(neighbour);
                    visited.add(neighbour);
                }
            }
        }
        return grid;
    }
}