/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.dao.BudgetRubricDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie MUHASA
 *
 */
public class BudgetRubricForm extends AbstractForm<BudgetRubric> {
	private static final long serialVersionUID = 4065575856375308388L;
	
	private final SimpleTextField fieldLabel = new SimpleTextField("Label du rubrique");
	private final JTextArea fieldDescription = new JTextArea();
	
	private final BudgetRubricDao budgetRubricDao;
	
	private boolean accept;
	private String[] rejectCause;

	public BudgetRubricForm() {
		super(DAOFactory.getDao(BudgetRubricDao.class));
		budgetRubricDao = DAOFactory.getDao(BudgetRubricDao.class);
		
		final JPanel fields = new JPanel(new BorderLayout()), center = new JPanel(new BorderLayout());

		center.add(new JLabel("Text de description de la rubrique"), BorderLayout.NORTH);
		center.add(fieldDescription, BorderLayout.CENTER);

		fields.add(fieldLabel, BorderLayout.NORTH);
		fields.add(center, BorderLayout.CENTER);
		getBody().add(fields, BorderLayout.CENTER);
	}

	@Override
	protected void doCleanFields() {
		fieldLabel.getField().setText("");
		fieldDescription.setText("");
	}

	@Override
	protected void doValidate() {
		String cause = "";
		
		BudgetRubric b = new BudgetRubric();
		String description = fieldDescription.getText().trim();
		description = description.isEmpty()? null : description;
		
		b.setLabel(fieldLabel.getField().getText().trim());
		b.setDescription(description);
		
		if (cause != "")
			rejectCause = cause.split(";");
		else
			rejectCause = null;

		accept = rejectCause == null;
		if (accept)
			budgetRubricDao.create(DEFAULT_ON_PERSIST_REQUEST_ID, b);
	}

	@Override
	protected boolean isAccept() {
		return accept;
	}

	@Override
	protected String[] getRejectCause() {
		return rejectCause;
	}

}
