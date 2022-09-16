/**
 * 
 */
package com.spiral.simple.store.swing.navs;

/**
 * @author Esaie Muhasa
 * interface d'ecoute des element du navbar
 */
public interface NavbarListener {
	
	/**
	 * lors du click sur un element du menu
	 * @param nav
	 * @param index
	 */
	void onAction(Navbar nav, int index);

}
