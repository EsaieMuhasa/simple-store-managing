/**
 * 
 */
package com.spiral.simple.store.beans.helper;

import com.spiral.simple.store.beans.MeasureUnit;

/**
 * @author Esaie Muhasa
 * incapsulation d'une grandeur physque quelconque
 */
public class PhysicalSize {
	
	private double value;//la valeur
	private MeasureUnit unit;//l'unite de mesure


	public PhysicalSize() {}

	/**
	 * constructeur d'initialisation
	 * @param value
	 * @param unit
	 */
	public PhysicalSize(double value, MeasureUnit unit) {
		super();
		this.value = value;
		this.unit = unit;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the unit
	 */
	public MeasureUnit getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(MeasureUnit unit) {
		this.unit = unit;
	}

}
