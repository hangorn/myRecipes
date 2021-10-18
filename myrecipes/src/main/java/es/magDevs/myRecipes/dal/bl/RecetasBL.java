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

import es.magDevs.myRecipes.dal.be.RecetaBean;
import es.magDevs.myRecipes.dal.dao.BasicDAO;
import es.magDevs.myRecipes.dal.dao.DAOFactory;

public class RecetasBL extends BasicBL<RecetaBean> {

	@Override
	protected BasicDAO<RecetaBean> getDao(DAOFactory daoFactory) throws Exception {
		return daoFactory.getRecetasDao();
	}

}
