import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final UI _ui;
    private long _sleepTime;
    private List<Portal> _portals;
    private Portal _selectedPortal;

    private PriorityQueue<Node> _openList;
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
     */
    public Search(char[][] environment, int startPosX, int startPosY,
            Node goalNode, UI output, List<Portal> portals)
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
        _ui = output;
        _sleepTime = 0;
        _portals = portals;
        _selectedPortal = null;
        Comparator<Node> comparator = new NodeComparator();
        _openList = new PriorityQueue<Node>(11, comparator);
        _closedList = new HashSet<Node>();
        _loopCounter = 0;
    }

    // TODO Portals are not working properly
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
        // TODO Dummy
        return new Path();
    }

    private void expandNode(Node currentNode)
    {
        List<Node> _neighbours = getNeighbours(currentNode);
        for (Node selectedNeighbour : _neighbours)
        {
            if (_closedList.contains(selectedNeighbour))
            {
                continue;
            }
            boolean selPathIsbestPath = true;
            selectedNeighbour.setPredecessor(currentNode);
            int cost = cost(selectedNeighbour);
            selectedNeighbour.setCost(cost);

            for (Node node : _openList)
            {
                if (selectedNeighbour.equals(node)
                        && selectedNeighbour.getCost() >= node.getCost())
                {
                    selPathIsbestPath = false;

                }
            }
            if (selPathIsbestPath)
            {
                selectedNeighbour.setFValue(f(selectedNeighbour, cost));
                _openList.add(selectedNeighbour);
                printEnvironment(selectedNeighbour);
            }
        }
    }

    /**
     * Returns all reachable neighbours of a node. If a neighbour is a portal,
     * the other entrance of the portal is returned in the list.
     * 
     * @param
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

    // TODO setFValue
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

    private boolean isPortalEntrance(Node node)
    {
        for (Portal portal : _portals)
        {
            if (node.equals(portal.get_entrance1())
                    || node.equals(portal.get_entrance2()))
            {
                _selectedPortal = portal;
                return true;
            }
        }
        return false;
    }

    private int f(Node node)
    {
        return h(node) + cost(node);
    }

    private int f(Node node, int costOfPathToNode)
    {
        return h(node) + costOfPathToNode;
    }

    private int cost(Node node)
    {
        int cost = 0;
        Node selectedNode = node.getPredecessor();
        while (selectedNode != null)
        {
            ++cost;
            selectedNode = selectedNode.getPredecessor();
        }
        return cost;
    }

    private int h(Node node)
    {
        int distanceNoPortal = distance(node, _startNode);
        int distanceWithPortal = Integer.MAX_VALUE;
        for (Portal portal : _portals)
        {
            Node entrance1 = portal.get_entrance1();
            Node entrance2 = portal.get_entrance2();
            int currentDistanceWithPortal = distance(node, entrance1)
                    + distance(entrance2, _startNode);
            if (currentDistanceWithPortal < distanceWithPortal)
            {
                distanceWithPortal = currentDistanceWithPortal;
            }
            currentDistanceWithPortal = distance(node, entrance2)
                    + distance(entrance1, _startNode);
            if (currentDistanceWithPortal < distanceWithPortal)
            {
                distanceWithPortal = currentDistanceWithPortal;
            }
        }

        return Math.min(distanceNoPortal, distanceWithPortal);
    }

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

    public void printEnvironment(Node addedNode)
    {
        // _ui.printEnvironment(_environment);
        // _ui.addText("Schleifendurchgänge: " + String.valueOf(loopCounter));
        // if (!isPortalEntrance(addedNode))
        // {
        _environment[addedNode.getY()][addedNode.getX()] = VISITED;
        // }
        print();
        System.out.println(_loopCounter);
        System.out.println(addedNode.toPreciseString());
    }

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
    // position are clear and have not been visited during search yet.
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

    private void reset()
    {
        // TODO Reset A* ergänzen
        _environment = Start.copy2DCharArray(_inputEnvironment);
        _currentPosX = _startPosX;
        _currentPosY = _startPosY;
        _loopCounter = 0;
        _selectedPortal = null;
        _openList.clear();
        _closedList.clear();
    }
}
