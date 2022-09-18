/**
 * 
 */
package com.spiral.simple.store.dao;

import java.util.Date;

import com.spiral.simple.store.beans.Currency;
import com.spiral.simple.store.beans.Spends;

/**
 * @author Esaie MUHASA
 *
 */
public interface SpendsDao extends CashMoneyDao<Spends> {
	
	/**
	 * check if rubric is already reference at least once
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	boolean checkByRubric (String rubricId) throws DAOException;
	
	/**
	 * count all operation referenced budget rubric
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	int countByRubric (String rubricId) throws DAOException;

	/**
	 * count all operations perfected at date interval and referenced budget rubric
	 * @param rubricId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	int countByRubric (String rubricId, Date min, Date max) throws DAOException;
	
	/**
	 * select all operation referenced budget rubric
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByRubric (String rubricId) throws DAOException;
	
	/**
	 * comptage des operations qui font reference a la rubrique par defaut
	 * @return
	 * @throws DAOException
	 */
	int countByDefaultRubric ()  throws DAOException;
	
	/**
	 * verification de l'existance d'une operation deja faite
	 * pour le compte default
	 * @return
	 * @throws DAOException
	 */
	boolean checkByDefaultRubric () throws DAOException;
	
	/**
	 * renvoie le tableau des operations qui ne font pas reference au rubrique budgetaire
	 * le trie des donnes est fait sur base de la date d'enregistrement en ordre decroisante
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByDefaultRubric () throws DAOException;
	
	/**
	 * selection d'une intervale des donnees qui ne sont pas liee au rubrique budgetaire
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByDefaultRubric (int limit, int offset) throws DAOException;
	
	/**
	 * renvoie le operations qui ont ete fait sur la rubrique par defaut,
	 * en une intervale de temps
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByDefaultRubric (Date min, Date max) throws DAOException;
	
	/**
	 * selectionne les depense faite la rubrique par defaut en une date
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default Spends[] findByDefaultRubric (Date date) throws DAOException{
		return findByDefaultRubric(date, date);
	}
	
	/**
	 * select part of operation referenced budget rubric
	 * @param rubricId
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByRubric (String rubricId, int limit, int offset) throws DAOException;
	
	/**
	 * select all operation perfected in budget rubric at date interval
	 * @param rubricId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByRubric (String rubricId, Date min, Date max) throws DAOException;
	
	/**
	 * select all part of operation execute at date interval and reference on 
	 * budget rubric
	 * @param rubricId
	 * @param min
	 * @param max
	 * @param limit
	 * @param offset
	 * @return
	 * @throws DAOException
	 */
	Spends[] findByRubric (String rubricId, Date min, Date max, int limit, int offset) throws DAOException;
	
	/**
	 * return sum of spends at budget rubric
	 * @param rubricId
	 * @return
	 * @throws DAOException
	 */
	double getSumByRubric(String rubricId, Currency currency, boolean currencyOnly) throws DAOException;
	
	/**
	 * return sum of spends at date interval for budget rubric
	 * @param rubricId
	 * @param min
	 * @param max
	 * @return
	 * @throws DAOException
	 */
	double getSumByRubric (String rubricId, Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException;
	
	/**
	 * return operation perfected in budget rubric at date
	 * @param rubricId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default Spends[] findByRubricAt (String rubricId, Date date) throws DAOException {
		return findByRubric(rubricId, date, date);
	}
	
	/**
	 * count all operation perfected at date and referenced budget rubric
	 * @param rubricId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default int countByRubric (String rubricId, Date date) throws DAOException {
		return countByRubric(rubricId, date, date);
	}
	
	/**
	 * return sum of spends at date for budget rubric
	 * @param rubricId
	 * @param date
	 * @return
	 * @throws DAOException
	 */
	default double getSumByRubric (String rubricId, Date date, Currency currency, boolean currencyOnly) throws DAOException {
		if (rubricId == null || rubricId.trim().isEmpty())
			return getSumByDefaultRubric(date, date, currency, currencyOnly);
		return getSumByRubric(rubricId, date, date, currency, currencyOnly);
	}
	
	/**
	 * renvoie le solde (montant total) des operations deja faite sur la rubrique par defaut
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	double getSumByDefaultRubric (Currency currency, boolean currencyOnly) throws DAOException;
	
	/**
	 * renvoie le solde des operations faite en une date sur la rubrique par defaut
	 * @param date
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	default double getSumByDefaultRubric (Date date, Currency currency, boolean currencyOnly) throws DAOException {
		return getSumByDefaultRubric(date, date, currency, currencyOnly);
	}
	
	/**
	 * renvoie le sold (montant total) des operations faite sur le compte default, en une intervale de date
	 * @param min
	 * @param max
	 * @param currency
	 * @param currencyOnly
	 * @return
	 * @throws DAOException
	 */
	double getSumByDefaultRubric (Date min, Date max, Currency currency, boolean currencyOnly) throws DAOException;
}
