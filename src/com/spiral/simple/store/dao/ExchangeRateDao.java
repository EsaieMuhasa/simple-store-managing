/**
 * 
 */
package com.spiral.simple.store.dao;

import java.util.Date;

import com.spiral.simple.store.beans.Command;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.ExchangeRate;
import com.spiral.simple.store.beans.helper.Money;

/**
 * @author Esaie MUHASA
 *
 */
public interface ExchangeRateDao extends DAOInterface<ExchangeRate> {
	
	/**
	 * convert value form valueCurrency to currency conversion
	 * @param value
	 * @param valueCurrency
	 * @param conversion
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 */
	default double convert (double value, Currency valueCurrency, Currency conversion) throws DAOException, IllegalArgumentException{
		return findAvailableByCurrencies(valueCurrency.getId(), conversion.getId()).convert(value, valueCurrency);
	}
	
	/**
	 * effectue la confersion d'un montant, d'une devise A a une devise B, en tenant compte du temps
	 * @param value
	 * @param valueCurrency
	 * @param conversion
	 * @param time
	 * @return
	 * @throws DAOException
	 * @throws IllegalArgumentException
	 */
	default double convert (double value, Currency valueCurrency, Currency conversion, Date time) throws DAOException, IllegalArgumentException {
		return findByCurrencies(valueCurrency.getId(), conversion.getId(), time).convert(value, valueCurrency);
	}
	
	/**
	 * traitement de verification de la satisfaction dela commande.
	 * la satisfaction dela command est conserver dans ladite command, dans l'attribut <strong>successfullyPaid</strong>.
	 * <p>Le informations concernant la commande doivent etre charger d'avance
	 * car cette methode ce contante uniquement de lire le etat dela dite command et effectuer des eventuel
	 * conversion, si la commande a ete payer en plusier devise</p>
	 * @param command
	 * @throws DAOException : lors de traitement de convesion des devises
	 * @throws IllegalStateException : dans le cas où la commande ne confient pas item
	 */
	default void processingCommandPayment(Command command) throws DAOException, IllegalStateException{
		Money [] totals = command.getTotalMoney();
		
		if (totals == null || totals.length == 0)
			return;
			//throw new IllegalStateException("Impossible d'effectuer cette operation les prix des element de la command ne sont pas determiné");
		
		double total = totals[0].getAmount();
		Currency currency = totals[0].getCurrency();
		
		if(totals.length != 1) {
			/**dans le cas où nous avons plusieur devise lors de la facturation
			 * de elements de la command*/
			for (int i = 1; i < totals.length; i++)
				total += convert(totals[i].getAmount(), totals[i].getCurrency(), currency, command.getDate());
		}
		
		Money [] paids = command.getPaidMoney();
		if(paids == null || paids.length == 0) {
			/**
			 * dans le cas ou il n'ya aucun payement, alors on considere directement que la commande n'est pas encore satisfait
			 */
			command.setSuccessfullyPaid(false);
			return;
		}
		
		double paid = paids[0].getAmount();
		Currency paidCurrency = paids[0].getCurrency();
		if(paids.length != 1) {
			/**
			 * dans le cas ou il y plus d'une devise, alors o convertie tout les reste des devise 
			 * dans le meme unite que le premier montant
			 */
			for (int i = 1; i < paids.length; i++)
				paid += convert(paids[i].getAmount(), paids[i].getCurrency(), paidCurrency, command.getDate());
		}
		
		/**
		 * devise ne sont pas identique, on convertie le montant deja payer 
		 * au meme unit que le montant que doit payer le client
		 */
		if (!currency.equals(paidCurrency))
			paid = convert(paid, paidCurrency, currency, command.getDate());
		
		boolean success = total <= paid;
		command.setSuccessfullyPaid(success);
	}
	
	/**
	 * check if currency has exchange rate in database 
	 * @param currency
	 * @return
	 * @throws DAOException
	 */
	boolean checkByCurrency (String currency) throws DAOException;
	
	/**
	 * check if currencies in method parameters has exchange rate
	 * @param currency1
	 * @param currency2
	 * @return
	 * @throws DAOException
	 */
	boolean checkByCurrencies (String currency1, String currency2) throws DAOException;
	
	/**
	 * selection de toutes le variation du taux d'echange de la devise dont l'ID est en parametre
	 * @param currencyKey
	 * @return
	 * @throws DAOException
	 */
	ExchangeRate [] findByCurrency (String currencyKey) throws DAOException;
	
	/**
	 * return all exchange rate available for currency
	 * @param currency
	 * @return
	 * @throws DAOException
	 */
	ExchangeRate[] findAvailableByCurrency (String currency) throws DAOException;
	
	/**
	 * select all available exchange rate
	 * @return
	 * @throws DAOException
	 */
	ExchangeRate[] findAvailable () throws DAOException;
	
	/**
	 * return exchange rate available for currencies keys in
	 * method parameters
	 * @param currency1
	 * @param currency2
	 * @return
	 * @throws DAOException
	 */
	ExchangeRate findAvailableByCurrencies (String currency1, String currency2) throws DAOException;
	
	/**
	 * renvoie le taut d'echange de ce deux devise  en tenant compte du temps
	 * @param currency1
	 * @param currency2
	 * @param time
	 * @return
	 * @throws DAOException
	 */
	ExchangeRate findByCurrencies (String currency1, String currency2, Date time) throws DAOException;
}
