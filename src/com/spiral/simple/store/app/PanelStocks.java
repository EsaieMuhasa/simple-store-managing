package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.spiral.simple.store.app.components.StockView;
import com.spiral.simple.store.app.form.StockForm;
import com.spiral.simple.store.beans.Stock;
import com.spiral.simple.store.dao.DAOBaseListener;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.StockDao;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.UIComponentBuilder;

public class PanelStocks extends JPanel{
	private static final long serialVersionUID = 290296541824600761L;

	private JDialog dialogFormStock;
	private StockForm stockForm;
	
	private final StockDao stockDao;
	
	private final GridLayout gridLayout = new GridLayout(5, 2, 10, 10);
	private final StocksContainer container;

	public PanelStocks() {
		super(new BorderLayout());
		
		stockDao = DAOFactory.getDao(StockDao.class);
		container = new StocksContainer();
		
		add(createBody(), BorderLayout.CENTER);
		add(createHeader(), BorderLayout.NORTH);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		container.reload();
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
		return top;
	}
	
	private JPanel createBody () {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		panel.add(container, BorderLayout.CENTER);
		return panel;
	}
	
	/**
	 * utilitaire de demande de creation d'un nouveau stock
	 */
	public void addStock () {
		createStock();
	}
	
	/**
	 * gere l'instatiation de la boite e dialogue d'insersion d'un stock
	 */
	private void createStock() {
		if(dialogFormStock == null) {
			dialogFormStock = new JDialog(MainWindow.getLastInstance(), "Formulaire d'insersion d'un nouveau stock", true);
			dialogFormStock.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			stockForm = new StockForm();
			stockForm.reload();
			JPanel c = (JPanel) dialogFormStock.getContentPane();
			c.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			c.add(stockForm, BorderLayout.CENTER);
			dialogFormStock.pack();
			dialogFormStock.setSize(450, dialogFormStock.getHeight()+20);
		}
		dialogFormStock.setLocationRelativeTo(dialogFormStock.getOwner());
		dialogFormStock.setVisible(true);
	}
	
	/**
	 * @author Esaie Muhasa
	 * 
	 */
	private class StocksContainer extends JPanel implements DAOBaseListener<Stock>{
		private static final long serialVersionUID = 5891556954122664513L;
		
		private final List<StockView> stocks = new ArrayList<>();
		private final JPanel contentPanel = new JPanel(gridLayout);

		public StocksContainer () {
			super(new BorderLayout());
			final Box box = Box.createVerticalBox();
			final JScrollPane scroll = new JScrollPane(contentPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			
			scroll.setBorder(null);
			box.add(scroll);
			box.add(Box.createVerticalGlue());
			
			add(box, BorderLayout.CENTER);
			stockDao.addBaseListener(this);
			contentPanel.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		}
		
		/**
		 * dispose all used resources
		 */
		private void dispose () {
			for (StockView view : stocks){
				contentPanel.remove(view);
				view.dispose();
			}
			
			stocks.clear();
		}
		
		/**
		 * reload all stock
		 */
		private void reload() {
			dispose();
			
			Stock data [] = stockDao.findAll();
			for (Stock stock : data) {
				StockView view = new StockView(stock);
				stocks.add(view);
				contentPanel.add(view);
			}
		}

		@Override
		public void onCreate(Stock... data) {
			StockView view = new StockView(data[0]);
			stocks.add(view);
			contentPanel.add(view);
			contentPanel.revalidate();
			contentPanel.repaint();
		}

		@Override
		public void onUpdate(Stock newState, Stock oldState) {}

		@Override
		public void onUpdate(Stock[] newState, Stock[] oldState) {}

		@Override
		public void onDelete(Stock... data) {
			for (Stock stock : data) {
				for (StockView view : stocks) {
					if (view.getStock().getId() == stock.getId()) {
						view.dispose();
						stocks.remove(view);
						break;
					}
				}
			}
		}
	}

}
