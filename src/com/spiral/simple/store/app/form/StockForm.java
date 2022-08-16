/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.swing.CaptionnablePanel;
import com.spiral.simple.store.swing.SimpleComboBox;
import com.spiral.simple.store.swing.SimpleDateField;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie MUHASA
 *
 */
public class StockForm extends AbstractForm {
	private static final long serialVersionUID = 7502747854364786988L;
	
	private final DefaultComboBoxModel<Currency> buyingComboModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<Currency> salesComboModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<String> measurUnitComboModel = new DefaultComboBoxModel<>();
	
	private final SimpleTextField fieldQuantity = new SimpleTextField("Quantité");
	private final SimpleComboBox<String> fieldQuantityUnit = new SimpleComboBox<>("Unité", measurUnitComboModel);
	private final SimpleTextField fieldBuyingPrice = new SimpleTextField("Montant");
	private final SimpleTextField fieldDefaultUnitPrice = new SimpleTextField("Montant");
	
	private final SimpleDateField fieldManufacturingDate = new SimpleDateField("Date de fabrication");
	private final SimpleDateField fieldExpiryDate = new SimpleDateField("Date d'expiration");
	private final SimpleDateField fieldDate = new SimpleDateField("Date de perception du stock");
	
	private final SimpleComboBox<Currency> fieldBuyingCurrency = new SimpleComboBox<>("Unité", buyingComboModel);
	private final SimpleComboBox<Currency> fieldSalesCurrency = new SimpleComboBox<>("Unité", salesComboModel);

	/**
	 * 
	 */
	public StockForm() {
		super();
		
		build();
	}
	
	/**
	 * building UI components
	 */
	private void build() {
		Box fields = Box.createVerticalBox();
		
		Box boxQuantity = Box.createHorizontalBox();
		boxQuantity.add(fieldQuantity);
		boxQuantity.add(fieldQuantityUnit);
		
		Box boxUnitPrice = Box.createHorizontalBox();
		boxUnitPrice.add(fieldDefaultUnitPrice);
		boxUnitPrice.add(fieldSalesCurrency);
		
		Box boxBuyingPrice = Box.createHorizontalBox();
		boxBuyingPrice.add(fieldBuyingPrice);
		boxBuyingPrice.add(fieldBuyingCurrency);
		
		Box boxProductDates = Box.createHorizontalBox();
		boxProductDates.add(fieldManufacturingDate);
		boxProductDates.add(fieldExpiryDate);
		
		final int strut = 10;
		
		fields.add(fieldDate);
		fields.add(Box.createVerticalStrut(strut));
		fields.add(new CaptionnablePanel("Qantité initale du stock", boxQuantity));
		fields.add(Box.createVerticalStrut(strut));
		fields.add(new CaptionnablePanel("Coup d'achat", boxBuyingPrice));
		fields.add(Box.createVerticalStrut(strut));
		fields.add(new CaptionnablePanel("Prix de vente par défaut", boxUnitPrice));
		fields.add(Box.createVerticalStrut(strut));
		fields.add(new CaptionnablePanel("Durée de vie du produit", boxProductDates));
		
		getBody().add(fields, BorderLayout.CENTER);
	}

}
