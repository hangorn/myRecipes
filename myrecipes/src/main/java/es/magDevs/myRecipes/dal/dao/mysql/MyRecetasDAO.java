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
import java.util.Arrays;
import java.util.List;

import es.magDevs.myRecipes.dal.be.RecetaBean;
import es.magDevs.myRecipes.dal.dao.BasicDAO;

public class MyRecetasDAO extends MyBasicDAO<RecetaBean> implements BasicDAO<RecetaBean> {
	private static final String TABLA = "recetas";
	private static final List<String> COLUMNAS = Arrays.asList("descripcion");
	
	public MyRecetasDAO(Connection connection) {
		super(connection);
	}

	@Override
	protected String getTabla() {
		return TABLA;
	}

	@Override
	protected List<String> getColumnas() {
		return COLUMNAS;
	}

	@Override
	protected RecetaBean getBeanFromRs(ResultSet rs) throws SQLException {
		RecetaBean bean = new RecetaBean();
		int i = 1;
		bean.setId(rs.getLong(i++));
		bean.setDescripcion(rs.getString(i++));
		return bean;
	}

	@Override
	protected String getConditions(RecetaBean filtro, List<Object> params) throws Exception {
		String where = addEqualCondition("", params, "id", filtro.getId());
		where = addLikeCondition(where, params, "descripcion", filtro.getDescripcion());
		return where;
	}

	@Override
	protected List<Object> getValoresFromBean(RecetaBean bean) {
		return Arrays.asList(bean.getDescripcion());
	}

	@Override
	protected RecetaBean getNewBean() {
		return new RecetaBean();
	}
}
