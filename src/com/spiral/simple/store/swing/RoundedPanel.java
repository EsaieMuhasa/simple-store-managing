package com.spiral.simple.store.swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JPanel;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class RoundedPanel extends JPanel {
	private static final long serialVersionUID = -1344023632858672730L;
	
    private int roundTopLeft = 0;
    private int roundTopRight = 0;
    private int roundBottomLeft = 0;
    private int roundBottomRight = 0;
    
    public RoundedPanel() {
        setOpaque(false);
    }

    public RoundedPanel (LayoutManager layout) {
		super(layout);
		setOpaque(false);
	}


	/**
     * return top-left round ration
     * @return
     */
	public int getRoundTopLeft() {
        return roundTopLeft;
    }
    
	/**
	 * top-left round ration to set
	 * @param roundTopLeft
	 */
    public void setRoundTopLeft(int roundTopLeft) {
        this.roundTopLeft = roundTopLeft;
        repaint();
    }
    
    /**
     * return top-right round ration
     * @return
     */
    public int getRoundTopRight() {
        return roundTopRight;
    }
    
    /**
     * top-right round ration to set
     * @param roundTopRight
     */
    public void setRoundTopRight(int roundTopRight) {
        this.roundTopRight = roundTopRight;
        repaint();
    }
    
    /**
     * return bottom-left round ration
     * @return
     */
    public int getRoundBottomLeft() {
        return roundBottomLeft;
    }
    
    /**
     * bottom-left round ration to set
     * @param roundBottomLeft
     */
    public void setRoundBottomLeft(int roundBottomLeft) {
        this.roundBottomLeft = roundBottomLeft;
        repaint();
    }
    
    /**
     * return bottom-right round ration
     * @return
     */
    public int getRoundBottomRight() {
        return roundBottomRight;
    }
    
    
    /**
     * the bottom-right round ration to set
     * @param roundBottomRight
     */
    public void setRoundBottomRight(int roundBottomRight) {
        this.roundBottomRight = roundBottomRight;
        repaint();
    }

    @Override
    protected void paintComponent (Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new GradientPaint(0, 0, new Color(130, 130, 130), getWidth(), 0, new Color(200, 200, 200)));
        Area area = new Area(createRoundTopLeft());
        if (roundTopRight > 0)
            area.intersect(createRoundTopRight());
        
        if (roundBottomLeft > 0)
            area.intersect(createRoundBottomLeft());
        
        if (roundBottomRight > 0)
            area.intersect(createRoundBottomRight());
            
        g2.fill(area);
        //g2.dispose();
        super.paintComponent(grphcs);
    }
    
    //round radius utilities methods
    //===========================

    private final RoundRectangle2D rectRoundBottomRight = new RoundRectangle2D.Double();
    private final RoundRectangle2D rectRoundBottomRightX = new RoundRectangle2D.Double();
    private final RoundRectangle2D rectRoundBottomRightY = new RoundRectangle2D.Double();
    
    private final RoundRectangle2D rectRoundTopLeft = new RoundRectangle2D.Double();
    private final RoundRectangle2D rectRoundTopLeftX = new RoundRectangle2D.Double();
    private final RoundRectangle2D rectRoundTopLeftY = new RoundRectangle2D.Double();
    private Area createRoundTopLeft() {
        int width = getWidth();
        int height = getHeight();
        int roundX = Math.min(width, roundTopLeft);
        int roundY = Math.min(height, roundTopLeft);
        
        rectRoundTopLeft.setRoundRect(0, 0, width, height, roundX, roundY);
        rectRoundTopLeftX.setFrame(roundX / 2, 0, width - roundX / 2, height);
        rectRoundTopLeftY.setFrame(0, roundY / 2, width, height - roundY / 2);
        
        Area area = new Area(rectRoundTopLeft);
        area.add(new Area(rectRoundTopLeftX));
        area.add(new Area(rectRoundTopLeftY));
        
        return area;
    }
    
    private final RoundRectangle2D rectRoundTopRight = new RoundRectangle2D.Double();
    private final RoundRectangle2D rectRoundTopRightX = new RoundRectangle2D.Double();
    private final RoundRectangle2D rectRoundTopRightY = new RoundRectangle2D.Double();
    private Area createRoundTopRight() {
        int width = getWidth();
        int height = getHeight();
        int roundX = Math.min(width, roundTopRight);
        int roundY = Math.min(height, roundTopRight);
        
        rectRoundTopRight.setRoundRect(0, 0, width, height, roundX, roundY);
        rectRoundTopRightX.setFrame(0, 0, width - roundX / 2, height);
        rectRoundTopRightY.setFrame(0, roundY / 2, width, height - roundY / 2);
        
        Area area = new Area(rectRoundTopRight);
        area.add(new Area(rectRoundTopRightX));
        area.add(new Area(rectRoundTopRightY));
        return area;
    }
    
    private final RoundRectangle2D rectRoundBottomLeft = new RoundRectangle2D.Double();
    private final RoundRectangle2D rectRoundBottomLeftX = new RoundRectangle2D.Double();
    private final RoundRectangle2D rectRoundBottomLeftY = new RoundRectangle2D.Double();
    private Area createRoundBottomLeft() {
        int width = getWidth();
        int height = getHeight();
        int roundX = Math.min(width, roundBottomLeft);
        int roundY = Math.min(height, roundBottomLeft);
        
        rectRoundBottomLeft.setRoundRect(0, 0, width, height, roundX, roundY);
        rectRoundBottomLeftX.setFrame(roundX / 2, 0, width - roundX / 2, height);
        rectRoundBottomLeftY.setFrame(0, 0, width, height - roundY / 2);
        
        Area area = new Area(rectRoundBottomLeft);
        area.add(new Area(rectRoundBottomLeftX));
        area.add(new Area(rectRoundBottomLeftY));
        return area;
    }
    
    private Area createRoundBottomRight() {
        int width = getWidth();
        int height = getHeight();
        int roundX = Math.min(width, roundBottomRight);
        int roundY = Math.min(height, roundBottomRight);
        
        rectRoundBottomRight.setRoundRect(0, 0, width, height, roundX, roundY);
        rectRoundBottomRightX.setFrame(0, 0, width - roundX / 2, height);
        rectRoundBottomRightY.setFrame(0, 0, width, height - roundY / 2);
        
        Area area = new Area(rectRoundBottomRight);
        area.add(new Area(rectRoundBottomRightX));
        area.add(new Area(rectRoundBottomRightY));
        return area;
    }
    //==
}
