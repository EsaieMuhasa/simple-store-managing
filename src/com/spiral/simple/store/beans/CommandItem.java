/**
 * 
 */
package com.spiral.simple.store.beans;

import java.util.ArrayList;
import java.util.List;

import com.spiral.simple.store.beans.helper.PhysicalSize;

/**
 * @author Esaie MUHASA
 *
 */
public class CommandItem extends DBEntity {
	private static final long serialVersionUID = -880889777646589531L;
	
	private Command command;
	private Product product;
	private double quantity;
	private double unitPrice;
	private Currency currency;
	private MeasureUnit measureUnit;//confert les stock d'un produit => uniquement dans la vue V_CommandItem
	
	private final List<AffectedStock> stocks = new ArrayList<>();

	/**
	 * 
	 */
	public CommandItem() {
		super();
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
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
		if(!stocks.isEmpty())
			stocks.get(0).setQuantity(quantity);
	}
	
	/**
	 * adding new affected stock
	 * @param stocks
	 */
	public void addStock (AffectedStock ...stocks) {
		for (AffectedStock stock : stocks){
			this.stocks.add(stock);
			stock.setItem(this);
		}
	}
	
	/**
	 * return table of affected stock by this client command item
	 * @return
	 */
	public AffectedStock [] getStocks () {
		if(stocks.size() == 0)
			return null;
		return stocks.toArray(new AffectedStock[stocks.size()]);
	}
	
	public AffectedStock getStockAt (int index) {
		return stocks.get(index);
	}
	
	/**
	 * count all stock affected by command item
	 * @return
	 */
	public int countStock() {
		return stocks.size();
	}
	
	/**
	 * remove affected stock
	 * @param stock
	 */
	public void removeStock (AffectedStock stock) {
		stocks.remove(stock);
	}
	
	/**
	 * removing stock affected at index
	 * @param index
	 */
	public void removeStockAt (int index) {
		stocks.remove(index);
	}
	
	/**
	 * removing all stock affected by item command
	 */
	public void removeStocks () {
		stocks.clear();
	}

	/**
	 * @return the unitPrice
	 */
	public double getUnitPrice() {
		return unitPrice;
	}
	
	/**
	 * return total price for command item
	 * @return
	 */
	public double getTotalPrice () {
		return quantity * unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	
	/**
	 * renvoie la sommes de stock disponible, groupee par unite de mesure 
	 * de la quantite du stock
	 * @return
	 */
	public PhysicalSize [] getAvailabelStock () {
		if(stocks.size() == 0)
			return null;
		
		List<PhysicalSize> sizes = new ArrayList<>();
		
		for (AffectedStock af : stocks) {
			int sizeIndex = -1;
			for (PhysicalSize size : sizes) {
				if(size.getUnit().equals(af.getStock().getMeasureUnit())) {
					sizeIndex = sizes.indexOf(size);
					break;
				}
			}
			
			PhysicalSize size = (sizeIndex == -1)? new PhysicalSize(0d, af.getStock().getMeasureUnit()) : sizes.get(sizeIndex);
			size.setValue(size.getValue() + af.getStock().getAvailableQuantity());
			if(sizeIndex == -1)
				sizes.add(size);
		}
		return sizes.toArray(new PhysicalSize[sizes.size()]);
	}
	
	/**
	 * repartition dela quantite commande sur les stocks ayant pour unite de mesure celuis en parametre
	 * dans le cas où la sommes des quantite disponible en stock ne peuvent pas satisfaire la dite commande,
	 * cette element de la commande se consideree comme invalide lors de la validation de la commande et 
	 * entreneras l'invlidite de toute la dite commande
	 * @param unit
	 */
	public void dispatchQantityTo (MeasureUnit unit) {
		double quantity = this.quantity;
		for (AffectedStock af : stocks) {
			if(af.getStock().getMeasureUnit().equals(unit) && quantity > 0) {
				double sub = (af.getStock().getAvailableQuantity() >= quantity)? quantity : af.getStock().getAvailableQuantity();				
				quantity -= sub;
				af.setQuantity(sub);
			} else 
				af.setQuantity(0d);
		}
		measureUnit = unit;
	}
	
	/**
	 * pouvons-vous faire confienace a cette element de la commande?
	 * dans l'absolut cette method verifie si la quantite requise pouras etre satisfait par 
	 * le stock disponible (pour l'unite de mesure choisie biensur)
	 * @return
	 */
	public boolean isValidable () {
		PhysicalSize [] sizes = getAvailabelStock();
		if (sizes == null)
			return false;
		
		for (PhysicalSize size : sizes) {
			if(size.getUnit().equals(measureUnit) && quantity <= size.getValue())
				return true;
		}
		return false;
	}
	
	/**
	 * compte les utites de mesures de stocks chargee dans l'elememt de la commande
	 * @return
	 */
	public int countStocksUnit () {
		if(stocks.size() <= 1)
			return stocks.size();

		PhysicalSize [] sizes = getAvailabelStock();
		return sizes.length;
	}
	
	/**
	 * renvoie la collection des unite des mesures de stock referencer
	 * @return
	 */
	public MeasureUnit [] getStocksUnit () {
		List<MeasureUnit> units = new ArrayList<>();
		for (AffectedStock af : stocks) {
			int index = -1;
			for (MeasureUnit unit : units) {
				if(unit.equals(af.getStock().getMeasureUnit())) {
					index = units.indexOf(unit);
					break;
				}
			}
			
			if(index == -1)
				units.add(af.getStock().getMeasureUnit());
		}
		return units.toArray(new MeasureUnit[units.size()]);
	}

	/**
	 * @return the measureUnit
	 */
	public MeasureUnit getMeasureUnit() {
		return measureUnit;
	}

	/**
	 * @param measureUnit the measureUnit to set
	 */
	public void setMeasureUnit(MeasureUnit measureUnit) {
		this.measureUnit = measureUnit;
	}

}
