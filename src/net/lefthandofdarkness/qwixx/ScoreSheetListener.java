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
public interface ScoreSheetListener {
    public void buttonPressed(int row, int index, Player player, boolean onSelf);
    public void passPressed();
    public void penaltyPressed(int index, Player player, boolean onSelf);
    public void diePressed(Die die);
    public void rollFinished();
}
