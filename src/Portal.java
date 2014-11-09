
public class Portal
{
    private final Node _initialValue = new Node(-1, -1);
    private Node _entrance1 = new Node(-1, -1);
    private Node _entrance2 = new Node(-1, -1);
    private final char _identifier;
    
    /**
     * Creates a new Portal
     * @param _entrance1 The position of one of the entry points
     * @param _entrance2 The position of the other entry point 
     * @param identifier The number that is used in the environment to denote the portal
     */
    public Portal(int[] _entrance1, int[] _entrance2, char identifier)
    {
        this._entrance1 = new Node(_entrance1);
        this._entrance2 = new Node(_entrance2);
        this._identifier = identifier;
    }

    /**
     * Creates a new Portal
     * @param identifier The number that is used in the environment to denote the portal
     */
    public Portal(char identifier)
    {
        this._identifier = identifier;
    }
    
    public int[] getEntrance1()
    {
        return _entrance1.getPosition();
    }


    public int[] getEntrance2()
    {
        return _entrance2.getPosition();
    }
    
    /**
     * @return the _entrance1
     */
    public Node get_entrance1()
    {
        return new Node(getEntrance1());
    }

    /**
     * @return the _entrance2
     */
    public Node get_entrance2()
    {
        return new Node(getEntrance2());
    }

    public Node getOtherEntrance(Node entrance)
    {
        if (entrance.equals(_entrance1))
        {
            return _entrance2;
        }
        else if (entrance.equals(_entrance2))
        {
            return _entrance1;
        }
        else
        {
            return new Node(-1, -1);
        }
    }
    
    public void setEntrancePoint(int[] entrance)
    {
        if (_entrance1.equals(_initialValue)) // Checks if the position has been set yet.
        {
            _entrance1 = new Node(entrance);
        }
        else if (_entrance2.equals(_initialValue)) // Checks if the position has been set yet.
        {
            _entrance2 = new Node(entrance);
        }
    }

    @Override
    /**
     * The hashCode of a portal is its identifier, this needs to stay that way for the program to work.
     * Comparisons are carried out with a portal and an identifier char. (portal.hashCode == char)
     */
    public int hashCode()
    {
        return _identifier;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Portal)
        {
            return _identifier == ((Portal) obj)._identifier;
        }
        else
        {
            return false;
        }
    }

    @Override
    public String toString()
    {
        // TODO Auto-generated method stub
        return "([" + _entrance1.getX() + ", " + _entrance1.getY() + "] [" + _entrance2.getX() + ", " + _entrance2.getY() + "])";
    }
}
