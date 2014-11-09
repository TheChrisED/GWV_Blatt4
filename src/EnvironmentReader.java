
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Set;

/**
 * An EnvironmentReader can read an environment for a search from a text file.
 * It converts the environment to a char[][] so a search algorithm can be
 * applied to it. It also detects the start point for a search that must be
 * spefied in the input file
 *
 * @author 3dibbern
 */
public class EnvironmentReader
{

    private String _location;
    private InputStream _inputStream;
    private final int _lineCount;
    private final int _lineLength;
    private final char START_CHAR = 's';
    private final char GOAL_CHAR = 'g';

    private char[][] _environment;
    private int[] _startPos;
    private int[] _goalPos;
    private List<Portal> _portals;

    /**
     * Creates an Environment reader that reads the specified file. The text
     * file must contain exactly one 's'. This denotes the start point for a
     * search. 
     *
     * @param location The path of the text file to be read specified as a URI
     * @param lineCount The number of lines that are in the text file or that
     * should be read
     * @param lineLength The length of a line (all lines need to have the same
     * length)
     * @throws IOException If the path is not valid or not a text file, an
     * IOException is thrown
     */
    public EnvironmentReader(InputStream location, int lineCount, int lineLength) throws IOException
    {
        _inputStream = location;
        _lineCount = lineCount;
        _lineLength = lineLength;
        _portals = new LinkedList<Portal>();

        readEnvironment();
    }
    
    private void readEnvironment() throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(_inputStream));

        char[][] environment = new char[_lineCount][_lineLength];

        for (int currentLine = 0; currentLine < _lineCount; ++currentLine)
        {
            String line = reader.readLine();
            for (int linePos = 0; linePos < line.length(); ++linePos)
            {
                char currentChar = line.charAt(linePos);
                if (currentChar == START_CHAR)
                {
                    _startPos = new int[2];
                    _startPos[0] = currentLine;
                    _startPos[1] = linePos;
                }
                else if (currentChar == GOAL_CHAR) {
                    _goalPos = new int[2];
                	_goalPos [0] = currentLine;
                	_goalPos [1] = linePos;
            	}
                else if (currentChar >= 48 && currentChar <= 57)
                {
                    boolean listContainsPortal = false;
                    int[] entrance = {linePos, currentLine};
                    for (Portal portal:_portals)
                    {
                        if (portal.hashCode() == currentChar)
                        {
                            listContainsPortal = true;
                            portal.setEntrancePoint(entrance);
                            break;
                        }
                    }
                    if (!listContainsPortal)
                    {
                        Portal newPortal = new Portal(currentChar);
                        newPortal.setEntrancePoint(entrance);
                        _portals.add(newPortal);
                    }
                }
                environment[currentLine][linePos] = currentChar;
            }
        }

        reader.close();
        _environment = environment;
    }

    /**
     * Returns the environment that was read during instanciation
     * @return the environment as a char[][]
     */
    public char[][] getEnvironment()
    {
        return _environment;
    }

    /**
     * Returns the start point of the environment that was read during instanciation
     * @return the start postions as an array. [startPosX, startPosY]
     */
    public int[] getStartPos()
    {
        return _startPos;
    }

    /**
     * Returns the start point of the environment that was read during instanciation
     * @return the X-Coordinate of the start point
     */
    public int getStartPosX()
    {
        return _startPos[1];
    }

    /**
     * Returns the start point of the environment that was read during instanciation
     * @return the Y-Coordinate of the start point
     */
    public int getStartPosY()
    {
        return _startPos[0];
    }
    
    /**
     * Returns the goal point of the environment that was read during instanciation
     * @return the X-Coordinate of the goal point
     */
    public int getGoalPosX()
    {
        return _goalPos[1];
    }

    /**
     * Returns the goal point of the environment that was read during instanciation
     * @return the Y-Coordinate of the goal point
     */
    public int getGoalPosY()
    {
        return _goalPos[0];
    }
    
    public char getGoalChar()
    {
        return GOAL_CHAR;
    }
    /**
     * Returns a list of the portals found in the environment
     * @return list of the portals
     */
    public List<Portal> getPortals()
    {
        return _portals;
    }
}
