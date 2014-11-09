
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author rw
 */
public class UI extends Observable
{
    
    private final String TITLE = "Search Robot";
    private final int WIDTH = 500;
    private final int HEIGHT = 500;
    public static final String DFS = "Start DFS";
    public static final String BFS = "Start BFS";
    
    private JFrame _frame;
    private JPanel _mainPanel, _btnPanel;
    private JButton _dfsButton, _bfsButton;
    private JTextArea _output;
    
    
    public UI(char[][] initialEnvironment)
    {
        initUI(initialEnvironment);
    }
    
    private void initUI(char[][] environment)
    {
        _frame = new JFrame(TITLE);
        _frame.setSize(WIDTH, HEIGHT);
        _frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        _mainPanel = new JPanel(new BorderLayout());
        _btnPanel = new JPanel(new FlowLayout());
        
        String text = charArrayToString(environment);
        _output = new JTextArea();
        _output.setFont(new Font("Menlo", Font.PLAIN, 12));
//        _output.setColumns(Start.LINE_LENGTH);
//        _output.setRows(Start.LINE_COUNT);
        _output.setText(text);
        
        _dfsButton = new JButton("Tiefensuche");
        _bfsButton = new JButton("Breitensuche");
        setListeners();
        
        _btnPanel.add(_dfsButton);
        _btnPanel.add(_bfsButton);
        _mainPanel.add(BorderLayout.SOUTH, _btnPanel);
        _mainPanel.add(BorderLayout.CENTER, _output);
        _frame.add(_mainPanel);
        
        _frame.setVisible(true);
        
    }

    private void setListeners()
    {
        _dfsButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setChanged();
                notifyObservers(DFS);
            }
        });
        
        _bfsButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                setChanged();
                notifyObservers(BFS);
            }
        });
    }
    
    private String charArrayToString(char[][] array)
    {
        String output = "";
        for (int y = 0; y < 10; ++y) {
            for (int x = 0; x < 20; ++x) {
                output = output + array[y][x];
            }
            output = output + "\n";
        }
        return output;
    }
    
    public void printEnvironment(char[][] environment) {
        _output.setText(charArrayToString(environment));
    }
    
    public void addText(String text)
    {
        String currentText = _output.getText();
        String newText = currentText.concat("\n" + text);
        _output.setText(newText);
    }
}
