/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.spiral.simple.store.swing.ImagePicker;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie MUHASA
 *
 */
public class ProductForm extends AbstractForm {
	private static final long serialVersionUID = 8162934088257733332L;
	
	private final SimpleTextField fieldName = new SimpleTextField("Nom du produit");
	private final JTextArea fieldDescription = new JTextArea();
	private ImagePicker imagePicker = new ImagePicker("photo du produit");

	/**
	 * 
	 */
	public ProductForm() {
		super();
		
		final JPanel fields = new JPanel(new BorderLayout()),
				center = new JPanel(new BorderLayout()),
				left = new JPanel(new BorderLayout()), 
				padding = new JPanel(new BorderLayout());
		
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
	}

}
