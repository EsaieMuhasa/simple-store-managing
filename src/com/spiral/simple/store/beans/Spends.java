/**
 * 
 */
package com.spiral.simple.store.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class Spends extends CashMoney {
	private static final long serialVersionUID = -4333486623296451396L;
	
	private BudgetRubric rubric;
	private String label;

	/**
	 * 
	 */
	public Spends() {
		super();
	}

	/**
	 * @return the rubric
	 */
	public BudgetRubric getRubric() {
		return rubric;
	}

	/**
	 * @param rubric the rubric to set
	 */
	public void setRubric(BudgetRubric rubric) {
		this.rubric = rubric;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

}
