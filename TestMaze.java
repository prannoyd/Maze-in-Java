
/**
 * Maze tested class
 *
 * @author
 */
public class TestMaze {

    public static void main(String[] args) {
        Maze maze = new Maze(20, 20);
        maze.generate();
        System.out.println(maze.printMaze());
        System.out.println(maze.printSolution());
    }
}
