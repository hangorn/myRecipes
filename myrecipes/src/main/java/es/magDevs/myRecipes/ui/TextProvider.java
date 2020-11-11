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
package es.magDevs.myRecipes.ui;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.LoggerFactory;

import com.vaadin.flow.i18n.I18NProvider;

public class TextProvider implements I18NProvider {
	
	private static final List<Locale> LOCALES = Arrays.asList(new Locale("es"));
	private static final ResourceBundle bundle = ResourceBundle.getBundle("language", LOCALES.get(0));

	@Override
	public List<Locale> getProvidedLocales() {
		return LOCALES;
	}

	@Override
	public String getTranslation(String key, Locale locale, Object... params) {
		String value = key;
		try {
            value = bundle.getString(key);
        } catch (final MissingResourceException e) {
            LoggerFactory.getLogger(TextProvider.class).warn("Recurso de texto no encontrado: "+key, e);
        }
        if (params.length > 0) {
            value = MessageFormat.format(value, params);
        }
        return value;
	}

}
