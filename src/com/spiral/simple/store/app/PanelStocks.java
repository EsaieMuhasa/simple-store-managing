package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import com.spiral.simple.store.app.components.StockView;
import com.spiral.simple.store.app.form.AbstractForm;
import com.spiral.simple.store.app.form.FormListener;
import com.spiral.simple.store.app.form.StockForm;
import com.spiral.simple.store.beans.Stock;
import com.spiral.simple.store.dao.DAOBaseListener;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.StockDao;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

public class PanelStocks extends JPanel{
	private static final long serialVersionUID = 290296541824600761L;

	private JDialog dialogFormStock;
	private StockForm stockForm;
	
	private final StockDao stockDao;
	
	private final GridLayout gridLayout = new GridLayout(5, 2, 10, 10);
	private final StocksContainer container;
	
	private final FormListener formListener = new FormListener() {
		
		@Override
		public void onValidate(AbstractForm<?> form) {}
		
		@Override
		public void onRejetData(AbstractForm<?> form, String... causes) {}
		
		@Override
		public void onCancel(AbstractForm<?> form) {
			stockForm.setStock(null);
		}
		
		@Override
		public void onAcceptData(AbstractForm<?> form) {
			Stock stock = stockForm.getStock();
			if(stock.getId() == null)
				stockDao.create(AbstractForm.DEFAULT_ON_PERSIST_REQUEST_ID, stock);
			else 
				stockDao.update(AbstractForm.DEFAULT_ON_PERSIST_REQUEST_ID, stock);
			
			stockForm.setEnabled(false);
		}
	};
	
	private final DAOListenerAdapter<Stock> stockListenerAdapter = new DAOListenerAdapter<Stock>() {
		@Override
		public void onCreate(Stock... data) {
			hideDialogStock();
		}

		@Override
		public void onUpdate(Stock newState, Stock oldState) {
			hideDialogStock();
		}
	};

	public PanelStocks() {
		super(new BorderLayout());
		
		stockDao = DAOFactory.getDao(StockDao.class);
		stockDao.addBaseListener(stockListenerAdapter);
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
		createDialogStock();
		dialogFormStock.setTitle("Insersion d'un nouveau stock");
		stockForm.setStock(new Stock());
		dialogFormStock.setLocationRelativeTo(dialogFormStock.getOwner());
		dialogFormStock.setVisible(true);
	}
	
	/**
	 * demande de mise en jour du stock en parametre
	 * @param stock
	 */
	public void updateStock (Stock stock) {
		Stock s = stockDao.findById(stock.getId());
		createDialogStock();
		dialogFormStock.setTitle("Mise en jour d'un stock");
		stockForm.setStock(s);
		dialogFormStock.setLocationRelativeTo(dialogFormStock.getOwner());
		dialogFormStock.setVisible(true);
	}
	
	/**
	 * gere l'instatiation de la boite e dialogue d'insersion d'un stock
	 */
	private void createDialogStock() {
		if(dialogFormStock == null) {
			dialogFormStock = new JDialog(MainWindow.getLastInstance(), "Stock", true);
			dialogFormStock.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

			stockForm = new StockForm();
			stockForm.addFormListener(formListener);
			stockForm.doReload();
			JPanel c = (JPanel) dialogFormStock.getContentPane();
			c.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			c.add(stockForm, BorderLayout.CENTER);
			dialogFormStock.pack();
			dialogFormStock.setSize(450, dialogFormStock.getHeight()+20);
		}
	}
	
	private void hideDialogStock () {
		stockForm.setEnabled(true);
		stockForm.setStock(null);
		dialogFormStock.setVisible(false);
		dialogFormStock.dispose();
	}
	
	/**
	 * @author Esaie Muhasa
	 * 
	 */
	private class StocksContainer extends JPanel implements DAOBaseListener<Stock>{
		private static final long serialVersionUID = 5891556954122664513L;
		
		private final List<StockView> stocks = new ArrayList<>();
		private final JPanel contentPanel = new JPanel(gridLayout);
		private int selectedIndex = -1;
		
		private final JPopupMenu popup = new JPopupMenu();
		private final JMenuItem [] options = {
				new JMenuItem("Modifier", new ImageIcon(Config.getIcon("edit"))),
				new JMenuItem("Supprimer", new ImageIcon(Config.getIcon("close"))),
				new JMenuItem("Fiche de stock", new ImageIcon(Config.getIcon("report")))
		};
		
		private final MouseAdapter mouseAdapter = new  MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() != MouseEvent.BUTTON1)
					return;
				
				StockView view = (StockView) e.getSource();
				setSelectedItem(view);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(!e.isPopupTrigger())
					return;
				
				StockView view = (StockView) e.getSource();
				setSelectedItem(view);
				options[1].setEnabled(view.getStock().getSoldQuantity() == 0);
				popup.show(view, e.getX(), e.getY());
			}
			
			/**
			 * activation de la view en parametre
			 * @param view
			 */
			private void setSelectedItem (StockView view) {
				if(selectedIndex != -1)
					stocks.get(selectedIndex).setSelected(false);
				
				selectedIndex = stocks.indexOf(view);
				view.setSelected(true);
			}
		};
		
		private final ActionListener optionListener = event -> onOptionItemAction(event);

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
			
			//pop up menu items
			for (JMenuItem item : options) {
				popup.add(item);
				item.addActionListener(optionListener);
				if(item == options[1])
					popup.addSeparator();
			}
			//
		}
		
		@Override
		public void doLayout() {
			int rows = gridLayout.getRows();
			int cols = gridLayout.getColumns();
			int width = this.getWidth();
			if ( width < 850) {
				rows = stocks.size();
				cols = 1;
			} else if (width >= 850 && width < 1350) {
				rows = (stocks.size() / 2) + (stocks.size() % 2);
				cols = 2;
			} else {
				cols = 3;
				rows = (stocks.size() / 3) + (stocks.size() % 3 != 0? 1 : 0);
			}
			
			gridLayout.setColumns(cols);
			gridLayout.setRows(rows);
			contentPanel.revalidate();
			super.doLayout();
		}
		
		/**
		 * lors d'une action sur un element du menu
		 * @param event
		 */
		private void onOptionItemAction (ActionEvent event) {
			if (event.getSource() == options[0]) {
				updateStock(stocks.get(selectedIndex).getStock());
			} else if (event.getSource() == options[1]) {
				
			} else if (event.getSource() == options[2]) {
				
			}
		}
		
		/**
		 * dispose all used resources
		 */
		private void dispose () {
			if(selectedIndex != -1)
				stocks.get(selectedIndex).setSelected(false);
			
			selectedIndex = -1;
			for (StockView view : stocks){
				contentPanel.remove(view);
				view.dispose();
				view.removeMouseListener(mouseAdapter);
			}
			
			stocks.clear();
		}
		
		/**
		 * reload all stock
		 */
		private void reload() {
			dispose();
			
			if(stockDao.countAll() == 0)
				return;
			
			Stock data [] = stockDao.findAll();
			for (Stock stock : data) {
				StockView view = new StockView(stock);
				view.addMouseListener(mouseAdapter);
				stocks.add(view);
				contentPanel.add(view);
			}
		}

		@Override
		public void onCreate(Stock... data) {
			StockView view = new StockView(data[0]);
			view.addMouseListener(mouseAdapter);
			stocks.add(view);
			contentPanel.add(view);
			revalidate();
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
						view.removeMouseListener(mouseAdapter);
						revalidate();
						contentPanel.repaint();
						break;
					}
				}
			}
		}
	}

}
