/**
 * 
 */
package com.spiral.simple.store.app.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.swing.Card;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.swing.DefaultCardModel;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;
import com.toedter.calendar.JCalendar;

/**
 * @author Esaie Muhasa
 *
 */
public class SellerSidebar extends JPanel {
	private static final long serialVersionUID = -8618632784321077214L;
	
	private final JButton btnAddCommand =new JButton("Nouvele commande", new ImageIcon(Config.getIcon("new")));
	private final JCalendar calendar = new JCalendar();
	
	private final DefaultCardModel<Double> cardModelInput = new DefaultCardModel<>(CustomTable.GRID_COLOR, Color.BLACK, Config.getIcon("btn-add"), "$");
	private final DefaultCardModel<Double> cardModelOutput = new DefaultCardModel<>(CustomTable.GRID_COLOR, Color.BLACK, Config.getIcon("btn-minus"), "$");
	private final DefaultCardModel<Double> cardModelAvailable = new DefaultCardModel<>(CustomTable.GRID_COLOR, Color.BLACK, Config.getIcon("caisse"), "$");
	
	private final List<SellerSidebarListener> listeners = new ArrayList<>();
	private final PropertyChangeListener calendarListener = event -> onSelectedDateChange();
	
	{
		calendar.addPropertyChangeListener("calendar", calendarListener);
		btnAddCommand.addActionListener(event -> {
			for (SellerSidebarListener ls : listeners)
				ls.onNewCommand();
		});
	}
	
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
		tabbed.addTab("", new ImageIcon(Config.getIcon("list")), initCards());
		tabbed.addTab("", new ImageIcon(Config.getIcon("pie")), new JPanel());
		
		final JLabel title = UIComponentBuilder.createH2("Synthèse du 28/08/2022");
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
	
	
	/**
	 * initialisation du panel qui contiens le cards qui visualise les etats
	 * @return
	 */
	private JPanel initCards() {

		final Card cardInput = new Card(cardModelInput);
		final Card cardOutput = new Card(cardModelOutput);
		final Card cardAvailable = new Card(cardModelAvailable);
		
		cardModelAvailable.setValue(0d);
		cardModelAvailable.setTitle("En caisse");
		cardModelAvailable.setInfo("Montant liquide encaisser");
		
		cardModelInput.setValue(0d);
		cardModelInput.setTitle("Recette");
		cardModelInput.setInfo("Vante total du jour");
		
		cardModelOutput.setValue(0d);
		cardModelOutput.setTitle("Dépense");
		cardModelOutput.setInfo("Depenses du ");
		
		final Box box = Box.createVerticalBox();
		final JPanel panel = new JPanel(new BorderLayout());
		box.add(cardInput);
		box.add(Box.createVerticalStrut(10));
		box.add(cardOutput);
		box.add(Box.createVerticalStrut(10));
		box.add(cardAvailable);
		
		panel.add(box, BorderLayout.CENTER);
		panel.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		return panel;
	}
	
	/**
	 * when selected date change
	 */
	private void onSelectedDateChange () {
		for (SellerSidebarListener ls : listeners)
			ls.onDateChange(calendar.getDate());
	}
	
	/**
	 * change status button sender for creation new command request
	 * @param enabled
	 */
	public void setEnabledAddCommand (boolean enabled ) {
		btnAddCommand.setEnabled(enabled);
	}
	
	/**
	 * subscribe a new listener on side bar events
	 * @param listener
	 */
	public void addSidebarListener (SellerSidebarListener listener) {
		if(!listeners.contains(listener)){
			listeners.add(listener);
			listener.onDateChange(calendar.getDate());
		}
	}
	
	/**
	 * unsubscribe listener on side bar events
	 * @param listener
	 */
	public void removeSidebarListener (SellerSidebarListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * @author Esaie Muhasa
	 * interface to listening seller side bar event
	 */
	public static interface SellerSidebarListener {
		
		/**
		 * Called on selected date change in JCalendar panel
		 * @param date
		 */
		void onDateChange (Date date);
		
		/**
		 * called when user click on add new command button
		 */
		void onNewCommand ();
		
	}
	
}
