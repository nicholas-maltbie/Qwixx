/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Nick_Pro
 */
public abstract class ControlledPlayer extends AbstractFSM<ControlledPlayer.PlayerState>
{
    public static final int PASS_VALUE = 0, PENALTY_VALUE = -1;
    
    private Player player;
    private boolean choseWhiteDie;
    private List<PlayerActionListener> listeners;
    
    public ControlledPlayer(Player player)
    {
        super(stateMap, PlayerState.NOTHING);
        this.player = player;
        this.listeners = new ArrayList();
    }
    
    @Override
    public void stateStarted(PlayerState state)
    {
        switch(state)
        {
            case WHITE_DIE:
                if(player.canCrossValue(player.getWhiteDiceValue()))
                    chooseWhiteDieResult();
                else
                {
                    choseWhiteDie = false;
                    actionChosen(null, PASS_VALUE);
                }
                break;
            case COLORED_DIE:
                if(player.canCrossAnyColored())
                    chooseColoredDieResult();
                else
                {
                    if(choseWhiteDie)
                        actionChosen(null, PASS_VALUE);
                    else
                        actionChosen(null, PENALTY_VALUE);
                }
                break;
        }
    }
    
    @Override
    public void stateEnded(PlayerState state)
    {
        switch(state)
        {
            case NOTHING:
                choseWhiteDie = false;
                break;
        }
    }
    
    abstract public void chooseWhiteDieResult();
    abstract public void chooseColoredDieResult();
    
    public void addListener(PlayerActionListener listener)
    {
        listeners.add(listener);
    }
    
    public void removeListener(PlayerActionListener listener)
    {
        listeners.remove(listener);
    }
    
    protected void actionChosen(Die die, int index)
    {
        if(getState() == PlayerState.WHITE_DIE && die != null)
            choseWhiteDie = true;
        for(PlayerActionListener listener : listeners)
            listener.actionChosen(player, die, index);
    }
    
    public Player getPlayer()
    {
        return player;
    }
    
    @Override
    public String toString()
    {
        return player.toString();
    }
    
    public static ControlledPlayer getEmptyPlayer(Player player)
    {
        return new ControlledPlayer(player) {
            @Override
            public void chooseWhiteDieResult()
            {
                
            }
            
            @Override
            public void chooseColoredDieResult()
            {
                
            }
        };
    }
    
    public static final HashMap<PlayerState, EnumSet<PlayerState>> stateMap;
    static {
        stateMap = new HashMap<>();
        stateMap.put(PlayerState.WHITE_DIE, EnumSet.of(PlayerState.NOTHING, PlayerState.COLORED_DIE));
        stateMap.put(PlayerState.COLORED_DIE, EnumSet.of(PlayerState.NOTHING));
        stateMap.put(PlayerState.NOTHING, EnumSet.of(PlayerState.WHITE_DIE));
    }
    
    public static enum PlayerState {WHITE_DIE, COLORED_DIE, NOTHING};
}
