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

import java.text.DecimalFormat;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.PropertySet;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.Setter;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
import com.vaadin.flow.function.ValueProvider;

import es.magDevs.myRecipes.ui.converters.DoubleToLongConverter;
import es.magDevs.myRecipes.ui.converters.StringToDoubleConverter;

/**
 * Clase util para hacer enlazar los campos de la interfaz de Vaadin con los beans del modelo de datos
 * 
 * @author javier.vaquero
 *
 * @param <BEAN> bean del modelo de datos
 */
public class BinderAdv<BEAN> extends Binder<BEAN> {
	
	private class BooleanConverter implements Converter<Boolean, Integer> {
		@Override
		public Result<Integer> convertToModel(Boolean value, ValueContext context) {
			return Result.ok(Boolean.TRUE.equals(value) ? 1 : 0);
		}

		@Override
		public Boolean convertToPresentation(Integer value, ValueContext context) {
			return value != null && value != 0;
		}
	}

	public BinderAdv() {
		super();
	}

	public BinderAdv(PropertySet<BEAN> propertySet) {
		super(propertySet);
	}

	public BinderAdv(Class<BEAN> beanType) {
		super(beanType);
	}

	public BinderAdv(Class<BEAN> beanType, BEAN bean) {
		super(beanType);
		setBean(bean);
	}

	public BinderAdv(Class<BEAN> beanType, boolean scanNestedDefinitions) {
		super(beanType, scanNestedDefinitions);
	}
	
	private String getRequiredError(HasValue<?,?> field) {
		if(field instanceof Component) {
			return UtilsUI.translate("msg.error.obligatorio", ((Component) field).getElement().getProperty("label"));
		}
		return UtilsUI.translate("msg.error.campo.obligatorio");
	}
	
	/**
	 * Enlaza el valor de un campo de Vaadin con el atributo de un bean
	 * 
	 * @param field
	 *            campo de Vaadin
	 * @param getter
	 *            getter del bean
	 * @param setter
	 *            setter del bean
	 * @param required
	 *            indica si el campo es obligatorio, en cuyo caso añade un
	 *            mensaje de validacion y la marca correspondiente al campo
	 * @return
	 * @see Binder#bind(HasValue, ValueProvider, Setter)
	 */
	public <FIELDVALUE> Binding<BEAN, FIELDVALUE> bind(HasValue<?,FIELDVALUE> field, ValueProvider<BEAN, FIELDVALUE> getter,
            Setter<BEAN, FIELDVALUE> setter, boolean required) {
		if(required) {
			return forField(field).asRequired(getRequiredError(field)).bind(getter, setter);
		}
		return forField(field).bind(getter, setter);
    }
	
	
	/**
	 * Enlaza el valor de un campo de texto con el atributo de tipo {@link Long} de un bean
	 * 
	 * @param field
	 *            campo de Vaadin
	 * @param getter
	 *            getter del bean
	 * @param setter
	 *            setter del bean
	 * @return
	 * @see Binder#bind(HasValue, ValueProvider, Setter)
	 */
	public Binding<BEAN, Long> bindLong(HasValue<?,Double> field, ValueProvider<BEAN, Long> getter, Setter<BEAN, Long> setter) {
		return bindLong(field, getter, setter, false);
	}
	
	/**
	 * Enlaza el valor de un campo de texto con el atributo de tipo {@link Long} de un bean
	 * 
	 * @param field
	 *            campo de Vaadin
	 * @param getter
	 *            getter del bean
	 * @param setter
	 *            setter del bean
	 * @param required
	 *            indica si el campo es obligatorio, en cuyo caso añade un
	 *            mensaje de validacion y la marca correspondiente al campo
	 * @return
	 * @see Binder#bind(HasValue, ValueProvider, Setter)
	 */
	public Binding<BEAN, Long> bindLong(HasValue<?,Double> field, ValueProvider<BEAN, Long> getter, Setter<BEAN, Long> setter, boolean required) {
		if(required) {
			return forField(field).asRequired(getRequiredError(field)).withConverter(new DoubleToLongConverter()).bind(getter, setter);
		}
		return forField(field).withConverter(new DoubleToLongConverter()).bind(getter, setter);
	}
	
	public Binding<BEAN, Integer> bindBoolean(HasValue<?,Boolean> field, ValueProvider<BEAN, Integer> getter, Setter<BEAN, Integer> setter) {
		return forField(field).withConverter(new BooleanConverter()).bind(getter, setter);
	}

	public Binding<BEAN, Double> bindDouble(HasValue<?,String> field, ValueProvider<BEAN, Double> getter, Setter<BEAN, Double> setter, boolean required) {
		StringToDoubleConverter converter = new StringToDoubleConverter(((Component) field).getTranslation("msg.error.formato.entero"));
		if(required) {
			return forField(field).asRequired(getRequiredError(field)).withConverter(converter).bind(getter, setter);
		}
		return forField(field).withConverter(converter).bind(getter, setter);
	}

	public Binding<BEAN, Double> bindDouble(HasValue<?,String> field, ValueProvider<BEAN, Double> getter, Setter<BEAN, Double> setter) {
		return bindDouble(field, getter, setter, false);
	}
	
	public Binding<BEAN, Double> bindDouble2Digitos(HasValue<?,String> field, ValueProvider<BEAN, Double> getter, Setter<BEAN, Double> setter, boolean required) {
		StringToDoubleConverter converter = new StringToDoubleConverter(((Component) field).getTranslation("msg.error.formato.entero"), new DecimalFormat("0.00"));
		if(required) {
			return forField(field).asRequired(getRequiredError(field)).withConverter(converter).bind(getter, setter);
		}
		return forField(field).withConverter(converter).bind(getter, setter);
	}

	public Binding<BEAN, Double> bindDouble2Digitos(HasValue<?,String> field, ValueProvider<BEAN, Double> getter, Setter<BEAN, Double> setter) {
		return bindDouble2Digitos(field, getter, setter, false);
	}
}
