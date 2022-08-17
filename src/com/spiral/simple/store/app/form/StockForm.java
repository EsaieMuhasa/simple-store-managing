/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.MeasureUnit;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.beans.Stock;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.ProductDao;
import com.spiral.simple.store.dao.StockDao;
import com.spiral.simple.store.swing.CaptionnablePanel;
import com.spiral.simple.store.swing.SimpleComboBox;
import com.spiral.simple.store.swing.SimpleDateField;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie MUHASA
 *
 */
public class StockForm extends AbstractForm<Stock> {
	private static final long serialVersionUID = 7502747854364786988L;
	
	private final DefaultComboBoxModel<Currency> 
		buyingComboModel = new DefaultComboBoxModel<>(),
		salesComboModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<MeasureUnit> measurUnitComboModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<Product> productComboModel = new DefaultComboBoxModel<>();
	
	private final SimpleTextField fieldQuantity = new SimpleTextField("Quantité");
	private final SimpleComboBox<MeasureUnit> fieldQuantityUnit = new SimpleComboBox<>("Unité", measurUnitComboModel);
	private final SimpleTextField fieldBuyingPrice = new SimpleTextField("Montant");
	private final SimpleTextField fieldDefaultUnitPrice = new SimpleTextField("Montant");
	
	private final SimpleDateField fieldManufacturingDate = new SimpleDateField("Date de fabrication");
	private final SimpleDateField fieldExpiryDate = new SimpleDateField("Date d'expiration");
	private final SimpleDateField fieldDate = new SimpleDateField("Date de perception du stock");
	
	private final SimpleComboBox<Currency> fieldBuyingCurrency = new SimpleComboBox<>("Unité", buyingComboModel);
	private final SimpleComboBox<Currency> fieldSalesCurrency = new SimpleComboBox<>("Unité", salesComboModel);
	private final SimpleComboBox<Product> fieldProduct = new SimpleComboBox<>("Produit", productComboModel);

	private final StockDao stockDao;
	private final ProductDao productDao;
	
	private boolean accept;
	private String [] rejectCause;
	
	public StockForm () {
		super(DAOFactory.getDao(StockDao.class));
		
		stockDao = DAOFactory.getDao(StockDao.class);
		productDao = DAOFactory.getDao(ProductDao.class);
		
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
		JComponent [] formFields = {
				fieldDate, fieldProduct, 
				new CaptionnablePanel("Qantité initale du stock", boxQuantity),
				new CaptionnablePanel("Coup d'achat", boxBuyingPrice),
				new CaptionnablePanel("Prix de vente par défaut", boxUnitPrice),
				new CaptionnablePanel("Durée de vie du produit", boxProductDates)
		};
		
		for (JComponent c : formFields) {			
			fields.add(c);
			fields.add(Box.createVerticalStrut(strut));
		}
		
		getBody().add(fields, BorderLayout.CENTER);
	}
	
	@Override
	public void doReload() {
		if(productDao.countAll() != 0) {
			Product [] products = productDao.findAll();
			for (Product p : products)
				productComboModel.addElement(p);
		}
	}

	@Override
	protected void doCleanFields() {
		fieldQuantity.getField().setText("");
		fieldDefaultUnitPrice.getField().setText("");
		fieldBuyingPrice.getField().setText("");
		fieldDate.getField().setDate(new Date());
	}

	@Override
	protected void doValidate() {
		String cause ="";
		Stock s = new Stock();
		
		if(cause != "")
			rejectCause = cause.split(";");
		else
			rejectCause = null;
		
		accept = rejectCause == null;
		if(accept)
			stockDao.create(DEFAULT_ON_CREATE_REQUEST_ID, s);
	}

	@Override
	protected boolean isAccept() {
		return accept;
	}

	@Override
	protected String[] getRejectCause() {
		return rejectCause;
	}

}
