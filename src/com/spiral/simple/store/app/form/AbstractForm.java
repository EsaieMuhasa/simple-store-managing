/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.spiral.simple.store.app.MainWindow;
import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.dao.DAOException;
import com.spiral.simple.store.dao.DAOException.ErrorType;
import com.spiral.simple.store.dao.DAOInterface;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 *
 */
public abstract class AbstractForm <H extends DBEntity> extends JPanel{
	private static final long serialVersionUID = -2251665578617321837L;
	
	public static final int DEFAULT_ON_CREATE_REQUEST_ID = 0xFF0000;
	
	private final JLabel title = UIComponentBuilder.createH1("Formulaire");
	private final JButton btnValidate = new JButton("Valider", UIComponentBuilder.loadIcon("success"));
	private final JButton btnCancel = new JButton("Annuler", UIComponentBuilder.loadIcon("close"));

	private final JPanel body = new JPanel(new BorderLayout(5, 5));
	private final JPanel center = new JPanel(new BorderLayout());
	private final Box top = Box.createHorizontalBox();
	private final JPanel bottom = new JPanel();
	
	private final List<FormListener> formListeners = new ArrayList<>();
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
	
	private final DAOListenerAdapter<H> listenerAdapter = new DAOListenerAdapter<H>() {
		@Override
		public void onCreate(DBEntity... data) {
			cleanFields();
		}

		@Override
		public void onUpdate(DBEntity newState, DBEntity oldState) {
			cleanFields();
		}

		@Override
		public void onUpdate(DBEntity[] newState, DBEntity[] oldState) {
			cleanFields();
		}

		@Override
		public void onError(int requestId, DAOException exception) {
			if(requestId == DEFAULT_ON_CREATE_REQUEST_ID ||
					(exception.getType() == ErrorType.ON_CREATE || exception.getType() == ErrorType.ON_UPDATE) )
				JOptionPane.showMessageDialog(AbstractForm.this, exception.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
		}
	};
	
	public AbstractForm (DAOInterface<H> daoInterface) {
		super(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		daoInterface.addBaseListener(listenerAdapter);
		daoInterface.addErrorListener(listenerAdapter);
	}
	
	/**
	 * emit request to reload data from form field models
	 */
	public final void reload () {
		if(isEnabled()){
			setEnabled(false);
			EventQueue.invokeLater(() -> {
				try {					
					doReload();
					setEnabled(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(MainWindow.getLastInstance(), e.getMessage(),
							"Erreur lors du chargement du formulaire", JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}
	
	/**
	 * emitted on reload data request
	 */
	public void doReload () {}
	
	@Override
	public void setEnabled (boolean enabled) {
		super.setEnabled(enabled);
		
		btnValidate.setEnabled(enabled);
		btnCancel.setEnabled(enabled);
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
	private final void onValidate (ActionEvent event) {
		setEnabled(false);
		EventQueue.invokeLater(()->{
			fireOnValidationEvent();
			doValidate();
			if(isAccept())
				fireOnAcceptDataEvent();
			else {
				String message = "", cause [] = getRejectCause();
				fireOnRejectDataEvent(cause);
				for (String m : cause)
					message += m + "\n";
				JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
			}
			setEnabled(true);
		});
	}
	
	/**
	 * called on clear content field request
	 */
	protected abstract void doCleanFields ();
	
	/**
	 * this method is automatically called when user click on validation button
	 */
	protected abstract void doValidate ();
	
	/**
	 * on validation data content in form field, if data is match form policy
	 *  this method must return true, otherwise false
	 * @return
	 */
	protected abstract boolean isAccept ();
	
	/**
	 * on in validation process, you must save errors message in string array.
	 * @return
	 */
	protected abstract String [] getRejectCause ();
	
	/**
	 * this method is called when canceling button is clicked
	 * @param event
	 */
	private final void onCancel (ActionEvent event) {
		fireOnCancelEvent();
		cleanFields();
	}
	
	/**
	 * send request to cancel current process
	 */
	public final void cancel () {
		if(!isEnabled())
			return;
		btnCancel.doClick();
	}
	
	/**
	 * if form is cancelable, true value is returned, otherwise false.
	 * by default, this method return result of isEnabled method
	 * @return
	 */
	public boolean isCancellable () {
		return isEnabled();
	}
	
	/**
	 * send request to clean all field
	 */
	public final void cleanFields () {
		doCleanFields();
	}
	
	
	/**
	 * subscribe new listener 
	 * @param listener
	 */
	public void addFormListener (FormListener listener) {
		if(!formListeners.contains(listener))
			formListeners.add(listener);
	}
	
	/**
	 * unsubscribe a listener
	 * @param listener
	 */
	public void removeFormListener (FormListener listener) {
		formListeners.remove(listener);
	}
	
	/**
	 * emit onValidation event
	 */
	protected void fireOnValidationEvent() {
		for (FormListener ls : formListeners)
			ls.onValidate(this);
	}
	
	/**
	 * emit onCancel event
	 */
	protected void fireOnCancelEvent () {
		for (FormListener ls : formListeners)
			ls.onCancel(this);
	}
	
	/**
	 * emit onAcceptData event
	 */
	protected void fireOnAcceptDataEvent () {
		for (FormListener ls : formListeners)
			ls.onAcceptData(this);
	}
	
	/**
	 * emit onRejetData event
	 * @param causes
	 */
	protected void fireOnRejectDataEvent (String...causes) {
		for (FormListener ls : formListeners)
			ls.onRejetData(this, causes);
	}

}
