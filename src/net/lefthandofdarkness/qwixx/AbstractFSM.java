/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Java implementation of a Finite State Machine, or at least similar to a 
 * Finite State Machine.
 * @author Nicholas Maltbie
 * @param <State> Enum that holds all the different states that AbstractFSM can
 * exist in.
 */
abstract public class AbstractFSM<State extends Enum<State>>
{
    /**
     * Logger to note errors within the log from the FSM.
     */
    protected static Logger log = Logger.getLogger("AbstractFSM");
    /**
     * Current state of the State Machine.
     */
    private State currentState;
    /**
     * A preset list of all the states that each state can reach in the form
     * of a Map.
     */
    private Map<State, EnumSet<State>> stateMap;
    /**
     * Represents if the state machine is running.
     */
    private boolean isRunning;
    
    /**
     * Constructs a state machine with a map for all possible state changes and
     * a default state to start at.
     * @param stateMap Map of all changes that can occur within the state 
     * machine.
     * @param inital The initial state that the machine will start in.
     */
    public AbstractFSM(Map<State, EnumSet<State>> stateMap, State inital)
    {
        currentState = inital;
        this.stateMap = stateMap;
    }
    
    /**
     * Starts the state machine. If the state machine has already stated, 
     * it will log an error and do nothing.
     */
    public void start()
    {
        if(!isRunning)
        {
            isRunning = true;
            stateStarted(currentState);
        }
        else
            log.log(Level.SEVERE, "State Machine {0} has attempted to start but"
                    + " is already currently running", this);
    }
    
    /**
     * Stops the state machine and ends the current state. If the state machine 
     * has already stated, it will log an error and do nothing.
     */
    public void stop()
    {
        if(isRunning)
        {
            isRunning = false;
            stateEnded(currentState);
        }
        else
            log.log(Level.SEVERE, "State Machine {0} has attempted to stop but is not currently running", this);
    }
    
    /**
     * Gets the current state of the State Machine.
     * @return Returns the current state.
     */
    public State getState()
    {
        return currentState;
    }
    
    /**
     * Gets if a specified state can be reached form the current state of the
     * state machine.
     * @param state State to attempt to reach.
     * @return Returns if the  specified state can be reached from the
     * current state.
     */
    protected boolean isReachable(State state)
    {
        return stateMap.get(currentState).contains(state);
    }
    
    /**
     * Sets the new state of the state machine, ends the current state and
     * starts the new state. If the state is not reachable from the current
     * state, it will not switch to the new state and will report an error
     * in the log.
     * @param state New state to set for the machine.
     */
    protected void setState(State state)
    {
        if(isReachable(state))
        {
            stateEnded(currentState);
            currentState = state;
            stateStarted(state);
        }
        else
        {
            log.log(Level.SEVERE, "State {0} cannot be reached from State {1}"
                    + " and has not changed", new Object[]{state.toString(),
                        currentState.toString()});
        }
    }
    
    /**
     * Method that is called any time a state is started.
     * @param state State started.
     */
    abstract protected void stateStarted(State state);
    /**
     * Method that is called any time a state ends.
     * @param state State ended.
     */
    abstract protected void stateEnded(State state);
}
