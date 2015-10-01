/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.lefthandofdarkness.qwixx.ScoreButton.ButtonState;

/**
 *
 * @author Nick_Pro
 */
public class PlayerView extends JPanel implements ActionListener
{
    private ScoreButton[][] scoreButtons;
    private boolean[][] interactive;
    private ScoreButton[] penalties;
    private JButton left, right, pass;
    private Player[] players;
    private int activePlayer, mainPlayer, values, dice;
    private boolean canChange = true;
    private String LOCK_CHARACTER = "★";  //a star is a lock
    private List<ScoreSheetListener> listeners;
    private JLabel name;
    private JLabel[] playerList;
    private Dimension dims;
    private int currentPlayer;
    
    public PlayerView(Player[] players, int mainPlayer, Dimension dims, Color background)
    {
        super();
        this.dims = dims;
        activePlayer = mainPlayer;
        this.mainPlayer = mainPlayer;
        this.setBackground(background);
        this.playerList = new JLabel[players.length];
        this.values = players[mainPlayer].getNumValues();
        this.dice = players[mainPlayer].getNumDice();
        pass = new JButton("Pass");
        left = new JButton("<");    //a left facing arrow
        right = new JButton(">");   //a right facing arrow
        penalties = new ScoreButton[Player.MAX_PENALTIES];
        this.players = players;
        Player player = players[mainPlayer];
        JPanel[] rows = new JPanel[player.getNumDice()];
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JPanel namesPanel = new JPanel();
        namesPanel.setLayout(new GridBagLayout());
        this.interactive = new boolean[dice][values];
        c.fill = GridBagConstraints.BOTH;
        //c.weightx = 1;
        c.gridwidth = 5;
        c.gridy = dice + 2;
        c.weighty = 1;
        add(namesPanel, c);
        namesPanel.setBackground(background);
        namesPanel.setLayout(new BoxLayout(namesPanel, BoxLayout.LINE_AXIS));
        namesPanel.add(Box.createHorizontalGlue());
        c.gridwidth = 1;
        for(int p = 0; p < players.length; p++)
        {
            playerList[p] = new JLabel(players[p].getName());
            playerList[p].setBackground(background);
            namesPanel.add(playerList[p], c);
            if(p == mainPlayer)
                playerList[p].setBorder(BorderFactory.createLineBorder(Color.WHITE, 5, true));
            else
                playerList[p].setBorder(BorderFactory.createLineBorder(background, 5, true));
            namesPanel.add(Box.createHorizontalGlue());
        }
        
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 0;
        c.weightx = .1;
        c.gridheight = dice + 1;
        add(left, c);
        c.gridx = 4;
        add(right, c);
        c.weightx = 1;
        left.addActionListener(this);
        right.addActionListener(this);
        pass.addActionListener(this);
        c.gridheight = 1;
        c.gridx = 1;
        scoreButtons = new ScoreButton[player.getNumDice()][player.getNumValues()];
        JPanel normalPanel = new JPanel();
        normalPanel.setBackground(background);
        normalPanel.setLayout(new BoxLayout(normalPanel, BoxLayout.LINE_AXIS));
        JPanel penaltiesPanel = new JPanel();
        penaltiesPanel.setLayout(new BoxLayout(penaltiesPanel, BoxLayout.LINE_AXIS));
        penaltiesPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        penaltiesPanel.setPreferredSize(new Dimension(dims.width/player.getNumValues() * 5, (int)(dims.getHeight()/(dice + 2))));
        penaltiesPanel.setBorder(BorderFactory.createTitledBorder("Penalties"));
        for(int i = 0; i < penalties.length; i++)
        {
            penalties[i] = new ScoreButton(Color.WHITE, Color.BLACK, "", new Dimension(dims.width/player.getNumValues(), dims.height/(dice + 2)));
            penalties[i].addActionListener(this);
            penalties[i].setMinimumSize(new Dimension(dims.width/player.getNumValues(), dims.height/(dice + 2)));
            penaltiesPanel.add(Box.createRigidArea(new Dimension(10, dims.height/(dice + 2) + 10)));
            penaltiesPanel.add(penalties[i]);
        }
        penaltiesPanel.setBackground(background);
        name = new JLabel(player.getName());
        normalPanel.add(name);
        normalPanel.add(Box.createHorizontalGlue());
        normalPanel.add(pass);
        normalPanel.add(Box.createHorizontalGlue());
        normalPanel.add(penaltiesPanel);
        c.weightx = 1;
        c.gridy = 0;
        c.anchor = GridBagConstraints.BASELINE_TRAILING;
        add(normalPanel, c);
        for(int row = 0; row < rows.length; row++)
        {
            Die die = player.getDie(row);
            rows[row] = new JPanel();
            rows[row].setBackground(die.getColor());
            rows[row].setLayout(new BoxLayout(rows[row], BoxLayout.LINE_AXIS));
            rows[row].add(Box.createHorizontalStrut(10));
            c.gridy = row + 1;
            for(int value = 0; value < player.getNumValues(); value++)
            {
                String text;
                int thingy = player.getValue(player.getDie(row), value);
                if(thingy == ScoreSheet.LOCK_VALUE)
                    text = LOCK_CHARACTER;
                else
                    text = Integer.toString(thingy);
                scoreButtons[row][value] = new ScoreButton(new Color((die.getColor().getRed() + 255) /2,
                        (die.getColor().getGreen() + 255) /2,
                        (die.getColor().getBlue() + 255) /2),
                        die.getColor(), text, new Dimension(dims.width/player.getNumValues(), dims.height/(dice + 2)));
                rows[row].add(scoreButtons[row][value]);
                scoreButtons[row][value].addActionListener(this);
                rows[row].add(Box.createRigidArea(new Dimension(10, dims.height/(dice + 2) + 10)));
                
                if(player.isNumberChecked(die, value))
                    scoreButtons[row][value].cross();
                else
                    scoreButtons[row][value].removeCross();
                
                if(player.canCross(die, value))
                    scoreButtons[row][value].setState(ButtonState.OPEN);
                else
                    scoreButtons[row][value].setState(ButtonState.CLOSED);
            }
            this.add(rows[row], c);
        }
        listeners = new ArrayList<>();
    }
    
    public boolean canChangePlayer()
    {
        return canChange;
    }
    
    public void allowChangePlayer()
    {
        canChange = true;
    }
    
    public void preventChangePlayer()
    {
        canChange = false;
    }
    
    private Player getActivePlayer()
    {
        return players[activePlayer];
    }
    
    public boolean isMainPlayer()
    {
        return activePlayer == mainPlayer;
    }
    
    public void setActivePlayer(int player)
    {
        activePlayer = player;
        updateSheet();
    }

    public void addListener(ScoreSheetListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeListener(ScoreSheetListener listener)
    {
        listeners.remove(listener);
    }
    
    public void setValuesActive(int value)
    {
        Player player = getActivePlayer();
        for(int row = 0; row < player.getNumDice(); row++)
        {
            Die die = player.getDie(row);
            for(int col = 0; col < player.getNumValues(); col++)
            {
                if(player.canCrossWithValue(die,col,value))
                    setValueActive(row, col);
            }
        }
    }
    
    public void setValueActive(int row, int index)
    {
        interactive[row][index] = true;
    }
    
    public void deActiveValue(int row, int index)
    {
        interactive[row][index] = false;
    }
    
    public void deActivateAllValues()
    {
        for(int r = 0; r < dice; r++)
            for(int c = 0; c < values; c++)
                interactive[r][c] = false;
    }
    
    public void updateSheet()
    {
        Player player = getActivePlayer();
        name.setText(player.getName());
        //pass.setVisible(isMainPlayer());
        
        for(int row = 0; row < player.getNumDice(); row++)
        {
            Die die = player.getDie(row);
            for(int col = 0; col < player.getNumValues(); col++)
            {
                if(player.isNumberChecked(die, col))
                    scoreButtons[row][col].cross();
                else
                    scoreButtons[row][col].removeCross();
                
                if(!player.canCross(die, col))
                    scoreButtons[row][col].setState(ButtonState.CLOSED);
                else if(isMainPlayer() && interactive[row][col])    
                    scoreButtons[row][col].setState(ButtonState.INTERACTABLE);
                else
                    scoreButtons[row][col].setState(ButtonState.OPEN);
            }
        }
        for(int index = 0; index < penalties.length; index++)
        {
            if(index < player.getPenalties())
                penalties[index].cross();
            else
                penalties[index].removeCross();
        }
        for(int p = 0; p < players.length; p++)
        {
            if(p == currentPlayer && p == activePlayer)
                playerList[p].setBorder(BorderFactory.createLineBorder(Color.BLUE, 5, true));
            else if(p == currentPlayer)
                playerList[p].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 5, true));
            else if(p == activePlayer)
                playerList[p].setBorder(BorderFactory.createLineBorder(Color.WHITE, 5, true));
            else
                playerList[p].setBorder(BorderFactory.createLineBorder(getBackground(), 5, true));
        }
        this.validate();
        this.repaint();
    }
    
    public void setCurrentPlayer(int player)
    {
        currentPlayer = player;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        name.setFont(new Font("ARIAL", Font.BOLD, GUIOperations.findFontSize(
                name.getText(), "ARIAL", Font.BOLD, new Rectangle2D.Float(0,0,dims.width/values * 10, dims.height/ (dice + 2)), g)));
        pass.setFont(new Font("ARIAL", Font.BOLD, GUIOperations.findFontSize(
                name.getText(), "ARIAL", Font.BOLD, new Rectangle2D.Float(0,0,dims.width/values * 10, dims.height/ (dice + 2)), g)));
        int buttonFontSize = GUIOperations.findFontSize(left.getText(), "ARIAL", Font.BOLD, new Rectangle2D.Float(0,0,left.getWidth() * .5f, left.getHeight() * .5f), g);
        left.setFont(new Font("ARIAL", Font.BOLD, buttonFontSize));
        right.setFont(new Font("ARIAL", Font.BOLD, buttonFontSize));
        Rectangle2D nameBox = new Rectangle2D.Float(0,0,this.getWidth()/players.length,(float)dims.getHeight()/(dice + 2));
        for(int p = 0; p < players.length; p++)
        {
            playerList[p].setFont(new Font(playerList[p].getFont().getName(), playerList[p].getFont().getStyle(), 
                    GUIOperations.findFontSize(playerList[p].getText(), playerList[p].getFont().getName(), playerList[p].getFont().getStyle(), nameBox, g)));
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        int row = -1, col = -1;
        for(int r = 0; r < dice; r++)
            for(int c = 0; c < values; c++)
                if(e.getSource() == scoreButtons[r][c])
                {
                    row = r;
                    col = c;
                }
        if(row != -1)
            for(ScoreSheetListener listener: listeners)
                listener.buttonPressed(row, col, getActivePlayer(), isMainPlayer());
        
        int index = -1;
        for(int i = 0; i < penalties.length; i++)
            if(penalties[i] == e.getSource())
                index = i;
        if(index != -1)
            for(ScoreSheetListener listener: listeners)
                listener.penaltyPressed(index, getActivePlayer(), isMainPlayer());
        
        if(e.getSource() == left)
        {
            activePlayer = (activePlayer + players.length - 1) % players.length;
            updateSheet();
        }
        else if(e.getSource() == right)
        {
            activePlayer = (activePlayer + 1) % players.length;
            updateSheet();
        }
        else if(e.getSource() == pass)
        {
            for(ScoreSheetListener listener : listeners)
            {
                listener.passPressed();
            }
        }
    }
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        Die[] dice = new Die[]{new Die(Color.RED.darker(), Color.WHITE, 6), new Die(Color.YELLOW.darker(), Color.WHITE, 6),
            new Die(Color.GREEN.darker(), Color.WHITE, 6), new Die(Color.BLUE.darker(), Color.WHITE, 6)};
        Die[] whiteDice = new Die[]{new Die(Color.WHITE, Color.BLACK, 6), new Die(Color.WHITE, Color.BLACK, 6)};
        Player[] players = new Player[]{new Player("Flyguy", dice, whiteDice), new Player("Escmo", dice, whiteDice), new Player("SockMonkey0", dice, whiteDice), new Player("Dicetower", dice, whiteDice)};
        PlayerView view = new PlayerView(players, 0, new Dimension(1000,400), new Color(173,216,230));
        frame.add(view);
        frame.setVisible(true);
        frame.setBounds(100,100,1350,550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.addListener(new ScoreSheetListener(){
            @Override
            public void buttonPressed(int row, int index, Player player, boolean onSelf)
            {
                player.crossNumber(player.getDie(row), index);
                System.out.println(view.getWidth() + " " + view.getHeight());
                view.setValuesActive(player.getValue(player.getDie(row), index) + 1);
                view.updateSheet();
            }
            @Override
            public void passPressed()
            {
                
            }
            @Override
            public void penaltyPressed(int index, Player player, boolean onSelf)
            {
                if(index == player.getPenalties())
                {
                    player.addPenalty();
                    view.updateSheet();
                }
            }
            @Override
            public void diePressed(Die die)
            {
                die.setSelected(!die.isSelected());
            }

            @Override
            public void rollFinished() {
                
            }
        });
    }
    
}
