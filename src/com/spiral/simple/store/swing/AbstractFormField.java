/**
 * 
 */
package com.spiral.simple.store.swing;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class AbstractFormField extends JPanel {
	private static final long serialVersionUID = 9114406944879975302L;
	
	protected final JLabel label = new JLabel();

	public AbstractFormField() {
		super(new BorderLayout());
	}
	
	public AbstractFormField(String label) {
		super(new BorderLayout());
		this.label.setText(label);
	}
	
	/**
	 * initialization for UI components
	 */
	protected void init() {
		add(label, BorderLayout.NORTH);
		add(getField(), BorderLayout.CENTER);
	}
	

	/**
	 * @return the label
	 */
	public JLabel getLabel() {
		return label;
	}
	
	/**
	 * return the field by this form field
	 * @return
	 */
	public abstract JComponent getField();
	
	/**
	 * reinitialisation des contenues du champ
	 */
	public abstract void reset () ;

}
