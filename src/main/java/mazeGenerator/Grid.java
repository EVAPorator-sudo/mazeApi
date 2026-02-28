package mazeGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * the grid of the maze
 */
public class Grid {

    private final int YLength;
    private final int XLength;
    private final ArrayList<Row> rowList = new ArrayList<>();

    /**
     * @param XLen Horisontal length of the grid
     * @param YLen Vertical height of the grid
     */
    public Grid(int XLen, int YLen) {

        YLength = YLen;
        XLength = XLen;

        for (int i = 0; i < YLen; i++) {

            rowList.add(new Row(XLength, i));

        }

    }


    /**
     * @return length of the grid
     */
    public int getLength() {
        return XLength;
    }

    /**
     * @return height of the grid
     */
    public int getHeight() {
        return YLength;
    }

    /**
     * @return list of rows
     */
    public ArrayList<Row> getList() {
        return rowList;
    }

    /**
     * returns cell at coordinates in the grid
     *
     * @param position cell position
     * @return cell at position
     */
    public Cell getCell(int[] position) {
        return rowList.get(position[1]).getCell(position[0]);
    }

    /**
     * removes walls between adjacent cells
     *
     * @param cell      starting cell
     * @param direction direction to merge in
     */
    public void Merge(Cell cell, int direction) {
        int[] cellPos = cell.getPosition();
        switch (direction) {
            case 0 -> {
                cell.setWall(false, 0);
                Cell adjacentCell = getCell(new int[]{cellPos[0] - 1, cellPos[1]});
                adjacentCell.setWall(false, 2);
            }
            case 1 -> cell.setWall(false, 1);
            case 2 -> {
                cell.setWall(false, 2);
                Cell adjacentCell = getCell(new int[]{cellPos[0] + 1, cellPos[1]});
                adjacentCell.setWall(false, 0);
            }
            case 3 -> {
                Cell adjacentCell = getCell(new int[]{cellPos[0], cellPos[1] - 1});
                adjacentCell.setWall(false, 1);
            }
        }
    }

    /**
     * returns adjacent cell at direction
     *
     * @param cell      starting cell
     * @param direction direction of cell
     * @return cell in direction
     */
    public Cell findCell(Cell cell, int direction) {
        int[] cellPos = cell.getPosition();
        switch (direction) {
            case 0 -> {
                return getCell(new int[]{cellPos[0] - 1, cellPos[1]});
            }
            case 1 -> {
                return getCell(new int[]{cellPos[0], cellPos[1] + 1});
            }
            case 2 -> {
                return getCell(new int[]{cellPos[0] + 1, cellPos[1]});
            }
            case 3 -> {
                return getCell(new int[]{cellPos[0], cellPos[1] - 1});
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    /**
     * @return all cells in a list
     */
    public List<Cell> getAllCells() {
        ArrayList<Cell> list = new ArrayList<>();
        for (Row row : rowList) {
            list.addAll(row.getList());
        }
        return list.stream().toList();
    }

    /**
     * finds valid neighbour directions to visit
     *
     * @param cell    start
     * @param Visited previously visited cells
     * @return neighbour directions
     */
    public ArrayList<Integer> findNeighbours(Cell cell, HashSet<Cell> Visited) {
        ArrayList<Integer> neighbours = new ArrayList<>();
        int[] cellPos = cell.getPosition();
        if (cell.getPosition()[0] > 0) {
            Cell adjacentCell = getCell(new int[]{cellPos[0] - 1, cellPos[1]});
            if (!Visited.contains(adjacentCell)) neighbours.add(0);
        }
        if (cell.getPosition()[1] < getHeight() - 1) {
            Cell adjacentCell = getCell(new int[]{cellPos[0], cellPos[1] + 1});
            if (!Visited.contains(adjacentCell)) neighbours.add(1);
        }
        if (cell.getPosition()[0] < getLength() - 1) {
            Cell adjacentCell = getCell(new int[]{cellPos[0] + 1, cellPos[1]});
            if (!Visited.contains(adjacentCell)) neighbours.add(2);
        }
        if (cell.getPosition()[1] > 0) {
            Cell adjacentCell = getCell(new int[]{cellPos[0], cellPos[1] - 1});
            if (!Visited.contains(adjacentCell)) neighbours.add(3);
        }
        return neighbours;
    }

    /**
     * returns valid neighbours to move to
     *
     * @param cell start
     * @return moveable cells
     */
    public ArrayList<Cell> movableNeighbours(Cell cell) {
        ArrayList<Cell> neighbours = new ArrayList<>();
        boolean[] cellWalls = cell.getWalls();
        ArrayList<Integer> possibleNeighbours = findNeighbours(cell, new HashSet<>());

        for (int i : possibleNeighbours) {
            Cell currentCell = findCell(cell, i);

            switch (i) {
                case 0, 1, 2 -> {
                    if (!cellWalls[i]) neighbours.add(currentCell);
                }
                case 3 -> {
                    if (!currentCell.getWalls()[1]) neighbours.add(currentCell);
                }
            }
        }

        return neighbours;
    }

}

