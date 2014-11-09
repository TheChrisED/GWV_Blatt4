import javax.annotation.PreDestroy;

public class Node implements Comparable
{

    private final int _x;
    private final int _y;
    private int _fValue;
    private Node _predecessor;
    private int _cost;

    public Node(int x, int y)
    {
        this._x = x;
        this._y = y;
    }

    public Node(int[] position)
    {
        _x = position[0];
        _y = position[1];
    }

    public int[] getPosition()
    {
        int[] position = { _x, _y };
        return position;
    }

    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public int getFValue()
    {
        return _fValue;
    }

    public void setFValue(int fValue)
    {
        this._fValue = fValue;
    }

    public int getCost()
    {
        return _cost;
    }

    public void setCost(int cost)
    {
        this._cost = cost;
    }

    public Node getPredecessor()
    {
        return _predecessor;
    }

    public void setPredecessor(Node predecessor)
    {
        this._predecessor = predecessor;
    }

    public Path reconstructPath()
    {
        Path reconstructedPath = new Path();
        Node currentNode = this;
        while (currentNode != null)
        {
            reconstructedPath.addNode(currentNode);
            currentNode = currentNode.getPredecessor();
        }

        return reconstructedPath;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 59 * hash + this._x;
        hash = 59 * hash + this._y;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Node other = (Node) obj;
        if (this._x != other._x)
        {
            return false;
        }
        if (this._y != other._y)
        {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o)
    {

        return 123;
    }

    @Override
    public String toString()
    {
        String text = "[" + _x + ", " + _y + "]";
        return text;
    }

    public String toPreciseString()
    {
        String text = "Position: " + toString() + "\n" + "f-value: " + getFValue() + "\n" + "Cost: "
                + getCost() + "\n";
        if (getPredecessor() != null)
        {
            text = text + "Predecessor: " + getPredecessor().toString();
        }
        return text;
    }
}
