import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Search
{

    private final char GOAL_CHAR = 'g';
    private final char START_CHAR = 's';
    private final char UP = 'u';
    private final char RIGHT = 'r';
    private final char DOWN = 'd';
    private final char LEFT = 'l';
    private final char VISITED = 'v';

    private final char[][] _inputEnvironment;
    private char[][] _environment;
    private final int _startPosX;
    private final int _startPosY;
    private final Node GOAL_NODE;
    private final Node _startNode;
    private long _sleepTime;
    private List<Portal> _portals;
    private Portal _selectedPortal;

    /**
     * Contains all nodes that have been expanded where a lowest cost path to
     * the node might not have been found yet
     */
    private PriorityQueue<Node> _openList;
    /**
     * Contains all nodes where lowest cost path to the node has been found
     */
    private Set<Node> _closedList;
    private int _currentPosX;
    private int _currentPosY;
    private int _loopCounter;

    /**
     * Creates a new Search instance that can search for a goal in a given
     * environment
     *
     * @param environment
     *            The environment to be used in the form of a char[][], 'x'
     *            denotes a wall, 'g' a goal and 's' the start point
     * @param startPosX
     *            The X-Coordinate of the start point in the environment array.
     *            char[y][x]
     * @param startPosY
     *            The X-Coordinate of the start point in the environment array.
     *            char[y][x]
     * @param goalNode
     *            A node which coordinates are the goal
     * @param portals
     *            If portals should be used in the search, they must be added as
     *            a list, if portals should not be used the list must be empty
     */
    public Search(char[][] environment, int startPosX, int startPosY,
            Node goalNode, List<Portal> portals)
    {
        _environment = Start.copy2DCharArray(environment);
        _inputEnvironment = Start.copy2DCharArray(environment);
        _startPosX = startPosX;
        _startPosY = startPosY;
        _currentPosX = startPosX;
        _currentPosY = startPosY;

        GOAL_NODE = goalNode;
        GOAL_NODE.setFValue(0);
        _startNode = new Node(startPosX, startPosY);
        _startNode.setFValue(0);
        _sleepTime = 0;
        _portals = portals;
        _selectedPortal = null;
        Comparator<Node> comparator = new NodeComparator();
        _openList = new PriorityQueue<Node>(11, comparator);
        _closedList = new HashSet<Node>();
        _loopCounter = 0;
    }

    /**
     * Starts an AStar Search on the environment
     * 
     * @return The shortest path to the goal, if a path is found. Else the empty
     *         Path is returned
     */
    public Path aStarSearch()
    {
        reset();
        // Searches from the goal to the start
        _openList.add(GOAL_NODE);
        _loopCounter = 0;
        while (!_openList.isEmpty())
        {
            ++_loopCounter;
            Node currentNode = _openList.poll();
            if (currentNode.equals(_startNode))
            {
                Path pathToGoal = currentNode.reconstructPath();
                printPathInEnvironment(pathToGoal);
                return pathToGoal;
            }
            _closedList.add(currentNode);
            expandNode(currentNode);

        }
        return new Path();
    }

    /**
     * Expands the current node. All reachable neighbours of the current node
     * are added to the _openList, if there is not already a shorter path to
     * this node
     * 
     * @param currentNode
     *            the node to expand
     */
    private void expandNode(Node currentNode)
    {
        List<Node> _neighbours = getNeighbours(currentNode);
        for (Node selectedNeighbour : _neighbours)
        {
            // Checks if a lowest cost path to selectedNeighbour has been found
            // yet
            if (_closedList.contains(selectedNeighbour))
            {
                continue;
            }
            boolean selPathIsbestPath = true;
            selectedNeighbour.setPredecessor(currentNode);
            int cost = cost(selectedNeighbour);
            selectedNeighbour.setCost(cost);

            // Checks if a lower cost path to selectedNeighbour has been found
            // yet
            for (Node node : _openList)
            {
                if (selectedNeighbour.equals(node)
                        && selectedNeighbour.getCost() >= node.getCost())
                {
                    // A shorter path has already been found, so
                    // selectedNeighbour should not be added to the _openList
                    selPathIsbestPath = false;
                }
            }
            if (selPathIsbestPath)
            {
                selectedNeighbour.setFValue(f(selectedNeighbour, cost));
                _openList.add(selectedNeighbour);
                printEnvironment(selectedNeighbour);
            }
            // Sleep
            try
            {
                Thread.sleep(_sleepTime);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns all reachable neighbours of a node. If a neighbour is a portal,
     * the other entrance of the portal is returned in the list.
     * 
     * @param node
     *            the node to get the neighbours for
     * @return All neighbours are returned as a list
     */
    private List<Node> getNeighbours(Node node)
    {
        List<Node> nodes = new LinkedList<Node>();
        _currentPosX = node.getX();
        _currentPosY = node.getY();

        if (topIsClear())
        {
            move(UP);
            addNeighbour(nodes);
            move(oppositeDirection(UP));
        }
        if (rightIsClear())
        {
            move(RIGHT);
            addNeighbour(nodes);
            move(oppositeDirection(RIGHT));
        }
        if (bottomIsClear())
        {
            move(DOWN);
            addNeighbour(nodes);
            move(oppositeDirection(DOWN));
        }
        if (leftIsClear())
        {
            move(LEFT);
            addNeighbour(nodes);
            move(oppositeDirection(LEFT));
        }

        return nodes;
    }

    /**
     * Adds the node for the current position the specified nodes list, if it is
     * not a portal. If it is the other entrance of the portal is added
     * 
     * @param nodes
     *            the list to add the node to
     */
    private void addNeighbour(List<Node> nodes)
    {
        Node currentNode = new Node(_currentPosX, _currentPosY);
        if (isPortalEntrance(currentNode))
        {
            Node otherEntrance = _selectedPortal.getOtherEntrance(currentNode);
            nodes.add(otherEntrance);
        }
        else
        {
            nodes.add(currentNode);
        }
    }

    /**
     * Checks if the specified node is the entrance of a portal in _portals If
     * node is a portal entrance, _selected portal is set to the portal
     * containing the node.
     * 
     * @param node
     *            The node to check
     * @return true if node is an entrance of a portal, false if not
     */
    private boolean isPortalEntrance(Node node)
    {
        for (Portal portal : _portals)
        {
            if (node.equals(portal.getEntrance1AsNode())
                    || node.equals(portal.getEntrance2AsNode()))
            {
                _selectedPortal = portal;
                return true;
            }
        }
        return false;
    }

    /**
     * Return the f-Value of the specified node
     * 
     * @param node
     *            The node to calculate the f-Value for
     * @return f-Value
     */
    private int f(Node node)
    {
        // f(n) = h(n) + cost(n)
        return h(node) + cost(node);
    }

    /**
     * Return the f-Value of the specified node
     * 
     * @param node
     *            The node to calculate the f-Value for
     * @param costOfPathToNode
     *            the cost of a path to the node
     * @return f-Value
     */
    private int f(Node node, int costOfPathToNode)
    {
        // f(n) = h(n) + cost(n)
        return h(node) + costOfPathToNode;
    }

    /**
     * The cost of a path to the specified node
     * 
     * @param node
     *            The node to calculate the cost for
     * @return cost of a path to the node
     */
    private int cost(Node node)
    {
        // The cost of a node is the cost of a path to the node, it is computed
        // by simply counting how many nodes are on the path. The path is
        // reconstructed by calling each node´s predecessor
        int cost = 0;
        Node selectedNode = node.getPredecessor();
        while (selectedNode != null)
        {
            ++cost;
            selectedNode = selectedNode.getPredecessor();
        }
        return cost;
    }

    /**
     * The heuristic value for a node
     * 
     * @param node
     *            the node to compute the heuristic value for
     * @return heuristic value
     */
    private int h(Node node)
    {
        // h is the distance from node to _startNode, or the shortest distance
        // to _startNode while using a portal
        int distanceNoPortal = distance(node, _startNode);
        int distanceWithPortal = Integer.MAX_VALUE;
        // Sets distanceWithPortal to the smallest distance to the start node
        // while using a portal
        for (Portal portal : _portals)
        {
            Node entrance1 = portal.getEntrance1AsNode();
            Node entrance2 = portal.getEntrance2AsNode();
            // The distance while entering entrance1 and exiting entrance2
            int currentDistanceWithPortal = distance(node, entrance1)
                    + distance(entrance2, _startNode);
            if (currentDistanceWithPortal < distanceWithPortal)
            {
                distanceWithPortal = currentDistanceWithPortal;
            }
            // The distance while entering entrance2 and exiting entrance1
            currentDistanceWithPortal = distance(node, entrance2)
                    + distance(entrance1, _startNode);
            if (currentDistanceWithPortal < distanceWithPortal)
            {
                distanceWithPortal = currentDistanceWithPortal;
            }
        }

        // The smallest possible distance with or without the use portals is
        // returned
        return Math.min(distanceNoPortal, distanceWithPortal);
    }

    /**
     * The shortest possible distance between two nodes.
     * 
     * @param a
     *            Node a
     * @param b
     *            Node b
     * @return distance between a and b
     */
    private int distance(Node a, Node b)
    {
        int smallestDistance = Math.abs(a.getX() - b.getX())
                + Math.abs(a.getX() - b.getX());
        return smallestDistance;
    }

    public void setSleepTime(int duration)
    {
        _sleepTime = duration;
    }

    /**
     * Takes a direction as a char and returns the opposite direction
     *
     * @param direction
     *            a direction ('u', 'r', 'd', 'l')
     * @return the opposite direction to the specified one
     */
    private char oppositeDirection(char direction)
    {
        char oppositeDir = ' ';

        switch (direction)
        {
        case 'u':
            oppositeDir = 'd';
            break;
        case 'r':
            oppositeDir = 'l';
            break;
        case 'd':
            oppositeDir = 'u';
            break;
        case 'l':
            oppositeDir = 'r';
            break;
        }

        return oppositeDir;
    }

    /**
     * Changes the values of currentPosX or currentPosY to move in the specified
     * diretion
     *
     * @param direction
     *            The direction to move to
     */
    private void move(char direction)
    {
        switch (direction)
        {
        case UP:
            _currentPosY -= 1;
            break;
        case RIGHT:
            _currentPosX += 1;
            break;
        case DOWN:
            _currentPosY += 1;
            break;
        case LEFT:
            _currentPosX -= 1;
            break;
        }
    }

    /**
     * Changes the values of currentPosX or currentPosY to move to position
     * specified in the Node that gets passed to the method
     *
     * @param position
     *            The Node containing the information where to move
     */
    private void moveTo(Node position)
    {
        _currentPosX = position.getX();
        _currentPosY = position.getY();
    }

    /**
     * Prints the environment and marks the specified node as visited in the
     * environment
     * 
     * @param addedNode
     *            the node to mark in the environment
     */
    public void printEnvironment(Node addedNode)
    {
        _environment[addedNode.getY()][addedNode.getX()] = VISITED;
        print();
        System.out.println(_loopCounter);
        System.out.println(addedNode.toPreciseString());
    }

    /**
     * Prints the current environment
     */
    private void print()
    {
        for (int y = 0; y < 10; ++y)
        {
            String line = "";
            for (int x = 0; x < 20; ++x)
            {
                line = line + _environment[y][x];
            }

            System.out.println(line);
        }
    }

    /**
     * Resets the current environment and marks the nodes on the path as
     * visited. Goals, start points and portals will not be marked
     * 
     * @param path
     *            the path to mark
     */
    private void printPathInEnvironment(Path path)
    {
        reset();
        for (Object node : path)
        {
            Node nodeToPrint = (Node) node;
            moveTo(nodeToPrint);
            char currentChar = _environment[_currentPosY][_currentPosX];
            if (!(currentChar == GOAL_CHAR) && !(currentChar == START_CHAR)
                    && !isPortalEntrance(new Node(_currentPosX, _currentPosY)))
            {
                _environment[_currentPosY][_currentPosX] = VISITED;
            }
        }
        print();
    }

    // ----Clear-Methods: ----
    // These methods check wether positions next to the current
    // position are clear. (As long as the position is not part of a wall true
    // is returned)
    private boolean topIsClear()
    {
        return _environment[_currentPosY - 1][_currentPosX] != 'x';
    }

    private boolean bottomIsClear()
    {
        return _environment[_currentPosY + 1][_currentPosX] != 'x';
    }

    private boolean leftIsClear()
    {
        return _environment[_currentPosY][_currentPosX - 1] != 'x';
    }

    private boolean rightIsClear()
    {
        return _environment[_currentPosY][_currentPosX + 1] != 'x';
    }

    /**
     * Resets all relevant values, so a new search process can be started
     */
    private void reset()
    {
        _environment = Start.copy2DCharArray(_inputEnvironment);
        _currentPosX = _startPosX;
        _currentPosY = _startPosY;
        _loopCounter = 0;
        _selectedPortal = null;
        _openList.clear();
        _closedList.clear();
    }
}
