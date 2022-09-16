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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private final List<NavbarListener> listeners = new ArrayList<>();
	private final ActionListener itemListener = event -> onItemAction(event);
	
	private int currentItem = -1;
	
	public Navbar() {
		setPreferredSize(new Dimension(400, 50));
		setLayout(new BorderLayout());
		nav.setOpaque(false);
		
		add(nav, BorderLayout.CENTER);
	}
	
	/**
	 * lors du click sur un boutons du navbar
	 * @param event
	 */
	private void onItemAction(ActionEvent event) {
		int index = items.indexOf(event.getSource());
		if(index == -1) 
			return;
		
		setCurrentItem(index);
	}
	
	/**
	 * ajout d'un item
	 * @param item
	 */
	private void addItem (NavbarItem item) {
		items.add(item);
		nav.add(item);
		item.addActionListener(itemListener);
		nav.add(Box.createHorizontalStrut(5));
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(Color.BLACK);
        g2.fillRect(0, getHeight() - 1, getWidth() - 1, 1);
	}
	
	/**
	 * creation d'un item du navbar
	 * @param item
	 * @return
	 */
	public Navbar addItem (String item) {
		NavbarItem c = new NavbarItem(item);
		addItem(c);
		return this;
	}
	
	/**
	 * demande la l'activation de l'item selectionnee
	 * @param currentItem
	 */
	public void setCurrentItem (int currentItem) {
		if(this.currentItem == currentItem)
			return;
		if(this.currentItem != -1)
			items.get(this.currentItem).setActive(false);
		
		this.currentItem = currentItem;
		items.get(currentItem).setActive(true);
		for (NavbarListener ls : listeners)
			ls.onAction(this, currentItem);
	}
	
	/**
	 * suscription aux evennement du navbar
	 * @param listener
	 */
	public void addNavbarListener (NavbarListener listener) {
		if(!listeners.contains(listener) && listener != null)
			listeners.add(listener);
	}
	
	/**
	 * desabonnement aux evennements du navbar
	 * @param listener
	 */
	public void removeNavbarListener (NavbarListener listener) {
		listeners.remove(listener);
	}

}
