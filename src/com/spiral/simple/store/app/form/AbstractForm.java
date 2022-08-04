/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 *
 */
public class AbstractForm extends JPanel {
	private static final long serialVersionUID = -2251665578617321837L;
	
	private final JLabel title = UIComponentBuilder.createH1("Formulaire");
	private final JButton btnValidate = new JButton("Valider", UIComponentBuilder.loadIcon("success"));
	private final JButton btnCancel = new JButton("Annuler", UIComponentBuilder.loadIcon("close"));

	private final JPanel body = new JPanel(new BorderLayout(5, 5));
	private final JPanel center = new JPanel(new BorderLayout());
	private final Box top = Box.createHorizontalBox();
	private final JPanel bottom = new JPanel();
	
	private final ActionListener listenerBtnValidate = event -> onValidate(event);
	private final ActionListener listenerBtnCancel = event -> onCancel(event);
	
	{//building UI components
		btnValidate.addActionListener(listenerBtnValidate);
		btnCancel.addActionListener(listenerBtnCancel);
		btnCancel.setVisible(false);
		
		top.add(title);
		top.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		top.setOpaque(true);
		top.setBackground(Color.LIGHT_GRAY);
		
		
		bottom.add(btnValidate);
		bottom.add(btnCancel);
		bottom.setBackground(Color.LIGHT_GRAY);
		bottom.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		
		center.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		center.add(body, BorderLayout.CENTER);
	}//==
	
	public AbstractForm() {
		super(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}
	
	/**
	 * change visibility of button to validation form
	 * @param visible
	 */
	public void setVisibilityButtonValidation (boolean visible) {
		btnValidate.setVisible(visible);
	}
	
	/**
	 * change cancellation button visibility
	 * @param visible
	 */
	public void setVisibilityButtonCancellation (boolean visible) {
		btnCancel.setVisible(visible);
	}
	
	
	/**
	 * change state of button to validation form
	 * @param enable
	 */
	public void setEnabledButtonValidation (boolean enable) {
		btnValidate.setEnabled(enable);
	}
	
	/**
	 * change state of cancellation button
	 * @param enable
	 */
	public void setEnabledButtonCancellation (boolean enable) {
		btnCancel.setEnabled(enable);;
	}
	
	/**
	 * updating title text
	 * @param text
	 */
	public void setTitle (String text) {
		title.setText(text);
	}
	
	/**
	 * @return the body
	 */
	protected JPanel getBody() {
		return body;
	}

	/**
	 * @return the center
	 */
	protected JPanel getCenter() {
		return center;
	}

	/**
	 * this method is called when button validation is clicked
	 * @param event
	 */
	protected void onValidate (ActionEvent event) {}
	
	/**
	 * this method is called when canceling button is clicked
	 * @param event
	 */
	protected void onCancel (ActionEvent event) {}
	
	/**
	 * send request to clean all field
	 */
	public final void cleanFields () {
		doCleanFields();
	}
	
	protected void doCleanFields () {};
	
	

}
