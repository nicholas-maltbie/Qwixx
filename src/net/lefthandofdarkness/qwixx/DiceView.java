/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Nick_Pro
 */
public class DiceView extends JPanel implements MouseListener
{
    public static final Color SELECTED_COLOR = new Color(255, 255, 0, 100);
    private Die[] dice;
    private int dieLength;
    private List<ScoreSheetListener> listeners;
    private boolean rolling = false;
    private Thread rollThread;
    
    public DiceView(Die[] dice, Color background, int dieLength)
    {
        super();
        this.dice = dice;
        this.dieLength = dieLength;
        this.setBackground(background);
        listeners = new ArrayList<>();
        this.addMouseListener(this);
        positionDice();
        this.add(Box.createVerticalStrut((int)(dieLength*1.2)));
    }
    
    public int getNumDice()
    {
        return dice.length;
    }
    
    public int getDieIndex(Die die)
    {
        for(int i = 0; i < dice.length; i++)
            if(die == dice[i])
                return i;
        return -1;
    }
    
    public void setDieSelected(int index)
    {
        dice[index].setSelected(true);
    }
    
    public void setDieDeSelected(int index)
    {
        dice[index].setSelected(false);
    }
    
    public void deSelectAllDice()
    {
        for(int i = 0; i < getNumDice(); i++)
            setDieDeSelected(i);
    }
    
    public void roll(int seconds, int intervalMillis)
    {
        if(rolling)
        {
            rollThread.stop();
        }
        rolling = true;
        rollThread = new Thread(){
            @Override
            public void run()
            {
                try {
                    long start = System.currentTimeMillis();
                    while(System.currentTimeMillis() < start + seconds*1000)
                    {
                        for(Die d : dice) {
                            d.roll();
                        }
                        repaint();
                        Thread.sleep(intervalMillis);
                    }
                    rolling = false;
                    for(ScoreSheetListener listener : listeners)
                    {
                        listener.rollFinished();
                    }
                } catch (InterruptedException ex) {

                }
            }
        };
        rollThread.start();
    }
    
    public void stopRolling()
    {
        rollThread.stop();
    }
    
    public void addListener(ScoreSheetListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeListener(ScoreSheetListener listener)
    {
        listeners.remove(listener);
    }
    
    public void positionDice()
    {
        for(int i = 0; i < dice.length; i++)
            dice[i].setPosition(new Point2D.Float((i + 1) * dieLength/5 + i * dieLength, dieLength/10));
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for(int i = 0; i < dice.length; i++)
            if(dice[i].isInGame())
                drawDie(dice[i], g2);
    }
    
    public void drawDie(Die die, Graphics2D g2)
    {
        if(die.isSelected())
        {
            g2.setColor(SELECTED_COLOR);
            g2.fill(new RoundRectangle2D.Float((float)die.getPosition().getX() - dieLength *.05f,
                    (float)die.getPosition().getY() - dieLength*.05f, dieLength*1.1f, dieLength*1.1f, dieLength/5, dieLength/5));
        }
        g2.setColor(die.getColor());
        g2.fill(new RoundRectangle2D.Float((float)die.getPosition().getX(), 
                (float)die.getPosition().getY(), dieLength, dieLength, dieLength/5, dieLength/5));
        float pipDiameter = dieLength/5;
        float spacing = dieLength/10;
        g2.setColor(die.getPipColor());
        boolean[][] pips = PIP_VALUES[0];
        if(die.getValue() > 0 && die.getValue() < 7)
            pips = PIP_VALUES[die.getValue() - 1];
        g2.setColor(die.getPipColor());
        for(int row = 0; row < pips.length; row++)
            for(int col = 0; col < pips[0].length; col++)
                if(pips[row][col])
                    g2.fill(new Ellipse2D.Float((3*col + 1) * spacing + (float)die.getPosition().getX(),
                            (3*row + 1) * spacing + (float)die.getPosition().getY(), pipDiameter, pipDiameter));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        
    }
    
    public Die getClicked(Point2D point)
    {
        for(Die die : dice)
        {
            if(die.isInGame() && new RoundRectangle2D.Float((float)die.getPosition().getX(), 
                    (float)die.getPosition().getY(), dieLength, dieLength, dieLength/5, dieLength/5).contains(point))
                    return die;
        }
        return null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Die die = getClicked(e.getPoint());
        if(die != null)
        {
            for(ScoreSheetListener listener : listeners)
                listener.diePressed(die);
        }        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    public static final boolean[][][] PIP_VALUES = new boolean[][][]{
        {{false, false, false},
         {false, true, false},
         {false, false, false}
        },
        {{true, false, false},
         {false, false, false},
         {false, false, true}
        },
        {{false, false, true},
         {false, true, false},
         {true, false, false}
        },
        {{true, false, true},
         {false, false, false},
         {true, false, true}
        },
        {{true, false, true},
         {false, true, false},
         {true, false, true}
        },
        {{true, false, true},
         {true, false, true},
         {true, false, true}
        },
    };
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        Die[] dice = new Die[]{new Die(Color.WHITE, Color.BLACK, 6), new Die(Color.WHITE, Color.BLACK, 6), 
            new Die(Color.RED.darker(), Color.WHITE, 6), new Die(Color.YELLOW.darker(), Color.WHITE, 6),
            new Die(Color.GREEN.darker(), Color.WHITE, 6), new Die(Color.BLUE.darker(), Color.WHITE, 6)};
        DiceView view = new DiceView(dice, new Color(173,216,230), 150);
        frame.add(view);
        frame.setVisible(true);
        frame.setBounds(100,100,1150,225);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.addListener(new ScoreSheetListener(){
            @Override
            public void buttonPressed(int row, int index, Player player, boolean onSelf)
            {
                
            }
            @Override
            public void passPressed()
            {
                
            }
            @Override
            public void penaltyPressed(int index, Player player, boolean onSelf)
            {
                
            }
            @Override
            public void diePressed(Die die)
            {
                die.setSelected(!die.isSelected());
                view.repaint();
            }

            @Override
            public void rollFinished() {
                
            }
        });
    }
    
}
