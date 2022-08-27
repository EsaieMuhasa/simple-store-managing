/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.spiral.simple.store.app.components.SellerSidebar;

/**
 * @author Esaie Muhasa
 *
 */
public class SellerWorkspace extends JPanel {
	private static final long serialVersionUID = -2474958501183133361L;
	
	private final SellerSidebar  sidebar = new SellerSidebar();
	private final JPanel center = new JPanel(new BorderLayout());

	/**
	 * 
	 */
	public SellerWorkspace() {
		super(new BorderLayout());
		
		add(sidebar, BorderLayout.EAST);
		add(center, BorderLayout.CENTER);
	}
	
	@Override
	public String getName() {
		return "Espace de vente";
	}


}
