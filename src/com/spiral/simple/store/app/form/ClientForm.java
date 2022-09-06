/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;

import javax.swing.Box;

import com.spiral.simple.store.beans.Client;
import com.spiral.simple.store.dao.ClientDao;
import com.spiral.simple.store.swing.SimpleTextField;

/**
 * @author Esaie Muhasa
 *
 */
public class ClientForm extends AbstractForm<Client> {
	private static final long serialVersionUID = -442672489219557075L;
	
	private final SimpleTextField fieldNames = new SimpleTextField("Noms");
	private final SimpleTextField fieldTelephone = new SimpleTextField("Téléphone");
	
	private final ClientDao clientDao;
	
	private Client client;

	public ClientForm(ClientDao daoInterface) {
		super(daoInterface);
		clientDao = daoInterface;
		
		Box rows = Box.createVerticalBox();
		rows.add(fieldNames);
		rows.add(Box.createVerticalStrut(5));
		rows.add(fieldTelephone);
		
		getBody().add(rows, BorderLayout.CENTER);
		setVisibilityButtonCancellation(true);
	}

	@Override
	protected void doCleanFields() {
		fieldNames.getField().setText("");
		fieldTelephone.getField().setText("");
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
		if(client == null){
			cleanFields();
			return;
		}
		
		fieldNames.getField().setText(client.getNames());
		fieldTelephone.getField().setText(client.getTelephone());
	}
	
	@Override
	public void persist() {
		if(client == null)
			return;
		
		if (client.getId() == null)
			clientDao.create(DEFAULT_ON_PERSIST_REQUEST_ID, client);
		else
			clientDao.update(DEFAULT_ON_PERSIST_REQUEST_ID, client);
	}

	@Override
	protected void doValidate() {
		String names = fieldNames.getField().getText().trim();
		String telephone = fieldTelephone.getField().getText().trim();
		
		client.setNames(names);
		client.setTelephone(telephone);
	}

	@Override
	protected boolean isAccept() {
		return true;
	}

	@Override
	protected String[] getRejectCause() {
		return null;
	}

}
