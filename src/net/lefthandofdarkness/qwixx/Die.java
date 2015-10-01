/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 *
 * @author Nick_Pro
 */
public class Die 
{
    private Color color, pipColor;
    private Point2D position;
    private boolean selected, inGame = true;
    private int sides, value;
    private Random gen;
    
    public Die(Color color, Color pipColor, int sides, Random random)
    {
        this.sides = sides;
        this.color = color;
        this.pipColor = pipColor;
        this.gen = random;
        this.value = 1;
    }
    
    public Die(Color color, Color pipColor, int sides)
    {
        this(color, pipColor, sides, new Random());
    }
    
    public void setValue(int value)
    {
        this.value = value;
    }
    
    public Point2D getPosition()
    {
        return position;
    }
    
    public boolean isInGame()
    {
        return inGame;
    }
    
    public void removeFromGame()
    {
        inGame = false;
    }
    
    public void addToGame()
    {
        inGame = true;
    }
    
    public void setPosition(Point2D pos)
    {
        position = pos;
    }
    
    public boolean isSelected()
    {
        return selected;
    }
    
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
    
    public int roll()
    {
        value = gen.nextInt(sides) + 1;
        return value;
    }
    
    public int getValue()
    {
        return value;
    }
    
    public int getSides()
    {
        return sides;
    }
    
    public Color getColor()
    {
        return color;
    }
    
    public Color getPipColor()
    {
        return pipColor;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Die)
        {
            Die d = (Die) other;
            return d.color.equals(color) && d.sides == sides;
        }
        return false;
    }
    
    @Override
    public String toString()
    {
        return "Die with " + sides + " sides and of color " + color.toString();
    }
}
