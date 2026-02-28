package mazeGenerator;

/**
 * cell within the grid
 */
public class Cell {

    private final int[] position;
    private final boolean[] walls = {true, true, true};

    // walls are represented by a boolean for each direction
    // {left, middle, right}

    /**
     * @param Xpos horisontal position of the cell
     * @param Ypos vertical position of the cell
     */
    public Cell(int Xpos, int Ypos) {
        position = new int[]{Xpos, Ypos};
    }

    /**
     * @return cell position
     */
    public int[] getPosition() {

        return position;

    }

    /**
     * wall order:
     * [left, below, right]
     *
     * @return cell walls
     */
    public boolean[] getWalls() {

        return walls;

    }

    /**
     * @param newWall new wall config
     */
    public void setWall(boolean newWall, int index) {

        walls[index] = newWall;

    }

}
