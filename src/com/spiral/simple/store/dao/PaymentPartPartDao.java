/**
 * 
 */
package com.spiral.simple.store.dao;

import java.util.Date;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.beans.CommandPayment;
import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.DistributionConfigItem;
import com.spiral.simple.store.beans.helper.PaymentPart;

/**
 * @author Esaie Muhasa
 *
 */
public interface PaymentPartPartDao extends DAOInterface<PaymentPart> {
	
	/**
	 * selection des operations de repartiton d'un element de la commande
	 * @param item
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByCommandPayment (CommandPayment item) throws DAOException;
	
	/**
	 * selectionne tout les operations faites dans l'intervale de temps
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByDate (Date min, Date max) throws DAOException;
	
	/**
	 * selection des operations faite en une date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default PaymentPart [] findByDate (Date date) throws DAOException {
		return findByDate(date, date);
	}
	
	/**
	 * renvoie une partie des operations faite dans le temps
	 * @param min
	 * @param max
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByDate (Date min, Date max, int limit, int offset) throws DAOException;
	
	/**
	 * renvoi la colleection des operations qui font reference a la configuration en parametre
	 * @param item
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByItem (DistributionConfigItem item) throws DAOException;
	
	/**
	 * renvoie le recette qui font reference a la dite rubrique, pour l'intervale de temps choisie
	 * @param item
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByItem (DistributionConfigItem item, Date min, Date max) throws DAOException;
	
	/**
	 * selection de tout les operations qui font reference a une rubrique, pour l'intervale de temps choisie
	 * @param rubric
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByRubric (BudgetRubric rubric, Date min, Date max) throws DAOException;
	
	/**
	 * comptage des operations qui reference a la rubrique budgetaire
	 * @param rubric
	 * @return
	 * @throws DAOException
	 */
	int countByRubric (BudgetRubric rubric) throws DAOException;
	
	/**
	 * compte le operations qui font reference a la rubrique par defaut
	 * @return
	 * @throws DAOException
	 */
	int countByDefaultRubric () throws DAOException;
	
	/**
	 * selection de tout les operations qui font reference a une rubrique budgetaire
	 * @param rubric
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByRubric (BudgetRubric rubric) throws DAOException;
	
	/**
	 * selection d'un partie des operations qui font reference a une rubrique budgetaire
	 * @param rubric
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByRubric (BudgetRubric rubric, int limit, int offset) throws DAOException;
	
	/**
	 * recuperation des operations qui font refenrence a une rubrique budgetaire, en une date X
	 * @param rubric
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default PaymentPart [] findByRubric (BudgetRubric rubric, Date date) throws DAOException {
		return findByRubric(rubric, date, date);
	}
	
	/**
	 * selectionne tout les operations qui font reference a la rubrique par defaut
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByDefaultRubric () throws DAOException;
	
	/**
	 * selectionne une partie des operations qui font reference a la rubrique par defaut
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByDefaultRubric (int limit, int offset) throws DAOException;
	
	/**
	 * selectionne tout les operations qui font reference a la rubrique par defaut,
	 * pour l'interfale de temps en parametre
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	PaymentPart [] findByDefaultRubric (Date min, Date max) throws DAOException;
	
	/**
	 * selectionne tout les operations qui font reference au compte par defaut pout ladite date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default PaymentPart [] findByDefaultRubric (Date date) throws DAOException {
		return findByDefaultRubric(date, date);
	}
	
	/**
	 * renvoei la somme des operations qui font reference a la rubrique en parametre.
	 * par defaut on sommes uniquement les operations qui font reference a la devise en deuxieme parametre.
	 * si le parametre currencryOnly vaut true, alors on effectue directement la convesion des autres operations
	 * qui satisfont egalement aux condition de selection
	 * @param rubric
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	double getSumByRubric (BudgetRubric rubric, Currency currency, boolean currencyOnly) throws DAOException;
	
	/**
	 * renvoie la somme des operations qui font reference au rubric budgetaire en parametre,
	 * pour l'intervale de temps choisie.
	 * par defautl, on somme le operations qui font reference a la devise proposee.
	 * si le parametre currenyOnly vaut false, alors on selectionne le reste de operations qui satsfait le filtre de selection,
	 * puis on effectue la convesion de leurs devise.
	 * 
	 * @param rubric
	 * @param min
	 * @param max
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	double getSumByRubric (BudgetRubric rubric, Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException;
	
	/**
	 * renvoie la somme de operations qui font reference a une rubrique, en une date X
	 * on somme le operation qui font reference  a la devise.
	 * si le parametre currencyOnly vaut false, alors on selectionne les autres operatins et on effectue la conversion
	 * des devises
	 * @param rubric
	 * @param date
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	default double getSumByRubric (BudgetRubric rubric, Date date, Currency currency, boolean currencyOnly) throws DAOException {
		return getSumByRubric(rubric, date, date, currency, currencyOnly);
	}
	
	/**
	 * renvoie le montant total deja affecter au compte par defaut.
	 * en premier vue, on selectionne uniquement les operations qui font reference a la devise en parametre.
	 * si le parametre currencyOnly faut false, alors on selectionne les autres operations qui font 
	 * reference a la rubrique par defaut et on fait la conversion des ceux-ci vers la devise en parametre
	 * 
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	double getSumByDefaultRubric (Currency currency, boolean currencyOnly) throws DAOException;
	
	/**
	 * renvoie le montant affecter au compte par defaut, pour une date precise. 
	 * par defaut, on somme uniquement les operations qui font reference a a devise en parametre.
	 * si le parametre currencyOnly vaut false, alors on selectionne le reste des operations
	 * et on effectue la conversion des devises de ceux-ci
	 * 
	 * @param min
	 * @param max
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	double getSumByDefaultRubric (Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException;
	
	/**
	 * renvoie le sold des montants affecter au compte par defaut, en une date
	 * 
	 * @param date
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	default double getSumByDefaultRubric (Date date, Currency currency, boolean currencyOnly) throws DAOException {
		return getSumByDefaultRubric(date, date, currency, currencyOnly);
	}
}
