/**
 * 
 */
package com.spiral.simple.store.beans;

import java.util.Date;

/**
 * @author Esaie MUHASA
 *
 */
public class Stock extends DBEntity {

	private static final long serialVersionUID = -2582753576092604646L;
	
	private Product product;
	private double quantity;
	private MeasureUnit measureUnit;
	private double buyingPrice;
	private Currency buyingCurrency;
	private Currency salesCurrency;
	private double defaultUnitPrice;//default unit price when selling
	private Date date;
	private Date manifacturingDate;
	private Date expiryDate;
	private String description;
	
	private double soldQuantity;//quantite deja vendue

	/**
	 * 
	 */
	public Stock() {
		super();
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
	}

	/**
	 * @return the buyingPrice
	 */
	public double getBuyingPrice() {
		return buyingPrice;
	}

	/**
	 * @param buyingPrice the buyingPrice to set
	 */
	public void setBuyingPrice(double buyingPrice) {
		this.buyingPrice = buyingPrice;
	}

	/**
	 * @return the buyingCurrency
	 */
	public Currency getBuyingCurrency() {
		return buyingCurrency;
	}

	/**
	 * @param buyingCurrency the buyingCurrency to set
	 */
	public void setBuyingCurrency(Currency buyingCurrency) {
		this.buyingCurrency = buyingCurrency;
	}

	/**
	 * @return the salesCurrency
	 */
	public Currency getSalesCurrency() {
		return salesCurrency;
	}

	/**
	 * @param salesCurrency the salesCurrency to set
	 */
	public void setSalesCurrency(Currency salesCurrency) {
		this.salesCurrency = salesCurrency;
	}

	/**
	 * @return the defaultUnitPrice
	 */
	public double getDefaultUnitPrice() {
		return defaultUnitPrice;
	}

	/**
	 * @param defaultUnitPrice the defaultUnitPrice to set
	 */
	public void setDefaultUnitPrice(double defaultUnitPrice) {
		this.defaultUnitPrice = defaultUnitPrice;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void initDate (long date) {
		this.date = buildDate(date);
	}

	/**
	 * @return the manifacturingDate
	 */
	public Date getManifacturingDate() {
		return manifacturingDate;
	}
	
	public void initManufacturingDate (long date) {
		manifacturingDate = buildDate(date);
	}

	/**
	 * @param manifacturingDate the manifacturingDate to set
	 */
	public void setManifacturingDate(Date manufacturingDate) {
		this.manifacturingDate = manufacturingDate;
	}

	/**
	 * @return the expiryDate
	 */
	public Date getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	
	public void initExpiryDate (long date) {
		expiryDate = buildDate(date);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	
	/**
	 * @return the soldQuantity
	 */
	public double getSoldQuantity() {
		return soldQuantity;
	}

	/**
	 * @param soldQuantity the soldQuantity to set
	 */
	public void setSoldQuantity(double soldQuantity) {
		this.soldQuantity = soldQuantity;
	}

	/**
	 * renvoie la quantite disponible en stock
	 * @return
	 */
	public double getAvailableQuantity() {
		double available = quantity - soldQuantity;
		if(available <= 0)
			return 0;
		return available;
	}
	
	/**
	 * renvoie la quantite disponible en pourcentage
	 * @return
	 */
	public double getAvailableQuantityToPercent () {
		return (quantity / 100) * getAvailableQuantity();
	}

	@Override
	public String toString() {
		return "Stock [product=" + product + ", quantity=" + quantity + ", measureUnit=" + measureUnit
				+ ", buyingPrice=" + buyingPrice + ", buyingCurrency=" + buyingCurrency + ", salesCurrency="
				+ salesCurrency + ", defaultUnitPrice=" + defaultUnitPrice + ", date=" + date + ", manifacturingDate="
				+ manifacturingDate + ", expiryDate=" + expiryDate + ", description=" + description + "]";
	}

}
