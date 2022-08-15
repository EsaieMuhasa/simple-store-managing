/**
 * 
 */
package com.spiral.simple.store.swing.navs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class Navbar extends JPanel {
	private static final long serialVersionUID = 1292212363276203184L;
	
	private final Box nav = Box.createHorizontalBox();
	private final List<NavbarItem> items = new ArrayList<>();
	
	public Navbar() {
		setPreferredSize(new Dimension(400, 50));
		setLayout(new BorderLayout());
		nav.setOpaque(false);
		
		add(nav, BorderLayout.CENTER);
	}
	
	public Navbar addItem (NavbarItem item) {
		items.add(item);
		nav.add(item);
		nav.add(Box.createHorizontalStrut(5));
		return this;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.BLACK);
        g2.fillRect(0, getHeight() - 2, getWidth() - 5, 2);
        
	}
	
	public Navbar addItem (String item) {
		NavbarItem c = new NavbarItem(item);
		addItem(c);
		return this;
	}

}
