/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author Esaie MUHASA
 *
 */
public class MainWorkspace extends JPanel{

	private static final long serialVersionUID = 8284401309978935780L;
	private final CardLayout layout = new CardLayout();
	
	public MainWorkspace() {
		super();
		setLayout(layout);
	}
	
	public void showAt (int index) {
		Component c = getComponent(index);
		layout.show(this, c.getName());
	}
	
	/**
	 * 
	 * @param components
	 */
	public void addElements (JComponent... components) {
		for (JComponent com : components)
			add(com, com.getName());
	}

}
