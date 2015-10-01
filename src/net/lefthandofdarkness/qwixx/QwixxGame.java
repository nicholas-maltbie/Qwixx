/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.lefthandofdarkness.qwixx;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.lefthandofdarkness.qwixx.ControlledPlayer.PlayerState;

/**
 *
 * @author Nick_Pro
 */
public class QwixxGame extends AbstractFSM<QwixxGame.GameState> implements ScoreSheetListener, PlayerActionListener
{    
    private Die[] dice;
    private ControlledPlayer[] players;
    private int currentPlayer;
    
    private List<Player> actionsTaken;
    
    private PlayerView playerView;
    private DiceView diceView;
    
    private JPanel pane;
    
    public QwixxGame(JFrame frame, ControlledPlayer[] players, Die[] dice)
    {
        super(stateMap, GameState.ROLL);
        currentPlayer = (int) (Math.random() * players.length);
        frame.setBounds(100,100,1350,775);
        Player[] nonControlledPlayers = new Player[players.length];
        for(int i = 0; i < players.length; i++)
        {
            nonControlledPlayers[i] = players[i].getPlayer();
            players[i].addListener(this);
        }
        playerView = new PlayerView(nonControlledPlayers, 0, new Dimension(1000,400), new Color(173,216,230));
        diceView = new DiceView(dice, new Color(173,216,230), 150);
        this.players = players.clone();
        pane = new JPanel();
        frame.setContentPane(pane);
        pane.setLayout(new BoxLayout(pane, BoxLayout.PAGE_AXIS));
        pane.add(diceView);
        pane.add(playerView);
        playerView.setCurrentPlayer(currentPlayer);
        playerView.updateSheet();
        playerView.setBorder(BorderFactory.createTitledBorder("Score Sheet"));
        diceView.addListener(this);
        playerView.addListener(this);
        if(players[0] instanceof HumanPlayer)
            ((HumanPlayer) players[0]).setGraphicalComponent(playerView, diceView);
        start();
    }
    
    public void nextPlayer()
    {
        currentPlayer = (currentPlayer + 1) % players.length;
        playerView.setCurrentPlayer(currentPlayer);
        playerView.updateSheet();
    }
    
    public ControlledPlayer getCurrentPlayer()
    {
        return players[currentPlayer];
    }

    @Override
    protected void stateStarted(GameState state) {
        switch(state)
        {
            case ROLL:
                diceView.roll(1, 20);
                break;
            case FIRST_CHOICE:
                actionsTaken = new ArrayList();
                for(ControlledPlayer p : players)
                    p.setState(PlayerState.WHITE_DIE);
                break;
            case SECOND_CHOICE:
                getCurrentPlayer().setState(PlayerState.COLORED_DIE);
                break;
            case END_GAME:
                Arrays.sort(players, new WinnerSorter());
                System.out.println(Arrays.toString(players));
                break;
        }
    }

    @Override
    protected void stateEnded(GameState state) {
        switch(state)
        {
            case SECOND_CHOICE:
                for(ControlledPlayer p : players)
                    p.setState(PlayerState.NOTHING);
                removeUsedDice();
                diceView.repaint();
                break;
            case FIRST_CHOICE:
                removeUsedDice();
                diceView.repaint();
                break;
        }
    }
    
    public void removeUsedDice()
    {
        for(ControlledPlayer p : players)
            for(int die = 0; die < p.getPlayer().getNumDice(); die++)
                if(p.getPlayer().isColorFinished(p.getPlayer().getDie(die)))
                    p.getPlayer().getDie(die).removeFromGame();
    }
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        Die[] dice = new Die[]{new Die(Color.WHITE, Color.BLACK, 6), new Die(Color.WHITE, Color.BLACK, 6), 
            new Die(Color.RED.darker(), Color.WHITE, 6), new Die(Color.YELLOW.darker(), Color.WHITE, 6),
            new Die(Color.GREEN.darker(), Color.WHITE, 6), new Die(Color.BLUE.darker(), Color.WHITE, 6)};
        Die[] coloredDice = new Die[4];
        Die[] whiteDice = new Die[2];
        System.arraycopy(dice, 2, coloredDice, 0, 4);
        System.arraycopy(dice, 0, whiteDice, 0, 2);
        Player player = new Player("Flyguy", coloredDice, whiteDice);
        QwixxGame game = new QwixxGame(frame, new ControlledPlayer[]{
            new HumanPlayer(player),
            new RandomPlayer(new Player("Player2", coloredDice, whiteDice)),
            new RandomPlayer(new Player("Player3", coloredDice, whiteDice)), 
            new RandomPlayer(new Player("Player4", coloredDice, whiteDice))},
                dice);
        frame.setVisible(true);
        frame.setBounds(100,100,1350,775);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }

    @Override
    public void buttonPressed(int row, int index, Player player, boolean onSelf) {
        
    }

    @Override
    public void penaltyPressed(int index, Player player, boolean onSelf) {
        
    }

    @Override
    public void diePressed(Die die) {
        
    }

    @Override
    public void rollFinished() {
        if(getState() == GameState.ROLL)
        {
            setState(GameState.FIRST_CHOICE);
        }
    }

    @Override
    public void actionChosen(Player player, Die die, int index) 
    {
        if(getState() == GameState.FIRST_CHOICE)
        {
            if(!actionsTaken.contains(player))
            {
                actionsTaken.add(player);
                if(die != null)
                {
                    player.crossNumber(die, index);
                }
                playerView.updateSheet();
                if(actionsTaken.size() == players.length)
                {
                    actionsTaken.clear();
                    setState(GameState.SECOND_CHOICE);
                }
            }
        }
        else if(getState() == GameState.SECOND_CHOICE)
        {
            if(die != null)
                player.crossNumber(die, index);
            else if(index == -1)
                player.addPenalty();
            playerView.updateSheet();
            removeUsedDice();
            nextPlayer();
            diceView.repaint();
            int removed = 0, maxPenalties = 0;
            for(int d = 0; d < players[0].getPlayer().getNumDice(); d++)
                if(!players[0].getPlayer().getDie(d).isInGame())
                    removed++;
            for(ControlledPlayer p : players)
                if(p.getPlayer().getPenalties() > maxPenalties)
                    maxPenalties = p.getPlayer().getPenalties();
            if(removed >= 2 || maxPenalties >= 4)
                setState(GameState.END_GAME);
            else
                setState(GameState.ROLL);
        }
    }

    @Override
    public void passPressed() {
        
    }
    
    public enum GameState {ROLL, FIRST_CHOICE, SECOND_CHOICE, END_GAME};
    
    public static final HashMap<GameState, EnumSet<GameState>> stateMap;
    static {
        stateMap = new HashMap<>();
        stateMap.put(GameState.ROLL, EnumSet.of(GameState.FIRST_CHOICE));
        stateMap.put(GameState.FIRST_CHOICE, EnumSet.of(GameState.SECOND_CHOICE));
        stateMap.put(GameState.SECOND_CHOICE, EnumSet.of(GameState.ROLL, GameState.END_GAME));
        stateMap.put(GameState.END_GAME, EnumSet.noneOf(GameState.class));
    }
    
    private class WinnerSorter implements Comparator<ControlledPlayer>
    {

        @Override
        public int compare(ControlledPlayer o1, ControlledPlayer o2) {
            if(o1.getPlayer().getScore() == o2.getPlayer().getScore())
                return 0;
            else if(o1.getPlayer().getScore() < o2.getPlayer().getScore())
                return 1;
            return -1;
        }
        
    }
}
