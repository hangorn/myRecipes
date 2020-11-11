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
package es.magDevs.myRecipes.ui.auth;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.WrappedSession;

public class AccessControl {
	public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = AccessControl.class.getCanonicalName();
	private static final AccessControl accessControl = new AccessControl();
	
    public boolean signIn(String username, String password) {
        if (username == null || username.isEmpty() || !checkLogin(username, password)) {
        	return false;
        }
        getSession().setAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY, username);
        return true;
    }

	private WrappedSession getSession() {
		return VaadinService.getCurrentRequest().getWrappedSession();
	}

    private boolean checkLogin(String username, String password) {
//		try {
//			return new UsuariosBL().autenticaUsuario(username, password);
//		} catch (AutenticaException e) {
//			return false;
//		}
    	return true;
	}

    public boolean isUserSignedIn() {
		return StringUtils.isNotBlank(getPrincipalName());
    }

    public String getPrincipalName() {
        return (String) getSession().getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);
    }

    public void signOut() {
        VaadinSession.getCurrent().getSession().invalidate();
        UI.getCurrent().getPage().reload();
    }

	public static AccessControl getInstance() {
		return accessControl;
	}
}
