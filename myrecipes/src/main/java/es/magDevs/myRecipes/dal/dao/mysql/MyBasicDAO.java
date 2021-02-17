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
package es.magDevs.myRecipes.dal.dao.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import es.magDevs.myRecipes.dal.be.BasicBean;
import es.magDevs.myRecipes.dal.dao.AbstractDAO;
import es.magDevs.myRecipes.dal.dao.BasicDAO;

/**
 * DAO que implementa operaciones basicas: crear, modificar, listar, borrar
 * @author javier.vaquero
 *
 * @param <BEAN>
 */
public abstract class MyBasicDAO<BEAN extends BasicBean> extends AbstractDAO implements BasicDAO<BEAN> {

	public MyBasicDAO(Connection connection) {
		super(connection);
	}

	@Override
	public List<BEAN> getList(BEAN filtro) throws Exception {
		List<Object> params = new ArrayList<Object>();
		String sql = "SELECT " + getColumnasSelect() + " FROM " + getTabla() + " ";
		
		return runSelect(sql + getConditions(filtro, params), params, this::getBeanFromRs);
	}
	
	@Override
	public List<BEAN> getList() throws Exception {
		return getList(getNewBean());
	}
	
	@Override
	public BEAN getById(Long id) throws Exception {
		BEAN filtro = getNewBean();
		filtro.setId(id);
		List<BEAN> beans = getList(filtro);
		return beans.isEmpty() ? null : beans.get(0);
	}

	/**
	 * Metodo para devolve las columnas a obtener con la select de este DAO, por
	 * defecto todas las de la tabla, pero se puede sobreescribir el metodo para
	 * personalizarlo
	 * 
	 * @return nombres de columnas separadas por comas, no es necesario poner espacio inicial/final
	 */
	protected String getColumnasSelect() {
		return "id," + StringUtils.join(getColumnas(), ",");
	}

	@Override
	public void borrar(Long id) throws Exception {
		delete(getTabla(), id);
	}

	@Override
	public Long insert(BEAN bean) throws Exception {
		return insert(getTabla(), getColumnas(), getValoresFromBean(bean));
	}

	@Override
	public void update(BEAN bean) throws Exception {
		List<Object> valores = new ArrayList<>(getValoresFromBean(bean));
		valores.add(bean.getId());
		update(getTabla(), getColumnas(), valores);
	}

	/**
	 * Metodo abstracto que devuelve el nombre de la tabla a la que afecta el DAO
	 * 
	 * @return nombre de la tabla
	 */
	protected abstract String getTabla();

	/**
	 * Metodo abstracto que devuelve todas las columnas de la tabla menos el ID
	 * 
	 * @return lista de columnas
	 */
	protected abstract List<String> getColumnas();

	/**
	 * Metodo que debe rellenar un bean con los valores del {@link ResultSet}
	 * suministrado incluido el ID que ira en primer lugar. El orden de los valores
	 * del resultset sera el mismo que el de las columnas recibidas en el metodo
	 * {@link MyBasicDAO#getColumnas()}
	 * 
	 * @param rs resultset con los datos de una tupla
	 * @return instancia del bean con los datos rellenados
	 */
	protected abstract BEAN getBeanFromRs(ResultSet rs) throws SQLException;

	/**
	 * Metodo para obtener las condiciones a aplicar al obtener los datos. Tambien puede contener una sentencia ORDER BY
	 * 
	 * @param filtro Bean con los datos que se tienen utilzar para filtrar
	 * @param params lista de datos donde se deben añadir los valores del filtro
	 * @return sentencia WHERE o una cadena vacia
	 */
	protected abstract String getConditions(BEAN filtro, List<Object> params) throws Exception;

	/**
	 * Metodo para devolver los valores de un Bean excepto el ID, el orden de los valores debe ser
	 * el mismo que el de las columnas recibidas en el metodo
	 * {@link MyBasicDAO#getColumnas()}
	 * 
	 * @param bean con datos
	 * @return lista con los datos
	 */
	protected abstract List<Object> getValoresFromBean(BEAN bean);
	

	protected abstract BEAN getNewBean();

}
