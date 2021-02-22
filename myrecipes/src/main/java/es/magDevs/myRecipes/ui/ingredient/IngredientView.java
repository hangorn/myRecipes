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
package es.magDevs.myRecipes.ui.ingredient;

import java.util.Arrays;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import es.magDevs.myRecipes.ui.MainView;

@Route(layout = MainView.class)
public class IngredientView extends VerticalLayout {

	public IngredientView() {
		add(new Span("Test"));
		Button btn1 = new Button("Boton 1", VaadinIcon.ABACUS.create());
		btn1.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		Button btn2 = new Button("Boton 2", VaadinIcon.ABACUS.create());
		btn2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		Button btn3 = new Button("Boton 2", VaadinIcon.ABACUS.create());
		btn3.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
		add(btn1, btn2, btn3);
		Grid<String> grid = new Grid<String>();
		grid.addColumn(s->s).setHeader("Cabecera");
		grid.setItems(Arrays.asList("Fila 1", "Fila 2", "Fila 3"));
		ComboBox<String> combo = new ComboBox<String>("Combo 1");
		combo.setItems(Arrays.asList("Fila 1", "Fila 2", "Fila 3"));
		add(btn1, btn2, btn3, grid, combo);
	}
}
