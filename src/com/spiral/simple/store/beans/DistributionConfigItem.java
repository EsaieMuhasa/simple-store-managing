/**
 * 
 */
package com.spiral.simple.store.beans;

/**
 * @author Esaie MUHASA
 *
 */
public class DistributionConfigItem extends DBEntity {

	private static final long serialVersionUID = 8204066369745995477L;
	
	private DistributionConfig owner;
	private BudgetRubric rubric;
	private double percent;

	/**
	 * 
	 */
	public DistributionConfigItem() {
		super();
	}

	/**
	 * @return the owner
	 */
	public DistributionConfig getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(DistributionConfig owner) {
		this.owner = owner;
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
	 * @return the percent
	 */
	public double getPercent() {
		return percent;
	}

	/**
	 * @param percent the percent to set
	 */
	public void setPercent(double percent) {
		this.percent = percent;
	}

}
