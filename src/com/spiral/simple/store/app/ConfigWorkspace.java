/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import com.spiral.simple.store.swing.navs.Navbar;

/**
 * @author Esaie MUHASA
 *
 */
public class ConfigWorkspace extends JPanel {
	private static final long serialVersionUID = 4347424585824918024L;
	
	private final CardLayout containerLayout = new CardLayout();
	
	private final Navbar navbar = new Navbar();
	private final JPanel container = new JPanel(containerLayout);

	/**
	 * 
	 */
	public ConfigWorkspace() {
		super(new BorderLayout());
		
		init();
		add(navbar,  BorderLayout.NORTH);
		add(container, BorderLayout.CENTER);
	}
	
	private void init() {
		navbar.addItem("Bureau d'echange").addItem("Repatition des recettes");

	}
	
	@Override
	public String getName() {
		return "main-configuration-workspace";
	}

}
