/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.awt.Color;

/**
 *
 * @author Nick_Pro
 */
public class Player 
{
    public static final int MAX_PENALTIES = 4;
    
    private String name;
    private ScoreSheet sheet;
    private Die[] coloredDice, whiteDice;
    
    public Player(String name, Die[] coloredDice, Die[] whiteDice, int[][] sheetValues)
    {
        this.name = name;
        this.coloredDice = coloredDice;
        this.whiteDice = whiteDice;
        sheet = new ScoreSheet(sheetValues);
    }
    
    public Player(String name, Die[] coloredDice, Die[] whiteDice)
    {
        this(name, coloredDice, whiteDice, ScoreSheet.DEFAULT_NUMBERS);
    }

    public boolean isColoredResult(Die die, int index)
    {
        for(int whiteDie = 0; whiteDie < 2; whiteDie++)
        {
            if(getValue(die, index) == die.getValue() + getWhiteDie(whiteDie).getValue())
            {
                return true;
            }
        }
        return false;
    }
    
    public boolean isColorFinished(Die die)
    {
        return sheet.check(getRow(die), sheet.getColumns() - 1);
    }
    
    public int getWhiteDiceValue()
    {
        return whiteDice[0].getValue() + whiteDice[1].getValue();
    }
    
    public String getName()
    {
        return name;
    }
    
    public int getValueIndex(Die die, int value)
    {
        for(int i = 0; i < sheet.getColumns(); i++)
            if(sheet.getValue(getRow(die), i) == value)
                return i;
        return -1;
    }
    
    public boolean canCrossWithValue(Die die, int index, int value)
    {
        return sheet.canCross(getRow(die), index, value);
    }
    
    public boolean canCross(Die die, int index)
    {
        return sheet.canCross(getRow(die), index);
    }
    
    public boolean isNumberChecked(Die die, int index)
    {
        return sheet.check(getRow(die), index);
    }
    
    public int getScore()
    {
        return sheet.getTotalScore();
    }
    
    public int getPenalties()
    {
        return sheet.getPenalties();
    }
    
    public void addPenalty()
    {
        sheet.addPenalty();
    }
    
    public int getRowScore(Die die)
    {
        return sheet.getScore(getRow(die));
    }
    
    public void removeDie(Die die)
    {
        sheet.removeDie(getRow(die));
    }
    
    public int getNumDice()
    {
        return coloredDice.length;
    }
    
    public Die getDie(int index)
    {
        return coloredDice[index];
    }
    
    public Die getWhiteDie(int index)
    {
        return whiteDice[index];
    }
    
    public int getNumValues()
    {
        return sheet.getColumns();
    }
    
    public int getValue(Die die, int index)
    {
        return sheet.getValue(getRow(die), index);
    }
    
    public void crossNumber(Die die, int index)
    {
        sheet.cross(getRow(die), index);
    }
    
    public int getRow(Die die)
    {
        for(int i = 0; i < coloredDice.length; i++)
            if(coloredDice[i].equals(die))
                return i;
        return -1;
    }
    
    public boolean canCrossValue(int value)
    {
        for(int row = 0; row < sheet.getRows(); row++)
            for(int col = 0; col < sheet.getColumns(); col++)
                if(sheet.getValue(row, col) == value && sheet.canCross(row, col))
                    return true;
        return false;
    }
    
    public boolean canCrossAnyColored()
    {
        for(int row = 0; row < sheet.getRows(); row++)
            for(int col = 0; col < sheet.getColumns(); col++)
                if(isColoredResult(getDie(row), col) && sheet.canCross(row, col))
                    return true;
        return false;
    }
    
    @Override
    public String toString()
    {
        return getName() + " score: " + getScore();
    }
}
