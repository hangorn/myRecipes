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
package es.magDevs.myRecipes.dal.bl;

import es.magDevs.myRecipes.dal.dao.DAOFactory;

public class AbstractBL {

	protected DAOFactory factory = null;
	
	public AbstractBL() {
		factory = DAOFactory.getDAOFactory(DAOFactory.MYSQL);
	}
	
	/**
	 * Interfaz para usar un {@link DAOFactory} y no gestionar internamente conexiones ni transacciones.
	 * 
	 * @author javier.vaquero
	 *
	 * @param <T>
	 *            tipo de dato que se generara al usar el factory
	 */
	public interface DaoFactoryHandler<T> {
		/**
		 * Metodo para realizar las operaciones necesarias con un {@link DAOFactory} sin tener que preocuparse de conexiones.
		 * 
		 * @param daoFactory
		 *            {@link DAOFactory} ya iniciado para realizar las operaciones deseadas y del que no es necesario cerrar la conexion.
		 */
		T useFactory(DAOFactory daoFactory) throws Exception;
	}
	
	protected <T extends Object> T execute(DaoFactoryHandler<T> factoryHandler) throws Exception {
		try {
			return factoryHandler.useFactory(factory);
		} catch (Exception e) {
			throw e;
		} finally {
			if (factory != null) {
				factory.closeConnection();
			}
		}
	}
	
	/**
	 * Metodo para ejecutar accciones usando un {@link DAOFactory}, este metodo
	 * proporciona una instancia ya inicializada con una transaccion abierta y se encarga de hacer un commit y cerrar
	 * conexiones. Se hara un rollback si {@link DaoFactoryHandler#useFactory(DAOFactory)} lanza cualquier excepcion.
	 * 
	 * @param factoryHandler
	 *            acciones a ejecutar
	 * @return resultado de las acciones o <code>null</code> si ocurre un error
	 *         o si {@link DaoFactoryHandler#useFactory(DAOFactory)} lanza una
	 *         excepcion (se hace un rollback)
	 */
	protected <T extends Object> T executeWithTransaction(DaoFactoryHandler<T> factoryHandler) throws Exception {
		DAOFactory daoFactory = null;

		try {
			daoFactory = factory;
			if (daoFactory.beginTransaction()) {
				T result = factoryHandler.useFactory(daoFactory);
				if(daoFactory.commit()) {
					return result;
				} else {
					throw new Exception("No se ha podido hacer commit");
				}
			} else {
				throw new Exception("No se ha podido inicar transaccion");
			}
		} catch (Exception e) {
			daoFactory.rollback();
			throw e;
		} finally {
			if (factory != null) {
				factory.closeConnection();
			}
		}
	}
	
}
