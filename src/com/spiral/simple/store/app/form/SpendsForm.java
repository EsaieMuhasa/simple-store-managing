/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.Spends;
import com.spiral.simple.store.dao.BudgetRubricDao;
import com.spiral.simple.store.dao.CommandDao;
import com.spiral.simple.store.dao.CommandPaymentDao;
import com.spiral.simple.store.dao.CurrencyDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.PaymentPartDao;
import com.spiral.simple.store.dao.SpendsDao;
import com.spiral.simple.store.swing.SimpleComboBox;
import com.spiral.simple.store.swing.SimpleDateField;
import com.spiral.simple.store.swing.SimpleTextField;
import com.trimeur.swing.chart.DefaultPieModel;
import com.trimeur.swing.chart.DefaultPiePart;
import com.trimeur.swing.chart.PieModel;
import com.trimeur.swing.chart.PiePanel;
import com.trimeur.swing.chart.PiePart;
import com.trimeur.swing.chart.tools.Utility;

/**
 * @author Esaie Muhasa
 */
public class SpendsForm extends AbstractForm<Spends> {
	private static final long serialVersionUID = 5972135310016077655L;
	
	private final BudgetRubricDao budgetRubricDao = DAOFactory.getDao(BudgetRubricDao.class);
	private final SpendsDao spendsDao = DAOFactory.getDao(SpendsDao.class);
	private final PaymentPartDao paymentPartDao = DAOFactory.getDao(PaymentPartDao.class);
	private final CurrencyDao currencyDao = DAOFactory.getDao(CurrencyDao.class);
	private final CommandDao commandDao = DAOFactory.getDao(CommandDao.class);
	private final CommandPaymentDao commandPaymentDao = DAOFactory.getDao(CommandPaymentDao.class);
	
	/**
	 * collection des models du graphique pie.
	 * sont classee par devise monaitaire.
	 * pour chaque devise monetaire on selectionne les soldes de la dite rubrique
	 */
	private final Map<Currency, DefaultPieModel> pieModels = new HashMap<>();
	private final PiePanel piePanel = new PiePanel();
	
	private final DefaultComboBoxModel<Currency> currencyModel = new DefaultComboBoxModel<>();
	private final DefaultComboBoxModel<BudgetRubric> rubricModel = new DefaultComboBoxModel<>();
	
	private final SimpleTextField fieldAmount = new SimpleTextField("Montant");
	private final SimpleComboBox<Currency> fieldCurrency = new SimpleComboBox<>("Devise", currencyModel);
	private final SimpleComboBox<BudgetRubric> fieldRubric = new SimpleComboBox<>("Rubric budgetaire", rubricModel);
	private final SimpleDateField fieldDate = new SimpleDateField("Date du jour");
	private final SimpleTextField fieldLabel = new SimpleTextField("Courte description");
	
	private final ItemListener currencyItemListener = event -> onCurrencyItemChange(event);
	private final ItemListener rubricItemListener = event -> onRubricItemChange(event);
	private final CaretListener amountCaretListener = (event) -> onAmountChange(event);
	
	private final DAOListenerAdapter<Command> commandAdapter = new DAOListenerAdapter<Command>() {
		@Override
		public void onCreate(Command... data) {
			reloadChart();
		}
	};
	
	private final DAOListenerAdapter<CommandPayment> paymentAdapter = new DAOListenerAdapter<CommandPayment>() {
		@Override
		public void onCreate(CommandPayment... data) {
			reloadChart();
		}

		@Override
		public void onUpdate(CommandPayment newState, CommandPayment oldState) {
			reloadChart();
		}
	};
	
	public final DAOListenerAdapter<Spends> spendsAdapter = new DAOListenerAdapter<Spends>() {
		@Override
		public void onUpdate(Spends newState, Spends oldState) {
			reloadChart();
		}

		@Override
		public void onCreate(Spends... data) {
			reloadChart();
		}
	};
	
	private String [] cause=null;
	private Spends spends;
	
	public SpendsForm() {
		super(DAOFactory.getDao(SpendsDao.class));
		init();
		
		//listening event
		fieldRubric.getField().addItemListener(rubricItemListener);
		fieldCurrency.getField().addItemListener(currencyItemListener);
		fieldAmount.getField().addCaretListener(amountCaretListener);
		
		spendsDao.addBaseListener(spendsAdapter);
		commandDao.addBaseListener(commandAdapter);
		commandPaymentDao.addBaseListener(paymentAdapter);
		//==
	}
	
	@Override
	public void doReload() {
		super.doReload();
		
		rubricModel.removeAllElements();
		currencyModel.removeAllElements();
		
		if(budgetRubricDao.checkAll(0)) {
			BudgetRubric [] rubrics = budgetRubricDao.findAll();
			for (BudgetRubric rubric : rubrics)
				rubricModel.addElement(rubric);
		}
		
		if (currencyDao.checkAll(0)) {
			Currency [] currencies = currencyDao.findAll();
			for (Currency currency : currencies)
				currencyModel.addElement(currency);
		}
		
		reloadChart();//models de pie chart
	}
	
	/**
	 * relecture des models du chart
	 */
	private void reloadChart () {
		
		if(currencyModel.getSize() == 0 || rubricModel.getSize() == 0)
			return;
		
		pieModels.clear();
		piePanel.setModel(null);
		
		for (int i = 0; i < currencyModel.getSize(); i++) {
			
			Currency currency = currencyModel.getElementAt(i);
			DefaultPieModel model = new DefaultPieModel();
			model.setTitle("Caisse en "+currency.getShortName());
			model.setRealMaxPriority(true);
			model.setSuffix(currency.getSymbol());
			
			for (int j = 0; j < rubricModel.getSize(); j++) {
				Color bkColor = Utility.getColorAt(j);
				BudgetRubric rubric = rubricModel.getElementAt(j);
				
				double amount = paymentPartDao.getSumByRubric(rubric, currency, true);
				amount -= spendsDao.getSumByRubric(rubric.getId(), currency, true);
				DefaultPiePart part = new DefaultPiePart(bkColor, amount, rubric.getLabel());
				part.setData(rubric);
				
				model.addPart(part);
			}
			
			pieModels.put(currency, model);
		}
		
		DefaultPieModel model = pieModels.get(currencyModel.getElementAt(fieldCurrency.getField().getSelectedIndex()));
		model.setSelectedIndex(model.indexOf(model.findByData(rubricModel.getElementAt(fieldRubric.getField().getSelectedIndex()))));
		piePanel.setModel(model);
	}
	
	/**
	 * ecouteur de l'evenement du changement de l'element selectionnee
	 * dans le combo box de rubrique budgetaire
	 * @param event
	 */
	private void onRubricItemChange (ItemEvent event) {
		if(event.getStateChange() != ItemEvent.SELECTED || piePanel.getModel() == null)
			return;
		
		BudgetRubric rubric = (BudgetRubric) event.getItem();
		DefaultPieModel model = (DefaultPieModel) piePanel.getModel();
		model.setSelectedIndex(model.indexOf(model.findByData(rubric)));
		revalidateAmount();
	}
	
	/**
	 * lors du changement de la valeur dans le text field qui permet de 
	 * saisir le montant depenser
	 * @param event
	 */
	private void onAmountChange (CaretEvent event) {
		revalidateAmount();
	}
	
	/**
	 * revalidation du montant
	 */
	private void revalidateAmount () {
		try {
			//le montant voulue doit etre inferieur ou egale au montant disponible
			double amount = Double.parseDouble(fieldAmount.getField().getText().trim());
			PiePart part = piePanel.getModel().findByData(rubricModel.getElementAt(fieldRubric.getField().getSelectedIndex()));
			setEnabledButtonValidation(amount <= part.getValue() && amount > 0);
		} catch (NumberFormatException e) {
			setEnabledButtonValidation(false);
		}
	}
	
	/**
	 * ecouteur de l'evenement du changement de l'elememt selectionnee
	 * dans le combo box de devises monetaires
	 * @param event
	 */
	private void onCurrencyItemChange (ItemEvent event) {
		if(event.getStateChange() != ItemEvent.SELECTED || piePanel.getModel() == null)
			return;
		Currency currency = (Currency) event.getItem();
		piePanel.setModel(pieModels.get(currency));
		PieModel model = piePanel.getModel();
		model.setSelectedIndex(model.indexOf(model.findByData(rubricModel.getElementAt(fieldRubric.getField().getSelectedIndex()))));
		revalidateAmount();
	}
	
	/**
	 * initialisation des composants graphiques
	 */
	private void init() {
		JPanel container = new JPanel(new BorderLayout(5, 5));
		JPanel fields = new JPanel(new BorderLayout());
		JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
		JPanel chart = new JPanel(new BorderLayout(5, 5));
		
		panel.add(fieldDate);
		panel.add(fieldRubric);
		panel.add(fieldAmount);
		panel.add(fieldCurrency);
		
		fields.add(fieldLabel, BorderLayout.NORTH);
		fields.add(panel, BorderLayout.CENTER);
		
		piePanel.setOpaque(false);
		piePanel.setBackground(Color.WHITE);
		piePanel.getRender().setBackground(Color.WHITE);
		piePanel.getCaption().setBackground(Color.WHITE);

		chart.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		chart.add(piePanel, BorderLayout.CENTER);
		
		container.add(fields, BorderLayout.NORTH);
		container.add(chart, BorderLayout.CENTER);
		
		getBody().add(container, BorderLayout.CENTER);
	}
	
	@Override
	public void persist() {
		if(spends == null)
			return;
		
		if(spends.getId() == null)
			spendsDao.create(DEFAULT_ON_PERSIST_REQUEST_ID, spends);
		else
			spendsDao.update(DEFAULT_ON_PERSIST_REQUEST_ID, spends);
	}

	/**
	 * @return the spends
	 */
	public Spends getSpends() {
		return spends;
	}

	/**
	 * @param spends the spends to set
	 */
	public void setSpends(Spends spends) {
		this.spends = spends;
	}

	@Override
	protected void doCleanFields() {
		fieldAmount.reset();
		fieldDate.reset();
	}

	@Override
	protected void doValidate() {
		if(spends == null)
			spends = new Spends();
		
		String error = "";
		cause = null;
		
		BudgetRubric rubric = rubricModel.getElementAt(fieldRubric.getField().getSelectedIndex());
		Currency currency = currencyModel.getElementAt(fieldCurrency.getField().getSelectedIndex());
		Date date = fieldDate.getField().getDate();
		
		spends.setDate(date);
		spends.setCurrency(currency);
		spends.setRubric(rubric);
		spends.setLabel(fieldLabel.getField().getText());
		
		if (date == null)
			error += "La date est obligatoire;";
		
		//validation du montant
		try {
			spends.setAmount(Double.parseDouble(fieldAmount.getField().getText().trim()));
			if(spends.getAmount() <= 0)
				error += "Entrez une valeur supperieur a zero;";
			else {
				double availableAmount = paymentPartDao.getSumByRubric(rubric, currency, true) - spendsDao.getSumByRubric(rubric.getId(), currency, true);
				if(spends.getAmount() > availableAmount)
					error += "Le solde du compte est insulfisant;";
			}
		} catch (NumberFormatException e) {
			error += "Entez le montant au format valide;";
		}
		
		if(!error.trim().isEmpty())
			cause = error.split(";");
		
	}

	@Override
	protected boolean isAccept() {
		return cause == null || cause.length == 0;
	}

	@Override
	protected String[] getRejectCause() {
		return cause;
	}

}
