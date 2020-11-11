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

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.server.VaadinServlet;

import es.magDevs.myRecipes.ui.error.MainErrorHandler;

@WebServlet(urlPatterns = "/*", name = "mainServlet", asyncSupported = true, initParams = {
		@WebInitParam(name = InitParameters.I18N_PROVIDER, value = "es.magDevs.myRecipes.ui.TextProvider")
})
public class MainServlet extends VaadinServlet {
	@Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionInitListener(e->e.getSession().setErrorHandler(new MainErrorHandler()));
    }
}
