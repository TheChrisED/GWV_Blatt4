import java.util.Comparator;


public class NodeComparator implements Comparator<Node>
{

    @Override
    public int compare(Node o1, Node o2)
    {
        int o1F = o1.getFValue();
        int o2F = o2.getFValue();
        if (o1F == o2F)
        {
            return 0; // Both have the same f Value and should be treated equally
        }
        else if (o1F < o2F)
        {
            return -1; // o1 has a better f Value and should be preferred
        }
        else
        {
            return 1; // o2 has a better f Value and should be preferred
        }
    }

}
