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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Locale;

import com.vaadin.flow.component.datepicker.DatePicker;

public class DatePickerEx extends DatePicker {

	public DatePickerEx() {
		super();
		
		esInit();
	}
	
	public DatePickerEx(LocalDate initialDate, Locale locale) {
		super(initialDate, locale);
		
		esInit();
	}

	public DatePickerEx(LocalDate initialDate, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		super(initialDate, listener);
		
		esInit();
	}

	public DatePickerEx(LocalDate initialDate) {
		super(initialDate);

		esInit();
	}

	public DatePickerEx(String label, LocalDate initialDate, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		super(label, initialDate, listener);
		
		esInit();
	}

	public DatePickerEx(String label, LocalDate initialDate) {
		super(label, initialDate);
		
		esInit();
	}

	public DatePickerEx(String label, ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		super(label, listener);
		
		esInit();
	}

	public DatePickerEx(String label) {
		super(label);
		
		esInit();
	}

	public DatePickerEx(ValueChangeListener<ComponentValueChangeEvent<DatePicker, LocalDate>> listener) {
		super(listener);
		
		esInit();
	}


	private void esInit() {
		setI18n(
		        new DatePickerI18n().setWeek("Semana").setCalendar("Calendario")
		                .setClear("Vaciar").setToday("Hoy")
		                .setCancel("Cancelar").setFirstDayOfWeek(1)
		                .setMonthNames(Arrays.asList("enero", "febrero",
		                        "marzo", "abril", "mayo", "junio",
		                        "julio", "agosto", "septiembre", "octubre",
		                        "noviembre", "diciembre")).setWeekdays(
		                Arrays.asList("domingo", "lunes", "martes",
		                        "miércoles", "jueves", "viernes",
		                        "lunes")).setWeekdaysShort(
		                Arrays.asList("dom", "lun", "mar", "mie", "jue", "vie",
		                        "sab")));
	}
}
