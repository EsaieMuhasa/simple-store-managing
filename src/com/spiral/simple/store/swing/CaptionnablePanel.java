/**
 * 
 */
package com.spiral.simple.store.swing;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 *
 */
public class CaptionnablePanel extends JPanel {

	private static final long serialVersionUID = 2224079186371984152L;
	private JLabel title = new JLabel();
	private JComponent container;

	/**
	 * 
	 */
	public CaptionnablePanel() {
		super(new BorderLayout());
		init();
	}
	
	public CaptionnablePanel(String title) {
		super(new BorderLayout());
		this.title.setText(title);
		init();
	}
	
	public CaptionnablePanel(String title, JComponent container) {
		super(new BorderLayout());
		this.container = container;
		this.title.setText(title);
		init();
	}
	
	private void init () {
		title.setOpaque(true);
		add(title, BorderLayout.NORTH);
		if(container  != null)
			add(container, BorderLayout.CENTER);
		
		title.setBackground(CustomTable.GRID_COLOR);
		setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
	}
	
	/**
	 * the title setter
	 * @param title
	 */
	public void setTitle(String title) {
		this.title.setText(title);
	}
	
	/**
	 * the container to set
	 * @param container
	 */
	public void setContainer(JComponent container) {
		if(this.container != null){
			this.container.setBorder(null);
			remove(this.container);
		}
		
		this.container = container;
		container.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		add(container, BorderLayout.CENTER);
		revalidate();
		repaint();
	}
}
