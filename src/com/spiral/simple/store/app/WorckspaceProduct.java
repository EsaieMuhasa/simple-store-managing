/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 *
 */
public class WorckspaceProduct extends JPanel {
	private static final long serialVersionUID = -1072961878916213987L;
	
	private final PanelProducts panelProducts = new PanelProducts();
	private final PanelStocks panelStocks = new PanelStocks();
	
	
	private JLabel title = UIComponentBuilder.createH1("Stocks et produits");
	private JTextField fieldSearch = new JTextField();
	
	private JButton btnAddStock = new JButton("Nouveau stock", UIComponentBuilder.loadIcon("new"));
	private JButton btnAddProduct = new JButton("Nouveau produit", UIComponentBuilder.loadIcon("new"));
	
	{//ecoute des events des boutons de creation des stocks est produits
		btnAddProduct.addActionListener(event -> {
			panelProducts.addProduct();			
		});
		btnAddStock.addActionListener(event -> {
			panelStocks.addStock();			
		});
	}//==

	public WorckspaceProduct() {
		super(new BorderLayout());
		
		final Box 
			top = Box.createHorizontalBox(), 
			tool = Box.createHorizontalBox();
		
		final JPanel center = new JPanel(new BorderLayout());
		
		top.add(title);
		top.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		//tools
		tool.add(fieldSearch);
		top.add(Box.createHorizontalStrut(10));
		tool.add(btnAddProduct);
		tool.add(btnAddStock);
		tool.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		//==
		
		JTabbedPane tabbe = new JTabbedPane(JTabbedPane.BOTTOM);
		tabbe.addTab("Stocks ", new ImageIcon(Config.getIcon("classeur")), panelStocks);
		tabbe.addTab("Liste de produit ", new ImageIcon(Config.getIcon("list")), panelProducts);
		
		center.add(tool, BorderLayout.NORTH);
		center.add(tabbe, BorderLayout.CENTER);
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
	}
	
	@Override
	public String getName() {
		return "Produits";
	}


}
