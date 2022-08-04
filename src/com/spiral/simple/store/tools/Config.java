/**
 * 
 */
package com.spiral.simple.store.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Esaie MUHASA
 *
 */
public class Config {
	
	private static final String CONFIG_FILE = "config.properties";
	private static Map<String, String> dictionary;

	/**
	 * 
	 */
	protected Config() {
		super();
	}
	
	/**
	 * return dictionary
	 * @return
	 * @throws FileNotFoundException 
	 */
	public static Map<String, String> getDictionary () throws FileNotFoundException {
		if(dictionary == null) {
			dictionary = new HashMap<>();
			InputStream fileConfig=new FileInputStream(new File(CONFIG_FILE));
			Properties properties=new Properties();
			try {
				properties.load(fileConfig);
			} catch (IOException e) {
				throw new RuntimeException("Erreur survenue lors du chargement du fichier de configuration: "+e.getMessage(), e);
			}
			
			for (Object key : properties.keySet()) 
				dictionary.put(key.toString(), properties.getProperty(key.toString()));
		}
		return dictionary;
	}
	
	/**
	 * return value associate with key parameter value
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		try {
			return getDictionary().get(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static String getIcon(String icon) {
		return "icon/"+icon+".png";
	}

}
