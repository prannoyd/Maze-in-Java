
import java.util.*;

/**
 * Simulate Maze
 *
 * @author
 */
public class Maze {

    private int[][] maze;
    private int height;
    private int width;
    // Direction (up right down left rightUp rightDown leftDown leftUp)
    private final int[] directionI = new int[]{-1, 0, 1, 0, -1, 1, 1, -1};
    private final int[] directionJ = new int[]{0, 1, 0, -1, 1, 1, -1, -1};
    // occupied percent in the maze
    private final double occupiedPercent = 0.75;
    // the location of my desk and machine
    private Point mydesk, machine;
    // solution of the paths from my desk to machine
    private Vector<Vector<Point>> sol = new Vector<Vector<Point>>();

    /**
     * Constructor with height and width of the maze
     *
     * @param height int
     * @param width int
     */
    public Maze(int height, int width) {
        this.height = height;
        this.width = width;
    }

    /**
     * Generate method called to generate and searching for solution and
     * guarantee find a maze with solution
     *
     */
    public void generate() {
        // if the maze generated dosen't have solution, iterate another time
        while (sol.size() == 0) {
            generateValidMaze();
            findSolution();
        }
    }

    /**
     * maze consist of numbers (0,1,2,3) which means (free space, desk, my desk,
     * machine)
     */
    private void generateValidMaze() {
        maze = new int[height][width];
        int cells = height * width; // all cells in the maze
        int entities = (int) (cells * occupiedPercent); // number of entities in the maze
        while (entities != 0) { // iterate until number of entities inhabit in the maze matrix
            // generate random I and J in the I-axis and J-axis
            int randI = generateRandomI();
            int randJ = generateRandomJ();
            // generate random direction up, down, right, left
            int randDir = generateRandomDirection();
            for (int i = 0; i < Math.min(3, entities); i++) {
                // checking that point selected valid (in range) and can put there a desk if there is no desks in his line neighbor to him
                if (!validPosition(randI, randJ) || !canPut(randI, randJ, randDir)) {
                    break;
                }
                // insert in that maze a desk in the chosen position
                maze[randI][randJ] = 1;
                // move to next point with the selected direction
                randI += directionI[randDir];
                randJ += directionJ[randDir];
                // reduce the number of entities by 1
                entities--;
            }
        }
        // place the machine in the maze
        placeMachine();
        // placing my desk in the maze
        placeMyDesk();
    }

    /**
     * Placing the machine
     */
    private void placeMachine() {
        // generate random I and J in the I-axis and J-axis
        int randI = generateRandomI();
        int randJ = generateRandomJ();
        // iterate until inserting the machine
        while (true) {
            // checking that the position was entity and not edge point
            if (maze[randI][randJ] == 1 && !edgePoint(randI, randJ)) {
                maze[randI][randJ] = 3;
                machine = new Point(randI, randJ);
                break;
            }
            // if that if-condition not true generate another point
            randI = generateRandomI();
            randJ = generateRandomJ();
        }
    }

    /**
     * Placing my desk
     */
    private void placeMyDesk() {
        // generate random I and J in the I-axis and J-axis
        int randI = generateRandomI();
        int randJ = generateRandomJ();
        // iterate until inserting the desk
        while (true) {
            // checking that the position was entity and not edge point
            if (maze[randI][randJ] == 1 && !edgePoint(randI, randJ)) {
                maze[randI][randJ] = 2;
                mydesk = new Point(randI, randJ);
                break;
            }
            // if that if-condition not true generate anthor point
            randI = generateRandomI();
            randJ = generateRandomJ();
        }
    }

    /**
     * Check that point is not edge point means that the point not in the last
     * line of any direction
     *
     * @param I int
     * @param J int
     * @return boolean
     */
    private boolean edgePoint(int I, int J) {
        return I == 0 || I == height - 1 || J == 0 || J == width - 1;
    }

    /**
     * To don't make three rows of desks between each other for a line if there
     * is a line of three rows of desks so the inner desks can't move
     *
     * @param I int
     * @param J int
     * @param dir int
     * @return boolean
     */
    private boolean canPut(int I, int J, int dir) {
        // if up or down checking point in right and left
        if (dir == 0 || dir == 2) { // up or down
            int cnt = 0;
            for (int i = -2; i < 3; i++) {
                if (i == 0) {
                    continue;
                }
                if (validPosition(I, J - i) && maze[I][J - i] == 1) {
                    cnt++;
                }
            }
            if (J == 0 || J == width - 1) {
                cnt++;
            }
            return cnt < 2;
            // if right or left checking point in up and down
        } else { // right or left
            int cnt = 0;
            for (int i = -2; i < 3; i++) {
                if (i == 0) {
                    continue;
                }
                if (validPosition(I - i, J) && maze[I - i][J] == 1) {
                    cnt++;
                }
            }
            if (I == 0 || I == height - 1) {
                cnt++;
            }
            return cnt < 2;
        }
    }

    /**
     * Check that point in the range
     *
     * @param I int
     * @param J int
     * @return boolean
     */
    private boolean validPosition(int I, int J) {
        return I < height && I >= 0 && J < width && J >= 0;
    }

    /**
     * Generate random direction (0,1,2,3) (up, right, down, left)
     *
     * @return int
     */
    private int generateRandomDirection() {
        return (int) (4 * Math.random());
    }

    /**
     * Generate random position on I-axis
     *
     * @return int
     */
    private int generateRandomI() {
        return (int) (height * Math.random());
    }

    /**
     * Generate random position on J-axis
     *
     * @return int
     */
    private int generateRandomJ() {
        return (int) (width * Math.random());
    }

    /**
     * Find the path from my desk to machine
     */
    private void findSolution() {
        // marking array to identify checked point from not checked
        boolean[][] vis = new boolean[height][width];
        // queue use in the BFS algorithm to store point
        Queue<Node> queue = new LinkedList<Node>();
        // create point of my desk and insert it in the queue
        Node cur = new Node(new Point(mydesk.getX(), mydesk.getY()));
        // mark that this point is visited
        vis[mydesk.getX()][mydesk.getY()] = true;
        // inserting the point in the queue
        queue.add(cur);
        // iterate until the queue being empty
        while (queue.size() > 0) {
            // take the first element
            cur = queue.peek();
            // remove it from the queue
            queue.remove();
            // if the point is the machine point add the solution in the vector sol
            if (cur.current.getX() == machine.getX() && cur.current.getY() == machine.getY()) {
                addSolution(cur);
                // marking that the machine point not visited yet
                vis[machine.getX()][machine.getY()] = false;
            }
            // check for the eight direction (up right down left rightUp rightDown leftDown leftUp)
            for (int i = 0; i < 8; i++) {
                // taking the next point with the i-direction
                int I = cur.current.getX() + directionI[i], J = cur.current.getY() + directionJ[i];
                // check that the point is valid and not visited and the point is not occupied
                if (validPosition(I, J) && !vis[I][J] && maze[I][J] != 1) {
                    // mark it as visited
                    vis[I][J] = true;
                    // create a node and store the new point in it
                    // make it's previous node the point which it come from
                    Node newNode = new Node(new Point(I, J), cur);
                    // insert it in the queue
                    queue.add(newNode);
                }
            }
        }
    }

    /**
     * Moving backward from given n to the previous nodes and add them in a
     * vector and add this vector in the general vector sol
     *
     * @param n node
     */
    private void addSolution(Node n) {
        // create the solution list
        Vector<Point> res = new Vector<Point>();
        while (true) {
            // insert a point in the list
            res.add(n.current);
            // when the point is the desk then stop
            if (n.current.getX() == mydesk.getX() && n.current.getY() == mydesk.getY()) {
                break;
            }
            // if not then iterate to it's previous node which it come from
            n = n.previous;
        }
        sol.add(res);
    }

    /**
     * Get the maze as a string
     *
     * @return String
     */
    public String printMaze() {
        String res = "The following matrix describes our maze\n"
                + "   (KEY -> my desk: = , machine: # , desk: |, free space: *)\n";
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (maze[i][j] == 0) {
                    res += "* ";
                } else if (maze[i][j] == 1) {
                    res += "| ";
                } else if (maze[i][j] == 2) {
                    res += "= ";
                } else {
                    res += "# ";
                }
            }
            res += "\n";
        }
        return res;
    }

    /**
     * Get path from my desk to machine
     *
     * @return String
     */
    public String printSolution() {
        String res = "The shortest path is the first soltion:\n"
                + "    (KEY-> my path:W and -, my desk: = , machine :# , desk: |, free space :*)\n";
        // iterate for each solution
        for (int i = 0; i < sol.size(); i++) {
            res += "Solution " + (i + 1) + ":\n";
            int[][] mat = new int[height][width];
            for (int j = 0; j < sol.get(i).size(); j++) {
                mat[sol.get(i).get(j).getX()][sol.get(i).get(j).getY()] = 1;
            }
            for (int k = 0; k < height; k++) {
                for (int j = 0; j < width; j++) {
                    if(mydesk.getX() == k && mydesk.getY() == j){
                        res += "= ";
                    }else if(machine.getX() == k && machine.getY() == j){
                        res += "# ";
                    }else if (mat[k][j] == 1) {
                        if(i == 0) res += "W ";
                        else res += "M ";
                    } else if (maze[k][j] == 0) {
                        res += "* ";
                    } else if (maze[k][j] == 1) {
                        res += "| ";
                    }
                }
                res += "\n";
            }
            res += "\n";
        }
        return res;
    }

    /**
     * Class node store a point and a node refer to the node which it come from
     */
    private class Node {

        protected Point current;
        protected Node previous;

        public Node(Point current) {
            this(current, null);
        }

        public Node(Point current, Node previous) {
            this.current = current;
            this.previous = previous;
        }

    }

}
