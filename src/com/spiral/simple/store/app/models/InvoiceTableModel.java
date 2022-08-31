/**
 * 
 */
package com.spiral.simple.store.app.models;

import com.spiral.simple.store.beans.AffectedStock;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandItem;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.ExchangeRate;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.beans.Stock;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CommandItemDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.ExchangeRateDao;
import com.spiral.simple.store.dao.StockDao;

/**
 * @author Esaie Muhasa
 *
 */
public class InvoiceTableModel extends DBEntityTableModel<CommandItem> {
	private static final long serialVersionUID = -1601268679481550985L;

	private static final String [] TITLES = {"N°", "Produit", "Quantité", "P.U", "Total"};
	
	private final CommandItemDao commandItemDao = DAOFactory.getDao(CommandItemDao.class);
	private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
	private final ExchangeRateDao exchangeRateDao = DAOFactory.getDao(ExchangeRateDao.class);
	private final StockDao stockDao = DAOFactory.getDao(StockDao.class);
	
	private Command command;
	
	public InvoiceTableModel() {
		super(DAOFactory.getDao(CommandItemDao.class));
	}
	
	@Override
	public synchronized void persist() {
		if (command == null)
			return;
		
		if(command.getId() == null || command.getId().trim().isEmpty())
			commandDao.create(RPERSIST_EQUEST_ID, command);
		else
			super.persist();		
	}
	
	@Override
	public void addRow(CommandItem row) {
		command.addItem(row);
		super.addRow(row);
	}

	@Override
	public synchronized void reload() {
		data.clear();
		if(command == null || command.countItems() == 0)
			return;
		
		CommandItem [] items = command.getItems();
		for (int i = 0; i < items.length; i++)
			data.add(items[i]);
		
		fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return TITLES.length;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column  < TITLES.length)
			return TITLES[column];
		return super.getColumnName(column);
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: return (rowIndex + 1);
			case 1: return data.get(rowIndex).getProduct().getName();
			case 2: return data.get(rowIndex).getQuantity();
			case 3: {
				if (data.get(rowIndex).getCurrency() == null)
					return "";
				return data.get(rowIndex).getUnitPrice() +" "+data.get(rowIndex).getCurrency().getShortName();
			}
			case 4: {
				if (data.get(rowIndex).getCurrency() == null)
					return "";
				return data.get(rowIndex).getTotalPrice()+" "+data.get(rowIndex).getCurrency().getShortName(); 
			}
		}
		return "";
	}
	
	/**
	 * create new command item for product
	 * @param product
	 */
	public CommandItem createByProduct (Product product) {
		CommandItem item = new CommandItem();
		item.setProduct(product);
		item.setQuantity(1d);
		if (stockDao.checkAvailableByProduct(product.getId())) {
			Stock stock = stockDao.findAvailableByProduct(product.getId())[0];
			
			AffectedStock affected = new AffectedStock();
			affected.setQuantity(1d);
			affected.setStock(stock);
			
			item.addStock(affected);
			item.setUnitPrice(stock.getDefaultUnitPrice());
			item.setCurrency(stock.getSalesCurrency());
		} else if (stockDao.checkByProduct(product.getId())) {
			Stock stock = stockDao.findLatestByProduct(product.getId());
			item.setUnitPrice(stock.getDefaultUnitPrice());
			item.setCurrency(stock.getSalesCurrency());
		}
		command.addItem(item);
		reload();
		return item;
	}
	
	/**
	 * search command item by product
	 * @param product
	 * @return
	 */
	public CommandItem findByProduct (Product product) {
		for (int i = 0; i < data.size(); i++) 
			if (data.get(i).getProduct() == product)
				return data.get(i);
		throw new RuntimeException("Produit non disponible sur la commande");
	}
	
	/**
	 * updating quantity by command item
	 * @param product
	 * @param quantity
	 */
	public void updateQuantity (Product product, double quantity) {
		for (int i = 0; i < data.size(); i++) {
			CommandItem item = data.get(i);
			if(item.getProduct() != product)
				continue;
			
			item.setQuantity(quantity);
			fireTableRowsUpdated(i, i);
			return;
		}
	}
	
	/**
	 * updating default unit price
	 * @param product
	 * @param unitPrice
	 */
	public void updateUnitPrice (Product product, double unitPrice) {
		for (int i = 0; i < data.size(); i++) {
			CommandItem item = data.get(i);
			if(item.getProduct() != product)
				continue;
			
			item.setUnitPrice(unitPrice);
			fireTableRowsUpdated(i, i);
			return;
		}
	}
	
	/**
	 * updating currency of unit price
	 * @param product
	 * @param currency
	 */
	public void updateUnitPriceCurrency(Product product, Currency currency) {
		for (int i = 0; i < data.size(); i++) {
			CommandItem item = data.get(i);
			if(item.getProduct() != product)
				continue;
			
			if (exchangeRateDao.checkByCurrencies(currency.getId(), item.getCurrency().getId())) {
				ExchangeRate rate = exchangeRateDao.findAvailableByCurrencies(currency.getId(), item.getCurrency().getId());
				double unitPrice = rate.convert(item.getUnitPrice(), item.getCurrency());
				item.setUnitPrice(unitPrice);
			}
			
			item.setCurrency(currency);
			fireTableRowsUpdated(i, i);
			return;
		}
	}
	
	/**
	 * remove table
	 * @param product
	 */
	public void removeByProduct (Product product) {
		for (int i = 0; i< data.size(); i++) {
			CommandItem item = data.get(i);
			if(item.getProduct() == product || item.getProduct().getId() == product.getId()) {
				command.removeItem(item);
				data.remove(item);
				reload();
				if (item.getId() != null && !item.getId().trim().isEmpty())
					commandItemDao.delete(DELETE_REQUEST_ID, item.getId());
				return;
			}
		}
	}

	/**
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(Command command) {
		this.command = command;
		if(command != null && command.getId() != null && !command.getId().trim().isEmpty() 
				&& commandItemDao.checkByCommand(command.getId())) {
			command.addItem(commandItemDao.findByCommand(command.getId()));
		}
		reload();
	}

}
