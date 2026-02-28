package mazeGenerator;

import java.util.*;

public class Solver {

    public static ArrayList<Cell> Dijkstras(Cell start, Cell end, Grid grid) {
        HashMap<Cell, Integer> g_score = new HashMap<>();
        HashMap<Cell, Cell> previous = new HashMap<>();

        PriorityQueue<Cell> queue = new PriorityQueue<Cell>(
                Comparator.comparingInt(g_score::get)
        );


        for (Cell cell : grid.getAllCells()) {
            g_score.put(cell, Integer.MAX_VALUE);
        }

        g_score.put(start, 0);
        queue.add(start);


        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.equals(end)) {
                break;
            }

            for (Cell neighbour : grid.movableNeighbours(current)) {
                int neighbourDistance = g_score.get(current) + 1;

                if (neighbourDistance < g_score.get(neighbour)) {
                    g_score.put(neighbour, neighbourDistance);
                    previous.put(neighbour, current);
                    queue.remove(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        ArrayList<Cell> path = new ArrayList<>();
        Cell step = end;

        if (!previous.containsKey(end) && !start.equals(end)) {
            return path;
        }

        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }

        Collections.reverse(path);
        return path;
    }

    public static ArrayList<Cell> A(Cell start, Cell end, Grid grid) {
        HashMap<Cell, Integer> g_score = new HashMap<>();
        HashMap<Cell, Integer> f_score = new HashMap<>();
        HashMap<Cell, Cell> previous = new HashMap<>();

        PriorityQueue<Cell> queue = new PriorityQueue<Cell>(
                (a, b) -> Integer.compare(
                        f_score.getOrDefault(a, Integer.MAX_VALUE),
                        f_score.getOrDefault(b, Integer.MAX_VALUE)
                )

        );


        for (Cell cell : grid.getAllCells()) {
            g_score.put(cell, Integer.MAX_VALUE);
            f_score.put(cell, Integer.MAX_VALUE);
        }

        g_score.put(start, 0);
        f_score.put(start, Manhattan(start, end));
        queue.add(start);


        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.equals(end)) {
                break;
            }

            for (Cell neighbour : grid.movableNeighbours(current)) {
                int neighbourG = g_score.get(current) + 1;

                if (neighbourG < g_score.get(neighbour)) {
                    g_score.put(neighbour, neighbourG);
                    previous.put(neighbour, current);
                    f_score.put(neighbour, neighbourG + Manhattan(neighbour, end));
                    queue.remove(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        ArrayList<Cell> path = new ArrayList<>();
        Cell step = end;

        if (!previous.containsKey(end) && !start.equals(end)) {
            return path;
        }

        while (step != null) {
            path.add(step);
            step = previous.get(step);
        }

        Collections.reverse(path);
        return path;
    }

    static int Manhattan(Cell start, Cell end) {
        int[] startPos = start.getPosition();
        int[] endPos = end.getPosition();
        return Math.abs(startPos[0] - endPos[0]) + Math.abs(startPos[1] - endPos[1]);
    }

}
