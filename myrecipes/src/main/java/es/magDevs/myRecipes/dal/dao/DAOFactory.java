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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.PooledConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DAOFactory {

	public static final int MYSQL = 1;
	
	protected String dataSource = "MySQLDB";
	
	protected static int numConexiones = 0;

	protected static int numFactorias = 0;

	// Conexion, sin dataSource
	protected Connection connection = null;	
	// primer intento de conexión
	protected static boolean bPriveraVez = true;

	// Objeto que mantiene la conexión asociada a la transacción actual para cada hilo de ejecución.
	// En caso de no estar en medio de una transacción, no almacenará referencia a conexión alguna.
	// Las propiedades de este objeto son locales a cada hilo, impidiendo su acceso o modificación por
	// parte de otros hilos. Asimismo, el objeto funciona como una variable global para el hilo actual
	protected static final ThreadLocal<Connection> transactionThread = new ThreadLocal<Connection>();

	// Control de transacciones anidadas para evitar la confirmación o cancelación incorrecta de cambios
	protected boolean transaccionAnidada = false;
	
	protected String url = null;

	protected String user = null;

	protected String pass = null;

	protected String dbaFileProperties = "dbal.properties";

	// Log de salida
	final static Logger logger = LoggerFactory.getLogger(DAOFactory.class);

	// Resources
	private static HashMap<String, ResourceBundle> properties;
	
	// pool interno
	protected static DataSource ocpds;
	protected static PooledConnection pc;

	static {
		properties = new HashMap<String, ResourceBundle>();
	}

	// Metodos DAO
	/*****************************************/

	/* BD */

//	public abstract UsuariosDAO getUsuariosDAO() throws Exception;
	
	public static DAOFactory getDAOFactory(int iType) {
		
		switch (iType) {
			
			case (MYSQL):
				return new MySqlDAOFactory();
			
			default:
				return null;
		}
	}

	public synchronized Connection getConnection() throws Exception {
		
		// Si el hilo actual está en medio de una transacción, devolvemos la conexión actual
		// correspondiente (evitaremos su modificación hasta la finalización de la misma)
		if (isTransaction()) {
			
			logger.debug("Recuperando la conexión para la transacción del hilo " + Thread.currentThread().getName());
			connection = transactionThread.get();
		
		// Si no estamos en una transacción, comprobamos si la conexión actual es nula o si
		// está cerrada (no podemos reutilizarla) para obtener una nueva
		} else if (connection == null || connection.isClosed()) {
			try {
				
				Context envTomcatContext = null;
				Context dsTomcatContext = null;
				
				try {
					// Modo TOMCAT
					envTomcatContext = new InitialContext();
					dsTomcatContext = (Context) envTomcatContext.lookup("java:comp/env");

					DataSource ds = (DataSource) dsTomcatContext.lookup("jdbc/" + dataSource);
					connection = ds.getConnection();
					numConexiones++;

					if (Thread.currentThread().getStackTrace().length > 5) {
						StackTraceElement stElem = Thread.currentThread().getStackTrace()[5];
						logger.debug("Creamos una conexion - POOL TOMCAT, hay " + numConexiones
								+ ". Origen: " + stElem.getClassName() + "." + stElem.getMethodName() + " ("
								+ stElem.getLineNumber() + ")");
					} else {
						logger.debug("Creamos una conexion, hay " + numConexiones);
					}
				} catch (NamingException e3) {
					throw e3;
				} finally {
					// Liberamos los recursos de contexto utilizados para acceder al datasource en modo Tomcat
					try {
						if (envTomcatContext != null) {
							envTomcatContext.close();
						}
					} catch (NamingException ne) {
						logger.error("Error liberando contexto al obtener conexión", ne);
					}
				}
			} catch (NamingException e) {
				throw e;
			} catch (Exception e) {
				throw e;
			}
		}
		return connection;
	}

	public synchronized void closeConnection() {
		boolean cierra = true;
		try {
			// Cerramos la conexión si existe, no está cerrada y no estamos en medio de una transacción
			if (connection != null && !connection.isClosed() && !isTransaction()) {
				if (bPriveraVez) {
					logger.debug("Se llama a cerrar conexión con directa");
				}
				connection.close();
				connection = null;
				numConexiones--;
			}
		} catch (Exception err) {
			cierra = false;
			logger.error("Error al cerrar conexión 1", err);
		} finally {
			if (!cierra) {
				logger.debug("Problema cerrando conexión, la considero cerrada");
				numConexiones--;
			}
		}
	}

	/**
	 * Establece inicio de transacción
	 * 
	 * @return
	 */
	public synchronized boolean beginTransaction() {
		boolean correcto = true;
		
		try {

			// En caso estar en medio de una transacción abierta, marcamos el anidamiento para evitar cambios incorrectos
			if (isTransaction()) {
				
				connection = transactionThread.get();
				
				logger.info("Transacción anidada en el hilo " + Thread.currentThread().getName());
				transaccionAnidada = true;
				
			// En otro caso, iniciamos la transacción del modo habitual y la asociamos al hilo actual para controlarla
			} else {
				if (connection == null || connection.isClosed()) {
					getConnection();
				}
				
				connection.setAutoCommit(false);
				connection.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED);
				
				logger.info("Asignando la conexión para la transacción del hilo " + Thread.currentThread().getName());
				transaccionAnidada = false;
				transactionThread.set(connection);
			}
			
		} catch (Exception e) {
			logger.error("Error iniciando transacción", e);
			correcto = false;
		}
		
		return correcto;
	}

	/**
	 * Finaliza una transacción
	 * 
	 * @return
	 */
	public synchronized boolean endTransaction() {
		boolean correcto = true;
		// Si estamos en una transacción anidada, no la finalizaremos salvo que sea la transacción inicial
		if (!transaccionAnidada) {
			try {
				connection.setAutoCommit(true);
			} catch (Exception e) {
				logger.error("Error finalizando transacción", e);
				correcto = false;
			} finally {
				logger.info("Quitando la conexión para la transacción del hilo " + Thread.currentThread().getName());
				transactionThread.remove();
			}
		}
		return correcto;
	}

	public synchronized boolean commit() {
		boolean correcto = true;
		// Si estamos en una transacción anidada, no confirmaremos los cambios salvo que sea la transacción inicial
		if (!transaccionAnidada) {
			try {
				connection.commit();
			} catch (SQLException e) {
				logger.error("Error haciendo commit", e);
				correcto = false;
			} finally {
				endTransaction();
			}
		}
		return correcto;
	}

	public synchronized boolean rollback() {
		boolean correcto = true;
		// Si estamos en una transacción anidada, no cancelaremos los cambios salvo que sea la transacción inicial
		if (!transaccionAnidada) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				logger.error("Error haciendo rollback", e);
				correcto = false;
			} finally {
				endTransaction();
			}
		}
		return correcto;
	}

	/**
	 * Indica si actualmente se está trabajando con la base de datos en modo transaccional
	 * 
	 * @return
	 */
	public synchronized boolean isTransaction() {
		return (transactionThread.get() != null);
	}
	

	public static ResourceBundle getProperties(String sKey) {
		return properties.get(sKey);
	}

	public static void putProperties(String sKey, ResourceBundle bundle) {
		properties.put(sKey, bundle);
	}

	public String getDbaFileProperties() {
		return dbaFileProperties;
	}

	public void setDbaFileProperties(String dbaFileProperties) {
		this.dbaFileProperties = dbaFileProperties;
	}

	/**
	 * Obtiene el número de conexiones que hay a la base de datos.
	 * 
	 * @return
	 */
	public static int getNumConexiones() {
		return numConexiones;
	}

	/**
	 * @return the numFactorias
	 */
	public static int getNumFactorias() {
		return numFactorias;
	}

	public int getPoolSize(){
		return -1;
	}
	
	public int getPoolUse(){
		return -1;
	}

}
