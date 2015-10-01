/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Nick_Pro
 */
public class ScoreSheet
{
    public static final int LOCK_VALUE = 0, MIN_CORSS = 5, PENALTY_VALUE = -5;
    public static final int[][] DEFAULT_NUMBERS = new int[][]{{2,3,4,5,6,7,8,9,10,11,-12,0},
        {2,3,4,5,6,7,8,9,10,11,-12,0},{12,11,10,9,8,7,6,5,4,3,-2,0},{12,11,10,9,8,7,6,5,4,3,-2,0}};
    
    private int[][] numbers;
    private boolean[][] scored;
    private boolean[] validRows;
    private int rows, columns, penalties;
    
    public ScoreSheet(int[][] numbers)
    {
        this.numbers = numbers;
        scored = new boolean[numbers.length][numbers[0].length];
        rows = numbers.length;
        columns = numbers[0].length;
        validRows = new boolean[rows];
        for(int i = 0; i < validRows.length; i++)
            validRows[i] = true;
    }
    
    public ScoreSheet()
    {
        this(DEFAULT_NUMBERS);
    }
    
    public void removeDie(int row)
    {
        validRows[row] = false;
    }
    
    public int getRows()
    {
        return rows;
    }
    
    public int getColumns()
    {
        return columns;
    }
    
    public void addPenalty()
    {
        penalties++;
    }
    
    public int getPenalties()
    {
        return penalties;
    }
    
    public void removePenalty()
    {
        penalties--;
    }
    
    public void cross(int row, int index)
    {
        scored[row][index] = true;
        if(index == getColumns() - 2)
            scored[row][index + 1] = true;
    }
    
    public void removeCross(int row, int index)
    {
        scored[row][index] = false;
    }
    
    public boolean canCross(int row, int index)
    {
        for(int i = getColumns()-1; i >= index; i--)
            if(scored[row][i] || 
                    (numbers[row][index] < 0 && getNumCrossed(row) < MIN_CORSS) ||
                    getValue(row, index) == LOCK_VALUE ||
                    !validRows[row])
                return false;
        return true;
    }
    
    public boolean canCross(int row, int index, int value)
    {
        return canCross(row, index) && value == getValue(row, index);
    }
    
    public boolean check(int row, int index)
    {
        return scored[row][index];
    }
    
    public int getValue(int row, int index)
    {
        return Math.abs(numbers[row][index]);
    }
    
    public int getNumCrossed(int row)
    {
        if(row > scored.length || row < 0)
            return 0;
        int num = 0;
        for(boolean x : scored[row])
            if(x) {
                num++;
            }
        return num;
    }
    
    public int getScore(int row)
    {
        int x = getNumCrossed(row);
        return x * (x + 1) / 2;
    }
    
    public int getTotalScore()
    {
        int score = 0;
        for(int row = 0; row < numbers.length; row++)
            score += getScore(row);
        return score + PENALTY_VALUE * getPenalties();
    }
    
    @Override
    public String toString()
    {
        String sheet = "sheet: {";
        for(int row = 0; row < getRows(); row++)
        {
            sheet += "{";
            for(int col = 0; col < getColumns(); col++)
            {
                sheet += getValue(row, col);
                if(check(row, col))
                    sheet += "x";
                else if(canCross(row, col))
                    sheet += " ";
                else
                    sheet += "-";
                
                if(col < getColumns() - 1)
                    sheet += ",";
            }
            sheet += "}";
            if(row < getRows() - 1)
                sheet += ";";
        }
        return sheet + "}; penalties: " + getPenalties();
    }

}
