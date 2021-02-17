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

import java.util.List;

import es.magDevs.myRecipes.dal.be.BasicBean;
import es.magDevs.myRecipes.dal.dao.BasicDAO;
import es.magDevs.myRecipes.dal.dao.DAOFactory;

public abstract class BasicBL<BEAN extends BasicBean> extends AbstractBL {
	
	public List<BEAN> getList(BEAN filtro) throws Exception {
		return execute(factory -> getDao(factory).getList(filtro));
	}
	
	public List<BEAN> getList() throws Exception {
		return execute(factory -> getDao(factory).getList());
	}

	public void borrar(Long id) throws Exception {
		executeWithTransaction(factory -> {
			getDao(factory).borrar(id);
			return true;
		});
	}

	public void guardar(BEAN bean) throws Exception {
		executeWithTransaction(factory -> {
			Long id = bean.getId();
			if(id == null) {
				id = getDao(factory).insert(bean);
			} else {
				getDao(factory).update(bean);
			}
			return true;
		});
	}

	public BEAN getById(Long id) throws Exception {
		return execute(factory -> getDao(factory).getById(id));
	}

	protected abstract BasicDAO<BEAN> getDao(DAOFactory daoFactory) throws Exception;
}
