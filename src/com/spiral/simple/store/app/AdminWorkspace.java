/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.spiral.simple.store.app.admin.AdminDashboard;
import com.spiral.simple.store.app.admin.PanelBudgetConfig;
import com.spiral.simple.store.app.admin.PanelSpends;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.swing.navs.Navbar;
import com.spiral.simple.store.swing.navs.NavbarListener;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 *
 */
public class AdminWorkspace extends JPanel {
	private static final long serialVersionUID = -7924902042401787185L;
	
	private final JPanel container = new JPanel(new BorderLayout());
	private final Navbar navbar = new Navbar();
	private final JPanel center = new JPanel(new BorderLayout());
	private final List<JPanel> containers = new ArrayList<>();
	
	private NavbarListener navbarListener = (nav , index) -> onNavbarAction(nav, index);
	
	public AdminWorkspace() {
		super(new BorderLayout());
		init();
	}
	
	/**
	 * ajout des composants graphique au panel
	 * et intiilisation des items du nav bar
	 */
	private void init() {
		container.add(navbar, BorderLayout.NORTH);
		container.add(center, BorderLayout.CENTER);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		add(container, BorderLayout.CENTER);
		container.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		
		navbar
		.addItem("Tableau de board")
		.addItem("Dépenses")
		.addItem("Configurations");
		
		containers.add(new AdminDashboard());
		containers.add(new PanelSpends());
		containers.add(new PanelBudgetConfig());
		
		navbar.addNavbarListener(navbarListener);
		navbar.setCurrentItem(0);
	}
	
	/**
	 * lors d'une action sur l'elemet du menu
	 * @param nav
	 * @param index
	 */
	private void onNavbarAction(Navbar nav, int index) {
		center.removeAll();
		center.add(containers.get(index), BorderLayout.CENTER);
		center.revalidate();
		center.repaint();
	}
	
	@Override
	public String getName() {
		return "Administration";
	}

}
