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
package es.magDevs.myRecipes.ui.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.flow.component.combobox.ComboBox;

import es.magDevs.myRecipes.dal.be.BasicBE;

/**
 * ComboBox con funciones mejoradas
 *
 * @param <T> tipo de bean que tendra como valor el combo
 */
public class ComboBoxEx<T extends BasicBE> extends ComboBox<T> {
	
	/**
	 * Valores que puede tomar el combo mapeador por ID
	 */
	private Map<Long, T> itemsMap;

	public ComboBoxEx() {
		super();
	}

	public ComboBoxEx(int pageSize) {
		super(pageSize);
	}

	public ComboBoxEx(String label, Collection<T> items) {
		super(label, items);
	}

	public ComboBoxEx(String label, T... items) {
		super(label, items);
	}

	public ComboBoxEx(String label) {
		super(label);
	}
	
	@Override
	public void setItems(Collection<T> items) {
		super.setItems(items);
		// Guardamos los valores mapeados por ID
		itemsMap = new HashMap<>(items.size());
		for (T item : items) {
			itemsMap.put(item.getId(), item);
		}
	}
	
	@Override
	public void setValue(T value) {
		if (value != null && value.getId() != null) {
			// Si el valor que se quiere fijar tiene ID, en lugar de el recibido, usamos el que tenemos mapeado, que tendra todos los datos necesarios
			value = itemsMap.get(value.getId());
		}
		super.setValue(value);
	}

}
