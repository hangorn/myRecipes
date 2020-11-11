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
package es.magDevs.myRecipes.ui.error;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

public class NotFoundErrorView extends VerticalLayout implements HasErrorParameter<NotFoundException> {

    private Span explanation;
	private Span exception;
	private VerticalLayout scroll;

    public NotFoundErrorView() {
    	setSizeFull();
        H1 header = new H1("La página solicitada no se ha encontrado o ha ocurrido un error al buscarla");
        add(header);
        Icon icon = VaadinIcon.CHEVRON_CIRCLE_RIGHT.create();
        icon.setColor("green");
        Button button = new Button("Volver a la pagina de inicio", icon, e->UI.getCurrent().navigate(""));
        Button masInf = new Button("Más informacion", VaadinIcon.INFO_CIRCLE_O.create(), e->scroll.setVisible(true));
        
		add(new HorizontalLayout(button, masInf));

        explanation = new Span();
        add(explanation);
        
        exception = new Span();
        exception.setWidthFull();
        exception.getStyle().set("white-space", " pre");
		scroll = new VerticalLayout(exception);
		scroll.setVisible(false);
		scroll.getStyle().set("overflow-y", "auto");
		scroll.getStyle().set("overflow-x", "hidden");
		scroll.getStyle().set("background-color", "lightgrey");
		scroll.getStyle().set("border", "1px solid black");
        add(scroll);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        explanation.setText("No se ha podido ir a la página '" + event.getLocation().getPath() + "'.");
        
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		parameter.getCaughtException().printStackTrace(new PrintStream(os));
        exception.setText(os.toString());
        return HttpServletResponse.SC_NOT_FOUND;
    }

}
