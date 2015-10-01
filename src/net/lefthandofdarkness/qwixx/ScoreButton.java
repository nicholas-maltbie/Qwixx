/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import javax.swing.JButton;

/**
 *
 * @author Nick_Pro
 */
public class ScoreButton extends JButton
{    
    private Color open, interactable, closed, textColor;
    private ButtonState state;
    private boolean crossed;
    private Dimension size, lastSize;
    
    public ScoreButton(Color open, Color interactable, Color closed, Color textColor,
            String fontName, int fontStyle, int fontSize, String text, Dimension size)
    {
        super();
        this.open = open;
        this.interactable = interactable;
        this.closed = closed;
        this.textColor = textColor;
        this.setText(text);
        this.setState(ButtonState.OPEN);
        this.setForeground(textColor);
        this.setFont(new Font(getFont().getName(), getFont().getStyle(), 12));
        this.size = size;
        if(size != null)
        {
            this.setMinimumSize(size);
            this.setMaximumSize(size);
        }
    }
    
    public ScoreButton(Color color, Color textColor,
            String fontName, int fontStyle, int fontSize, String text, Dimension size)
    {
        this(color, color.brighter(), color.darker(), textColor, fontName, fontStyle, fontSize, text, size);
    }
    
    public ScoreButton(Color color, Color interactable, Color closed, Color textColor, String text, Dimension size)
    {
        this(color, interactable, closed, textColor, "ARIAL", Font.BOLD, 35, text, size);
    }
    
    public ScoreButton(Color color, Color textColor, String text, Dimension size)
    {
        this(color, textColor, "ARIAL", Font.BOLD, 35, text, size);
    }
    
    public void cross()
    {
        crossed = true;
    }
    
    public void removeCross()
    {
        crossed = false;
    }
    
    public boolean isCrossed()
    {
        return crossed;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if(size != lastSize)
        {
            this.setFont(new Font(getFont().getName(), getFont().getStyle(), 
                    GUIOperations.findFontSize(getText(), getFont().getName(), getFont().getStyle(),
                    new Rectangle2D.Float(0,0,(int)(size.width*.6),(int)(size.height*.8)), this.getGraphics())));
            lastSize = size;
        }
        Graphics2D g2 = (Graphics2D) g;
        int width = this.getWidth();
        int height = this.getHeight();
        if(crossed)
        {
            g2.setColor(Color.BLACK);
            Stroke s = g2.getStroke();
            g2.setStroke(new BasicStroke(Math.min(getWidth(), getHeight())/10));
            g2.drawLine((int)(width * .2), (int)(height * .2), (int)(width * .8), (int)(height * .8));
            g2.drawLine((int)(width * .8), (int)(height * .2), (int)(width * .2), (int)(height * .8));
            g2.setStroke(s);
        }
    }
    
    public ButtonState getState()
    {
        return state;
    }
    
    public void setState(ButtonState state)
    {
        this.state = state;
        switch(state)
        {
            case INTERACTABLE:
                setBackground(interactable);
                break;
            case CLOSED:
                setBackground(closed);
                break;
            default:
                setBackground(open);
        }
    }
    
    public static enum ButtonState {OPEN, INTERACTABLE, CLOSED};
}
