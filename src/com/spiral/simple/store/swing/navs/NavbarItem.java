package com.spiral.simple.store.swing.navs;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

/**
 * @author Esaie MUHASA
 * Rendu d'un MenuItem
 */
public class NavbarItem extends JButton {
	private static final long serialVersionUID = -1236143905781402069L;
	
	/**
	 * L'index du sous menu dans le menu principale
	 */
	private int index;
	
	private Animator animator;
	private int targetSize;
	private float animatSize;
	protected Point pressedPoint;//le point où l'on a cliquer avec la souris
	private float alpha;
	private final Color effectColor = new Color(255, 255, 255, 150);
	private boolean active;
    
    /**
     * Contructeur d'initialisation d'un item de menu simple
     * @param text
     */
    public NavbarItem(String text) {
        super(text);
        init();
        setBorder(new EmptyBorder(5, 10, 5, 10));
        setForeground(Color.BLACK);
    }
    
    /**
	 * @return the active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(boolean active) {
		if(active  == this.active)
			return;
		
		this.active = active;
		setFont(active? getFont().deriveFont(Font.BOLD) : getFont().deriveFont(Font.PLAIN));
	}

	/**
     * renvoie l'index du menu, -1 dans le cas où l'initialisation de l'inde n'a pas eu lieux
     * @return
     */
    public int getIndex() {
    	return index;
    }
    
    /**
     * Revoie l'index du menu
     * @param index
     */
    public void setIndex(int index) {
    	this.index = index;
    }

    /**
     * personnalisation du bouton
     * - Evennements sourie
     * - Animation
     */
    private void init() {
        setContentAreaFilled(false);
        setForeground(new Color(255, 255, 255));
        setHorizontalAlignment(JButton.LEFT);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                targetSize = Math.max(getWidth(), getHeight()) * 2;
                animatSize = 0;
                pressedPoint = me.getPoint();
                alpha = 0.5f;
                if (animator.isRunning()) {
                    animator.stop();
                }
                animator.start();
            }
        });
        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                if (fraction > 0.5f)
                    alpha = 1 - fraction;
                animatSize = fraction * targetSize;
                repaint();
            }
        };
        animator = new Animator(400, target);
        animator.setResolution(0);
        index = -1;
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (pressedPoint != null) {
            g2.setColor(effectColor);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fillOval((int) (pressedPoint.x - animatSize / 2), (int) (pressedPoint.y - animatSize / 2), (int) animatSize, (int) animatSize);
        }
        g2.setComposite(AlphaComposite.SrcOver);
        super.paintComponent(grphcs);
    }
}
