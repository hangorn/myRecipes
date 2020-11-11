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

import java.io.InputStream;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;

public class UtilsUI {
	/**
	 * Estilo para añadir a un contenedor un borde
	 */
	public static final String ESTILO_CAJA_BORDE = "caja-borde";
	/**
	 * Dialogo sin margenes
	 */
	public static final String ESTILO_DIALOGO_SIN_BORDES = "no-border-dialog";
	/**
	 * Pestañas con bordes
	 */
	public static final String ESTILO_TAB_BORDE = "tab-borde";
	/**
	 * Varias lineas de texto dentro de un componente
	 */
	public static final String ESTILO_MULTILINEA = "multiline";
	
	/**
	 * Estilo para mostrar un texto como etiqueta: con border redondeados y margen
	 * horizontal. Pensado para usarlo junto con un estilo de color de fondo
	 */
	public static final String ESTILO_ETIQUETA = "etiqueta";

	/**
	 * Estilo para hacer clickable las filas de una tabla
	 */
	public static final String ESTILO_CLICKABLE = "clickable";
	/**
	 * Estilo para mostrar un texto con negrita
	 */
	public static final String ESTILO_NEGRITA = "negrita";
	/**
	 * Estilo para fijar como color de fondo el color de error claro (rojo) y como color de texto el color de error mas oscuro
	 */
	public static final String ESTILO_ERROR_BACKGROUND = "error-background";
	/**
	 * Estilo para fijar como color de fondo el color de error mas oscuro (rojo) y como color de texto el color de contraste (normalmente blanco)
	 */
	public static final String ESTILO_ERROR_BACKGROUND_CONTRAST = "error-background-contrast";
	/**
	 * Estilo para fijar como color de fondo el color de error claro (rojo) en el contenido de un campo
	 */
	public static final String ESTILO_ERROR_BACKGROUND_FIELD = "error-background-field";
	/**
	 * Estilo para fijar como color de fondo el color de exito claro (verde) y como color de texto el color de exito mas oscuro
	 */
	public static final String ESTILO_SUCCESS_BACKGROUND = "success-background";
	/**
	 * Estilo para fijar como color de fondo el color de exito mas oscuro (verde) y como color de texto el color de contraste (normalmente blanco)
	 */
	public static final String ESTILO_SUCCESS_BACKGROUND_CONTRAST = "success-background-contrast";
	/**
	 * Estilo para fijar como color de fondo el color de exito claro (verde) en el contenido de un campo
	 */
	public static final String ESTILO_SUCCESS_BACKGROUND_FIELD = "success-background-field";
	/**
	 * Estilo para fijar como color de fondo el color de principal claro (azul por defecto) y como color de texto el color principal mas oscuro
	 */
	public static final String ESTILO_PRIMARY_BACKGROUND = "primary-background";
	/**
	 * Estilo para fijar como color de fondo el color de principal mas oscuro (rojo) y como color de texto el color de contraste (normalmente blanco)
	 */
	public static final String ESTILO_PRIMARY_BACKGROUND_CONTRAST = "primary-background-contrast";
	/**
	 * Estilo para fijar como color de fondo el color de principal claro (azul por defecto) en el contenido de un campo
	 */
	public static final String ESTILO_PRIMARY_BACKGROUND_FIELD = "primary-background-field";
	
	/**
	 * Azul
	 */
	public static final String ESTILO_BLUE_BACKGROUND = "blue-background";
	public static final String ESTILO_BLUE_BACKGROUND_CONTRAST = "blue-background-contrast";
	public static final String ESTILO_BLUE_BACKGROUND_FIELD = "blue-background-field";
	/**
	 * Dorado
	 */
	public static final String ESTILO_GOLD_BACKGROUND = "gold-background";
	public static final String ESTILO_GOLD_BACKGROUND_CONTRAST = "gold-background-contrast";
	public static final String ESTILO_GOLD_BACKGROUND_FIELD = "gold-background-field";
	/**
	 * Gris
	 */
	public static final String ESTILO_GREY_BACKGROUND = "grey-background";
	public static final String ESTILO_GREY_BACKGROUND_CONTRAST = "grey-background-contrast";
	public static final String ESTILO_GREY_BACKGROUND_FIELD = "grey-background-field";

	public static final VaadinIcon ICONO_RECIPES = VaadinIcon.CLIPBOARD_TEXT;
	
	/**
	 * Ancho para campos de fechas
	 */
	public static final String WIDTH_DATE = "130px";
	
	/**
	 * Crea un icono de Vaadin con el color indicado
	 * @param vIcon icono de vaadin
	 * @param color
	 * @return instancia del icono
	 */
	public static Icon icon(VaadinIcon vIcon, String color) {
		Icon icon = vIcon.create();
		icon.setColor(color);
		return icon;
	}
	
	/**
	 * Crea un icono de Vaadin con el color indicado
	 * @param vIcon icono de vaadin
	 * @param color
	 * @return instancia del icono
	 */
	public static Icon icon(VaadinIcon vIcon, String color, String size) {
		Icon icon = vIcon.create();
		icon.setColor(color);
		icon.setSize(size);
		return icon;
	}

	/**
	 * Obtiene un texto del fichero de recursos
	 * @param key clave del texto a obtener
	 * @param args argumentos opcionales para rellenar un texto parametrizado
	 * @return
	 */
	public static String translate(String key, Object... args) {
		I18NProvider i18nProvider = VaadinService.getCurrent().getInstantiator().getI18NProvider();
		return i18nProvider.getTranslation(key, i18nProvider.getProvidedLocales().get(0), args);
	}
	
	/**
	 * Muestra una notificacion con un boton de cerrar
	 * @param text texto de la notificacion
	 * @return
	 */
	public static Dialog notification(String text) {
		Dialog dialog = new Dialog();
		Button cerrar = new Button(dialog.getTranslation("action.cerrar"), VaadinIcon.CLOSE.create(), e->dialog.close());
		Span span = new Span(text);
		span.setWidth("max-content");
		VerticalLayout layout = new VerticalLayout(span, cerrar);
		layout.setPadding(false);
		layout.setAlignSelf(Alignment.CENTER, cerrar);
		dialog.add(layout);
		dialog.open();
		return dialog;
	}
	
	/**
	 * Muestra un mensaje de error con un boton de cerrar
	 * @param text texto del error
	 * @return
	 */
	public static Notification error(String text) {
		Notification notification = new Notification();
		notification.setDuration(0);
		notification.setPosition(Position.MIDDLE);
		notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

		Button cerrar = new Button(notification.getTranslation("action.cerrar"), VaadinIcon.CLOSE.create(), e -> notification.close());
		cerrar.setMinWidth("min-content");
		
		HorizontalLayout layout = new HorizontalLayout();
		Span textarea = new Span(text);
		textarea.getStyle().set("white-space", " pre");
		layout.add(textarea, cerrar);
		layout.expand(layout.getComponentAt(0));
		layout.setMinWidth("500px");
		layout.setAlignItems(Alignment.CENTER);
		notification.add(layout);
		notification.open();
		return notification;
	}
	
	/**
	 * Crear y muestra un dialogo de confirmacion: un texto, un boton de aceptar y otro de cancelar
	 * @param text texto a mostrar en el dialogo
	 * @param onConfirm accion que se ejecutara al configurar, pulsar el boton de aceptar
	 * @return
	 */
	public static Dialog confirmDialog(String text, ComponentEventListener<ClickEvent<Button>> onConfirm) {
		Dialog dialog = new Dialog();
		Button cerrar = new Button(dialog.getTranslation("action.aceptar"), UtilsUI.icon(VaadinIcon.CHECK, "green"), e->{onConfirm.onComponentEvent(e);dialog.close();});
		Button volver = new Button(dialog.getTranslation("action.cancelar"), UtilsUI.icon(VaadinIcon.CLOSE, "red"), e->dialog.close());
		Span msg = new Span(text);
		msg.getElement().getStyle().set("padding-right", "5px");
		HorizontalLayout layout = new HorizontalLayout(msg, volver, cerrar);
		layout.setAlignItems(Alignment.CENTER);
		dialog.add(layout);
		dialog.open();
		return dialog;
	}
	
	/**
	 * Crear un boton para descargar un fichero
	 * 
	 * @param button      boton que se mostrara
	 * @param fileName    nombre del fichero a descargar
	 * @param inputStream factoria que debe crear un {@link InputStream} que
	 *                    contenga los datos a descargar
	 * @return componente de Vaadin que permitira descargar un fichero
	 */
	public static Anchor download(Button button, String fileName, InputStreamFactory inputStream) {
		Anchor download = new Anchor();
		download.setHref(new StreamResource(fileName, inputStream));
		download.add(button);
		download.setTarget("_blank");
		// Flag para indicar que hay que descargar el fichero, en firefox no funciona para formatos pdf, txt, xml
		//download.getElement().setAttribute("download",true);
		return download;
	}
	
	/**
	 * Hace que el componente y todos los que contenga (si es un contenedor) sean de solo lectura de forma recursiva.
	 * @param component componente que se pondra como solo lectura
	 */
	public static void readOnly(Component component, boolean readOnly) {
		if (component instanceof HasValueAndElement) {
			((HasValueAndElement<?,?>)component).setReadOnly(readOnly);
		}
		component.getChildren().forEach(child->readOnly(child, readOnly));
	}
	
	/**
	 * Fija un tooltip html (atributo title) para el componente indicado
	 * @param component componente al que se pondra el tooltip
	 * @param tooltip texto del tooltip
	 * @return el mismo componente al que se le ha puesto el tooltip
	 */
	public static <T extends Component> T tooltip(T component, String tooltip) {
		component.getElement().setAttribute("title", tooltip);
		return component;
	}
	
	/**
	 * Añade al componente recibido como parametro un texto similar al resto de campos de vaadin
	 * @param caption texto
	 * @param component 
	 * @return compoente con el texto añadido
	 */
	public static Component addCaption(String caption, Component component) {
		Div div = new Div();
		div.getStyle().set("padding-top", "var(--lumo-space-m)");
		div.getStyle().set("display", "flex");
		div.getStyle().set("flex-direction", "column");
		Span text = new Span(caption);
		text.getStyle().set("color", "var(--lumo-secondary-text-color)");
		text.getStyle().set("font-weight", "500");
		text.getStyle().set("font-size", "var(--lumo-font-size-s)");
		text.getStyle().set("line-height", "1");
		text.getStyle().set("padding-bottom", "0.5em");
		div.add(text);
		div.add(component);
		component.getElement().getStyle().set("height", "var(--lumo-text-field-size)");
		return div;
	}
	
	public static Dialog notificationBig(String text) {
		Dialog dialog = new Dialog();
		Button cerrar = new Button(dialog.getTranslation("action.cerrar"), VaadinIcon.CLOSE.create(), e->dialog.close());
		cerrar.addThemeVariants(ButtonVariant.LUMO_LARGE, ButtonVariant.LUMO_PRIMARY);
		H3 span = new H3(text);
		span.setWidth("max-content");
		VerticalLayout layout = new VerticalLayout(span, cerrar);
		layout.setPadding(false);
		layout.setAlignSelf(Alignment.CENTER, cerrar);
		dialog.add(layout);
		dialog.open();
		return dialog;
	}
	
	/**
	 * Crear y muestra un dialogo de confirmacion: un texto, un boton de aceptar y otro de cancelar
	 * @param text texto a mostrar en el dialogo
	 * @param onConfirm accion que se ejecutara al configurar, pulsar el boton de aceptar
	 * @return
	 */
	public static Dialog confirmDialogBig(String text, ComponentEventListener<ClickEvent<Button>> onConfirm) {
		Dialog dialog = new Dialog();
		Button volver = new Button(dialog.getTranslation("action.cancelar"), UtilsUI.icon(VaadinIcon.CLOSE, "red"), e->dialog.close());
		volver.addThemeVariants(ButtonVariant.LUMO_LARGE);
		volver.getStyle().set("margin", "10px 30px");
		volver.setHeight("80px");
		Button aceptar = new Button(dialog.getTranslation("action.aceptar"), UtilsUI.icon(VaadinIcon.CHECK, "green"), e->{onConfirm.onComponentEvent(e);dialog.close();});
		aceptar.addThemeVariants(ButtonVariant.LUMO_LARGE);
		aceptar.setHeight("80px");
		aceptar.getStyle().set("margin", "10px");
		H3 msg = new H3(text);
		msg.getElement().getStyle().set("padding-right", "5px");
		HorizontalLayout layout = new HorizontalLayout(msg, volver, aceptar);
		layout.setAlignItems(Alignment.CENTER);
		dialog.add(layout);
		dialog.open();
		return dialog;
	}
	
	public static Span buildLeyendaItem(String txt, String classname) {
		Span span = new Span(txt);
		span.setHeight("min-content");
		span.addClassNames(UtilsUI.ESTILO_ETIQUETA, classname);
		return span;
	}

	public static void addAsPrefix(Component container, Component component) {
		component.getElement().setAttribute("slot", "prefix");
		container.getElement().appendChild(component.getElement());
	}

	public static void addAsSuffix(Component container, Component component) {
		component.getElement().setAttribute("slot", "suffix");
		container.getElement().appendChild(component.getElement());
	}
}
