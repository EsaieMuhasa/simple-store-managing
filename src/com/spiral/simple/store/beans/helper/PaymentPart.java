/**
 * 
 */
package com.spiral.simple.store.beans.helper;

import java.util.Date;

import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.beans.DistributionConfigItem;

/**
 * @author Esaie Muhasa
 * ce entite represente la repartition des revenues (recettes) d'un element de la commande.
 * Donc aucun opperation d'enregistrement/update/delete est prise en charge depuis le dit DAO.
 * la repartiton et faite depuis une vue quelcoque dans la BDD
 */
public class PaymentPart extends DBEntity {
	private static final long serialVersionUID = -3840728451074383580L;
	
	private CommandPayment source;
	private DistributionConfigItem itemPart;
	private double amount;
	private double percent;

	/**
	 * 
	 */
	public PaymentPart() {
		super();
	}
	
	/**
	 * verifie s'il s'agit de la repartition par de faut.
	 * donc, aucune repartition n'a ete faite
	 * @return
	 */
	public boolean isDefault () {
		return source == null ||source.getConfig()  == null;
	}
	
	@Override
	public String getId() {
		return source.getId();
	}

	@Override
	public void setId(String id) {
		throw new RuntimeException("Operation non pris en charge");
	}

	@Override
	public Date getRecordingDate() {
		return source.getRecordingDate();
	}

	@Override
	public void setRecordingDate(Date recordingDate) {
		throw new RuntimeException("Operation non pris en charge");
	}

	@Override
	public Date getLastUpdateDate() {
		return source.getLastUpdateDate();
	}

	@Override
	public void setLastUpdateDate(Date lastUpdateDate) {
		throw new RuntimeException("Operation non pris en charge");
	}
	
	/**
	 * renvoie la devise auquel le la repartition fais reference
	 * @return
	 */
	public Currency getCurrency () {
		return source.getCurrency();
	}

	/**
	 * @return the source
	 */
	public CommandPayment getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(CommandPayment source) {
		this.source = source;
	}

	/**
	 * @return the itemPart
	 */
	public DistributionConfigItem getItemPart() {
		return itemPart;
	}

	/**
	 * @param itemPart the itemPart to set
	 */
	public void setItemPart(DistributionConfigItem itemPart) {
		this.itemPart = itemPart;
	}

	/**
	 * @return the amount
	 */
	public double getAmount() {
		if(source != null && source.getConfig() == null)
			return source.getAmount();
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(double amount) {
		this.amount = amount;
	}

	/**
	 * @return the percent
	 */
	public double getPercent() {
		return percent;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(double percent) {
		this.percent = percent;
	}

}
