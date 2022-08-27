/**
 * 
 */
package com.spiral.simple.store.app.components;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;
import com.toedter.calendar.JCalendar;

/**
 * @author Esaie Muhasa
 *
 */
public class SellerSidebar extends JPanel {
	private static final long serialVersionUID = -8618632784321077214L;
	
	private final JButton btnAddCommand =new JButton("Ajouter une commande", new ImageIcon(Config.getIcon("new")));
	private final JCalendar calendar = new JCalendar();
	
	public SellerSidebar() {
		super(new BorderLayout());
		
		final JPanel top = new JPanel(new BorderLayout());
		final JPanel center = new JPanel(new BorderLayout());
		final JPanel btn = new JPanel(new BorderLayout());
		
		btn.add(btnAddCommand, BorderLayout.CENTER);
		btn.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		
		top.add(calendar, BorderLayout.CENTER);
		top.add(btn, BorderLayout.NORTH);
		
		final JTabbedPane tabbed = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbed.addTab("", new ImageIcon(Config.getIcon("list")), new JPanel());
		tabbed.addTab("", new ImageIcon(Config.getIcon("pie")), new JPanel());
		
		final JLabel title = UIComponentBuilder.createH2("Synthèse des opérations");
		title.setOpaque(true);
		title.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		title.setBackground(CustomTable.GRID_COLOR);
		
		center.add(title, BorderLayout.NORTH);
		center.add(tabbed, BorderLayout.CENTER);
		center.setBorder(BorderFactory.createLineBorder(title.getBackground()));
		
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);

	}
	
}
