/**
 * Copyright (c) 2014-2020, Javier Vaquero
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
package es.magDevs.myRecipes.dal.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import javax.naming.NamingException;

public class MySqlDAOFactory extends DAOFactory {

	private static String DRIVER_NAME = "com.mysql.jdbc.Driver";

	@Override
	public synchronized Connection getConnection() throws Exception{
		try {
			return super.getConnection();
		} catch (NamingException e) {
			logger.info("Intentado conexion directa!!");
			
			Properties propiedades = new Properties();
			propiedades.load(MySqlDAOFactory.class.getClassLoader().getResourceAsStream("config.properties"));
			
			String user = propiedades.getProperty("user");
			String pass = propiedades.getProperty("pass");
			String url = propiedades.getProperty("url");
			Class.forName(DRIVER_NAME);
			
			connection = DriverManager.getConnection(url, user, pass);
			return connection;
		}
	}

//	@Override
//	public UsuariosDAO getUsuariosDAO() throws Exception {
//		return new MyUsuariosDAO(getConnection());
//	}

}
