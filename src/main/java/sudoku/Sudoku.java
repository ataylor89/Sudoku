package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class Sudoku extends JFrame implements ActionListener {
    
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem newFile, openFile, saveFile, saveFileAs, validate, exit;
    private JPanel contentPane;
    private JPanel[][] panels;
    private JTextField[][] squares;
    private MouseListener mouseListener;
    private KeyListener keyListener;
    private File file;
    private JFileChooser fileChooser;
    
    public Sudoku() {
        super("Sudoku");
    }
    
    public void createAndShowGui() {
        setSize(900, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        newFile = new JMenuItem("New");
        openFile = new JMenuItem("Open");
        saveFile = new JMenuItem("Save");
        saveFileAs = new JMenuItem("Save as");
        validate = new JMenuItem("Validate");
        exit = new JMenuItem("Exit");
        newFile.addActionListener(this);
        openFile.addActionListener(this);
        saveFile.addActionListener(this);
        saveFileAs.addActionListener(this);
        validate.addActionListener(this);
        exit.addActionListener(this);
        fileMenu.add(newFile);
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(saveFileAs);
        fileMenu.add(validate);
        fileMenu.add(exit);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
        contentPane = new JPanel(new GridLayout(3, 3));
        panels = new JPanel[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                panels[i][j] = new JPanel(new GridLayout(3, 3));
                panels[i][j].setBorder(BorderFactory.createLineBorder(Color.GRAY));
                contentPane.add(panels[i][j]);
            }
        }
        squares = new JTextField[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squares[i][j] = new JTextField();
                squares[i][j].setHorizontalAlignment(JTextField.CENTER);
                squares[i][j].setFont(new Font("Sans Serif", Font.PLAIN, 30));
                squares[i][j].setDocument(new PlainDocument() {
                    @Override
                    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                        if (getLength() == 0 && str.length() == 1) {
                            char c = str.charAt(0);
                            if (c >= '1' && c <= '9') {
                                super.insertString(offs, str, a);
                            }
                        }
                    }
                });
                panels[i/3][j/3].add(squares[i][j]);
            }
        }
        setContentPane(contentPane);
        fileChooser = new JFileChooser(System.getProperty("user.dir"));
        setupMouseAndKeyListeners();
    }
    
    public void setupMouseAndKeyListeners() {
        mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof JTextField square) {
                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                        if (!square.isEditable()) {
                            square.setForeground(Color.BLACK);
                            square.setEditable(true);
                        }
                    }
                }
            }
        };
        keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getSource() instanceof JTextField square) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (square.isEditable()) {
                            square.setForeground(new Color(0x800000));
                            square.setEditable(false);
                        }
                    }
                } 
            }
        };
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squares[i][j].addMouseListener(mouseListener);
                squares[i][j].addKeyListener(keyListener);
            }
        }
    }
    
    public void clear() {
        this.file = null;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squares[i][j].setText("");
                squares[i][j].setForeground(Color.BLACK);
                squares[i][j].setEditable(true);
            }
        }
    }
    
    public void open(File file) {
        this.file = file;
        try {
            String contents = Files.readString(file.toPath());
            String[] lines = contents.split("\n");
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    char c = lines[i].charAt(j);
                    if (c >= '1' && c <= '9') {
                        squares[i][j].setText(String.valueOf(c));
                        squares[i][j].setForeground(new Color(0x800000));
                        squares[i][j].setEditable(false);
                    }
                    else {
                        if (c >= 'a' && c <= 'i') {
                            int digit = c - 'a' + 1;
                            squares[i][j].setText(String.valueOf(digit));
                        }
                        else {
                            squares[i][j].setText("");
                        }
                        squares[i][j].setForeground(Color.BLACK);
                        squares[i][j].setEditable(true);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    public void open() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
	    open(fileChooser.getSelectedFile());
	}
    }
    
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            String data = "";
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    String text = squares[i][j].getText();
                    if (squares[i][j].isEditable()) {
                        if (text.isEmpty()) {
                            data += "0";
                        }
                        else {
                            data += (char) ('a' + text.charAt(0) - '1');
                        }
                    }
                    else {
                        data += text;
                    }
                }
                data += "\n";
            }
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
    public void saveAs() {
	if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.file = fileChooser.getSelectedFile();
	    save();
	}
    }
        
    public boolean solved() {
        boolean[] values = new boolean[9];
        // Verify that every row has the digits one through nine
        for (int i = 0; i < 9; i++) {
            Arrays.fill(values, false);
            for (int j = 0; j < 9; j++) {
                String text = squares[i][j].getText();
                if (text.isEmpty())
                    return false;
                int offset = text.charAt(0) - '1';
                values[offset] = true;
            }
            for (boolean value : values)
                if (!value)
                    return false;
        }
        // Verify that every column has the digits one through nine
        for (int i = 0; i < 9; i++) {
            Arrays.fill(values, false);
            for (int j = 0; j < 9; j++) {
                String text = squares[j][i].getText();
                int offset = text.charAt(0) - '1';
                values[offset] = true;
            }
            for (boolean value : values)
                if (!value)
                    return false;
        }
        // Verify that every panel has the digits one through nine
        for (int x = 0; x < 9; x += 3) {
            for (int y = 0; y < 9; y += 3) {
                Arrays.fill(values, false);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        String text = squares[i + x][j + y].getText();
                        int offset = text.charAt(0) - '1';
                        values[offset] = true;
                    }
                }
                for (boolean value : values)
                    if (!value)
                        return false;
            }
        }
        return true;
    }
    
    public void checkSolution() {
        if (solved()) {
            JOptionPane.showMessageDialog(this, "You solved the puzzle.", "Solved!", JOptionPane.PLAIN_MESSAGE);
        }
        else {
            JOptionPane.showMessageDialog(this, "The solution is not valid", "Solution not valid", JOptionPane.PLAIN_MESSAGE);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newFile) {
            clear();
        }
        else if (e.getSource() == openFile) {
            open();
        }
        else if (e.getSource() == saveFile) {
            save();
        }
        else if (e.getSource() == saveFileAs) {
            saveAs();
        }
        else if (e.getSource() == validate) {
            checkSolution();
        }
        else if (e.getSource() == exit) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            System.exit(0);
        }
    }
        
    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        sudoku.createAndShowGui();
        sudoku.setVisible(true);
    }
}