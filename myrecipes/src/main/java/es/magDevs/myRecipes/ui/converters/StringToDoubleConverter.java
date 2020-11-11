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
package es.magDevs.myRecipes.ui.converters;

import java.text.DecimalFormat;

import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class StringToDoubleConverter implements Converter<String, Double> {

	private String errorMsg;
	private DecimalFormat format;

	public StringToDoubleConverter(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public StringToDoubleConverter(String errorMsg, DecimalFormat format) {
		this.errorMsg = errorMsg;
		this.format = format;
	}

	@Override
	public Result<Double> convertToModel(String value, ValueContext context) {
		if (value == null || value.isEmpty()) {
			return Result.ok(null);
		}
		try {
			return Result.ok(Double.valueOf(value.replaceAll("[,.]", ".")));
		} catch (NumberFormatException e) {
			return Result.error(errorMsg);
		}
	}

	@Override
	public String convertToPresentation(Double value, ValueContext context) {
		if (value == null) {
			return "";
		}
		if (format == null) {
			return value.toString().replace('.', ',');
		}
		return format.format(value);
	}

}
