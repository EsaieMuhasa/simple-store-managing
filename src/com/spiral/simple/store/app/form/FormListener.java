/**
 * 
 */
package com.spiral.simple.store.app.form;

/**
 * @author Esaie MUHASA
 * listener interface
 */
public interface FormListener {
	/**
	 * emitted when user click on button submit 
	 * @param form
	 */
	void onValidate (AbstractForm<?> form);
	
	/**
	 * emitted after validation of data in form field.
	 * in this optic, date in form field match form data policy
	 * @param form
	 */
	void onAcceptData (AbstractForm<?> form);
	
	/**
	 * emitted after validation of date in form field.
	 * if data is not match form data policy.
	 * @param form
	 * @param causes
	 */
	void onRejetData (AbstractForm<?> form, String...causes);
	
	/**
	 * emitted when user click on button cancel
	 * @param form
	 */
	void onCancel (AbstractForm<?> form);
}
