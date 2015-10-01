/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

/**
 *
 * @author Nick_Pro
 */
public class HumanPlayer extends ControlledPlayer implements ScoreSheetListener
{
    private PlayerView view;
    private DiceView dice;
    private boolean whiteDie, coloredDie, crossedWhite;
    private int whiteDieSelected = -1, coloredDieSelected = -1;

    public HumanPlayer(Player player) {
        super(player);
    }

    public void setGraphicalComponent(PlayerView view, DiceView dice)
    {
        this.view = view;
        this.dice = dice;
        view.addListener(this);
        dice.addListener(this);
    }
    
    @Override
    public void chooseWhiteDieResult() {
        whiteDie = true;
        crossedWhite = false;
        view.setValuesActive(getPlayer().getWhiteDiceValue());
        view.updateSheet();
    }

    @Override
    public void chooseColoredDieResult() {
        coloredDie = true;
        for(int whiteDie = 0; whiteDie < 2; whiteDie++)
        {
            for(int coloredDie = 0; coloredDie < getPlayer().getNumDice(); coloredDie++)
            {
                view.setValueActive(coloredDie, getPlayer().getValueIndex(getPlayer().getDie(coloredDie), 
                        getPlayer().getDie(coloredDie).getValue() + getPlayer().getWhiteDie(whiteDie).getValue()));
            }
        }
        view.updateSheet();
    }
    
    @Override
    public void buttonPressed(int row, int index, Player player, boolean onSelf) {
        if(whiteDie && onSelf && player.canCross(player.getDie(row), index) && player.getValue(player.getDie(row), index) == player.getWhiteDiceValue())
        {
            actionChosen(player.getDie(row), index);
        }
        if(coloredDie && onSelf && getPlayer().isColoredResult(getPlayer().getDie(row), index) && player.canCross(player.getDie(row), index))
        {
            actionChosen(player.getDie(row), index);
        }
    }

    @Override
    public void passPressed() {
        if(whiteDie)
        {
            actionChosen(null, -1);
        }
        else if(coloredDie && crossedWhite)
        {
            actionChosen(null, 0);
        }
    }

    @Override
    public void penaltyPressed(int index, Player player, boolean onSelf) {
        if(coloredDie && onSelf && player.getPenalties() == index)
        {
            actionChosen(null, -1);
        }
    }

    @Override
    public void diePressed(Die die) {
        if(coloredDie)
        {
            int index = dice.getDieIndex(die);
            if(index < 2)
            {
                if(whiteDieSelected != -1)
                    dice.setDieDeSelected(whiteDieSelected);
                if(whiteDieSelected != index)
                {
                    whiteDieSelected = index;
                    dice.setDieSelected(whiteDieSelected);
                }
                else
                {
                    whiteDieSelected = -1;
                    dice.setDieDeSelected(index);
                }
            }
            else 
            {
                if(coloredDieSelected != -1)
                    dice.setDieDeSelected(coloredDieSelected);
                if(coloredDieSelected != index)
                {
                    coloredDieSelected = index;
                    dice.setDieSelected(coloredDieSelected);
                }
                else
                {
                    coloredDieSelected = -1;
                    dice.setDieDeSelected(index);
                }
            }
            view.updateSheet();
            dice.repaint();
            
            if(whiteDieSelected != -1 && coloredDieSelected != -1 && 
                    getPlayer().canCross(getPlayer().getDie(coloredDieSelected - 2), 
                            getPlayer().getValueIndex(getPlayer().getDie(coloredDieSelected - 2),
                                    getPlayer().getDie(coloredDieSelected - 2).getValue() + getPlayer().getWhiteDie(whiteDieSelected).getValue())))
            {
                Die colored = getPlayer().getDie(coloredDieSelected - 2);
                actionChosen(colored, getPlayer().getValueIndex(colored, colored.getValue() + getPlayer().getWhiteDie(whiteDieSelected).getValue()));
            }
        }
    }

    @Override
    public void rollFinished() {
        
    }
    
    @Override
    public void actionChosen(Die die, int index)
    {
        if(whiteDie)
        {
            view.deActivateAllValues();
            whiteDie = false;
            if(die != null)
                crossedWhite = true;
        }
        if(coloredDie)
        {
            view.deActivateAllValues();
            coloredDie = false;
            dice.deSelectAllDice();
            whiteDieSelected = -1;
            coloredDieSelected = -1;
            crossedWhite = false;
            dice.repaint();
        }
        super.actionChosen(die, index);
    }
}
