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

import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.DefaultErrorHandler;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;

public class MainErrorHandler implements ErrorHandler {
	
	public static void handleError(Throwable e) {
		new MainErrorHandler().error(new ErrorEvent(e));
	}

	@Override
	public void error(ErrorEvent event) {
		Throwable t = DefaultErrorHandler.findRelevantThrowable(event.getThrowable());

		LoggerFactory.getLogger(MainErrorHandler.class).error("", t);

		Button masInf = new Button(VaadinIcon.INFO.create());

		Notification notification = new Notification();
		notification.setDuration(0);
		notification.setPosition(Position.MIDDLE);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

		Button cerrar = new Button(masInf.getTranslation("action.cerrar"), VaadinIcon.CLOSE.create(), e -> notification.close());
		masInf.setText(masInf.getTranslation("action.mas.informacion"));
		masInf.addClickListener(e-> {
			masInf.setVisible(false);
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			t.printStackTrace(new PrintStream(os));
			Span textarea = new Span(os.toString());
			textarea.setWidthFull();
			textarea.getStyle().set("white-space", " pre");
			VerticalLayout scroll = new VerticalLayout(textarea);
			scroll.getStyle().set("overflow", "auto");
			scroll.getStyle().set("background-color", "#db403d");
			scroll.getStyle().set("color", "white");
			scroll.setMaxHeight("500px");
			scroll.setMaxWidth("1000px");
			notification.add(scroll);
		});
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.add(new Span(masInf.getTranslation("msg.error")), cerrar, masInf);
		layout.expand(layout.getComponentAt(0));
		layout.setMinWidth("500px");
		layout.setAlignItems(Alignment.CENTER);
		notification.add(layout);
		notification.open();
	}
}
