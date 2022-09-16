/**
 * 
 */
package com.spiral.simple.store.app.admin;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.tools.Config;

/**
 * @author Esaie Muhasa
 * tabeau de board de l'administration de l'application
 */
public class AdminDashboard extends JPanel {
	private static final long serialVersionUID = -6628170417791066111L;

	private final JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
	private final PieChartPanel piePanel = new PieChartPanel();
	private final HistogrammPanel histogrammPanel = new HistogrammPanel();
	
	public AdminDashboard() {
		super(new BorderLayout());
		init();
	}
	
	/**
	 * initialisation des composants graphiques
	 */
	private void init() {
		add(tabbed, BorderLayout.CENTER);
		
		tabbed.addTab("Etats ", new ImageIcon(Config.getIcon("pie")), piePanel);
		tabbed.addTab("Entrées/Sorties ", new ImageIcon(Config.getIcon("chart")), histogrammPanel);
	}

	/**
	 * @author Esaie Muhasa
	 * panel de visualisation du pie chart du liquidite disponible en caisse
	 */
	private class PieChartPanel extends JPanel {
		private static final long serialVersionUID = -1108138932826650587L;
		
		public PieChartPanel() {
			super(new BorderLayout());
		}
		
	}

	/**
	 * @author Esaie Muhasa
	 * panel de visualisation des entrees/sorties
	 */
	private class HistogrammPanel extends JPanel {
		private static final long serialVersionUID = -7134623874177241934L;
		
	}

}
