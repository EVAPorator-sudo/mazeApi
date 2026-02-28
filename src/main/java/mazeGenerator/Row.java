package mazeGenerator;

import java.util.ArrayList;

/**
 * a row within the grid
 */
public class Row {
    private final int XLength;
    private final int YPosition;
    ArrayList<Cell> cellList = new ArrayList<>();

    /**
     *
     * @param XLen length of the row
     * @param Ypos which row within the grid
     */
    public Row(int XLen, int Ypos) {

        XLength = XLen;
        YPosition = Ypos;

        for (int i = 0; i < XLength; i++) {
            cellList.add(new Cell(i, YPosition));
        }

    }

    /**
     * @return length of row
     */
    public int getLength() {

        return XLength;

    }

    /**
     * returns a cell at index
     *
     * @param index index of the cell to be returned
     * @return cell
     */
    public Cell getCell(int index) {

        if (index > XLength - 1) {
            throw new IndexOutOfBoundsException();
        }

        return cellList.get(index);

    }

    /**
     * @return list of cells
     */
    public ArrayList<Cell> getList() {

        return cellList;

    }
}
