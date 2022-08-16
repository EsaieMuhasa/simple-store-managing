package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.spiral.simple.store.app.form.StockForm;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.UIComponentBuilder;

public class PanelStocks extends JPanel{
	private static final long serialVersionUID = 290296541824600761L;
	
	private JLabel title = UIComponentBuilder.createH1("Stocks");
	private JButton btnAdd = new JButton("Nouveau stock", UIComponentBuilder.loadIcon("new"));
	
	private final ActionListener listenerBtnAdd = event -> createStock(event);
	{
		btnAdd.addActionListener(listenerBtnAdd);
	}
	
	private JDialog dialogFormStock;

	public PanelStocks() {
		super(new BorderLayout());
		
		add(createBody(), BorderLayout.CENTER);
		add(createHeader(), BorderLayout.NORTH);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);
	}
	
	@Override
	public String getName() {
		return "Stocks";
	}
	
	/**
	 * init header components
	 * and listing mouse click to btnAdd
	 */
	private JPanel createHeader() {
		JPanel top = new JPanel(new BorderLayout());
		top.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		top.add(title, BorderLayout.CENTER);
		top.add(btnAdd, BorderLayout.EAST);
		return top;
	}
	
	private JPanel createBody () {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		return panel;
	}
	
	private void createStock(ActionEvent event) {
		if(dialogFormStock == null) {
			dialogFormStock = new JDialog(MainWindow.getLastInstance(), "Formulaire d'insersion d'un nouveau stock", true);
			dialogFormStock.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialogFormStock.add(new StockForm(), BorderLayout.CENTER);
			JPanel c = (JPanel) dialogFormStock.getContentPane();
			dialogFormStock.pack();
			dialogFormStock.setSize(450, dialogFormStock.getHeight()+20);
			c.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		}
		dialogFormStock.setLocationRelativeTo(dialogFormStock.getOwner());
		dialogFormStock.setVisible(true);
	}

}
