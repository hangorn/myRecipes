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

import java.util.List;

import es.magDevs.myRecipes.dal.be.BasicBean;

/**
 * Intefaz que define operaciones basicas: crear, modificar, listar, borrar
 * @author javier.vaquero
 *
 * @param <BEAN>
 */
public interface BasicDAO<BEAN extends BasicBean> {
	List<BEAN> getList(BEAN filtro) throws Exception;
	List<BEAN> getList() throws Exception;
	BEAN getById(Long id) throws Exception;

	void borrar(Long id) throws Exception;

	Long insert(BEAN bean) throws Exception;

	void update(BEAN bean) throws Exception;

}
