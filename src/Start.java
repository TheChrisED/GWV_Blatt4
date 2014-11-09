import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 3dibbern
 */
public class Start implements Observer
{

    public static final int LINE_COUNT = 10;
    public static final int LINE_LENGTH = 20;
    public static final int BFS_SLEEP = 200;
    public static final int DFS_SLEEP = 500;
    public static final int VIEWING_TIME = 10000;
    public UI _ui = null;

    private Search _search;
    private String _modus;

    public static char[][] copy2DCharArray(char[][] original)
    {
        int sizeY = original.length;
        char[][] copy = new char[sizeY][];

        for (int y = 0; y < sizeY; ++y)
        {
            copy[y] = original[y].clone();
        }
        return copy;
    }

    public static void print2DCharArray(char[][] array)
    {

    }

    public Start(String modus)
    {
        initSearch(modus);
    }

    private void initSearch(String modus)
    {
        URL urlreader = null;
        switch (modus)
        {
        case "a":
            urlreader = ClassLoader.getSystemClassLoader().getResource(
                    "resources/blatt4_environment_a.txt");
            break;
        case "b":
            urlreader = ClassLoader.getSystemClassLoader().getResource(
                    "resources/blatt4_environment_b2.txt");
            break;
        case "3":
            urlreader = ClassLoader.getSystemClassLoader().getResource(
                    "resources/blatt3_environment.txt");
            break;
        }

        EnvironmentReader reader = null;
        try
        {
            reader = new EnvironmentReader(urlreader.toURI(), LINE_COUNT,
                    LINE_LENGTH);
        }
        catch (IOException ex)
        {
            Logger.getLogger(Start.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (URISyntaxException ex)
        {
            ex.printStackTrace();
        }
        char[][] environment = reader.getEnvironment();

        int startPosX = reader.getStartPosX();
        int startPosY = reader.getStartPosY();
        int goalPosX = reader.getGoalPosX();
        int goalPosY = reader.getGoalPosY();
        char goalChar = reader.getGoalChar();
        List<Portal> portals = reader.getPortals();

        System.out.println("X: " + startPosX + ", Y: " + startPosY);
        System.out.println(portals.toString());

        Node goalNode = new Node(goalPosX, goalPosY);

        // _ui = new UI(environment);
        // _ui.addObserver(this);

        _search = new Search(environment, startPosX, startPosY, goalNode, _ui,
                portals);
    }

    public void startAStarSearch()
    {
        Path starPath = _search.aStarSearch();
        System.out.println(starPath.toString());
    }

    @Override
    public void update(Observable o, Object arg)
    {
        String action = (String) arg;

        switch (action)
        {
        // TODO No Button interaction
        }
    }

}
