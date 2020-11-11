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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.server.PageConfigurator;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;

import es.magDevs.myRecipes.ui.auth.AccessControl;
import es.magDevs.myRecipes.ui.common.UtilsUI;
import es.magDevs.myRecipes.ui.recipes.RecipesView;

/**
 * The main view contains a button and a click listener.
 */
@PWA(name = "Gestión de recetas", shortName = "Mis recetas", description = "Gestión de recetas")
@Theme(value = Lumo.class)
@CssImport("./styles/shared-styles.css")
@CssImport(value="./styles/grid-styles.css", themeFor = "vaadin-grid")
@CssImport(value="./styles/dialog-styles.css", themeFor = "vaadin-dialog-overlay")
@CssImport(value="./styles/common-styles.css", themeFor = "*")
@Push
public class MainView extends AppLayout implements PageConfigurator, RouterLayout {

	final static Logger log = LoggerFactory.getLogger(MainView.class);
	private Map<Class<? extends Component>, Tab> mapTabs = new HashMap<>();
	private Tabs tabs;
	private Tabs subtabs;
	private RouterLink tabSubmenu;

    public MainView() {
    	getElement().getStyle().set("height", "100%");
    	
		HorizontalLayout barraSuperior = new HorizontalLayout(new DrawerToggle());
		barraSuperior.setWidthFull();
		barraSuperior.getStyle().set("padding-left", "2px");
		barraSuperior.getStyle().set("border-bottom", "1px solid var(--lumo-contrast-20pct)");
		barraSuperior.getElement().setAttribute("theme", Lumo.DARK);
		addToNavbar(barraSuperior);
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setPadding(false);
        menuLayout.setHeightFull();
        menuLayout.getElement().setAttribute("theme", Lumo.DARK);
        addToDrawer(menuLayout);
		
        tabs = new Tabs(buildTab("field.configuracion", UtilsUI.ICONO_RECIPES.create(), RecipesView.class),
        		buildTab("field.configuracion", VaadinIcon.COG.create(), null));
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setSizeFull();

        subtabs = new Tabs(false);
        subtabs.setVisible(false);
        subtabs.setOrientation(Tabs.Orientation.VERTICAL);
        subtabs.getStyle().set("padding-left", "50px");
        subtabs.getStyle().set("margin-left", "0");
        
        subtabs.addSelectedChangeListener(e->tabs.setSelectedTab(null));
        tabs.addSelectedChangeListener(e->{
        	if (e.getSelectedTab() == tabSubmenu.getParent().get()) {
        		//Si el menu seleccionado es el de configuracion, restauramos el menu que estuviera antes seleccionado
        		tabs.setSelectedTab(e.getPreviousTab());
        	} else if (e.getSelectedTab() != null) {
        		subtabs.setSelectedTab(null);
        	}
        	
        });
        
        menuLayout.add(tabs);
        tabs.add(subtabs);
        
        Button btnSalir = new Button(getTranslation("action.salir"), VaadinIcon.EXIT_O.create(), e->AccessControl.getInstance().signOut());
    	menuLayout.add(new VerticalLayout(btnSalir));
    }

	private Tab buildTab(String txtKey, Icon icon, Class<? extends Component> view) {
		Span txt = new Span(getTranslation(txtKey));
		txt.getElement().getStyle().set("padding-left", "10px");
		RouterLink routerLink = new RouterLink();
		routerLink.add(icon, txt);
		Tab tab = new Tab(routerLink);
		
		if(view==null) {
			tabSubmenu = routerLink;
			ComponentUtil.addListener(tab, ClickEvent.class, e->{
				boolean submenu=!subtabs.isVisible();
				subtabs.setVisible(submenu);
				
				routerLink.getChildren().findFirst().ifPresent(primero-> routerLink.remove(primero));
				routerLink.addComponentAsFirst(UtilsUI.icon(submenu ? VaadinIcon.CARET_DOWN : VaadinIcon.CARET_RIGHT, null, "15px"));
			});
			routerLink.addComponentAsFirst(UtilsUI.icon(VaadinIcon.CARET_RIGHT, null, "15px"));
			tab.getStyle().set("padding-left", "1px");
		}else {
			routerLink.setRoute(view);
			
			ComponentUtil.addListener(tab, ClickEvent.class, e->{
				if (tab.getParent().get() == tabs) {
					subtabs.setVisible(false);
					tabSubmenu.getChildren().findFirst().ifPresent(primero-> tabSubmenu.remove(primero));
					tabSubmenu.addComponentAsFirst(UtilsUI.icon(VaadinIcon.CARET_RIGHT, null, "15px"));
				}
			});
		}
		
		mapTabs.put(view, tab);	
		
		return tab;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		Tab selectedTab = mapTabs.get(getContent().getClass());
		Tabs parent = (Tabs) selectedTab.getParent().get();
		parent.setSelectedTab(selectedTab);
		if (parent == subtabs) {
			subtabs.setVisible(true);
			tabSubmenu.getChildren().findFirst().ifPresent(primero-> tabSubmenu.remove(primero));
			tabSubmenu.addComponentAsFirst(UtilsUI.icon(VaadinIcon.CARET_DOWN, null, "15px"));
		}
	}

	@Override
	public void configurePage(InitialPageSettings settings) {
		// Textos de perdida de conexion
		settings.getReconnectDialogConfiguration().setDialogText(getTranslation("msg.error.lost.connection.reconnect"));
		settings.getReconnectDialogConfiguration().setDialogTextGaveUp(getTranslation("msg.error.lost.connection"));
		// Configuracion del indicador de cargando
		settings.getLoadingIndicatorConfiguration().setApplyDefaultTheme(false);
	}
}
