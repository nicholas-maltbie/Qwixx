/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.util.Random;

/**
 *
 * @author Nick_Pro
 */
public class RandomPlayer extends ControlledPlayer
{
    private Random random;
    
    public RandomPlayer(Player player) {
        super(player);
        this.random = new Random();
    }

    @Override
    public void chooseWhiteDieResult() {
        int choice = random.nextInt(5), index = -1;
        if(choice < 4)
            index = getPlayer().getValueIndex(getPlayer().getDie(choice), getPlayer().getWhiteDiceValue());
        if(choice < 4 && index != -1 && getPlayer().canCrossWithValue(getPlayer().getDie(choice),
                index, getPlayer().getWhiteDiceValue()))
            super.actionChosen(getPlayer().getDie(choice), index);
        else
            super.actionChosen(null, index);
    }

    @Override
    public void chooseColoredDieResult() {
        int choice = random.nextInt(10), index = -1;
        if(choice < 8)
            index = getPlayer().getValueIndex(getPlayer().getDie(choice/2), 
                getPlayer().getWhiteDie(choice/5).getValue() + getPlayer().getDie(choice/2).getValue());
        if(choice < 8 && index != -1 && getPlayer().canCrossWithValue(getPlayer().getDie(choice/2),
                index, getPlayer().getWhiteDie(choice/5).getValue() + getPlayer().getDie(choice/2).getValue()))
            super.actionChosen(getPlayer().getDie(choice/2), index);
        else
            super.actionChosen(null, index);
    }
    
}
