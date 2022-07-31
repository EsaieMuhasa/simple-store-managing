package com.spiral.simple.store.swing.navs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

public class KaliNavItem extends JButton {
	private static final long serialVersionUID = 395260339581550890L;
	
	private static final Dimension DEFAULT_SIZE = new Dimension(40, 40);
	private static final Dimension TRANSFORM_SIZE = new Dimension(50, 50);
	private static final Color COLOR_ACTIVE = new Color(0x505050);

    private final Animation animation;
    private Image image;
    private Dimension imageSize = DEFAULT_SIZE;
    private boolean active = false;
    
    private MouseAdapter mouseAdapter = new MouseAdapter() {
    	@Override
    	public void mouseEntered(MouseEvent me) {
    		animation.mouseEnter();
    	}
    	
    	@Override
    	public void mouseExited(MouseEvent me) {
    		animation.mouseExit();
    	}
    };

    public KaliNavItem () {
        setContentAreaFilled(false);
        setBorder(null);
        animation = new Animation();
        addMouseListener(mouseAdapter);
    }

	public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        repaint();
    }

    public Dimension getImageSize() {
        return imageSize;
    }

    public void setImageSize(Dimension imageSize) {
        this.imageSize = imageSize;
        repaint();
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
		if(this.active == active)
			return;
		
		this.active = active;
		repaint();
	}

	@Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        int width = getWidth();
        int height = getHeight();
        int x = (width - imageSize.width) / 2;
        int y = (height - imageSize.height) / 2;
        
        if(active) {
        	g2.setColor(COLOR_ACTIVE.brighter());
        	g2.fillRect(0, 0, width, height);
        	g2.setColor(COLOR_ACTIVE.darker());
        	g2.fillRect(0, 0, 4, height);
        }
        g2.drawImage(image, x, y, imageSize.width, imageSize.height, null);
        
        g2.dispose();
        super.paintComponent(grphcs);
    }
    
    private class Animation {
        private Animator animator;
        private TimingTarget target;

        public Animation () {
            animator = new Animator(200);
            animator.setResolution(0);
        }

        public void mouseEnter() {
            stop();
            animator.removeTarget(target);
            target = new PropertySetter(KaliNavItem.this, "imageSize", getImageSize(), TRANSFORM_SIZE);
            animator.addTarget(target);
            animator.start();
        }

        public void mouseExit() {
            stop();
            animator.removeTarget(target);
            target = new PropertySetter(KaliNavItem.this, "imageSize", getImageSize(), DEFAULT_SIZE);
            animator.addTarget(target);
            animator.start();
        }

        public void stop () {
            if (animator.isRunning()) {
                animator.stop();
            }
        }
    }
}
