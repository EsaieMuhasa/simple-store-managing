/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.spiral.simple.store.app.MainWindow;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.ProductDao;
import com.spiral.simple.store.swing.ImagePicker;
import com.spiral.simple.store.swing.SimpleTextField;
import com.spiral.simple.store.tools.Config;

/**
 * @author Esaie MUHASA
 *
 */
public class ProductForm extends AbstractForm<Product> {
	private static final long serialVersionUID = 8162934088257733332L;

	private final SimpleTextField fieldName = new SimpleTextField("Nom du produit");
	private final JTextArea fieldDescription = new JTextArea();
	private ImagePicker imagePicker = new ImagePicker("photo du produit");

	private ProductDao productDao;

	private boolean accept;
	private String[] rejectCause;
	
	private Product product;

	/**
	 * 
	 */
	public ProductForm() {
		super(DAOFactory.getDao(ProductDao.class));
		productDao = DAOFactory.getDao(ProductDao.class);

		final JPanel fields = new JPanel(new BorderLayout()), center = new JPanel(new BorderLayout()),
				left = new JPanel(new BorderLayout()), padding = new JPanel(new BorderLayout());

		center.add(new JLabel("Text de description du produit"), BorderLayout.NORTH);
		center.add(fieldDescription, BorderLayout.CENTER);

		left.add(imagePicker);

		fields.add(fieldName, BorderLayout.NORTH);
		fields.add(center, BorderLayout.CENTER);

		fields.setBorder(new EmptyBorder(0, 5, 0, 5));

		padding.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		padding.add(fields, BorderLayout.CENTER);

		getBody().add(padding, BorderLayout.CENTER);
		getBody().add(left, BorderLayout.EAST);
		
		setVisibilityButtonCancellation(true);

	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}
	
	@Override
	public void persist() {
		if(product == null)
			return;
		
		//picture
		File file = new File(Config.get("workspace") + product.getPicture());
		BufferedImage image = imagePicker.getImage();
		if(image != null) {
			try {
				ImageIO.write(image, imagePicker.getImageType(), file);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(MainWindow.getLastInstance(), e.getMessage(),
						"Erreur d'ecriture de l'image sur le disquedur", JOptionPane.ERROR_MESSAGE);
			}
		}
		//==
		product.setPicture(product.getPicture());
		
		if(product.getId() == null)
			productDao.create(DEFAULT_ON_PERSIST_REQUEST_ID, product);
		else
			productDao.update(DEFAULT_ON_PERSIST_REQUEST_ID, product);
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
		
		if(product == null || product.getId() == null)
			cleanFields();
		else {
			fieldName.getField().setText(product.getName());
			fieldDescription.setText(product.getDescription());
		}
	}

	@Override
	protected void doCleanFields() {
		fieldName.getField().setText("");
		fieldDescription.setText("");
		imagePicker.show(null);
	}

	@Override
	protected void doValidate() {
		String cause = "";
		Product p = product;

		String name = fieldName.getField().getText();
		String description = fieldDescription.getText();
		String picture = imagePicker.isCropableImage()
				? "products/" + System.currentTimeMillis() + "." + imagePicker.getImageType()
				: product.getPicture();
		
		p.setName(name);
		p.setDescription(description);
		p.setPicture(picture);
		
		if (cause != "")
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
