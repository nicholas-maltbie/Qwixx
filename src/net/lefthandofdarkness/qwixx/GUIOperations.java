/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package net.lefthandofdarkness.qwixx;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

/**
 * Class this is a group of useful graphical methods.
 * @author Nicholas Maltbie
 */
public class GUIOperations 
{
    /**
     * Will find font size for a graphical component that will fit text into 
     * a specified area.
     * @param text Text to fit into space.
     * @param font Font type that the text will be displayed in.
     * @param style Font style that the text will be displayed in.
     * @param targetBounds Target bounds that text will be sized to fit within.
     * @param context The graphical component that text will be drawn within.
     * @return Returns a font size that will allow text to fit within the
     * specified bounds.
     */
    public static int findFontSize(String text, String font, int style, 
            Rectangle2D targetBounds, Graphics context)
    {
        int fontSize = 1;
        FontMetrics metrics = context.getFontMetrics(new Font(font, Font.BOLD, fontSize));
        Rectangle2D fontBounds = metrics.getStringBounds(text, context);
        Rectangle2D bounds = new Rectangle2D.Float(0,0,(float)fontBounds.getWidth(), (float)fontBounds.getHeight());
        if(!targetBounds.contains(bounds))
            return 1;
        while(targetBounds.contains(bounds))
        {
            fontSize *= 2;
            metrics = context.getFontMetrics(new Font(font, Font.BOLD, fontSize));
            fontBounds = metrics.getStringBounds(text, context);
            bounds = new Rectangle2D.Float(0,0,(float)fontBounds.getWidth(), (float)fontBounds.getHeight());
        }
        while(!targetBounds.contains(bounds))
        {
            fontSize--;
            metrics = context.getFontMetrics(new Font(font, Font.BOLD, fontSize));
            fontBounds = metrics.getStringBounds(text, context);
            bounds = new Rectangle2D.Float(0,0,(float)fontBounds.getWidth(), (float)fontBounds.getHeight());
        }
        return fontSize;
    }
    
}
