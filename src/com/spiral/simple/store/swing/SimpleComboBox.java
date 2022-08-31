package com.spiral.simple.store.swing;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;

public class SimpleComboBox <T> extends AbstractFormField {
	private static final long serialVersionUID = 3482698475958293605L;
	private final JComboBox<T> field;

	/**
	 * default construct
	 */
	public SimpleComboBox() {
		super();
		field = new JComboBox<>();
		init();
	}
	
	/**
	 * construct to initialization text of field label
	 * @param label
	 * @param model
	 */
	public SimpleComboBox(String label, ComboBoxModel<T> model) {
		super(label);
		field = new JComboBox<>(model);
		init();
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		field.setEnabled(enabled);
	}

	/**
	 * @return the field
	 */
	public JComboBox<T> getField() {
		return field;
	}

}
