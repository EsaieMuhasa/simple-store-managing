package com.spiral.simple.store.swing;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SimpleTextField extends JPanel {
	private static final long serialVersionUID = 3482698475958293605L;
	
	private final JLabel label = new JLabel();
	private final JTextField field = new JTextField();

	/**
	 * default construct
	 */
	public SimpleTextField() {
		this("");
	}
	
	/**
	 * construct to initialization text of field label
	 * @param label
	 */
	public SimpleTextField(String label) {
		super(new BorderLayout());
		this.label.setText(label);
		init();
	}
	
	/**
	 * initialization graphics components
	 */
	private void init() {
		add(label, BorderLayout.NORTH);		
		add(field, BorderLayout.CENTER);
	}

	/**
	 * @return the label
	 */
	public JLabel getLabel() {
		return label;
	}

	/**
	 * @return the field
	 */
	public JTextField getField() {
		return field;
	}

}
