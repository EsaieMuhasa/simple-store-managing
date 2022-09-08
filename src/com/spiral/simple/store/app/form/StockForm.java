/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Date;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.MeasureUnit;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.beans.Stock;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.MeasureUnitDao;
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

	private final ProductDao productDao;
	private final MeasureUnitDao measureUnitDao;
	private final CurrencyDao currencyDao;
	
	private boolean accept;
	private String [] rejectCause;
	
	private Stock  stock;
	
	public StockForm () {
		super(DAOFactory.getDao(StockDao.class));
		
		productDao = DAOFactory.getDao(ProductDao.class);
		measureUnitDao = DAOFactory.getDao(MeasureUnitDao.class);
		currencyDao = DAOFactory.getDao(CurrencyDao.class);
		
		build();
	}
	
	/**
	 * @return the stock
	 */
	public Stock getStock() {
		return stock;
	}

	/**
	 * @param stock the stock to set
	 */
	public void setStock(Stock stock) {
		this.stock = stock;
		
		if(stock == null || stock.getId() == null)
			cleanFields();
		else {
			fieldQuantity.getField().setText(stock.getQuantity()+"");
			fieldBuyingPrice.getField().setText(stock.getBuyingPrice()+"");
			fieldDate.getField().setDate(stock.getDate());
			fieldExpiryDate.getField().setDate(stock.getExpiryDate());
			fieldManufacturingDate.getField().setDate(stock.getManifacturingDate());
			fieldDefaultUnitPrice.getField().setText(stock.getDefaultUnitPrice()+"");
			
			for (int i = 0; i < buyingComboModel.getSize(); i++) {
				if(buyingComboModel.getElementAt(i).equals(stock.getBuyingCurrency())){
					fieldBuyingCurrency.getField().setSelectedIndex(i);
					break;
				}
			}
			
			for (int i = 0; i < measurUnitComboModel.getSize(); i++) {
				if(measurUnitComboModel.getElementAt(i).equals(stock.getMeasureUnit())) {
					fieldQuantityUnit.getField().setSelectedIndex(i);
					break;
				}
			}
			
			for (int i = 0; i < productComboModel.getSize(); i++) {
				if(productComboModel.getElementAt(i).equals(stock.getProduct())) {
					fieldProduct.getField().setSelectedIndex(i);
					break;
				}
			}
		}
	}

	/**
	 * building UI components
	 */
	private void build() {
		Box fields = Box.createVerticalBox();
		
		final JPanel boxQuantity = new JPanel(new GridLayout(1, 2));
		boxQuantity.add(fieldQuantity);
		boxQuantity.add(fieldQuantityUnit);
		
		final JPanel boxUnitPrice = new JPanel(new GridLayout(1, 2));
		boxUnitPrice.add(fieldDefaultUnitPrice);
		boxUnitPrice.add(fieldSalesCurrency);
		
		final JPanel boxBuyingPrice = new JPanel(new GridLayout(1, 2));
		boxBuyingPrice.add(fieldBuyingPrice);
		boxBuyingPrice.add(fieldBuyingCurrency);
		
		final JPanel boxProductDates = new JPanel(new GridLayout(1, 2));
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
		
		if (measureUnitDao.countAll()  != 0) {
			MeasureUnit [] units = measureUnitDao.findAll();
			for (MeasureUnit unit : units) {
				measurUnitComboModel.addElement(unit);
			}
		}
		
		if (currencyDao.countAll() != 0) {
			Currency [] currencies = currencyDao.findAll();
			for (Currency currency : currencies) {
				buyingComboModel.addElement(currency);
				salesComboModel.addElement(currency);
			}
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
		Stock s = stock;
		
		try {
			double quantity = Double.parseDouble(fieldQuantity.getField().getText().trim());
			s.setQuantity(quantity);
			if(s.getSoldQuantity() != 0 && s.getQuantity() < s.getAvailableQuantity())
				cause += "le stock doit etre suppérieur ou égale a la quanité déjà vendue;";
		} catch (NumberFormatException e) {
			cause += "La quantité doit être une valeur numérique valide;";
		}
		
		s.setDate(fieldDate.getField().getDate());
		s.setProduct(productComboModel.getElementAt(fieldProduct.getField().getSelectedIndex()));
		s.setBuyingCurrency(buyingComboModel.getElementAt(fieldBuyingCurrency.getField().getSelectedIndex()));
		s.setSalesCurrency(salesComboModel.getElementAt(fieldSalesCurrency.getField().getSelectedIndex()));
		s.setMeasureUnit(measurUnitComboModel.getElementAt(fieldQuantityUnit.getField().getSelectedIndex()));
		
		s.setManifacturingDate(fieldManufacturingDate.getField().getDate());
		s.setExpiryDate(fieldExpiryDate.getField().getDate());
		try {
			s.setBuyingPrice(Double.parseDouble(fieldBuyingPrice.getField().getText()));
		} catch (NumberFormatException e) {
			cause += "Le prix d'achat doit être une valeur numérique valide;";
		}
		
		try {
			s.setDefaultUnitPrice(Double.parseDouble(fieldDefaultUnitPrice.getField().getText()));
		} catch (NumberFormatException e) {
			cause += "Le prix unitaire doit être une valeur numérique valide;";
		}
		
		if(cause != "")
			rejectCause = cause.split(";");
		else
			rejectCause = null;
		
		accept = rejectCause == null;
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
